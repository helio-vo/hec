package eu.heliovo.hfe.controller;
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import javax.xml.transform.stream.StreamSource

import net.ivoa.xml.votable.v1.*

import org.springframework.web.context.request.RequestContextHolder

import ch.i4ds.helio.frontend.parser.*
import ch.i4ds.helio.frontend.query.*
import eu.heliovo.clientapi.frontend.*
import eu.heliovo.clientapi.linkprovider.*
import eu.heliovo.clientapi.processing.ProcessingResult
import eu.heliovo.clientapi.processing.ProcessingServiceFactory
import eu.heliovo.clientapi.processing.context.FlarePlotterService
import eu.heliovo.clientapi.processing.context.GoesPlotterService
import eu.heliovo.clientapi.processing.context.GoesPlotterService.PlotType;
import eu.heliovo.clientapi.processing.context.SimpleParkerModelService
import eu.heliovo.clientapi.query.*
import eu.heliovo.clientapi.query.asyncquery.*
import eu.heliovo.clientapi.utils.STILUtils
import eu.heliovo.hfe.model.HelioMemoryBar
import eu.heliovo.hfe.model.HelioQuery
import eu.heliovo.hfe.service.*
import eu.heliovo.registryclient.*
import eu.heliovo.registryclient.impl.AccessInterfaceImpl
import eu.heliovo.shared.props.HelioFileUtil

/**
 * Main controller for the HFE.
 * TOOD: rename to ExplorerController
 */
class PrototypeController {

    /**
     * Ref to the Data query Service
     */
    def dataQueryService;
    
    /**
     * Ref to the ResultVTManagerService
     */
    def resultVTManagerService;
    
    /**
     * Execute an asynchronous query
     * TODO: Move to MetadataQueryController
     */
    def asyncQuery ={
        log.info("asyncQuery =>" +params);
        String sessionId = RequestContextHolder.getRequestAttributes()?.getSessionId()

        if(params.maxDate != null){
            try{
               
                ResultVT result = search(params);
                String serviceName = params.serviceName;
                
                int resultId= resultVTManagerService.addResult(result,serviceName);
                def responseObject = [result:result,resultId:resultId ];
                //helioquery.result = result.getStringTable();
                
                render template:'templates/response', bean:responseObject, var:'responseObject'
            } catch(Exception e){
                println e.getMessage();                
                
                def responseObject = [error:e.getMessage() ];
                
                render template:'templates/response', bean:responseObject, var:'responseObject';
            }
        }
        else {
            // TODO: what todo?
        }
    }

    /**
     * Use explorer as default action.
     */
    def index = {
        redirect(action:"explorer");
    }

    /**
     * Remove the current session and load entry page.
     * TODO: move to SessionController
     */
    def deleteSession = {
        log.info("deleteSession =>" +params)
        HelioMemoryBar item = HelioMemoryBar.findByHUID(params.HUID);
        if(item!=null) {
             item.delete();
        }
        def queries = HelioQuery.findAllByHUID(params.HUID);
        for(HelioQuery query : queries){
            query.delete();
        }
        render "success"
    }

    /**
     * Initialize the client session and show the main page. 
     */
    def explorer={
        log.info("Explorer =>" +params)     
        // TODO: read cookie value and load persisted session
        //Get sessionId to persist client history
        String sessionId = RequestContextHolder.getRequestAttributes()?.getSessionId()

        def initParams = [hecCatalogs:servletContext.eventListDescriptors, instrumentDescriptors: servletContext.instrumentDescriptors, HUID:sessionId];
        render view:'explorer', model:initParams
    }

    /**
     * Generic method to handle searches to catalog services
     * TODO: Move to MetadataQueryController 
     */
    def search = {
        log.info("Search =>" +params);
        ArrayList<String> maxDateList= new ArrayList<String>(); // initialize lists for webservice request
        ArrayList<String> minDateList= new ArrayList<String>();

        if(params.maxDate.contains(",")) {
            ArrayList<String> tempMaxDateList = params.maxDate.split(",");
            ArrayList<String> tempMinDateList = params.minDate.split(",");
            for(int i = 0; i<tempMaxDateList.size();i++){
                // TODO: rather use DateUtils.fromIsoDate / toIsoDate
                Date minDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",tempMinDateList.get(i));
                Date maxDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",tempMaxDateList.get(i));
                maxDateList.add(maxDate.format("yyyy-MM-dd'T'HH:mm:ss"));
                minDateList.add(minDate.format("yyyy-MM-dd'T'HH:mm:ss"));
            }
        }else{
            Date minDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",params.minDate);
            Date maxDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",params.maxDate);
            maxDateList.add(maxDate.format("yyyy-MM-dd'T'HH:mm:ss"));
            minDateList.add(minDate.format("yyyy-MM-dd'T'HH:mm:ss"));
        }
        ArrayList<String> extraList = new ArrayList<String>();
        if(params.extra.contains(",")){
            extraList = params.extra.split(",")
        }else{
            extraList.push(params.extra);
        }
         
        String where ="";
    	if(params.where != null) {
            where = params.where;
    	}
    	HelioServiceName serviceName = HelioServiceName.valueOf(params.serviceName.toUpperCase());
        
        // call the helio-clientapi
    	ResultVT result = dataQueryService.queryService(serviceName.getServiceName(), minDateList, maxDateList, extraList, where);
        return result;
    }
    
    /**
     * Download selected rows as VOTable
     * TODO: move to VoTableController
     */
    def downloadPartialVOTable = {
        log.info("downloadPartialVOTable =>" + params  + session)
        ResultVT result = resultVTManagerService.getResult(Integer.parseInt(params.resultId));
        // TODO: move functionality to ResultVTManagerService.
        if(result !=null){

            String indexes =params.indexes;
            String[] fields = indexes.split(",");
            def rowIndexSelection = [];
            def tableIndexSelection = [];
            for(String field:fields){
                String[] holder = field.split("resultTable");
                int row = Integer.parseInt(holder[0]);
                int table = Integer.parseInt(holder[1]);
                rowIndexSelection.add(table+"."+row);
                if(!tableIndexSelection.contains(table))tableIndexSelection.add(table);
            }
            VOTABLE res = result.getVOTABLE();
            for(int resourceIndex =0;resourceIndex < res.getRESOURCE().size();resourceIndex++)
            {
                RESOURCE resource = res.getRESOURCE().get(resourceIndex);
                if(!tableIndexSelection.contains(resourceIndex)){
                    res.getRESOURCE().set(resourceIndex,null);
                    continue;
                }
                for(int tableIndex =0;tableIndex < resource.getTABLE().size();tableIndex++)
                {
                    TABLE table= resource.getTABLE().get(tableIndex);
                    for(int trIndex=0;trIndex < table.getDATA().getTABLEDATA().getTR().size();trIndex++)
                    {
                        if(!rowIndexSelection.contains(resourceIndex+"."+trIndex)){

                            table.getDATA().getTABLEDATA().getTR().set(trIndex,null);
                        }
                    }
                }
            }
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date date = new Date();
            def name= formatter.format(date);
            name = resultVTManagerService.getResultServiceReference(Integer.parseInt(params.resultId)) +"-reduced-"+name;
            
            response.setContentType("application/xml")
            response.setHeader("Content-disposition", "attachment;filename="+name+".xml");
            response.outputStream << result.getStringTable()

        }

    }

    /**
     * Get a previously stored result
     * TODO: Move to VoTableController (if still needed.) 
     */
    def asyncGetSavedResult = {
        log.info("asyncGetSavedResult =>" + params);
        
        ResultVT result = resultVTManagerService.getResult(Integer.parseInt(params.resultId));
        def responseObject = [result:result,resultId:params.resultId ];
        //helioquery.result = result.getStringTable();
        //helioquery.save();
        render template:'templates/response', bean:responseObject, var:'responseObject'
        
    }
    
    /**
     * Persist the History Bar
     * TODO: Move to HistoryController
     */
    def asyncSaveHistoryBar = {
        log.info("asyncSaveHistoryBar =>" + params);
        
        HelioMemoryBar item = HelioMemoryBar.findByHUID(params.HUID);
        
        if(item == null)item = new HelioMemoryBar(hUID:params.HUID,html:params.html);

        item.html = params.html;
        item.save();
        
        for(HelioMemoryBar temp :HelioMemoryBar.list()){
            println temp.hUID;
        }

        render ""
        
    }
    
    
    /**
     * Load the content of the history bar
     * TODO: Move to HistoryController
     */
    def asyncGetHistoryBar = {
        log.info("asyncGetHistoryBar =>" + params);
        try{

            HelioMemoryBar item = HelioMemoryBar.findByHUID(params.HUID);
            if(item==null)render "";
            render item.html;

        }catch(Exception e){
            render "";

        }
    }

    /**
     * Used for Error handling after loading an invalid HEC table.
     * TODO: move to TaskController?
     */
    def asyncGetPreviousTaskState ={
        log.info("asyncGetPreviousTaskState =>" + params);
        
        HelioQuery query = HelioQuery.findByHUIDAndTaskName(params.HUID,params.taskName);

        if(query == null){
            render ""
            //query = new HelioMemoryBar(hUID:params.HUID,taskName:params.taskName,html:params.html);
        }else{
            println query;
            render query.html
        }
   
    }
    
    /**
     * Set the task state. 
     * TODO: move to TaskController? find out how this is used. Can probably be moved to the server side.
     */
    def asyncSetPreviousTaskState ={
        log.trace("asyncGetPreviousTaskState =>" + params);
        
        HelioQuery query = HelioQuery.findByHUIDAndTaskName(params.HUID,params.taskName);

        if(query == null){
            query = new HelioQuery(hUID:params.HUID,taskName:params.taskName,html:params.html);
            query.save();
        }else {
            query.html = params.html;
            query.save();
        }
        
        render ""
    }

    /**
     * Query the context service
     * TODO: Move to PlotController
     */
    def asyncQueryContextService = {
        log.info("asyncQueryContextService =>" + params);
        
        // temporary workaround until the registry is switched.
        AccessInterface ai = new AccessInterfaceImpl(AccessInterfaceType.SOAP_SERVICE, ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE, HelioFileUtil.asURL("http://msslkz.mssl.ucl.ac.uk/cxs/services/CommonExecutionConnectorService"));
        
        ProcessingServiceFactory factory = ProcessingServiceFactory.getInstance();
        // TODO: Use DateUtils
        Date minDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",params.minDate);
        Date maxDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",params.maxDate);
        if(params.type =="fplot"){
            
            FlarePlotterService flarePlotterService = factory.getFlarePlotterService(ai);
            
            ProcessingResult result = flarePlotterService.flarePlot(minDate);

            URL url = result.asURL(60, TimeUnit.SECONDS);

            println url;
            render url;
        } else if(params.type =="gplot") {
            GoesPlotterService goesPlotterService = factory.getGoesPlotterService(ai);
            Calendar cal = Calendar.getInstance();
            cal.set(2003, Calendar.JANUARY, 1, 0, 0, 0);
            Date startTime =  cal.getTime();
            cal.set(2003, Calendar.JANUARY, 3, 0, 0, 0);
            Date endTime =  cal.getTime();
            ProcessingResult result = goesPlotterService.goesPlot(minDate, maxDate);

            URL url = result.asURL(60, TimeUnit.SECONDS);
            println url;
            render url;
        } else if(params.type =="pplot") {
            SimpleParkerModelService parkerModelService = factory.getSimpleParkerModelService(ai);
            
            ProcessingResult result = parkerModelService.parkerModel(minDate);

            URL url = result.asURL(60, TimeUnit.SECONDS);
            render url;
        }
    }
    
    /**
     * Call a HELIO LinkProviderService
     * TODO: Move to LinkProviderController
     */
    def asyncQueryLinkService = {
        log.info("asyncQueryLinkService =>" + params);
        ProcessingServiceFactory factory = ProcessingServiceFactory.getInstance();
        // TODO: Use DateUtil
        Date minDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",params.minDate);
        Date maxDate =null;
        if( params.maxDate != ""){
            maxDate = Date.parse("yyyy-MM-dd'T'HH:mm:ss",params.maxDate);
        }else{
            maxDate = minDate;
        }
        URL url = null;
        
        if(params.type =="fplot"){
            FlarePlotterService flarePlotterService = factory.getFlarePlotterService((AccessInterface[])null);
            ProcessingResult result = flarePlotterService.flarePlot(minDate);
            render url = result.asURL(60, TimeUnit.SECONDS);
        }else if(params.type =="cplot"){
            GoesPlotterService goesPlotterService = factory.getGoesPlotterService((AccessInterface[])null);
            ProcessingResult result = goesPlotterService.goesPlot(minDate, maxDate, PlotType.BOTH);
            render url = result.asURL(60, TimeUnit.SECONDS);
        }else if(params.type =="pplot"){
            SimpleParkerModelService parkerModelService = factory.getSimpleParkerModelService((AccessInterface[])null);
            ProcessingResult result = parkerModelService.parkerModel(minDate, null, null);
            render url = result.asURL(60, TimeUnit.SECONDS);
        }else if(params.type =="link"){
            LinkProviderFactory lfactory = LinkProviderFactory.getInstance();
            LinkProviderService[] linkProviders = lfactory.getLinkProviders();
            String result = "";

            for (LinkProviderService provider : linkProviders) {
                URL link = provider.getLink(minDate, maxDate);
                if(link == null)continue;
                String title = provider.getTitle(minDate, maxDate);

                result = result +"<tr><td><a target='_blank' href='"+ link+"'>"+title+"</a></td></tr>";
            }
            render result;
        }
    }
    
    /**
    * Download results as a VOTable
    * TODO: move to VoTableController. Done, ready to be removed
    */
   def downloadVOTable = {
       log.info("downloadVOTable =>" + params);
       ResultVT result = resultVTManagerService.getResult(Integer.parseInt(params.resultId));
       if(result !=null){
           DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
           Date date = new Date();
           def name= formatter.format(date);
           name = resultVTManagerService.getResultServiceReference(Integer.parseInt(params.resultId)) +"-"+name;
           response.setContentType("application/xml")
           response.setHeader("Content-disposition", "attachment;filename="+name+".xml");
           response.outputStream << result.getStringTable()
       } else {
           render(status: 503, text: 'Failed to retrieve votable with id ' + params.resultId)
       }
   }
   
   /**
   * Upload a file
   * TODO: Move to VoTableController. Done, ready to be removed.
   */
  def asyncUpload ={
      log.info("asyncUpload =>" +params);

      try{
          if (request.getFile("fileInput").getOriginalFilename()=="") {
              throw new RuntimeException("A valid xml VO-table file must be selected to continue.");
          }
          if (!request.getFile("fileInput").getOriginalFilename().endsWith(".xml")) {
              throw new RuntimeException("Not a valid xml file. The name should end with .xml");
          }
          
          def starTable = STILUtils.load(request.getFile("fileInput").inputStream);
          //StilUtils.persist(starTable)
          
          // TODO: Use StilUtils
          JAXBContext context = JAXBContext.newInstance(VOTABLE.class);
          Unmarshaller unmarshaller = context.createUnmarshaller();

          VOTABLE votable = (VOTABLE) unmarshaller.unmarshal(new StreamSource(new StringReader( request.getFile("fileInput").inputStream.text)));
          
          def serviceName = 'upload';
          ResultVT result = new ResultVT(votable);
          int resultId= resultVTManagerService.addResult(result,serviceName);
          def uploadId =request.getFile("fileInput").getOriginalFilename();
          
          def responseObject = [result:result,resultId:resultId,uploadId:uploadId];
          render template:'templates/response', bean:responseObject, var:'responseObject'
      } catch(Exception e) {
          //e.printStackTrace();
          def responseObject = [error:e.getMessage() ];
          render template:'templates/response', bean:responseObject, var:'responseObject'
      }
  }
 


}
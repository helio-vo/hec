

<div id="actionViewer">
  <div class="viewerHeader">
    <table  style="height:30px;width: 100%;">
      <tr >
        <td>
           <!--img height="30px" src="${resource(dir:'images/icons/toolbar',file:'hec.png')}" /!-->
        </td>
        <td>
          <h1>Data Mining</h1>

        </td>


      </tr>
    </table>
  </div>

<%-- Query Form --%>
  <div  id="query_form" class="module ">
    <div class="header queryHeader viewerHeader">
      <h1>Query Form</h1>
    </div>
    <div class="content">
          <form id="actionViewerForm"  action="asyncQuery" method="POST">
        <input id="service_name" name="serviceName" type="hidden" value="DES"/>
        <input id="task_name" name="taskName" type="hidden" value="task_datamining"/>
        <input id="task_label" type="hidden" value="Events"/>
        <table width="100%" cellpadding="0" cellspacing="0">
          <col width="*" />
          <col width="250"/>
          <tbody>
<%-- date selection area --%>
            <tr>
              <td style="border-top: solid 1px gray;"><b>Date Selection</b><br/>
          <g:render template="templates/dates" />
          </td>
          <td style="border-top: solid 1px gray; vertical-align: top;">
            <div class="message"><b>Step 1</b><br/>Click on the 'Select' button to define the time range/s of interest.</div>
          </td>
          <tr>
<%-- Block Construction Area --%>
          <tr>
            <td colspan="2" style="border-top: solid 1px gray;">
              <b>Argument Selection</b>

            </td>
          </tr>
          <tr>
            <td>
              <table>
                <tr>
                  <td style="vertical-align:middle;" >
                    <div   class="resultDroppableInst" style="width: 70px; height: 70px; padding: 0; float: left; margin: 10px;">
                      <img id="block_drop" style="margin:11px" src="${resource(dir:'images/helio',file:'circle_block_grey.png')}" />
                    </div>
                  </td>
                  <td id="block_area" class="candybox"></td>


                </tr>
                <tr align="center"><td><div class="custom_button" id="block_button">Select</div></td></tr>
              </table>
            </td>
            <td valign="top">
              <div class="message"><b>Step 2</b><br/>To be Defined</div>
            </td>
          </tr>
<%-- result overview --%>
          <tr >
          <table id="result_overview" style="display:none" width="100%" cellpadding="0" cellspacing="0">
            <col width="*" />
            <col width="250"/>
            <tbody>
              <tr >
                <td colspan="2" style="border-top: solid 1px gray;">
                  <b>Result Overview</b>

                </td>
              </tr>
              <tr>
                <td>
                  <table style="margin-bottom: 10px;">
                    <tr>
                      <td style="vertical-align:middle;" >
                        <div   class="resultDroppable4" style="width: 70px; height: 70px; padding: 0; float: left; margin: 10px;">
                          <img id="result_drop" class ="drop_able" style="margin:0px" src="${resource(dir:'images/helio',file:'result.png')}" />
                        </div>
                      </td>

                      <td id="result_area">



                    </tr>

                  </table>
                </td>
                <td valign="top">
                  <!--div class="message"><b>Step 3</b><br/>Click on the 'Display' button when your result finish loading</div-->
                </td>
              </tr>
            </tbody>
          </table>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
  <div id="displayableResult" class="displayable" style="display:block"></div>
</div>
package eu.heliovo.hfe.utils

import eu.heliovo.clientapi.processing.context.SimpleParkerModelService.PlotType
import eu.heliovo.clientapi.processing.context.impl.FlarePlotterServiceImpl
import eu.heliovo.clientapi.processing.context.impl.GoesPlotterServiceImpl
import eu.heliovo.clientapi.processing.context.impl.SimpleParkerModelServiceImpl
import eu.heliovo.clientapi.processing.context.impl.des.AcePlotterServiceImpl
import eu.heliovo.clientapi.processing.context.impl.des.StaPlotterServiceImpl
import eu.heliovo.clientapi.processing.context.impl.des.StbPlotterServiceImpl
import eu.heliovo.clientapi.processing.hps.impl.CmeBackwardPropagationModelImpl
import eu.heliovo.clientapi.processing.hps.impl.CmePropagationModelImpl
import eu.heliovo.clientapi.processing.hps.impl.SepPropagationModelImpl
import eu.heliovo.clientapi.processing.hps.impl.SolarWindPropagationModelImpl
import eu.heliovo.clientapi.processing.taverna.impl.TavernaWorkflow2283
import eu.heliovo.hfe.model.param.TimeRange
import eu.heliovo.registryclient.HelioServiceName
import eu.heliovo.registryclient.ServiceCapability

/**
 * Utility to get the task descriptor map. 
 * TODO: needs some refactoring (i.e. move to clientapi).
 * 
 * @author MarcoSoldati
 *
 */
class TaskDescriptor {
    
    def static Map<String, Map<String, Object>> taskDescriptor = initTaskDescriptors();
    
    /**
    * Create task descriptors, if required.
    * @return
    */
    private static def initTaskDescriptors() {
/************* PROPAGATION MODEL *****************/
      ["pm_cme" : [
        "label" : "CME Forward Propagation Model (from Sun to objects)",
        "description" : "CME Propagation Model from the Sun to a collection of predefined objects.",
        "serviceName" : HelioServiceName.HPS,
        "serviceCapability" : ServiceCapability.HELIO_PROCESSING_SERVICE,
        "serviceVariant" : CmePropagationModelImpl.SERVICE_VARIANT,
        "inputParams" : [
            "timeRanges" : ["timeRanges" : [type : TimeRange.class]],
            "paramSet" : [
                "longitude" : [label : "Longitude", description : "Longitude in Degrees", type : Float, defaultValue : 0],
                "width" : [label : "Width", description : "Width in Degrees", type : Float, defaultValue : 45.0],
                "speed" : [label : "Speed", description : "Speed in km/s", type : Float, defaultValue : 800],
                "speedError" : [label : "SpeedError &plusmn;", description : "Speed Error in km/s", type : Float, defaultValue : 0]
            ]
        ],
        "outputParams" : [
            "voTableUrl" : [id : "voTableUrl", label: "VOTable", type : "votable" ],
            "innerPlotUrl" : [id : "innerPlotUrl", label: "Plot of inner planets", type : "url" ],
            "outerPlotUrl" : [id : "outerPlotUrl", label: "Plot of outer planets", type : "url" ],
            "voyagerPlotUrl" : [id : "voyagerPlotUrl", label: "Plot containing voyager", type : "url" ]
        ]
      ],
      "pm_cme_back" : [
          "label" : "CME Backward Propagation Model (from planet or satellite to Sun)",
          "description" : "CME Propagation Model from a collection of predefined objects back to the Sun.",
          "serviceName" : HelioServiceName.HPS,
          "serviceCapability" : ServiceCapability.HELIO_PROCESSING_SERVICE,
          "serviceVariant" : CmeBackwardPropagationModelImpl.SERVICE_VARIANT,
          "inputParams" : [
              "timeRanges" : ["timeRanges" : [type : TimeRange.class]],
              "paramSet" : [
                  "width" : [label : "Width", description : "Width in Degrees", type : Float, defaultValue : 45.0],
                  "hitObject" : [label : "Object", description : "Planet or Satellite hit by the CME", type : String, defaultValue : "Earth", 
                      valueDomain : ["Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto", 
                          "Cassini", "Dawn", "Galileo", "Messenger", "NewHorizons", "Rosetta", "StereoA", "StereoB", 
                          "Ulysses", "Voyager1", "Voyager2"]
                  ],
                  "speed" : [label : "Speed", description : "Speed in km/s", type : Float, defaultValue : 800],
                  "speedError" : [label : "SpeedError &plusmn;", description : "Speed Error in km/s", type : Float, defaultValue : 0]
              ]
          ],
          "outputParams" : [
              "voTableUrl" : [id : "voTableUrl", label: "VOTable", type : "votable" ],
              "innerPlotUrl" : [id : "innerPlotUrl", label: "Plot of inner planets", type : "url" ],
              "outerPlotUrl" : [id : "outerPlotUrl", label: "Plot of outer planets", type : "url" ],
              "voyagerPlotUrl" : [id : "voyagerPlotUrl", label: "Plot containing voyager", type : "url" ]
          ]
      ],
      "pm_sw" : [
          "label" : "Solar Wind Propagation Model (from Sun to object)",
          "description" : "Solar Wind Propagation Model from the Sun to a collection of predefined objects.",
          "serviceName" : HelioServiceName.HPS,
          "serviceCapability" : ServiceCapability.HELIO_PROCESSING_SERVICE,
          "serviceVariant" : SolarWindPropagationModelImpl.SERVICE_VARIANT,
          "inputParams" : [
              "timeRanges" : ["timeRanges" : [type : TimeRange.class]],
              "paramSet" : [
                  "longitude" : [label : "Longitude", description : "Longitude in Degrees", type : Float, defaultValue : 0],
                  "speed" : [label : "Speed", description : "Speed in km/s", type : Float, defaultValue : 600]
              ]
          ],
          "outputParams" : [
              "voTableUrl" : [id : "voTableUrl", label: "VOTable", type : "votable" ],
              "innerPlotUrl" : [id : "innerPlotUrl", label: "Plot of inner planets", type : "url" ],
              "outerPlotUrl" : [id : "outerPlotUrl", label: "Plot of outer planets", type : "url" ],
          ]
      ],
      "pm_sep" : [
          "label" : "Solar Energetic Particles Propagation Model (from Sun to objects)",
          "description" : "Solar Energetic Particles from the Sun to a collection of predefined objects.",
          "serviceName" : HelioServiceName.HPS,
          "serviceCapability" : ServiceCapability.HELIO_PROCESSING_SERVICE,
          "serviceVariant" : SepPropagationModelImpl.SERVICE_VARIANT,
          "inputParams" : [
              "timeRanges" : ["timeRanges" : [type : TimeRange.class]],
              "paramSet" : [
                  "longitude" : [label : "Longitude", description : "Longitude in Degrees", type : Float, defaultValue : 0],
                  "speed" : [label : "Speed", description : "Speed in km/s", type : Float, defaultValue : 600],
                  "speedError" : [label : "SpeedError &plusmn;", description : "Speed Error in km/s", type : Float, defaultValue : 0],
                  "beta" : [label : "Beta", description : "Beta as percentage of lightspeed", type : Float, defaultValue : 0.9],
              ]
          ],
          "outputParams" : [
              "voTableUrl" : [id : "voTableUrl", label: "VOTable", type : "votable" ],
              "innerPlotUrl" : [id : "innerPlotUrl", label: "Plot of inner planets", type : "url" ],
              "outerPlotUrl" : [id : "outerPlotUrl", label: "Plot of outer planets", type : "url" ],
          ]
      ],
/************* TAVERNA *****************/
      "tav_2283" : [
        "label" : "Crossmatching of two HELIO event lists",
        "description" : "Merge two HELIO event lists",
        "serviceName" : HelioServiceName.TAVERNA_SERVER,
        "serviceCapability" : ServiceCapability.TAVERNA_SERVER,
        "serviceVariant" : TavernaWorkflow2283.SERVICE_VARIANT,
        "inputParams" : [
            "timeRanges" : ["timeRanges" : [type : TimeRange.class]],
            "paramSet" : [
                "catalogue1" : [label : "Catalogue 1", description : "1st Event list (not all HEC lists are supported)", type : String, defaultValue : "goes_sxr_flare", 
                    valueDomain: ["goes_sxr_flare", "ngdc_halpha_flare", "noaa_energetic_event", "yohkoh_hxr_flare", 
                        "kso_halpha_flare", "stereo_euvi_event", "wind_typeii_soho_cme", "ulysses_hxr_flare", 
                        "timed_see_flare", "goes_flare_sep_event"]],
                "catalogue2" : [label : "Catalogue 2", description : "2nd Event list", type : String, defaultValue : "ngdc_halpha_flare",
                    valueDomain: ["goes_sxr_flare", "ngdc_halpha_flare", "noaa_energetic_event", "yohkoh_hxr_flare", 
                        "kso_halpha_flare", "stereo_euvi_event", "wind_typeii_soho_cme", "ulysses_hxr_flare", 
                        "timed_see_flare", "goes_flare_sep_event"]],
                "timeDelta" : [label : "Time delta", description : "Max time delta between the two lists in seconds", type : Integer, defaultValue : 0],
                "locationDelta" : [label : "Location delta", description : "Max delta in degrees", type : Double, defaultValue : 1.5d],
            ]
        ],
        "outputParams" : [
            "voTableUrl" : [id : "voTableUrl", label: "VOTable", type : "votable" ],
        ]
      ],
/************* PLOTS *****************/
      "goesplot" : [
        "label" : "GOES XRay timeline plot",
        "description" : "GOES XRay timeline plot",
        "serviceName" : HelioServiceName.CXS,
        "serviceCapability" : ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE,
        "serviceVariant" : GoesPlotterServiceImpl.SERVICE_VARIANT,
        "timeout" : 60,  // timeout in seconds.
        "inputParams" : [
         "timeRanges" : ["timeRanges" : [type : TimeRange.class]]
        ],
        "outputParams" : [
          "url" : [id : "url", label: "GOES Plot", type : "url" ],
        ]
      ],
      "flareplot" : [
        "label" : "SOHO EIT Flare Plot",
        "description" : "SOHO EIT flare plot",
        "serviceName" : HelioServiceName.CXS,
        "serviceCapability" : ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE,
        "serviceVariant" : FlarePlotterServiceImpl.SERVICE_VARIANT,
        "timeout" : 60,
        "inputParams" : [
          "timeRanges" : ["timeRanges" : [type : TimeRange.class]]
        ],
        "outputParams" : [
          "url" : [id : "url", label: "Flare Plot", type : "url" ],
        ]
      ],
      "parkerplot" : [
        "label" : "Simple parker spiral",
        "description" : "Simple parker spiral",
        "serviceName" : HelioServiceName.CXS,
        "serviceCapability" : ServiceCapability.COMMON_EXECUTION_ARCHITECTURE_SERVICE,
        "serviceVariant" : SimpleParkerModelServiceImpl.SERVICE_VARIANT,
        "timeout" : 60,
        "inputParams" : [
          "timeRanges" : ["timeRanges" : [type : TimeRange.class]],
          "paramSet" : [
            "velocity" : [label : "Velocity", description : "Velocity in km/s (would speed be the better term?)", type : int, defaultValue : 400],
            //"plotType" : [label : "Area to plot", description : "Plot inner or outer planets", type : PlotType, defaultValue : PlotType.INNER]
          ]
        ],
        "outputParams" : [
            "url" : [id : "url", label: "Parker Spiral Plot", type : "url" ],
        ]
      ],
      "aceplot" :  [
        "label" : "ACE timeline plot",
        "description" : "ACE timeline plot",
        "serviceName" : HelioServiceName.DES,
        "serviceCapability" : ServiceCapability.ASYNC_QUERY_SERVICE,
        "serviceVariant" : AcePlotterServiceImpl.SERVICE_VARIANT,
        "timeout" : 60,
        "inputParams" : [
          "timeRanges" : ["timeRanges" : [type : TimeRange.class]]
        ],
        "outputParams" : [
            "url" : [id : "url", label: "ACE Plot", type : "url" ],
        ]
      ],
      "staplot" :  [
        "label" : "STEREO-A timeline plot",
        "description" : "STEREO-A timeline plot",
        "serviceName" : HelioServiceName.DES,
        "serviceCapability" : ServiceCapability.ASYNC_QUERY_SERVICE,
        "serviceVariant" : StaPlotterServiceImpl.SERVICE_VARIANT,
        "timeout" : 60,
        "inputParams" : [
          "timeRanges" : ["timeRanges" : [type : TimeRange.class]]
        ],
        "outputParams" : [
          "url" : [id : "url", label: "STEREO-A Plot", type : "url" ],
        ]
      ],
      "stbplot" :  [
          "label" : "STEREO-B timeline plot",
          "description" : "STEREO-B timeline plot",
          "serviceName" : HelioServiceName.DES,
          "serviceCapability" : ServiceCapability.ASYNC_QUERY_SERVICE,
          "serviceVariant" : StbPlotterServiceImpl.SERVICE_VARIANT,
          "timeout" : 60,
          "inputParams" : [
            "timeRanges" : ["timeRanges" : [type : TimeRange.class]]
          ],
          "outputParams" : [
            "url" : [id : "url", label: "STEREO-B Plot", type : "url" ],
          ]
      ],
      "ulyssesplot" :  [
        "label" : "Ulysses timeline plot",
        "description" : "Ulysses timeline plot",
        "serviceName" : HelioServiceName.DES,
        "serviceCapability" : ServiceCapability.ASYNC_QUERY_SERVICE,
        "serviceVariant" : StaPlotterServiceImpl.SERVICE_VARIANT,
        "timeout" : 60,
        "inputParams" : [
          "timeRanges" : ["timeRanges" : [type : TimeRange.class]]
        ],
        "outputParams" : [
          "url" : [id : "url", label: "Ulysses Plot", type : "url" ],
        ]
      ],
      "windplot" :  [
          "label" : "WIND timeline plot",
          "description" : "WIND timeline plot",
          "serviceName" : HelioServiceName.DES,
          "serviceCapability" : ServiceCapability.ASYNC_QUERY_SERVICE,
          "serviceVariant" : StaPlotterServiceImpl.SERVICE_VARIANT,
          "timeout" : 60,
          "inputParams" : [
            "timeRanges" : ["timeRanges" : [type : TimeRange.class]]
          ],
          "outputParams" : [
            "url" : [id : "url", label: "WIND Plot", type : "url" ],
          ]
      ],  
    ]}
    
    /**
     * Find a task descriptor by name
     * @param taskName
     * @return
     */
    static def findTaskDescriptor(taskName) {
        taskDescriptor[taskName]    
    }
}
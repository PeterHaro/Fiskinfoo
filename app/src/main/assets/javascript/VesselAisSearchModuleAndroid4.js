 "use strict";

function VesselAisSearchModule() {
    var vesselMap = {};

    this.setVesselData = function(vessels) {
        vesselMap = {};
        vessels.forEach(function (item, index) { vesselMap[item.N.Callsign] = item });
    };

    this.attachTools = function(tools) {
        tools.forEach(function (tool) {
            if (vesselMap[tool.get("ircs")] !== undefined) {
                if (vesselMap[tool.get("ircs")].tools === undefined) {
                    vesselMap[tool.get("ircs")].tools = [tool];
                } else {
                    vesselMap[tool.get("ircs")].tools.push(tool);
                }
            }
        });
    };

    this.getVessel = function(call_signal) {
        return vesselMap[call_signal];
    };

    this.getVesselObject = function() {

    }
}

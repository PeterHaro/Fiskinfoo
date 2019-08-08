geoJSON = new ol.format.GeoJSON();

vesselSearchData = [];

vesselsSource
    .onDataAdded(function(dataContainer) {
        dataContainer.forEachRecord(function(vessel) {
            var callSign = vessel.get("Callsign");
            var name = vessel.get("Name");
            var key = vessel.key();

            if (vesselMap[callSign] === undefined)
                vesselMap[callSign] = { 
                    key: key
                };
            else
                vesselMap[callSign].key = key;

            vesselSearchData.push({
                "Name": name,
                "Callsign": callSign
            });
        });

        var autoCompleteData = JSON.stringify(vesselSearchData);
        Android.setAutoCompleteData(autoCompleteData);
        Android.aisFinishedLoading();
    }, true);

toolsSource
    .onDataAdded(function(dataContainer) {
        dataContainer.forEachRecord(function(tool) {
            var callSign = tool.get("ircs");
            var toolTypeCode = tool.get("tooltypecode");
            var setupTime = tool.get("setupdatetime");
            var key = tool.key();

            var coordinate = getPositionFromGeometry(tool.getGeometry());

            var toolData = {
                type: formatToolType(toolTypeCode),
                key: key,
                info: {
                    "Tid i havet": {
                        icon: "date_range",
                        value: formatDateDifference(setupTime)
                    },
                    "Satt": {
                        icon: "date_range",
                        value: formattedDate(setupTime)
                    },
                    "Posisjon": {
                        icon: "place",
                        value: formatLocation(coordinate)
                    }
                }
            };

            if (callSign === null) return;

            if (vesselMap[callSign] === undefined)
                vesselMap[callSign] = {};
            
            if (vesselMap[callSign].tools === undefined)
                vesselMap[callSign].tools = [toolData];
            else
                vesselMap[callSign].tools.push(toolData);
        });

        Android.toolsFinishedLoading();
    }, true);

geoJSON = new ol.format.GeoJSON();

function getPositionFromGeometry(geometry) {
    var toolFeature = new ol.Feature({
        geometry: geoJSON.readGeometry(geometry, {
            featureProjection: 'EPSG:3857'
        })
    });
    return ol.proj.transform(ol.extent.getCenter(toolFeature.getGeometry().getExtent()), 'EPSG:3857', 'EPSG:4326');
}

vesselsSource
    .getDataContainerAsync()
    .then(function(dataContainer) {
        var vessels = dataContainer.getData().all();
        var labelLookup = dataContainer.getLabelLookup();
        vessels.forEach(function(vessel) {
            var callsign = vessel[labelLookup["Callsign"]];
            var key = vessel[labelLookup["__key"]];

            if (vesselMap[callsign] === undefined)
                vesselMap[callsign] = { 
                    key: key
                };
            else
                vesselMap[callsign].key = key;

        });

        Android.aisFinishedLoading();
    });

toolsSource
    .getDataContainerAsync()
    .then(function(dataContainer) {
        var tools = dataContainer.getData().all();
        var labelLookup = dataContainer.getLabelLookup();
        tools.forEach(function(tool) {
            var callsign = tool[labelLookup["ircs"]];
            var toolTypeCode = tool[labelLookup["tooltypecode"]];
            var setupTime = tool[labelLookup["setupdatetime"]];
            var key = tool[labelLookup["__key"]];
            var geometry = tool[labelLookup["geometry"]];

            var coordinate = getPositionFromGeometry(geometry);
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

            if (callsign === null) return;

            if (vesselMap[callsign] === undefined)
                vesselMap[callsign] = {};
            
            if (vesselMap[callsign].tools === undefined)
                vesselMap[callsign].tools = [toolData];
            else
                vesselMap[callsign].tools.push(toolData);
        });

        Android.toolsFinishedLoading();
    });

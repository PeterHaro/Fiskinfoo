
function toolsSelectionFunction(e) {
    if (selectedFeature) unsetSelectedFeature();

    selectedFeature = e.getFirstSelectedFeature();
    var coordinate = selectedFeature.getCenterCoordinate();
    var record = selectedFeature.getRecord();
    var toolTypeCode = record("tooltypecode");
    var toolName = formatToolType(toolTypeCode);
    
    var vesselName = record("vesselname");
    var callsignal = record("ircs");
    
    infoTemplate.setData({
        title: toolName,
        subTitle: "Redskap",
        info: {
            "Tid i havet": formatDateDifference(record("setupdatetime")),
            "Satt": formattedDate(record("setupdatetime")),
            "Posisjon": formatLocation(coordinate),
            "Se Marinogram": marinogramLink(coordinate)
        },
        infoWithHeader: {
            "Om Eier": {
                "Fartøy": showVesselLink(callsignal, vesselName),
                "Telefon": record("vesselphone"),
                "Kallesignal(IRCS)": callsignal,
                "MMSI": record("mmsi"),
                "IMO": record("imo"),
                "E-post": record("vesselemail")
            }
        },
        moreInfoFish: true
    });

    e.getMap().clearMarkers();
    infoDrawer.open();
    selectedFeature.setText(toolName, 25);
}

var toolsSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/fishingfacility/?format=JSON",
    authenticator: authenticator
});

var toolsLayerColors = [ "#2b83ba", "#d4c683", "#abdda4", "#fdae61", "#6bb0af", "#d7191c", "#ea643f"];

var toolsLayer = Sintium.vectorLayer({
    layerId: 'Redskap',
    dataSource: toolsSource,
    clusteredByProperty: "tooltypecode",
    geometryProperty: "geometry",
    addPointToGeometries: true,
    visible: false,
    lazyLoading: false,
    style: Sintium.style({
        colors: toolsLayerColors,
        clusterSize: 13,
        shape: "triangle"
    }),
    selectedStyle: Sintium.style({
        size: 18,
        clusterSize: 13
    }),
    selections: [
        Sintium.selection(['single click'], toolsSelectionFunction)
    ]
});


function locateTool(key) {
    var feature = toolsLayer.getFeatureByKey(key);
    if (!feature) return;
    toolsLayer.setVisible(true);
    Android.addActiveLayer("Redskap");
    map.SelectionManager.triggerSingleSelection(feature);
    map.zoomToFeature(feature, 10);
}
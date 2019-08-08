function toolsSelectionFunction(e) {
    if (selectedFeature) unsetSelectedFeature();

    selectedFeature = e.popFeature();
    var coordinate = selectedFeature.getCenterCoordinate();
    var record = e.popRecord();
    var toolTypeCode = record.get("tooltypecode");
    var toolName = formatToolType(toolTypeCode);

    var vesselName = record.get("vesselname");
    var callSignal = record.get("ircs");

    infoTemplate.setData({
        title: toolName,
        subTitle: "Redskap",
        info: {
            "Tid i havet": formatDateDifference(record.get("setupdatetime")),
            "Satt": formattedDate(record.get("setupdatetime")),
            "Posisjon": formatLocation(coordinate),
            "Se Marinogram": marinogramLink(coordinate)
        },
        infoWithHeader: {
            "Om Eier": {
                "Fartøy": showVesselLink(callSignal, vesselName),
                "Telefon": record.get("vesselphone"),
                "Kallesignal(IRCS)": callSignal,
                "MMSI": record.get("mmsi"),
                "IMO": record.get("imo"),
                "E-post": record.get("vesselemail")
            }
        },
        moreInfoFish: true
    });

    infoDrawer.open(null, closeSheetCallBack);
}

var toolsSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/fishingfacility/?format=JSON",
    authenticator: authenticator
});

var toolsLayerColors = [ "#2b83ba", "#d4c683", "#abdda4", "#fdae61", "#6bb0af", "#d7191c", "#ea643f"];

var toolsLayer = Sintium.vectorLayer2({
    layerId: 'Redskap',
    dataSource: toolsSource,
    clusteredByProperty: "tooltypecode",
    geometryProperty: "geometry",
    addPointToGeometries: true,
    visible: false,
    lazyLoad: false,
    useThread: true,
    unrollClustersAtZoom: unrollAtZoom,
    clusterRadius: 150,
    style: {
        colors: toolsLayerColors
    },
    selectedStyle: {
        single: {
            size: 18,
            shape: "triangle",
            textFromProperty: "tooltypecode",
            textOffset: 28
        }
    }
});

toolsLayer.addSelection({
    selector: "single",
    condition: "click",
    callback: toolsSelectionFunction
});

function selectFeature(feature) {
    if (feature.getFeatureKey() !== selectedKey) return;
    map.getSelectionHandler().clearSelection();
    map.getSelectionHandler().triggerSingleSelection(feature);
    selectedKey = null;
}

toolsLayer.getFeatureSource().onFeatureChange(function(features) {
    features.forEach(selectFeature);
});

function locateTool(key) {
    selectedKey = key;
    var record = toolsSource.getDataContainer().getRecord(selectedKey);
    if (!record) return;
    var geometry = record.getGeometry();
    var coordinates = ol.extent.getCenter(geometry.getExtent());
    map.zoomToCoordinates(coordinates, unrollAtZoom);
    toolsLayer.getFeatureSource().getFeatures().forEach(selectFeature);
}
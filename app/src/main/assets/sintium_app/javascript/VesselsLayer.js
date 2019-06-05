
function vesselSelectionFunction(e) {
    if (selectedFeature) unsetSelectedFeature();
    selectedFeature = e.getFirstSelectedFeature();
    var coordinate = selectedFeature.getCenterCoordinate();
    var record = selectedFeature.getRecord();
    var tools = getMaxThreeToolsFromCallSign(record("Callsign"));
    var destination = record("Destination");
    var name = record("Name");
    
    vesselInfoTemplate.setData({
        title: !name ? "Mangler navn" : name,
        subtitle: vesselCodeToShipTypeName(record),
        shipType: record("ShipType"),
        sog: record("Sog"),
        cog: record("Cog"),
        info: {
            "Signal mottatt": formattedDate(record("TimeStamp")),
            "Posisjon": formatLocation(coordinate),
            "Destinasjon": !destination ? "Mangler destinasjon" : destination,
            "Marinogram": marinogramLink(coordinate)
        },
        tools: tools,
        hasTools: tools.length > 0
    });

    var elems = document.querySelectorAll('.collapsible');
    M.Collapsible.init(elems, {});
    
    vesselInfoDrawer.open();

    e.getMap().clearMarkers();
    selectedFeature.setText(record("Name"), 28);
}

function iconFunction(record, zoom) {
    return zoom >= 10
        ? "file:///android_asset/sintium_app/images//boat.svg" 
        : null;
}

function iconColorFunction(record) {
    return record("ShipType") === 30
        ? "#4b0000" 
        : "#7c7c80";
}

function iconRotationFunction(record) {
    return record("Cog");
}

function fillColorFunction(record) {
    return record("ShipType") === 30 
        ? "#4b0000"
        : "#7c7c80";
}

// Instantiating vessel layer
var vesselsSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/ais/positions?xmin=0&ymin=25&xmax=60&ymax=95",
    authenticator: authenticator
});

var vesselsLayer = Sintium.vectorLayer({
    layerId: 'AIS',
    dataSource: vesselsSource,
    clustered: true,
    createFeatureFunction: Sintium.generatePointFeatureFunction("Lon", "Lat"),
    visible: false,
    lazyLoad: false,
    style: Sintium.style({
        icon: iconFunction,
        iconColor: iconColorFunction,
        iconRotation: iconRotationFunction,
        iconScale: 0.5,
        shape: "circle",
        size: 4,
        fillColor: fillColorFunction,
        clusterFillColor: "#7c7c80",
        clusterStrokeColor: "#aeaeb3",
        clusterShape: "circle",
        clusterSize: 11
    }),
    selectedStyle: Sintium.style({
        icon: "file:///android_asset/sintium_app/images/boat.svg",
        iconScale: 1.2,
        iconColor: iconColorFunction,
        iconRotation: iconRotationFunction,
        fillColor: fillColorFunction
    }),
    selections: [
        Sintium.selection(['single click'], vesselSelectionFunction)
    ]
});

function showVesselAndBottomsheet(callsignal) {
    var key = vesselMap[callsignal].key;
    var feature = vesselsLayer.getFeatureByKey(key);
    if (!feature) return;
    vesselsLayer.setVisible(true);
    map.SelectionManager.triggerSingleSelection(feature);
    map.zoomToFeature(feature, 10);
}
zIndex = -1;

function vesselSelectionFunction(e) {
    if (selectedFeature) unsetSelectedFeature();
    selectedFeature = e.popFeature();
    var coordinate = selectedFeature.getCenterCoordinate();
    var record = e.popRecord();
    var tools = getMaxThreeToolsFromCallSign(record.get("Callsign"));
    var destination = record.get("Destination");
    var name = record.get("Name");

    vesselInfoTemplate.setData({
        title: !name ? "Mangler navn" : name,
        subtitle: vesselCodeToShipTypeName(record),
        shipType: record.get("ShipType"),
        sog: record.get("Sog"),
        cog: record.get("Cog"),
        info: {
            "Signal mottatt": formattedDate(record.get("TimeStamp")),
            "Posisjon": formatLocation(coordinate),
            "Destinasjon": !destination ? "Mangler destinasjon" : destination,
            "Marinogram": marinogramLink(coordinate)
        },
        tools: tools,
        hasTools: tools.length > 0
    });

    var elems = document.querySelectorAll('.collapsible');
    M.Collapsible.init(elems, {});
    vesselInfoDrawer.open(null, closeSheetCallBack);
}

var selectedText = new ol.style.Text({
   text: "",
   font: "bold 13px sans-serif",
   offsetY: 28,
   fill: new ol.style.Fill({
       color: 'black'
   })
});

var fillFishingVessel = new ol.style.Fill({ color: "#4b0000" });
var strokeFishingVessel = new ol.style.Stroke({
    color: "#7d0000",
    width: 2
});

var fillOtherVessel = new ol.style.Fill({ color: "#7c7c80" });
var strokeOtherVessel = new ol.style.Stroke({
    color: "#adadb2",
    width: 2
});

var clusterStyle = new ol.style.Style({
    image: new ol.style.Circle({
        radius: 10,
        fill: new ol.style.Fill({ color: "#7c7c80" }),
        stroke: new ol.style.Stroke({ color: "#aeaeb3" })
    }),
    text: new ol.style.Text({
        text: "",
        fill: new ol.style.Fill({
            color: "white"
        })
    })
});

var pointStyleFishingVessel = new ol.style.Style({
    image: new ol.style.Circle({
        radius: 5,
        fill: fillFishingVessel,
        stroke: strokeFishingVessel
    })
});

var pointIconStyleFishingVessel = new ol.style.Style({
    image: new ol.style.Icon(({
        anchor: [0.5, 0.5],
        anchorXUnits: 'fraction',
        anchorYUnits: 'fraction',
        src: './images/boat_brown.svg',
        rotation: 0,
        scale: 0.8
    }))
});

var pointStyleOtherVessel = new ol.style.Style({
    image: new ol.style.Circle({
        radius: 5,
        fill: fillOtherVessel,
        stroke: strokeOtherVessel
    })
});

var pointIconStyleOtherVessel = new ol.style.Style({
    image: new ol.style.Icon(({
        anchor: [0.5, 0.5],
        anchorXUnits: 'fraction',
        anchorYUnits: 'fraction',
        src: './images/boat_gray.svg',
        rotation: 0,
        scale: 0.8
    }))
});

var pointStyleFishingVesselSelected = new ol.style.Style({
    image: new ol.style.Icon(({
        anchor: [0.5, 0.5],
        anchorXUnits: 'fraction',
        anchorYUnits: 'fraction',
        src: './images/boat_brown.svg',
        rotation: 0,
        scale: 1.3
    })),
    text: selectedText,
    zIndex: Number.POSITIVE_INFINITY
});

var pointStyleOtherVesselSelected = new ol.style.Style({
    image: new ol.style.Icon(({
        anchor: [0.5, 0.5],
        anchorXUnits: 'fraction',
        anchorYUnits: 'fraction',
        src: './images/boat_gray.svg',
        rotation: 0,
        scale: 1.3
    })),
    text: selectedText,
    zIndex: Number.POSITIVE_INFINITY
});

function vesselStyleFunction(feature, resolution) {
    var key = feature.getFeatureKey();
    var selected = feature.getSelected();
    var zoom = Math.ceil( (Math.log(resolution) - Math.log(156543.03390625) ) / Math.log(0.5));

    if (feature.isCluster()) {
        var clusterCount = feature.getRecordKeys().length;
        var clusterText = nFormatter(clusterCount, 0);
        clusterStyle.setZIndex(zIndex--);
        clusterStyle.getText().setText(clusterText);
        return clusterStyle;
    } else {
        var record = vesselsSource.getDataContainer().getRecord(key);
        var isVesselShip = record.get("ShipType") === 30;
        if (selected) {
            var name = record.get("Name") || "Mangler navn";
            var selectedStyle = isVesselShip ? pointStyleFishingVesselSelected : pointStyleOtherVesselSelected;
            selectedStyle.getText().setText(name);
            var rotation = degreesToRadians(record.get("Cog"));
            selectedStyle.getImage().setRotation(rotation);
            return selectedStyle;
        } else {
            var style;

            if (zoom < unrollAtZoom)
                style = isVesselShip ? pointStyleFishingVessel : pointStyleOtherVessel;
            else {
                style = isVesselShip ? pointIconStyleFishingVessel : pointIconStyleOtherVessel;
                var rotation = degreesToRadians(record.get("Cog"));
                style.getImage().setRotation(rotation);
            }

            style.setZIndex(zIndex--);
            return style;
        }
    }
}

// Instantiating vessel layer
var vesselsSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/ais/positions?xmin=0&ymin=25&xmax=60&ymax=95",
    authenticator: authenticator
});

var vesselsLayer = Sintium.vectorLayer2({
    layerId: 'AIS',
    dataSource: vesselsSource,
    clustered: true,
    lonProperty: "Lon",
    latProperty: "Lat",
    visible: false,
    lazyLoad: false,
    useThread: true,
    unrollClustersAtZoom: unrollAtZoom,
    styleFunction: vesselStyleFunction,
    clusterRadius: 150
}); 

vesselsLayer.addSelection({
    selector: "single",
    condition: "click",
    callback: vesselSelectionFunction
});

var selectedKey = null;

function selectFeature(feature) {
    if (feature.getFeatureKey() !== selectedKey) return;
    map.getSelectionHandler().clearSelection();
    map.getSelectionHandler().triggerSingleSelection(feature);
    selectedKey = null;
}

vesselsLayer.getFeatureSource().onFeatureChange(function(features) {
    features.forEach(selectFeature);
});

function showVesselAndBottomsheet(callsignal) {
    selectedKey = vesselMap[callsignal].key;
    var record = vesselsSource.getDataContainer().getRecord(selectedKey);
    if (!record) return;
    var coordinates = [record.get("Lon"), record.get("Lat")];
    map.zoomToCoordinates(coordinates, unrollAtZoom);
    vesselsLayer.getFeatureSource().getFeatures().forEach(selectFeature);
}
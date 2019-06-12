
function iceChartStyleFunction(feature, record) {
    var color;
    switch (record("icetype")) {
        case "Close Drift Ice":
            color = "rgba(251, 156, 69, 0.5)";
            break;
        case "Very Close Drift Ice":
            color = "rgba(255, 64, 64, 0.5)";
            break;
        case "Fast Ice":
            color = "rgba(195, 197, 199, 0.5)";
            break;
        case "Open Drift Ice":
            color = "rgba(255, 255, 64, 0.5)";
            break;
        case "Very Open Drift Ice":
            color = "rgba(165, 253, 184, 0.5)";
            break;
        case "Open Water":
            color = "rgba(176, 214, 255, 0.5)";
            break;
    }
    return color ? [new ol.style.Style({
        fill: new ol.style.Fill({
            color: color
        })
    })] : null;
}

// Instantiating planned seismic activity layer
var iceConcentrationSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/icechart/?format=JSON"
});

var iceConcentrationLayer = Sintium.vectorLayer({
    layerId: "Iskonsentrasjon",
    dataSource: iceConcentrationSource,
    visible: false,
    styleFunction: iceChartStyleFunction
});

// Instantiating planned seismic activity layer
var iceEdgeSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/iceedge/?format=JSON"
});

var iceEdgeLayer = Sintium.vectorLayer({
    layerId: "Iskant",
    dataSource: iceEdgeSource,
    visible: false,
    style: Sintium.style({
        fillColor: "#7cb5ec",
        strokeSize: 4
    })
});

// Instantiating ice group
var iceGroup = Sintium.layerGroup("Is", iceConcentrationLayer, iceEdgeLayer);

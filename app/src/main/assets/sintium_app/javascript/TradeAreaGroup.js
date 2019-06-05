
// Instantiating trade area bank fishing layer
var tradeAreaBankFishingSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/tradearea-bankfiske1/?format=JSON"
});

var tradeAreaBankFishingLayer = Sintium.vectorLayer({
    layerId: "Fartsområde bankfiske 1",
    dataSource: tradeAreaBankFishingSource,
    visible: false,
    style: Sintium.style({
        fillColor: "#1c5385",
        strokeSize: 3
    })
});

// Instantiating trade area bank fishing layer
var tradeAreaSmallCostSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/tradearea-litenkystfart/?format=JSON"
});

var tradeAreaSmallCostLayer = Sintium.vectorLayer({
    layerId: "Fartsområde liten kystfart",
    dataSource: tradeAreaSmallCostSource,
    visible: false,
    style: Sintium.style({
        fillColor: "#1c5385",
        strokeSize: 3
    })
});

// Instantiating ice group
var tradeAreaGroup = Sintium.layerGroup("Fartsområder", tradeAreaBankFishingLayer, tradeAreaSmallCostLayer);

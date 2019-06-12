
// Instantiating j messages layer
var jMessagesSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/jmelding/?format=JSON"
});

var jMessagesLayer = Sintium.vectorLayer({
    layerId: "J-Meldinger - stengte omr.",
    dataSource: jMessagesSource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], function(e) {
            selectedFeature = e.getAllSelectedFeatures()[0];
            var record = selectedFeature.getRecord();
            infoTemplate.setData({
                title: record("name"),
                subTitle: "Midlertidig stengt område",
                description: record("description"),
                info: {
                    "Stengt fra dato": formattedDate(record("closed_date")),
                    "Stengt for": record("type_name"),
                    "Fiskegruppe": record("speciestype_name"),
                    "Område": record("area_name"),
                    "J-melding": record("jmelding_name")
                },
                moreInfoFish: true
            });
            e.getMap().clearMarkers();
            infoDrawer.open();
        })
    ],
    style: Sintium.style({
        fillColor: "rgba(255, 255, 255, 0.3)",
        strokeColor: "rgba(144, 237, 125, 1.0)"
    }),
    selectedStyle: Sintium.style({
        fillColor: "rgba(144, 237, 125, 0.5)",
        strokeColor: "rgba(144, 237, 125, 1)"
    })
});

// Instantiating prohibited areas layer
var prohibitedAreasSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/coralreef/?format=JSON"
});

var prohibitedAreasLayer = Sintium.vectorLayer({
    layerId: "Forbudsområde - Korallrev",
    dataSource: prohibitedAreasSource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], function(e) {
            selectedFeature = e.getAllSelectedFeatures()[0];
            var record = selectedFeature.getRecord();
            infoTemplate.setData({
                title: record("navn"),
                subTitle: "Forbudsområde - Korallrev",
                description: record("info")
            });
            e.getMap().clearMarkers();
            infoDrawer.open();
        })
    ],
    style: Sintium.style({
        fillColor: "rgba(255, 255, 255, 0.3)",
        strokeColor: "rgba(228, 211, 84, 1.0)"
    }),
    selectedStyle: Sintium.style({
        fillColor: "rgba(228, 211, 84, 0.5)",
        strokeColor: "rgba(228, 211, 84, 1)"
    })
});

// Instantiating fjord lines layer
var fjordLinesSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/coastalcodregulations/?format=JSON"
});

var fjordLinesLayer = Sintium.vectorLayer({
    layerId: "Fjordlinjer - kysttorsk",
    dataSource: fjordLinesSource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], function(e) {
            selectedFeature = e.getAllSelectedFeatures()[0];
            var record = selectedFeature.getRecord();
            infoTemplate.setData({
                title: "Kysttorsk",
                subTitle: "Fjordlinjer - Kysttorsk",
                description: "Fra " + record("start_point_description") + " til " + record("end_point_description")
            });
            e.getMap().clearMarkers();
            infoDrawer.open();
        })
    ],
    style: Sintium.style({
        fillColor: "#2b908f",
        strokeSize: 2
    }),
    selectedStyle: Sintium.style({
        fillColor: "#2b908f",
        strokeSize: 4
    })
});

// Instantiating fish regulations group
var fishRegulationsGroup = Sintium.layerGroup("Fiskerireguleringer", jMessagesLayer, prohibitedAreasLayer, fjordLinesLayer);

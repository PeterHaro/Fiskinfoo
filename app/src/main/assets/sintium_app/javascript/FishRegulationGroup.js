
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
                }
            });
            e.getMap().clearMarkers();
            infoDrawer.open();
        })
    ]

});

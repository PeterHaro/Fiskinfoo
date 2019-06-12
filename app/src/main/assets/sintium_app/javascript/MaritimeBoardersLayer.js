
function martimeBordersSelection(e) {
    selectedFeature = e.getAllSelectedFeatures()[0];
    var record = selectedFeature.getRecord();
    infoTemplate.setData({
        title: record("navn"),
        subTitle: "Martime grenser",
        description: record("informasjon"),
        infoWithIcon: {
            "_Land": {
                icon: "far fa-flag",
                value: landCodeToCountryName(record, "landkode")
            },
            "Gyldig fra:": {
                icon: "fas fa-calendar-alt",
                value: formattedDate(record("gyldig_fra"))
            },
            "Siste oppdatering": {
                icon: "fas fa-calendar-alt",
                value: formattedDate(record("oppdateringsdato"))
            }
        }
    });
    e.getMap().clearMarkers();
    infoDrawer.open();
}

// Instantiating maritime borders layer
var maritimeBordersSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/maritimeboundary/?format=JSON"
});

var maritimeBordersLayer = Sintium.vectorLayer({
    layerId: "Maritime grenser",
    dataSource: maritimeBordersSource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], martimeBordersSelection)
    ],
    style: Sintium.style({
        fillColor: "#1c5385",
        strokeSize: 2
    }),
    selectedStyle: Sintium.style({
        fillColor: "#1c5385",
        strokeSize: 4
    })

});

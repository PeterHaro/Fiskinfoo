function maritimeBordersSelection(e) {
    selectedFeature = e.popFeature();
    var record = e.popRecord();
    infoTemplate.setData({
        title: record.get("navn"),
        subTitle: "Martime grenser",
        description: record.get("informasjon"),
        infoWithIcon: {
            "_Land": {
                icon: "far fa-flag",
                value: landCodeToCountryName(record.get("landkode"))
            },
            "Gyldig fra:": {
                icon: "fas fa-calendar-alt",
                value: formattedDate(record.get("gyldig_fra"))
            },
            "Siste oppdatering": {
                icon: "fas fa-calendar-alt",
                value: formattedDate(record.get("oppdateringsdato"))
            }
        }
    });
    infoDrawer.open();
}

// Instantiating maritime borders layer
var maritimeBordersSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/maritimeboundary/?format=JSON",
});

var maritimeBordersLayer = Sintium.vectorLayer2({
    layerId: "Maritime grenser",
    dataSource: maritimeBordersSource,
    visible: false,
    style: {
        single: {
            fillColor: "#1c5385",
            strokeSize: 2
        }
    },
    selectedStyle: {
        single: {
            fillColor: "#1c5385",
            strokeSize: 4
        }
    }
});

maritimeBordersLayer.addSelection({
    selector: "single",
    condition: "click",
    callback: maritimeBordersSelection
});
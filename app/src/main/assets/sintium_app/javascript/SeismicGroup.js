
function ongoingSeismicActivitySelection(e) {
    selectedFeature = e.getAllSelectedFeatures()[0];
    var record = selectedFeature.getRecord();
    infoTemplate.setData({
        title: record("surveyname"),
        subTitle: "Pågående seismikk",
        info: {
            "Område": record("geoarea"),
            "Seismikkfartøy": record("vesselall"),
            "Type": record("surmaintyp"),
            "Undertype": record("surparttyp"),
            "Periode": formattedTimePeriod(record, "plnfrmdate", "plntodate"),
            "Ansvarlig selskap": record("compreport"),
            "Kilde": record("sourcetype"),
            "Sensortype": record("sensortype"),
            "Sensorantall": record("senslength"),
            "Sensorlengde": record("sensnote")
        }
    });
    e.getMap().clearMarkers();
    infoDrawer.open();
} 

function plannedSeismicActivitySelection(e) {
    selectedFeature = e.getAllSelectedFeatures()[0];
    var record = selectedFeature.getRecord();
    infoTemplate.setData({
        title: record("surveyname"),
        subTitle: "Planlagt seismikk",
        info: {
            "Område": record("geoarea"),
            "Seismikkfartøy": record("vesselall"),
            "Type": record("surmaintyp"),
            "Undertype": record("surparttyp"),
            "Periode": formattedTimePeriod(record("plnfrmdate"), record("plntodate")),
            "Ansvarlig selskap": record("compreport"),
            "Kilde": record("sourcetype"),
            "Sensortype": record("sensortype"),
            "Sensorantall": record("senslength"),
            "Sensorlengde": record("sensnote")
        }
    });
    e.getMap().clearMarkers();
    infoDrawer.open();
}

function ongoingElectromagneticSurveysSelection(e) {
    selectedFeature = e.getAllSelectedFeatures()[0];
    var record = selectedFeature.getRecord();
    infoTemplate.setData({
        title: record("surveyname"),
        subTitle: "Pågående elektromagnetiske undersøkelser",
        info: {
            "Område": record("geoarea"),
            "Seismikkfartøy": record("vesselall"),
            "Type": record("surmaintyp"),
            "Undertype": record("surparttyp"),
            "Periode": formattedTimePeriod(record, "plnfrmdate", "plntodate"),
            "Ansvarlig selskap": record("compreport"),
            "Kilde": record("sourcetype"),
            "Sensortype": record("sensortype"),
            "Sensorantall": record("senslength"),
            "Sensorlengde": record("sensnote")
        }
    });
    e.getMap().clearMarkers();
    infoDrawer.open();
}

function plannedElectromagneticSurveys(e) {
    selectedFeature = e.getAllSelectedFeatures()[0];
    var record = selectedFeature.getRecord();
    infoTemplate.setData({
        title: record("surveyname"),
        subTitle: "Planlagte elektromagnetiske undersøkelser",
        info: {
            "Område": record("geoarea"),
            "Seismikkfartøy": record("vesselall"),
            "Type": record("surmaintyp"),
            "Undertype": record("surparttyp"),
            "Periode": formattedTimePeriod(record, "plnfrmdate", "plntodate"),
            "Ansvarlig selskap": record("compreport"),
            "Kilde": record("sourcetype"),
            "Sensortype": record("sensortype"),
            "Sensorantall": record("senslength"),
            "Sensorlengde": record("sensnote")
        }
    });
    e.getMap().clearMarkers();
    infoDrawer.open();
}

// Instantiating ongoing seismic activity layer
var ongoingSeismicActivitySource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/npdsurveyongoing/?format=JSON"
});

var ongoingSeismicActivityLayer = Sintium.vectorLayer({
    layerId: "Pågående seismikk",
    dataSource: ongoingSeismicActivitySource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], ongoingSeismicActivitySelection)
    ],
    style: Sintium.style({
        fillColor: "rgba(255, 255, 255, 0.3)",
        strokeColor: "rgba(119, 190, 149, 1.0)"
    }),
    selectedStyle: Sintium.style({
        fillColor: "rgba(119, 190, 149, 0.5)",
        strokeColor: "rgba(119, 190, 149, 1.0)"
    })
});

// Instantiating planned seismic activity layer
var plannedSeismicActivitySource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/npdsurveyplanned/?format=JSON"
});

var plannedSeismicActivityLayer = Sintium.vectorLayer({
    layerId: "Planlagt seismikk",
    dataSource: plannedSeismicActivitySource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], plannedSeismicActivitySelection)
    ],
    style: Sintium.style({
        fillColor: "rgba(255, 255, 255, 0.3)",
        strokeColor: "rgba(237, 128, 128, 1.0)"
    }),
    selectedStyle: Sintium.style({
        fillColor: "rgba(237, 128, 128, 0.5)",
        strokeColor: "rgba(237, 128, 128, 1)"
    })
});

// Instantiating planned seismic activity layer
var ongoingElectromagneticSurveysSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/npdemsurveyongoing/?format=JSON",
    projection: "EPSG:23032"
});

var ongoingElectromagneticSurveysLayer = Sintium.vectorLayer({
    layerId: "Pågående elektromagnetiske undersøkelser",
    dataSource: ongoingElectromagneticSurveysSource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], ongoingElectromagneticSurveysSelection)
    ],
    style: Sintium.style({
        fillColor: "rgba(255, 255, 255, 0.3)",
        strokeColor: "rgba(128, 133, 233, 1.0)"
    }),
    selectedStyle: Sintium.style({
        fillColor: "rgba(128, 133, 233, 0.5)",
        strokeColor: "rgba(128, 133, 233, 1)"
    })
});

// Instantiating planned seismic activity layer
var plannedElectromagneticSurveysSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/npdemsurveyplanned/?format=JSON",
    projection: "EPSG:23032"
});

var plannedElectromagneticSurveysLayer = Sintium.vectorLayer({
    layerId: "Planlagte elektromagnetiske undersøkelser",
    dataSource: plannedElectromagneticSurveysSource,
    visible: false,
    selections: [
        Sintium.selection(['single click'], plannedElectromagneticSurveys)
    ],
    style: Sintium.style({
        fillColor: "rgba(255, 255, 255, 0.3)",
        strokeColor: "rgba(241, 92, 128, 1.0)"
    }),
    selectedStyle: Sintium.style({
        fillColor: "rgba(241, 92, 128, 0.5)",
        strokeColor: "rgba(241, 92, 128, 1)"
    })
});

// Instantiating seismic group
var seismicGroup = Sintium.layerGroup("Seismikk", ongoingSeismicActivityLayer, plannedSeismicActivityLayer, ongoingElectromagneticSurveysLayer, plannedElectromagneticSurveysLayer);

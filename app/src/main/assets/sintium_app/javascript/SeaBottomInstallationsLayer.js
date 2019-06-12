
// Instantiating sea bottom installations layer
var seaBottomInstallationsSource = Sintium.dataSource({
    url: "https://www.barentswatch.no/api/v1/geodata/download/npdfacility/?format=JSON"
});

var seaBottomInstallationsLayer = Sintium.vectorLayer({
    layerId: "Havbunnsinstallasjoner",
    dataSource: seaBottomInstallationsSource,
    visible: false,
    clustered: true,
    selections: [
        Sintium.selection(['single click'], function(e) {
            if (selectedFeature) unsetSelectedFeature(selectedFeature);

            selectedFeature = e.getAllSelectedFeatures()[0];
            var record = selectedFeature.getRecord();
            var coordinate = selectedFeature.getCenterCoordinate();
            infoTemplate.setData({
                title: record("facname"),
                subTitle: "Havbunnsinstallasjoner",
                info: {
                    "Type": record("fackind"),
                    "Funksjon": record("facfunc"),
                    "Dybde": record("waterdepth"),
                    "Tilhørende felt": record("belong2nm"),
                    "Oppstart": formattedDate(record("dtstartup")),
                    "Operatør": record("curopernam"),
                    "Posisjon: ": formatLocation(coordinate),
                    "Marinogram": marinogramLink(coordinate)
                }
            });
            e.getMap().clearMarkers();
            infoDrawer.open();
            selectedFeature.setText(record("facname"), 28);
        })
    ],
    style: Sintium.style({
        size: 5,
        colors: toolsLayerColors,
        clusterSize: 13,
        shape: "circle",
        fillColor: "rgba(102, 204, 255, 1.0)",
        strokeColor: "rgba(8, 113, 114, 1.0)"
    }),
    selectedStyle: Sintium.style({
        shape: "circle",
        size: 18,
        clusterSize: 13,
        fillColor: "rgba(102, 204, 255, 1.0)",
        strokeColor: "rgba(8, 113, 114, 1.0)"
    })
});
function closeInfoDrawer(e) {
    e.getMap().getSelectionHandler().clearSelection();
    infoDrawer.close();
}

// Instantiating layer switcher

var zoomControl = Sintium.zoomControl();

// Instantiating map
var map = Sintium.map({
    domId: "map",
    layers: [toolsLayer, vesselsLayer, seaBottomInstallationsLayer, maritimeBordersLayer, fishRegulationsGroup, seismicGroup, iceGroup, tradeAreaGroup],
    use: [infoDrawer, vesselInfoDrawer],
    controls: [zoomControl],
    zoomOnClusterClick: true,
    onClickEmptySpace: closeInfoDrawer,
    onClickCluster: closeInfoDrawer,
    hitTolerance: 4,
    rotation: false
});
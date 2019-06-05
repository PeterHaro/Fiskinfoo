
function onClickClusterFunction(e) {
    infoDrawer.close();
}

// Instantiating layer switcher

var zoomControl = Sintium.zoomControl();

// Instantiating map
var map = Sintium.map({
    domId: "map",
    layers: [vesselsLayer, fishRegulationsGroup, seismicGroup, iceGroup, tradeAreaGroup, toolsLayer, seaBottomInstallationsLayer, maritimeBordersLayer],
    use: [infoDrawer, vesselInfoDrawer],
    controls: [zoomControl],
    zoomOnClusterClick: true,
    unrollClustersAtZoom: 10,
    onClickCluster: onClickClusterFunction
});

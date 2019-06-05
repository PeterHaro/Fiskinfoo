function getColors() {
    var stringifiedColors = JSON.stringify(toolsLayerColors);
    Android.setToolColors(stringifiedColors);
}

function getLayers() {
    var layerNames = map.getLayerManager().getLayerNames();
    var stringifiedLayerNames = JSON.stringify(layerNames);
    Android.setLayers(stringifiedLayerNames);
}

function toggleLayers(layers) {
    var layerManager = map.getLayerManager();
    var layerNames = layerManager.getLayerNames();
    for (var layerIndex in layerNames) {
        var layer = layerNames[layerIndex];
        var visibility = layers.indexOf(layer) >= 0;
        layerManager.setLayerVisibleById(layer, visibility);
    }
}

function fail() {
    alert("Noe gikk galt, venligst sjekk om du har internett- eller Ggps (gps, glonass osv) forbindelse");
}

function populateUserPosition(callback) {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(callback, fail, {timeout: 60000});
        return true;
    } else {
        return false;
    }
}

function zoomToUserPosition() {
    sensor = populateUserPosition(function (position) {
        var userPosition = ol.proj.transform([position.coords.longitude, position.coords.latitude], 'EPSG:4326', 'EPSG:3857');
        map.setView(new ol.View({
            center: userPosition,
            zoom: 10
        }));
    });
}
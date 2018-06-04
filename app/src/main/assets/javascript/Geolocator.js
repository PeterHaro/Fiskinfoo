function Geolocator(trackUser, _projection) {
    this._positionAccuracy = -1;
    this._altitude = -1;
    this._altitudeAccuracy = -1;
    this._heading = -1;
    this._speed = -1;

    this._geolocation = new ol.Geolocation({
        projection: _projection
    });
    this._geolocation.setTracking(trackUser);
}

Geolocator.prototype.setErrorHandler = function (handler) {
    this._geolocation.on("error", handler);
};

/**
 * Simple ugly hack to return geolocator. TODO: Wrap geolocator into getters/setters here, and do code-deduplicated logic for exposed elements and fields.
 * @returns {ol.Geolocation}
 */
Geolocator.prototype.getGeolocation = function () {
    return this._geolocation;
};

Geolocator.prototype.disableTracking = function() {
    this._geolocation.setTracking(false);
};
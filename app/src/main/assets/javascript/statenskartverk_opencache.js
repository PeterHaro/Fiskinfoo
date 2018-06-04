function StatensKartverkCommunicator() {
    this._wmts_url = "http://opencache.statkart.no/gatekeeper/gk/gk.open_wmts?";
    this._wms_url = "http://opencache.statkart.no/gatekeeper/gk/gk.open_wmts";

    this.sProjection = 'EPSG:3857';
    this.projection = ol.proj.get(this.sProjection),
    this.projectionExtent = this.projection.getExtent(),
    this.size = ol.extent.getWidth(this.projectionExtent) / 256,
    this.resolutions = [],
    this.matrixIds = [];
    for (var z = 0; z < 15; ++z) {
        // generate resolutions and matrixIds arrays for this WMTS
        this.resolutions[z] = this.size / Math.pow(2, z);
        this.matrixIds[z] = this.sProjection + ":" + z;
    }
}

//sjo_hovedkart2
StatensKartverkCommunicator.prototype.CreateSourceWmts = function (layer) {
    return new ol.source.WMTS({
        url: this._wmts_url,
        layer: layer,
        matrixSet: this.sProjection,
        format: 'image/png',
        projection: this.projection,
        tileGrid: new ol.tilegrid.WMTS({
            origin: ol.extent.getTopLeft(this.projectionExtent),
            resolutions: this.resolutions,
            matrixIds: this.matrixIds
        }),
        style: 'default'
    });
};

StatensKartverkCommunicator.prototype.CreateTileLayerWTMSFromSource = function (sourceWMTS, type, title) {
    return new ol.layer.Tile({
        title: title,
        source: sourceWMTS,
        type: type,
        bgcolor: '0x80BDE3'
    });
};
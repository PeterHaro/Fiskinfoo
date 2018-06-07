//DEPENDS ON BarentswatchStylesRepository
function BarentswatchMapServicesCommunicator() {
    this._token = "";
    this._wms_url = "https://geo.barentswatch.no/geoserver/bw/wms";
    this._map_services_base_url = "https://www.barentswatch.no/api/v1/geodata/download/";
    this._ais_service_url = "https://www.barentswatch.no/api/v1/geodata/ais/positions?xmin=0&ymin=53&xmax=38&ymax=81";
    this._tool_serive_url = "https://www.barentswatch.no/api/v1/geodata/download/fishingfacility?format=JSON";
    this._map_services_format = "?format=JSON";
    this._format = "image/png";
    this._crossOriginPolicy = "anonymous";
    this._barentswatchServerType = "geoserver";
    this._transparencyPolicy = true;
    this._wmsRatio = 1;
    this._barentswatchWMSLayerVersion = "1.1.1";
    this.sProjection = "EPSG:3857";
    this.projection = ol.proj.get(this.sProjection);
    this.projectionExtent = this.projection.getExtent();
    this._map = null;
    this._aisStyle = null;
    this._aisSearchPlugin = null; // This plugin facilities the search for vessels and other entities with AIS enabled
}

BarentswatchMapServicesCommunicator.prototype.setMap = function (map) {
    this._map = map;
};

BarentswatchMapServicesCommunicator.prototype.setAISSearchPlugin = function (plugin) {
    this._aisSearchPlugin = plugin;
};

// __BEGIN_AIS_SERVICE_
BarentswatchMapServicesCommunicator.prototype.fetchAISData = function () {
    return new ol.layer.Vector({
        source: new ol.source.Vector({
            url: this._ais_service_url,
            format: new ol.format.GeoJSON()
        })
    });
};
// __END_AIS_SERVICE_

// __BEGIN_API_SERVICES_
BarentswatchMapServicesCommunicator.prototype._buildApiServiceQueryString = function (layerName) {
    return this._map_services_base_url + layerName + this._map_services_format;
};


BarentswatchMapServicesCommunicator.prototype.createApiServiceVectorLayer = function (layerName, style) {
    return new ol.layer.Vector({
        source: new ol.source.Vector({
            url: this._buildApiServiceQueryString(layerName),
            format: new ol.format.GeoJSON()
        }),
        style: style,
        title: layerName
    });
};

BarentswatchMapServicesCommunicator.prototype.parseAuthenticatedAISVectorLayer = function (data) {
    var jsonData = JSON.parse(data);
    var geoJsonData = {
        "type": "FeatureCollection",
        "features": []
    };

    for (var i = 0; i < jsonData.length; i++) {
        geoJsonData.features.push({
            "type": "Feature",
            "geometry": {
                "type": "Point",
                "coordinates": [jsonData[i].Lon, jsonData[i].Lat]
            },
            "properties": {
                "TimeStamp": jsonData[i].TimeStamp,
                "Sog": jsonData[i].Sog,
                "Rot": jsonData[i].Rot,
                "Navstat": jsonData[i].Navstat,
                "Mmsi": jsonData[i].Mmsi,
                "Cog": jsonData[i].Cog,
                "ShipType": jsonData[i].ShipType,
                "Name": jsonData[i].Name,
                "Imo": jsonData[i].Imo,
                "Callsign": jsonData[i].Callsign,
                "Country": jsonData[i].Country,
                "Eta": jsonData[i].Eta,
                "Destination": jsonData[i].Destination,
                "IsSurvey": jsonData[i].IsSurvey,
                "Source": jsonData[i].Source
            }
        });
    }

    var layer = new ol.layer.Vector({
        source: new ol.source.Cluster({
            distance: 35,
            source: new ol.source.Vector({
                features: new ol.format.GeoJSON().readFeatures(geoJsonData, {
                    featureProjection: "EPSG:3857"
                })
            })
        }),
        style: BarentswatchStylesRepository.BarentswatchAisStyle,
        title: "AIS",
        renderMode: 'image'
    });

    var interactionSelection;
    if (this.map != null) {
        BarentswatchStylesRepository.SetAisVectorLayer(layer);
        map.addLayer(layer);
        interactionSelection = BarentswatchStylesRepository.BarentswatchAisSelectionStyle();
        map.addInteraction(interactionSelection);
    }
    if (this.aisSearchModule != null) { // TODO: FIXME: REPLACE THIS!!! This is fetched from outer scope as a UUUUUUUUGLY hack
        this.aisSearchModule.setVesselData(BarentswatchStylesRepository.GetAisVectorReference().getSource().getSource().getFeatures());
        $(document).ready(function () {
            $('input.autocomplete').autocomplete({
                data: aisSearchModule.getVesselObject(),
                onAutocomplete: function (val) {
                    map.getView().fit(aisSearchModule.getVessel(val).getGeometry(), map.getSize());
                    interactionSelection.getFeatures().push(aisSearchModule.getVessel(val));
                    interactionSelection.dispatchEvent({
                        type: 'select',
                        selected: [aisSearchModule.getVessel(val)],
                        deselected: []
                    });
                },
                limit: 5
            });
        });


    }
};

BarentswatchMapServicesCommunicator.prototype.parseAuthenticatedToolsVectorLayer = function (data) {
    //SORRY!!!
    let _createClusteredVectorToolLayer = function (_features, _title, _style) {
        return new ol.layer.Vector({
            source: new ol.source.Cluster({
                distance: 35,
                source: new ol.source.Vector({
                    features: _features
                }),
                geometryFunction: function (feature) {
                    return new ol.geom.Point(ol.extent.getCenter(feature.getGeometry().getExtent()));
                }
            }),
            style: _style,
            title: _title
        });
    };

    let featureData = new ol.format.GeoJSON().readFeatures(data, {
        featureProjection: "EPSG:3857"
    });

    let netsData = [];
    let crabPotData = [];
    let mooringSystemData = [];
    let longLineData = [];
    let danishPurseSeineData = [];
    let sensorCableData = [];
    let unknownData = [];

    featureData.forEach(function (feature) {
        switch (feature.values_.tooltypecode) {
            case "NETS":
                netsData.push(feature);
                break;
            case "CRABPOT":
                crabPotData.push(feature);
                break;
            case "MOORING":
                mooringSystemData.push(feature);
                break;
            case "LONGLINE":
                longLineData.push(feature);
                break;
            case "DANPURSEINE":
                danishPurseSeineData.push(feature);
                break;
            case "SENSORCABLE":
                sensorCableData.push(feature);
                break;
            case "UNK":
            default:
                unknownData.push(feature);
        }
    });

    let netsLayer = _createClusteredVectorToolLayer(netsData, "Tools-nets", BarentswatchStylesRepository.BarentswatchToolNetsStyle);
    let crabpotLayer = _createClusteredVectorToolLayer(crabPotData, "Tools-crabpot", BarentswatchStylesRepository.BarentswatchCrabpotToolStyle);
    let mooringLayer = _createClusteredVectorToolLayer(mooringSystemData, "Tools-mooring", BarentswatchStylesRepository.BarentswatchMooringToolStyle);
    let longLineLayer = _createClusteredVectorToolLayer(longLineData, "Tools-longLine", BarentswatchStylesRepository.BarentswatchLonglineToolStyle);
    let danishPurseSeineLayer = _createClusteredVectorToolLayer(danishPurseSeineData, "Tools-danishPurseSeine", BarentswatchStylesRepository.BarentswatchDanishPureSeineToolStyle);
    let sensorCableLayer = _createClusteredVectorToolLayer(sensorCableData, "Tools-sensorcables", BarentswatchStylesRepository.BarentswatchSenosCableToolStyle);
    let unknownToolLayer = _createClusteredVectorToolLayer(unknownData, "Tools-unknown", BarentswatchStylesRepository.BarentswatchUnknownToolStyle);
    if (this.map != null) {
        BarentswatchStylesRepository.BarentswatchSetNetsVectorReference(netsLayer);
        BarentswatchStylesRepository.BarentswatchSetCrabpotVectorReference(crabpotLayer);
        BarentswatchStylesRepository.BarentswatchSetMooringVectorReference(mooringLayer);
        BarentswatchStylesRepository.BarentswatchSetLonglineVectorReference(longLineLayer);
        BarentswatchStylesRepository.BarentswatchSetDanishPurSeineVectorReference(danishPurseSeineLayer);
        BarentswatchStylesRepository.BarentswatchSetSensorCableVectorReference(sensorCableLayer);
        BarentswatchStylesRepository.BarentswatchSetUnknownVectorReference(unknownToolLayer);
        map.addLayer(netsLayer);
        map.addLayer(crabpotLayer);
        map.addLayer(mooringLayer);
        map.addLayer(longLineLayer);
        map.addLayer(danishPurseSeineLayer);
        map.addLayer(sensorCableLayer);
        map.addLayer(unknownToolLayer);
        map.addInteraction(BarentswatchStylesRepository.BarentswatchToolSelectionStyle());
    }
};

/*BarentswatchMapServicesCommunicator.prototype.createClusteredVectorToolLayer = function (_features, _title) {
    return new ol.layer.Vector({
        source: new ol.source.Cluster({
            distance: 17,
            source: new ol.source.Vector({
                features: _features
            }),
            geometryFunction: function (feature) {
                return new ol.geom.Point(ol.extent.getCenter(feature.getGeometry().getExtent()));
            }
        }),
        style: BarentswatchStylesRepository.BarentswatchToolStyle,
        title: _title
    });
};
*/
BarentswatchMapServicesCommunicator.prototype.createAuthenticatedServiceVectorLayer = function (token, query, authenticatedCall) {
    if (authenticatedCall === "ais") {
        FiskInfoUtility.corsRequest(query, "GET", "", this.parseAuthenticatedAISVectorLayer, corsErrBack, token);
    } else if (authenticatedCall === "tools") {
        FiskInfoUtility.corsRequest(query, "GET", "", this.parseAuthenticatedToolsVectorLayer, corsErrBack, token);
    }

};

function corsErrBack(error) {
    alert(error);
}

// ?????
BarentswatchMapServicesCommunicator.prototype.createClusturedApiServiceVectorLayer = function (layerName, style) {
    var vectorSource = new ol.layer.Vector({
        source: new ol.source.Vector({
            url: this._buildApiServiceQueryString(layerName),
            format: new ol.format.GeoJSON()
        }),
        style: style
    });

    return new ol.source.Cluster({
        distance: 10,
        source: vectorSource
    });
};

BarentswatchMapServicesCommunicator.prototype._createClusteredSource = function (_distance, _source) {
    return new ol.source.Cluster({
        distance: parseInt(_distance, 10),
        source: _source
    });
};

BarentswatchMapServicesCommunicator.prototype._createAuthenticatedAiSLayer = function (token, that) {
    that._token = token;
    if (that !== null) {
        return that.createAuthenticatedServiceVectorLayer(that._token, that._ais_service_url, "ais");
    } else {
        return this.barentswatchCommunicator.createAuthenticatedServiceVectorLayer(this._token, this._ais_service_url, "ais");
    }

};

BarentswatchMapServicesCommunicator.prototype.createAisVectorLayer = function (backend, aisStyle) {
    if (aisStyle !== null) {
        this._aisStyle = aisStyle;
    }
    if (this._token === "") {
        backend.getToken(this._createAuthenticatedAiSLayer, this);
    } else {
        return this.createAuthenticatedServiceVectorLayer(this._token, this._ais_service_url, "ais");
    }
};

BarentswatchMapServicesCommunicator.prototype._createAuthenticatedToolsLayer = function (token, that) {
    that._token = token;
    if (that !== null) {
        that.createAuthenticatedServiceVectorLayer(that._token, that._tool_serive_url, "tools")
    } else {
        this.barentswatchCommunicator.createAuthenticatedServiceVectorLayer(this._token, this._tool_serive_url, "tools")
    }
};

BarentswatchMapServicesCommunicator.prototype.createToolsVectorLayer = function (backend) {
    if (this._token === "") {
        backend.getToken(this._createAuthenticatedToolsLayer, this);
    } else {
        this.createAuthenticatedServiceVectorLayer(this._token, this._tool_serive_url, "tools")
    }
};

// __END_API_SERVICES_

// __BEGIN_WMS_SERVICES_
BarentswatchMapServicesCommunicator.prototype.createWaveWarningSingleTileWMS = function () {
    return this.createSingleTileWMS("bw:waveforecast_area_iso_latest");
};

BarentswatchMapServicesCommunicator.prototype.createIceEdgeSingleTileWMS = function () {
    return this.createSingleTileWMS("bw:icechart_latest");
};

BarentswatchMapServicesCommunicator.prototype.createIceEdgeSingleTileWMS = function () {
    return this.createSingleTileWMS("bw:icechart_latest");
};
BarentswatchMapServicesCommunicator.prototype.createIceEdgeSingleTileWMS = function () {
    return this.createSingleTileWMS("bw:icechart_latest");
};
BarentswatchMapServicesCommunicator.prototype.createIceEdgeSingleTileWMS = function () {
    return this.createSingleTileWMS("bwdev:iceedge_latest");
};

BarentswatchMapServicesCommunicator.prototype.createSingleTileWMS = function (layername) {
    return new ol.layer.Image({
        source: new ol.source.ImageWMS({
            ratio: this._wmsRatio,
            url: this._wms_url,
            crossOrigin: this._crossOriginPolicy,
            params: {
                "FORMAT": this._format,
                "VERSION": this._barentswatchWMSLayerVersion,
                LAYERS: layername,
                STYLES: ''
            },
            serverType: this._barentswatchServerType,
            transparent: this._transparencyPolicy
        }),
        title: layername
    });
};

// __END_WMS_SERVICES_
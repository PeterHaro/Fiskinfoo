var BarentswatchStylesRepository = function () {
    "use strict";

    var maxFeatureCount = 0;
    var aisVectorReference = null;
    var toolsVectorReference = null;
    let netsVectorReference = null;
    let crabpotVectorReference = null;
    let mooringVectorReference = null;
    let longlineeVectorReference = null;
    let danishPurSeineVectorReference = null;
    let sensorCableVectorReference = null;
    let unknownVectorReference = null;
    const unrollClusterResolutionLimit = 25;

    var textFill = new ol.style.Fill({
        color: '#fff'
    });
    var textStroke = new ol.style.Stroke({
        color: 'rgba(0, 0, 0, 0.6)',
        width: 3
    });

    var iceChartStyles = {
        "Close Drift Ice": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(251, 156, 69, 0.5)"
            })
        })],
        "Very Close Drift Ice": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(255, 64, 64, 0.5)"
            })
        })],
        "Fast Ice": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(195, 197, 199, 0.5)"
            })
        })],
        "Open Drift Ice": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(255, 255, 64, 0.5)"
            })
        })],
        "Very Open Drift Ice": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(165, 253, 184, 0.5)"
            })
        })],
        "Open Water": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(176, 214, 255, 0.5)"
            })
        })]
    };

    var iceChartSelectStyles = {
        "Polygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(102, 204, 255, 0.5)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(51, 153, 255, 1)",
                width: 2
            })
        })],
        zIndex: 2
    };

    var activeSeismicStyles = {
        "Polygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(119, 190, 149, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(78, 183, 123, 1)",
                width: 2
            })
        })]
    };

    var activeSeismicSelectStyles = {
        "Polygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(102, 204, 255, 0.5)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(78, 183, 123, 1)",
                width: 2
            })
        })],
        zIndex: 2
    };

    var plannedSeismicStyles = {
        "Polygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(238, 150, 149, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(237, 128, 128, 1)",
                width: 2
            })
        })]
    };

    var plannedSeismicSelectStyles = {
        "Polygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(102, 204, 255, 0.5)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(237, 128, 128, 1)",
                width: 2
            })
        })],
        zIndex: 2
    };

    var seaBottomInstallationsStyles = {
        "Point": [new ol.style.Style({
            image: new ol.style.Circle({
                radius: 5,
                fill: new ol.style.Fill({
                    color: "rgba(238, 150, 149, 0.8)"
                }),
                stroke: new ol.style.Stroke({
                    color: "rgba(8, 113, 114, 1)", width: 2
                })
            })
        })]
    };

    var seaBottomInstallationsSelectStyles = {
        "Point": [new ol.style.Style({
            image: new ol.style.Circle({
                radius: 5,
                fill: new ol.style.Fill({
                    color: "rgba(102, 204, 255, 0.8)"
                }),
                stroke: new ol.style.Stroke({
                    color: "rgba(8, 113, 114, 1)", width: 2
                })
            })
        })],
        zIndex: 2
    };

    var jMessageStyles = {
        "MultiPolygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(238, 150, 149, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(237, 128, 128, 1)",
                width: 2
            })
        })]
    };

    var jMessageSelectionStyles = {
        "MultiPolygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(102, 204, 255, 0.5)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(78, 183, 123, 1)",
                width: 2
            })
        })], zIndex: 2
    };

    var coastalRegulationsStyles = {
        "LineString": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(217, 207, 56, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(217, 207, 56, 1)",
                width: 6
            })
        })]
    };

    var coastalRegulationsSelectionStyles = {
        "LineString": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(217, 207, 56, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(78, 183, 123, 1)",
                width: 6
            })
        })], zIndex: 2
    };

    var coralReefStyles = {
        "Polygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(238, 150, 149, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(237, 128, 128, 1)",
                width: 2
            })
        })]
    };

    var coralReefSelectionStyles = {
        "Polygon": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(238, 150, 149, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(78, 183, 123, 1)",
                width: 2
            })
        })], zIndex: 2
    };

    function createAisSingleFeatureStyle(feature) {
        if (!feature) {
            return;
        }
        var featureName = "";
        if (feature.values_.ShipType === 30) {
            featureName = "fishing-vessel";
        } else {
            featureName = "non-fishing-vessel";
        }
        var style = aisStyles[featureName];
        style.image_.setRotation(feature.values_.Cog);
        return style;
    }

    function createToolSingleFeatureStyle(feature) {
        if (!feature) {
            return;
        }
        if (feature.getGeometry().getType() === "LineString") {
            var style = new ol.style.Style({
                stroke: new ol.style.Stroke({
                    color: '#EAE911',
                    width: 2
                }),
                geometry: feature.getGeometry()
            });
            return style;
        }
        return toolStyles[feature.getGeometry().getType()];
    }

    var aisStyles = {
        "fishing-vessel": new ol.style.Style({
            image: new ol.style.Icon({
                src: './boat-orange.svg',
            })
        }),
        "non-fishing-vessel": new ol.style.Style({
            image: new ol.style.Icon({
                src: './boat-grey.svg'
            })
        })
    };

    var toolStyles = {
        "Point": [new ol.style.Style({
            image: new ol.style.Circle({
                radius: 5,
                fill: new ol.style.Fill({
                    color: "rgba(0, 255, 0, 0.8)"
                }),
                stroke: new ol.style.Stroke({
                    color: "rgba(51, 153, 255, 1)", width: 2
                })
            })
        })],
        "LineString": [new ol.style.Style({
            fill: new ol.style.Fill({
                color: "rgba(0, 255, 0, 0.8)"
            }),
            stroke: new ol.style.Stroke({
                color: "rgba(255, 0, 0, 1)", width: 2
            })
        })],
        'line': new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({color: 'red'}),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 10
            })
        }),
        'teine': new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({
                    color: "rgba(255, 204, 102, 1)"
                }),
                stroke: new ol.style.Stroke({color: 'white', width: 2}),
                points: 3,
                radius: 10
            })
        }),
        'sensorkabel': new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({color: 'green'}),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 10
            })
        }),
        'garn': new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({
                    color: "rgba(0, 0, 153, 1)"
                }),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 10,
                rotation: 0,
                angle: 0
            })/*, // WORKS
            text: new ol.style.Text({
                text: "10",
                fill: textFill,
                stroke: textStroke
            })*/
        })
    };

    function createCrabPotStyle(size) {
        return new ol.style.Style({
            zIndex: 500,
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({
                    color: "rgba(255, 204, 102, 1)"
                }),
                stroke: new ol.style.Stroke({color: 'white', width: 2}),
                points: 3,
                radius: 20
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: textFill,
                stroke: textStroke
            })
        });
    }

    function createMooringSystemStyle(size) {
        return new ol.style.Style({
            zIndex: 500,
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({color: 'pink'}),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 20
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: textFill,
                stroke: textStroke
            })
        });
    }

    function createLongLineStyle(size) {
        return new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({color: 'red'}),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 20
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: textFill,
                stroke: textStroke
            })
        });
    }

    function createNetStyle(size) {
        return new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({
                    color: "rgba(0, 51, 204, 1)"
                }),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 20,
                rotation: 0,
                angle: 0
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: textFill,
                stroke: textStroke
            })
        });
    }

    function createPurseSeineStyle(size) {
        return new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({color: 'purple'}),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 20
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: textFill,
                stroke: textStroke
            })
        });
    }

    function createSensorCableStyle(size) {
        return new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({color: 'green'}),
                stroke: new ol.style.Stroke({color: 'black', width: 2}),
                points: 3,
                radius: 20
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: textFill,
                stroke: textStroke
            })
        });
    }

    function createUnknownToolStyle(size) {
        return new ol.style.Style({
            image: new ol.style.RegularShape({
                fill: new ol.style.Fill({color: 'rgba(105,105,105, 1)'}),
                stroke: new ol.style.Stroke({color: 'white', width: 2}),
                points: 3,
                radius: 20
            }),
            text: new ol.style.Text({
                text: size.toString(),
                fill: textFill,
                stroke: textStroke
            })
        });
    }

    var iceChartStyleFunction = function (feature, resolution) {
        if (!feature) {
            return;
        }
        return iceChartStyles[feature.get("icetype")];
    };
    var activeSeismicStyleFunction = function (feature, resolution) {
        if (!feature) {
            return;
        }
        return activeSeismicStyles[feature.getGeometry().getType()];
    };
    var plannedSeismicStyleFunction = function (feature, resolution) {
        if (!feature) {
            return;
        }
        return plannedSeismicStyles[feature.getGeometry().getType()];
    };
    var seaBottomInstallationsStyleFunction = function (feature, resolution) {
        if (!feature) {
            return;
        }
        return seaBottomInstallationsStyles[feature.getGeometry().getType()];
    };
    var jMessagesStyleFunction = function (feature, resolution) {
        if (!feature) {
            return;
        }
        return jMessageStyles[feature.getGeometry().getType()];
    };
    var coastalRegulationsStyleFunction = function (feature, resolution) {
        if (!feature) {
            return;
        }
        return coastalRegulationsStyles[feature.getGeometry().getType()];
    };
    var coralReefStyleFunction = function (feature, resoltion) {
        if (!feature) {
            return;
        }
        return coralReefStyles[feature.getGeometry().getType()];
    };

    var calculateClusterInfo = function (resolution, clusterType) {
        maxFeatureCount = 0;
        var features = null;
        switch (clusterType) {
            case "NETS":
                features = BarentswatchStylesRepository.BarentswatchGetNetsVectorReference().getSource().getFeatures();
                break;
            case "CRABPOT":
                features = BarentswatchStylesRepository.BarentswatchGetCrabpotVectorReference().getSource().getFeatures();
                break;
            case "MOORING":
                features = BarentswatchStylesRepository.BarentswatchGetMooringVectorReference().getSource().getFeatures();
                break;
            case "LONGLINE":
                features = BarentswatchStylesRepository.BarentswatchGetLonglineVectorReference().getSource().getFeatures();
                break;
            case "DANPURSEINE":
                features = BarentswatchStylesRepository.BarentswatchGetDanishPurSeineVectorReference().getSource().getFeatures();
                break;
            case "SENSORCABLE":
                features = BarentswatchStylesRepository.BarentswatchGetSensorCableVectorReference().getSource().getFeatures();
                break;
            case "UNK":
                features = BarentswatchStylesRepository.BarentswatchGetUnknownVectorReference().getSource().getFeatures();
                break;
            case "ais":
                features = BarentswatchStylesRepository.GetAisVectorReference().getSource().getFeatures();
                break;
            default:
                console.log(clusterType);
                console.log("Invalid typetype. What do we do now?");
        }
        var feature, radius;
        for (var i = features.length - 1; i >= 0; --i) {
            feature = features[i];
            var originalFeatures = feature.get('features');
            var extent = ol.extent.createEmpty();
            var j, jj;
            for (j = 0, jj = originalFeatures.length; j < jj; ++j) {
                ol.extent.extend(extent, originalFeatures[j].getGeometry().getExtent());
            }
            maxFeatureCount = Math.max(maxFeatureCount, jj);
            radius = 0.25 * (ol.extent.getWidth(extent) + ol.extent.getHeight(extent)) /
                resolution;

            feature.set('radius', radius);
        }
    };

    var oldAISClusterStyleResolution;
    var oldToolClusterStyleResolution;
    var netsClusterStyleResolution;
    var crabPotClusterStyleResolution;
    var mooringSystemStyleResolution;
    let longLineClusterStyleResolution;
    let danishPurseSeineClusterStyleResolution;
    let sensorCableClusterStyleResolution;
    let unknownClusterStyleResolution;

    var aisClusterStyleFunction = function (feature, resolution) {
        if (resolution != oldAISClusterStyleResolution) {
            calculateClusterInfo(resolution, "ais");
            oldAISClusterStyleResolution = resolution;
        }
        var style;
        var size = feature.get('features').length;
        if (size > 1) {
            let radiusSize = 15;
            if ((feature.get("radius") * 0.6) > radiusSize) {
                radiusSize = (feature.get("radius") * 0.6);
            }
            style = new ol.style.Style({
                zIndex: -999,
                image: new ol.style.Circle({
                    zIndex: -999,
                    radius: radiusSize,
                    stroke: new ol.style.Stroke({
                        color: "rgba(255, 255, 255, 1)",
                        width: 2
                    }),
                    fill: new ol.style.Fill({
                        color: [255, 153, 0, 1]
                    })
                }),
                text: new ol.style.Text({
                    text: size.toString(),
                    fill: textFill,
                    stroke: textStroke
                }),
            });
        } else {
            var originalFeature = feature.get("features")[0];
            style = createAisSingleFeatureStyle(originalFeature);
        }
        return style;
    };

    var _aisSelectionStyleFunction = function (feature, resolution) {
        if (feature.get('features') === undefined) {
            dispatchDataToBottomsheet(feature, BarentswatchApiObjectTypes.AIS); //TODO: REMOVE THIS SUPERHACK
            map.getInteractions().forEach(function (interaction) {
                if (interaction instanceof ol.interaction.Select) {
                    if (interaction.featureOverlay_.style_.name === "_aisSelectionStyleFunction") {
                        interaction.getFeatures().clear();
                    }
                }
            });
            return createAisSingleFeatureStyle(feature);
        }
        if (feature.get('features').length === 1) {
            return createAisSingleFeatureStyle(feature.get('features')[0]);
        }

        var extent = new ol.extent.createEmpty();
        feature.get('features').forEach(function (f, index, array) {
            ol.extent.extend(extent, f.getGeometry().getExtent());
        });
        map.getView().fit(extent, map.getSize());
        aisClusterStyleFunction(feature, resolution);
        map.getInteractions().forEach(function (interaction) {
            if (interaction instanceof ol.interaction.Select) {
                if (interaction.featureOverlay_.style_.name === "_aisSelectionStyleFunction") {
                    interaction.getFeatures().clear();
                }
            }
        });
    };

    var _toolsSelectionStyleFunction = function (feature, resolution) {
        if (feature.get('features').length === 1) {
            return createToolSingleFeatureStyle(feature.get('features')[0]);
        }
        var extent = new ol.extent.createEmpty();
        feature.get('features').forEach(function (f, index, array) {
            ol.extent.extend(extent, f.getGeometry().getExtent());
        });

        map.getView().fit(extent, map.getSize());
        switch (feature.get('features')[0].values_.tooltypecode) {
            case "NETS":
                netsClusterStyleFunction(feature, resolution);
                break;
            case "CRABPOT":
                crabpotClusterStyleFunction(feature, resolution);
                break;
            case "MOORING":
                mooringClusterStyleFunction(feature, resolution);
                break;
            case "LONGLINE":
                longLineClusterStyleFunction(feature, resolution);
                break;
            case "DANPURSEINE":
                danPurSeineClusterStyleFunction(feature, resolution);
                break;
            case "SENSORCABLE":
                sensorCableClusterStyleFunction(feature, resolution);
                break;
            case "UNK":
                unknownClusterStyleResolution(feature, resolution);
                break;
            default:
                console.log("Invalid selected tool");
        }
        map.getInteractions().forEach(function (interaction) {
            if (interaction instanceof ol.interaction.Select) {
                if (interaction.featureOverlay_.style_.name === "_toolsSelectionStyleFunction") {
                    interaction.getFeatures().clear();
                }
            }
        });
    };


    //<editor-fold desc="Tool style functions">
    var toolsStyleCache = {};
    let netsClusterStyleFunction = function (feature, resolution) {
        if (resolution !== netsClusterStyleResolution) {
            calculateClusterInfo(resolution, "NETS");
            netsClusterStyleResolution = resolution;
        }
        if (resolution < unrollClusterResolutionLimit) { // TODO: SUPER HACK
            return unrollCluster(feature);
        }

        let size = feature.get("features").length;
        if (!(size > 1)) {
            return createToolSingleFeatureStyle(feature.get("features")[0]);
        }
        return createNetStyle(size);
    };

    function unrollCluster(feature) {
        let styles = [];
        let features = feature.get("features");
        for (let i = 0; i < features.length; i++) {
            features[i].superHack = true;
            styles.push(createToolSingleFeatureStyle(features[i]));
        }
        return styles;
    }

    let crabpotClusterStyleFunction = function (feature, resolution) {
        if (resolution !== crabPotClusterStyleResolution) {
            calculateClusterInfo(resolution, "CRABPOT");
            crabPotClusterStyleResolution = resolution;
        }
        if (resolution < unrollClusterResolutionLimit) { //THESE ARE NOT SELECTABLE
            return unrollCluster(feature);
        }

        let size = feature.get("features").length;
        if (!(size > 1)) {
            return createToolSingleFeatureStyle(feature.get("features")[0]);
        }
        return createCrabPotStyle(size);
    };
    let mooringClusterStyleFunction = function (feature, resolution) {
        if (resolution !== mooringSystemStyleResolution) {
            calculateClusterInfo(resolution, "MOORING");
            mooringSystemStyleResolution = resolution;
        }
        if (resolution < unrollClusterResolutionLimit) { // TODO: SUPER HACK
            return unrollCluster(feature);
        }
        let size = feature.get("features").length;
        if (!(size > 1)) {
            return createToolSingleFeatureStyle(feature.get("features")[0]);
        }
        return createMooringSystemStyle(size);
    };
    let longLineClusterStyleFunction = function (feature, resolution) {
        if (resolution !== longLineClusterStyleResolution) {
            calculateClusterInfo(resolution, "LONGLINE");
            longLineClusterStyleResolution = resolution;
        }
        if (resolution < unrollClusterResolutionLimit) { // TODO: SUPER HACK
            return unrollCluster(feature);
        }
        let size = feature.get("features").length; //TODO: CHECK IF 2 AND DOUBLE DOUBLE SINGLE STYLE FEATURE FOR FEAT!
        if (!(size > 1)) {
            return createToolSingleFeatureStyle(feature.get("features")[0]);
        }
        return createLongLineStyle(size);
    };
    let danPurSeineClusterStyleFunction = function (feature, resolution) {
        if (resolution !== danishPurseSeineClusterStyleResolution) {
            calculateClusterInfo(resolution, "DANPURSEINE");
            danishPurseSeineClusterStyleResolution = resolution;
        }
        if (resolution < unrollClusterResolutionLimit) { // TODO: SUPER HACK
            return unrollCluster(feature);
        }
        let size = feature.get("features").length;
        if (!(size > 1)) {
            return createToolSingleFeatureStyle(feature.get("features")[0]);
        }
        return createPurseSeineStyle(size);
    };
    let sensorCableClusterStyleFunction = function (feature, resolution) {
        if (resolution !== sensorCableClusterStyleResolution) {
            calculateClusterInfo(resolution, "SENSORCABLE");
            sensorCableClusterStyleResolution = resolution;
        }
        if (resolution < unrollClusterResolutionLimit) { // TODO: SUPER HACK
            return unrollCluster(feature);
        }
        let size = feature.get("features").length;
        if (!(size > 1)) {
            return createToolSingleFeatureStyle(feature.get("features")[0]);
        }
        return createSensorCableStyle(size);
    };
    let unknownClusterStyleFunction = function (feature, resolution) {
        if (resolution !== unknownClusterStyleResolution) {
            calculateClusterInfo(resolution, "UNK");
            unknownClusterStyleResolution = resolution;
        }
        if (resolution < unrollClusterResolutionLimit) { // TODO: SUPER HACK
            return unrollCluster(feature);
        }
        let size = feature.get("features").length;
        if (!(size > 1)) {
            return createToolSingleFeatureStyle(feature.get("features")[0]);
        }
        return createUnknownToolStyle(size);
    };
    //</editor-fold>

// __BEGIN_SELECT_STYLES_
    var iceChartSelectStyleFunction = function () {
        return new ol.interaction.Select({
            style: function (feature, resolution) {
                return iceChartSelectStyles[feature.getGeometry().getType()];
            }
        });
    };
    var activeSeismicSelectStyleFunction = function () {
        return new ol.interaction.Select({
            style: function (feature, resolution) {
                return activeSeismicSelectStyles[feature.getGeometry().getType()];
            }
        });
    };
    var plannedSeismicSelectStyleFunction = function () {
        return new ol.interaction.Select({
            style: function (feature, resolution) {
                return plannedSeismicSelectStyles[feature.getGeometry().getType()];
            }
        });
    };
    var seaBottomInstallationsSelectStyleFunction = function () {
        return new ol.interaction.Select({
            style: function (feature, resolution) {
                console.log(feature.getGeometry().getType());
                return seaBottomInstallationsSelectStyles[feature.getGeometry().getType()];
            }
        });
    };
    var jMessagesSelectStyleFunction = function () {
        return new ol.interaction.Select({
            style: function (feature, resolution) {
                return jMessageSelectionStyles[feature.getGeometry().getType()];
            }
        });
    };
    var coastalRegulationsSelectStyleFunction = function () {
        return new ol.interaction.Select({
            style: function (feature, resolution) {
                return coastalRegulationsSelectionStyles[feature.getGeometry().getType()];
            }
        });
    };
    var coralReefSelectStyleFunction = function () {
        return new ol.interaction.Select({
            style: function (feature, resolution) {
                return coralReefSelectionStyles[feature.getGeometry().getType()];
            }
        });
    };
    var aisSelectionStyleFunction = function () {
        return new ol.interaction.Select({
            condition: function (evt) {
                return evt.type == 'singleclick' || evt.type == 'click';
            },
            style: _aisSelectionStyleFunction,
            name: "Ais-selection"
        });
    };
    var toolSelectionStyleFunction = function () {
        return new ol.interaction.Select({
            condition: function (evt) {
                return evt.type === "singleclick" || evt.type == 'click';
            },
            style: _toolsSelectionStyleFunction,
            name: "Tool-selection"
        });
    };
// __END_SELECT_STYLES_

    // GETTERS_AND_SETTERS
    //TODO: HACK, FIGURE OUT HOW TO REMOVE THIS
    // These are used to style the clustering layers, in order to get correct references
    function setAisVectorLayer(layer) {
        this.aisVectorReference = layer;
    }

    function getAisVectorReference() {
        return this.aisVectorReference;
    }

    function setToolVectorLayer(layer) {
        this.toolsVectorReference = layer;
    }

    function getToolVectorLayeer() {
        return this.toolsVectorReference;
    }

    function setNetsVectorReference(layer) {
        this.netsVectorReference = layer;
    }

    function setCrabpotVectorReference(layer) {
        this.crabpotVectorReference = layer;
    }

    function setMooringVectorReference(layer) {
        this.mooringVectorReference = layer;
    }

    function setLonglineeVectorReference(layer) {
        this.longlineeVectorReference = layer;
    }

    function setDanishPurSeineVectorReference(layer) {
        this.danishPurSeineVectorReference = layer;
    }

    function setSensorCableVectorReference(layer) {
        this.sensorCableVectorReference = layer;
    }

    function setUnknownVectorReference(layer) {
        this.unknownVectorReference = layer;
    }

    function getNetsVectorReference() {
        return this.netsVectorReference;
    }

    function getCrabpotVectorReference() {
        return this.crabpotVectorReference;
    }

    function getMooringVectorReference() {
        return this.mooringVectorReference;
    }

    function getLonglineVectorReference() {
        return this.longlineeVectorReference;
    }

    function getDanishPurSeineVectorReference() {
        return this.danishPurSeineVectorReference;
    }

    function getSensorCableVectorReference() {
        return this.sensorCableVectorReference;
    }

    function getUnknownVectorReference() {
        return this.unknownVectorReference;
    }


    // __END_GETTERS_AND_SETTERS

    return {
        BarentswatchIceChartStyle: iceChartStyleFunction,
        BarentswatchIceChartSelectionStyle: iceChartSelectStyleFunction,
        BarentswatchActiveSeismicStyle: activeSeismicStyleFunction,
        BarentswatchActiveSeismicSelectionStyle: activeSeismicSelectStyleFunction,
        BarentswatchPlannedSeismicStyle: plannedSeismicStyleFunction,
        BarentswatchPlannenSeismicSelectionStyle: plannedSeismicSelectStyleFunction,
        BarentswatchSeaBottomInstallationsStyle: seaBottomInstallationsStyleFunction,
        BarentswatchSeaBottomInstallationsSelectionStyle: seaBottomInstallationsSelectStyleFunction,
        BarentswatchJMessagesStyle: jMessagesStyleFunction,
        BarentswatchJMessagesSelectionStyle: jMessagesSelectStyleFunction,
        BarentswatchCoastalRegulationStyle: coastalRegulationsStyleFunction,
        BarentswatchCoastalRegulationSelectionStyle: coastalRegulationsSelectStyleFunction,
        BarentswatchCoralReefStyle: coralReefStyleFunction,
        BarentswatchCoralReefSelectionStyle: coralReefSelectStyleFunction,
        BarentswatchAisStyle: aisClusterStyleFunction,
        BarentswatchAisSelectionStyle: aisSelectionStyleFunction,
        SetAisVectorLayer: setAisVectorLayer,
        GetAisVectorReference: getAisVectorReference,
        //BarentswatchToolStyle: toolsClusterStyleFunction,
        //__BEGIN_TOOLS_
        BarentswatchToolNetsStyle: netsClusterStyleFunction,
        BarentswatchCrabpotToolStyle: crabpotClusterStyleFunction,
        BarentswatchMooringToolStyle: mooringClusterStyleFunction,
        BarentswatchLonglineToolStyle: longLineClusterStyleFunction,
        BarentswatchDanishPureSeineToolStyle: danPurSeineClusterStyleFunction,
        BarentswatchSenosCableToolStyle: sensorCableClusterStyleFunction,
        BarentswatchUnknownToolStyle: unknownClusterStyleFunction,
        BarentswatchSetNetsVectorReference: setNetsVectorReference,
        BarentswatchSetCrabpotVectorReference: setCrabpotVectorReference,
        BarentswatchSetMooringVectorReference: setMooringVectorReference,
        BarentswatchSetLonglineVectorReference: setLonglineeVectorReference,
        BarentswatchSetDanishPurSeineVectorReference: setDanishPurSeineVectorReference,
        BarentswatchSetSensorCableVectorReference: setSensorCableVectorReference,
        BarentswatchSetUnknownVectorReference: setUnknownVectorReference,
        BarentswatchGetNetsVectorReference: getNetsVectorReference,
        BarentswatchGetCrabpotVectorReference: getCrabpotVectorReference,
        BarentswatchGetMooringVectorReference: getMooringVectorReference,
        BarentswatchGetLonglineVectorReference: getLonglineVectorReference,
        BarentswatchGetDanishPurSeineVectorReference: getDanishPurSeineVectorReference,
        BarentswatchGetSensorCableVectorReference: getSensorCableVectorReference,
        BarentswatchGetUnknownVectorReference: getUnknownVectorReference,
        //__END_TOOLS_
        SetToolsVectorLayer: setToolVectorLayer,
        GetToolsVectorReference: getToolVectorLayeer,
        BarentswatchToolSelectionStyle: toolSelectionStyleFunction
    }

}();
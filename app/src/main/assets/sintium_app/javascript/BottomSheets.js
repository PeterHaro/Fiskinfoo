var infoTemplate = Sintium.templateWidget({
    domId: "info-template"
});

var infoDrawer = Sintium.drawer({
    position: 'bottom',
    widgets: [infoTemplate],
    previewSize: 88,
    size: '100%'
});

var vesselInfoTemplate = Sintium.templateWidget({
    domId: "vessel-info-template"
});

var vesselInfoDrawer = Sintium.drawer({
    position: 'bottom',
    widgets: [vesselInfoTemplate],
    previewSize: 88,
    size: '100%'
});

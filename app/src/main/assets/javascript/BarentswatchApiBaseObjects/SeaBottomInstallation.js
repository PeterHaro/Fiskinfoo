function SeaBottomInstallation() {
    this._type = BarentswatchApiObjectTypes.SEABOTTOM_INSTALLATION;
    this._norwegianTitle = "Havbunninstallasjon";
    this._name = "";
    this._installationType = "";
    this._functionality = "";
    this._depth = "";
    this._belongsToField = "";
    this._startup = "";
    this._operator = "";
    this._position = "";
    this._marinogramUrl = "";
    this._oilDirectorateFactPageURL = "";
    this._oildirectoryMapURL = "";
}

SeaBottomInstallation.prototype.parseObject = function (seaBottomInstallation) {
    this._name = seaBottomInstallation.get("facname");
    this._installationType = seaBottomInstallation.get("fackind");
    this._functionality = seaBottomInstallation.get("facfunc");
    this._depth = seaBottomInstallation.get("waterdepth");
    this._belongsToField = seaBottomInstallation.get("belong2knd");
    this._startup = seaBottomInstallation.get("dtstartup");
    this._operator = seaBottomInstallation.get("curopernam");
    this._position = ol.proj.transform(ol.extent.getCenter(seaBottomInstallation.getGeometry().getExtent()), 'EPSG:3857', 'EPSG:4326');
    this._operatorURL = seaBottomInstallation.get("cutoperurl");
    this._oilDirectorateFactPageURL = seaBottomInstallation.get("facturl");
    this._oildirectoryMapURL = seaBottomInstallation.get("apurl");
};

SeaBottomInstallation.prototype.getJson = function () {
    return JSON.stringify(this);
};

SeaBottomInstallation.prototype.getCoordinates = function () {
    return ol.coordinate.toStringHDMS(this._position, 1);
};

SeaBottomInstallation.prototype.getCoordinates = function () {
    return ol.coordinate.toStringHDMS(this._position, 1);
};
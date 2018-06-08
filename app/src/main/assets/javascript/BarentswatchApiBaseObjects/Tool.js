function Tool() {
    this._type = BarentswatchApiObjectTypes.TOOL;
    this._norwegianTitle = "Redskap";
    this._name = "";
    this._timePlacedInOcean = "";
    this._id = "";
    this._imo = "";
    this._ircs = "";
    this._lastchangedbysource = "";
    this._lastchangeddatetime = "";
    this._mmsi = "";
    this._removeddatetime = "";
    this._setupdatetime = "";
    this._source = "";
    this._toolcolor = "";
    this._toolid = "";
    this._tooltypecode = "";
    this._tooltypename = "";
    this._version = "";
    this._vesselemail = "";
    this._vesselname = "";
    this._vesselphone = "";
}

Tool.prototype.parseObject = function (toolObject) {
    this._id = toolObject.get("id");
    this._position = ol.extent.getCenter(toolObject.getGeometry().getExtent());
    this._imo = toolObject.get("imo");
    this._ircs = toolObject.get("ircs");
    this._lastchangedbysource = toolObject.get("lastchangedbysource");
    this._lastchangeddatetime = toolObject.get("lastchangeddatetime");
    this._mmsi = toolObject.get("mmsi");
    this._removeddatetime = toolObject.get("removeddatetime");
    this._setupdatetime = toolObject.get("setupdatetime");
    this._source = toolObject.get("source");
    this._toolcolor = toolObject.get("toolcolor");
    this._toolid = toolObject.get("toolid");
    this._tooltypecode = toolObject.get("tooltypecode");
    this._tooltypename = toolObject.get("tooltypename");
    this._version = toolObject.get("version");
    this._vesselemail = toolObject.get("vesselemail");
    this._vesselname = toolObject.get("vesselname");
    this._vesselphone = toolObject.get("vesselphone");
    this._name = this.getName();
};

Tool.prototype.getCoordinates = function() {
    return ol.coordinate.toStringHDMS(this._position);
};

Tool.prototype.getTimePlacedInOcean = function() {
    var currentDate = new Date();
    var setupDate = new Date(this._setupdatetime);
    var deltaTime = Math.abs(setupDate - currentDate) / 1000;

    var days = Math.floor(deltaTime / 86400);
    deltaTime -= days * 86400;

    var hours = Math.floor(deltaTime / 3600) % 24;
    deltaTime -= hours * 3600;

    var minutes = Math.floor(deltaTime / 60) % 60;
    return "" + days.toString() + " Døgn " + hours.toString() + " timer " + minutes + " minutter";
};

Tool.prototype.getFormattedTimeSetInOcean = function() {
    return "Satt: " + FiskInfoUtility.formatDate(new Date(this._setupdatetime));
};

Tool.prototype.getName = function () {
    switch (this._tooltypename) {
        case "Nets":
            return "Fiskenett";
        default:
            return this._tooltypename;
    }
};

Tool.prototype.isToolPotentiallyOutdated = function () {
    // if hasBeenPlacedOverTimeLimit(this._timePlacedInOcean) {
    //    return true
    //
    //} return false;
};
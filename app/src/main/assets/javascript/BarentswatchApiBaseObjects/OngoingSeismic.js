function OngoingSeismic() {
    this._type = BarentswatchApiObjectTypes.ONGOING_SEISMIC;
    this._norwegianTitle = "Pågående seismikk";
    this._name = "";
    this._areaSubheader = "";
    this._seismicVessel = "";
    this._operationType = "";
    this._underType = "";
    this._fromDate = "";
    this._toDate = "";
    this.responsibleCompany = "";
    this._sourceType = "";
    this._sensorType = "";
    this._numberOfSensors = "";
    this._sensorLength = "";
    this._sourcePres = "";
    this._mapUrl = "";
    this._factPage = "";
}

OngoingSeismic.prototype.parseObject = function (ongoingSeismicObject) {
    this._name = ongoingSeismicObject.get("surveyname");
    this._areaSubheader = ongoingSeismicObject.get("polkind");
    this._seismicVessel = ongoingSeismicObject.get("vesselall");
    this._operationType = ongoingSeismicObject.get("surmaintyp");
    this._underType = ongoingSeismicObject.get("surparttyp");
    this._fromDate = ongoingSeismicObject.get("plnfrmdate");
    this._toDate = ongoingSeismicObject.get("plntodate");
    this.responsibleCompany = ongoingSeismicObject.get("compreport");
    this._sourceType = ongoingSeismicObject.get("sourcetype");
    this._sensorType = ongoingSeismicObject.get("sensortype");
    this._numberOfSensors = ongoingSeismicObject.get("sensnote");
    this._sensorLength = ongoingSeismicObject.get("senslength");
    this._sourcePres = ongoingSeismicObject.get("sourcepres");
    this._mapUrl = ongoingSeismicObject.get("mapurl");
    this._factPage = ongoingSeismicObject.get("factv2url");
};

OngoingSeismic.prototype.getPeriod = function() {
    return FiskInfoUtility.formatDate(new Date(this._fromDate)) + " - " + FiskInfoUtility.formatDate(new Date(this._toDate));
};
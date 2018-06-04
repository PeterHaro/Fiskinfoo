function PlannedSeismic() {
    this._type = BarentswatchApiObjectTypes.PLANNED_SEISMIC;
    this._norwegianTitle = "Planlagt seismikk";

}

PlannedSeismic.prototype.parseObject = function(plannedSeismicObject) {
    this._areaSubheader = plannedSeismicObject.get("polkind");
    this._name = plannedSeismicObject.get("surveyname");
    this._seismicVessel = plannedSeismicObject.get("vesselall");
    this._operationType = plannedSeismicObject.get("surmaintyp");
    this._underType = plannedSeismicObject.get("surparttyp");
    this._fromDate = plannedSeismicObject.get("plnfrmdate");
    this._toDate = plannedSeismicObject.get("plntodate");
    this._responsibleCompany = plannedSeismicObject.get("compreport");
    this._sourceType = plannedSeismicObject.get("sourcetype");
    this._sensorType = plannedSeismicObject.get("sensortype");
    this._numberOfSensors = plannedSeismicObject.get("sensnote");
    this._sensorLength = plannedSeismicObject.get("senslength");
    this._mapUrl = plannedSeismicObject.get("mapurl");
    this._factPage = plannedSeismicObject.get("factv2url");
};

PlannedSeismic.prototype.getPeriod = function() {
    return FiskInfoUtility.formatDate(new Date(this._fromDate)) + " - " + FiskInfoUtility.formatDate(new Date(this._toDate));
};
function CoastlinesCod() {
    this._type = BarentswatchApiObjectTypes.COASTLINES_COD;
    this._start_point_description = "";
    this._end_point_description = "";
}

CoastlinesCod.prototype.parseObject = function(coastlinesCodObject) {
    this._start_point_description = coastlinesCodObject.get("start_point_description");
    this._end_point_description = coastlinesCodObject.get("end_point_description");
};
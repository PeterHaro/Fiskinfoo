function CoralReef() {
    this._type = BarentswatchApiObjectTypes.CORAL_REEF;
    this._norwegianTitle = "Forbudsområde - Korallrev";
    this._info = "";
    this.name = "";
    this._paragraph = "";
}

CoralReef.prototype.parseObject = function(coralReefObject) {
    this._info = coralReefObject.get("info");
    this._name = coralReefObject.get("navn");
    this._paragraph = coralReefObject.get("paragraf");
};
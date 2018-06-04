function IceConsentration() {
    this._type = BarentswatchApiObjectTypes.ICE_CONSENTRATION;
    this._icetype = "";
}

IceConsentration.prototype.parseObject = function(iceConsentrationObject) {
    this._icetype = iceConsentrationObject.get("icetype");
};
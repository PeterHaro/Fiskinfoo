function JMessage() {
    this._type = BarentswatchApiObjectTypes.JMESSAGE;
    this._norwegianTitle = "Midlertidig stengt område";
    this._name = "";
    this._description = "";
    this._closedDate = "";
    this._closedDescription = "";
    this._closedFor = "";
    this._fishingGroup = "";
    this._area = "";
    this._isClosed = true;
    this._jMessageName = "";
}

JMessage.prototype.parseObject = function(jMessageObject) {
    this._name = jMessageObject.get("name");
    this._description = jMessageObject.get("description");
    this._closedDate = jMessageObject.get("closed_date");
    this._closedDescription = jMessageObject.get("closed_desc");
    this._closedFor = jMessageObject.get("type_name");
    this._fishingGroup = jMessageObject.get("speciestype_name");
    this._area = jMessageObject.get("area_name");
    this._isClosed = jMessageObject.get("isclosed");
    this._jmessageName = jMessageObject.get("jmelding_name");
    this._jMessageParagraphCount = jMessageObject.get("jmelding_paragraph_count");
    this._jMessageToDate = jMessageObject.get("jmelding_todate");
};
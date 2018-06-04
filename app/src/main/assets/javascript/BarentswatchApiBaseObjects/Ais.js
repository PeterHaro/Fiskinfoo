function Ais() {
    this._type = BarentswatchApiObjectTypes.AIS;
    this._norwegianTitle = "Ais";
    this._name = "";
    this._timeStamp = "";
    this._sog = "";
    this._closedDescription = "";
    this._shipType = "";
    this._eta = "";
    this._mmsi = "";
    this._timeStamp = "";
    this._destination = "";
    this._position = "";
}

Ais.prototype.parseObject = function(aisObject) {
    this._name = aisObject.get("Name");
    this._shipType = aisObject.get("ShipType");
    this._eta = aisObject.get("Eta");
    this._mmsi = aisObject.get("Mmsi");
    this._destination = aisObject.get("Destination");
    this._timeStamp = aisObject.get("TimeStamp");
    this._sog = aisObject.get("Sog");
    this._cog = aisObject.get("Cog");
    this._country = aisObject.get("Country");
    this._position = ol.extent.getCenter(aisObject.getGeometry().getExtent());
};

Ais.prototype.getShipTypeString = function () {
    return AisShipTypeNumberTranslator.translateNumber(this._shipType);
};

Ais.prototype.getFormattedDate = function() { // TODO: Consider adding HH:MM
  return FiskInfoUtility.formatDate(new Date(this._timeStamp));
};

Ais.prototype.fetchShipType = function(shipType) { //TODO: Is there any directory with the different ship types ? Once we know. ADD HERE!
    return shipType === 30 ? "Fiskefartøy" : "Fritidsbåt";
};

/* "properties": {
                "TimeStamp": jsonData[i].TimeStamp,
                "Sog": jsonData[i].Sog,
                "Rot": jsonData[i].Rot,
                "Navstat": jsonData[i].Navstat,
                "Mmsi": jsonData[i].Mmsi,
                "Cog": jsonData[i].Cog,
                "ShipType": jsonData[i].ShipType,
                "Name": jsonData[i].Name,
                "Imo": jsonData[i].Imo,
                "Callsign": jsonData[i].Callsign,
                "Country": jsonData[i].Country,
                "Eta": jsonData[i].Eta,
                "Destination": jsonData[i].Destination,
                "IsSurvey": jsonData[i].IsSurvey,
                "Source": jsonData[i].Source
            }*/
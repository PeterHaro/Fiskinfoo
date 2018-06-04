function AndroidBackend() {
    this._token = "";
    this._httpBuilder = new SimpleHtmlBuilder();
}

AndroidBackend.prototype.getToken = function (_callback, that) {
    if (this._token !== "") {
        return this._token;
    }

    this._token = Android.getToken();
    if (that !== null) {
        _callback(this._token, that)
    } else {
        _callback(this._token);
    }

    return this._token;
};

// TODO: Create getters and setters / "interface" for feature(s)
AndroidBackend.prototype.showBottmsheet = function (feature) {
    var body = $("#bottom_sheet_container");
    body.text("");
    body.append(this._httpBuilder.getSelfContainedHeading(4, feature._name));
    this._httpBuilder.clear();
    if (feature._type === BarentswatchApiObjectTypes.AIS) { //TODO: REFACTOR
        body.append("<h6 class='grey-text grey lighten-5 align-material-c-to-title'>" + feature.getShipTypeString() + "</h6>");
    } else {
        body.append("<h6 class='grey-text grey lighten-5 align-material-c-to-title'>" + feature._norwegianTitle + "</h6>");
    }
    body.append("<br>");
    body.append("<div class='divider'></div>");
    var content = "";

    switch (feature._type) {
        case BarentswatchApiObjectTypes.TOOL:
            content = this._showToolBottomsheet(feature);
            break;
        case BarentswatchApiObjectTypes.SEABOTTOM_INSTALLATION:
            content = this._showSubsurfaceFacilityBottomsheet(feature);
            break;
        case BarentswatchApiObjectTypes.JMESSAGE:
            content = this._createJMessageBottomsheetContent(feature);
            break;
        case BarentswatchApiObjectTypes.CORAL_REEF:
            content = this._createCoralReefBottomsheet(feature);
            break;
        case BarentswatchApiObjectTypes.COASTLINES_COD:
            return new CoastlinesCod();
        case BarentswatchApiObjectTypes.ICE_CONSENTRATION:
            return new IceConsentration();
        case BarentswatchApiObjectTypes.ONGOING_SEISMIC:
        case BarentswatchApiObjectTypes.PLANNED_SEISMIC:
            content = this._buildSeismicBottomsheetText(feature);
            break;
        case BarentswatchApiObjectTypes.AIS:
            content = this._createAisBottomsheet(feature);
            break;
        default:
            return null;
    }

    body.append(content);
    var bottomSheet = document.querySelector("#bottom_sheet");
    var instance = M.Modal.getInstance(bottomSheet);
    instance.open();
};

AndroidBackend.prototype._createIceChartConsentrationContent = function (feature) {

};

AndroidBackend.prototype._showToolBottomsheet = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createModalIconLine("date_range", "Tid i havet", feature.getTimePlacedInOcean());
    retval += this._httpBuilder.createModalIconLine("date_range", "Satt", feature.getFormattedTimeSetInOcean());
    retval += this._httpBuilder.createModalIconLine("place", "Posisjon", FiskInfoUtility.ddToDms(feature._position[1], feature._position[0]));
    //TODO: MARINOGRAM HER

    retval += this._httpBuilder.getSelfContainedHeading(6, "Om Fartøyet");
    retval += "<div class='divider'></div>";

    retval += this._httpBuilder.createTitleLineWithStrongText("Fartøy", feature._vesselname);
    retval += this._httpBuilder.createTitleLineWithStrongText("Telefon", feature._vesselphone);
    retval += this._httpBuilder.createTitleLineWithStrongText("Kallesignal(IRCS)", feature._ircs);
    retval += this._httpBuilder.createTitleLineWithStrongText("MMSI", feature._mmsi);
    retval += this._httpBuilder.createTitleLineWithStrongText("IMO", feature._imo);
    retval += this._httpBuilder.createTitleLineWithStrongText("E-post", feature._vesselemail);

    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createModalIconLine("link", "Fiskerimeldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/Fiskerimeldinger'>Fiskerimeldinger</a>");
    retval += this._httpBuilder.createModalIconLine("link", "J-meldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/J-meldinger/Gjeldende-J-meldinger/'>J-meldinger</a>");
    return retval;
};

AndroidBackend.prototype._createAisBottomsheet = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createModalIconLine("directions_boat", "Fart", feature._sog);
    retval += this._httpBuilder.createModalIconLine("compass_calibration", "Kurs", feature._cog);
    retval += this._httpBuilder.createModalIconLine("gps_fixed", "Posisjon", feature._cog);
    retval += this._httpBuilder.createModalIconLine("place", "Posisjon", FiskInfoUtility.ddToDms(feature._position[1], feature._position[0]));
    retval += this._httpBuilder.createModalIconLine("date_range", "Signal mottatt", feature.getFormattedDate());
    retval += this._httpBuilder.createModalIconLine("add_location", "Destinasjon", feature._destination);
    //TODO: Redskaper
    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createModalIconLine("link", "Fiskerimeldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/Fiskerimeldinger'>Fiskerimeldinger</a>");
    retval += this._httpBuilder.createModalIconLine("link", "J-meldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/J-meldinger/Gjeldende-J-meldinger/'>J-meldinger</a>");
    return retval;
};

AndroidBackend.prototype._createJMessageBottomsheetContent = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createModalIconLine("date_range", "Stengt fra dato", feature._closedDate);
    retval += this._httpBuilder.createModalIconLine("settings", "Stengt for", feature._closedFor);
    retval += this._httpBuilder.createModalIconLine("group_work", "Fiskegruppe", feature._fishingGroup);
    retval += this._httpBuilder.createModalIconLine("layers", "Område", feature._area);
    retval += this._httpBuilder.createModalIconLine("highlight_off", "J-melding", feature._jmessageName);
    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createModalIconLine("link", "Fiskerimeldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/Fiskerimeldinger'>Fiskerimeldinger</a>");
    retval += this._httpBuilder.createModalIconLine("link", "J-meldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/J-meldinger/Gjeldende-J-meldinger/'>J-meldinger</a>");
    return retval;
};

AndroidBackend.prototype._createCoralReefBottomsheet = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createModalIconLine("highlight_off", "Info", feature._info);
    return retval;
};

AndroidBackend.prototype._showSubsurfaceFacilityBottomsheet = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createModalIconLine("build", "Type", feature._installationType);
    retval += this._httpBuilder.createModalIconLine("settings", "Funksjon", feature._functionality);
    retval += this._httpBuilder.createModalIconLine("get_app", "Dybde", feature._depth);
    retval += this._httpBuilder.createModalIconLine("settings_applications", "Tilhører felt", feature._belongsToField);
    retval += this._httpBuilder.createModalIconLine("bu/siness", "Operatør", feature._operator);
    retval += this._httpBuilder.createModalIconLine("place", "Posisjon", FiskInfoUtility.ddToDms(feature._position[1], feature._position[0]));
    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createModalIconLine("link", "Oljedirektoratets faktasider", feature._oilDirectorateFactPageURL); //TODO: Make it look like URL
    retval += this._httpBuilder.createModalIconLine("link", "Oljedirektoratets kart", feature._oildirectoryMapURL);
    return retval;
};

AndroidBackend.prototype._buildSeismicBottomsheetText = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createModalIconLine("layers", "Område", feature._areaSubheader);
    retval += this._httpBuilder.createModalIconLine("directions_boat", "Seismikkfartøy", feature._seismicVessel);
    retval += this._httpBuilder.createModalIconLine("settings_applications", "Type", feature._operationType);
    retval += this._httpBuilder.createModalIconLine("visibility", "Undertype", feature._underType);
    retval += this._httpBuilder.createModalIconLine("access_time", "Periode", feature.getPeriod());
    retval += this._httpBuilder.createModalIconLine("business", "Ansvarlig selskap", feature._responsibleCompany);
    retval += this._httpBuilder.createModalIconLine("info", "Kilde", feature._sourceType);
    retval += this._httpBuilder.createModalIconLine("settings_input_antenna", "Sensortype", feature._sensorType);
    retval += this._httpBuilder.createModalIconLine("format_list_numbered", "Sensorantall", feature._numberOfSensors);
    retval += this._httpBuilder.createModalIconLine("arrow_forward", "Sensorlengde", feature._sensorLength);
    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createModalIconLine("link", "Oljedirektoratets faktasider", feature._factPage); //TODO: Ma  ke it look like URL
    retval += this._httpBuilder.createModalIconLine("link", "Oljedirektoratets kart", feature._mapUrl);
    return retval;
};
function AndroidBackend() {
    this._token = "";
    this._httpBuilder = new SimpleHtmlBuilder();
}

AndroidBackend.prototype.getToken = function (_callback, that) {
    if (this._token !== "") {
        return this._token;
    }

    this._token = Android.getToken();
    //console.log("Token is : " + this._token);
    if (that !== null) {
        _callback(this._token, that)
    } else {
        _callback(this._token);
    }

    return this._token;
};


// TODO: Create getters and setters / "interface" for feature(s)
AndroidBackend.prototype.showBottmsheet = function (feature) {
    console.time("showBottom Android");
    var body = $("#bottom_sheet_container");
    body.text("");
    body.append(this._httpBuilder.getSelfContainedHeading(4, feature._name));
    this._httpBuilder.clear();
    if (feature._type === BarentswatchApiObjectTypes.AIS) {
        body.append("<h6 class='grey-text grey lighten-5 align-material-c-to-title'>" + feature.getShipTypeString() + "</h6>");
    } else {
        body.append("<h6 class='grey-text grey lighten-5 align-material-c-to-title'>" + feature._norwegianTitle + "</h6>");
    }
    body.append("<br>");
    body.append("<div class='divider'></div>");
    var content = "";

    switch (feature._type) {
        case BarentswatchApiObjectTypes.TOOL:
            console.time("showTool");
            content = this._showToolBottomsheet(feature);
            console.timeEnd("showTool");
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
            console.time("showAIS");
            content = this._createAisBottomsheet(feature);
            console.timeEnd("showAIS");
            break;
        default:
            return null;
    }

    body.append(content);
    var bottomSheet = document.querySelector("#bottom_sheet");
    $('.collapsible').collapsible();
    var instance = M.Modal.getInstance(bottomSheet);
        instance.options.onCloseStart = function() {
        $("#bottom_sheet").scrollTop(0);
    };
    instance.open();
    console.timeEnd("showBottom Android");
};

AndroidBackend.prototype._createIceChartConsentrationContent = function (feature) {

};

AndroidBackend.prototype._showToolBottomsheet = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createTitleLineWithStrongText("Tid i havet", feature.getTimePlacedInOcean());
    retval += this._httpBuilder.createTitleLineWithStrongText("Satt", feature.getFormattedTimeSetInOcean());
    retval += this._httpBuilder.createTitleLineWithStrongText("Posisjon", feature.getCoordinates());
    retval += this._httpBuilder.createTitleLineWithStrongText("Se Marinogram", "<a target='_blank' href='https://www.yr.no/sted/hav/" + feature._position[1] + "_" + feature._position[0] + "'" + ">Marinogram</a>");

    retval += this._httpBuilder.getSelfContainedHeading(6, "Om Eier");
    retval += "<div class='divider'></div>";

    retval += this._httpBuilder.createTitleLineWithStrongText("Fartøy", "<a target='_blank' href='javascript:locateVessel(" + "\"" + feature._vesselname + "\"" + ")'>" + feature._vesselname + "</a>");
    retval += this._httpBuilder.createTitleLineWithStrongText("Telefon", feature._vesselphone);
    retval += this._httpBuilder.createTitleLineWithStrongText("Kallesignal(IRCS)", feature._ircs);
    retval += this._httpBuilder.createTitleLineWithStrongText("MMSI", feature._mmsi);
    retval += this._httpBuilder.createTitleLineWithStrongText("IMO", feature._imo);
    retval += this._httpBuilder.createTitleLineWithStrongText("E-post", feature._vesselemail);

    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createTitleLineWithStrongText("Fiskerimeldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/Fiskerimeldinger'>Fiskerimeldinger</a>");
    retval += this._httpBuilder.createTitleLineWithStrongText("J-meldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/J-meldinger/Gjeldende-J-meldinger/'>J-meldinger</a>");
    return retval;
};

AndroidBackend.prototype._createAisBottomsheet = function (feature) {
    console.time("createAISBottomsheet function");
    var retval = "";
    console.time("temp01");
    retval += this._httpBuilder.createTitleLineWithStrongText("Fart", (feature._sog + " knop"));
    console.timeEnd("temp01");
    console.time("temp02");
    retval += this._httpBuilder.createTitleLineWithStrongText("Kurs", (feature._cog + "\xB0"));
    console.timeEnd("temp02");
    console.time("temp03");
    retval += this._httpBuilder.createTitleLineWithStrongText("Posisjon", feature.getCoordinates());
    console.timeEnd("temp03");
    console.time("temp04");
    retval += this._httpBuilder.createTitleLineWithStrongText("Signal mottatt", feature.getFormattedDate());
    console.timeEnd("temp04");
    console.time("temp05");
    retval += this._httpBuilder.createTitleLineWithStrongText("Destinasjon", feature._destination);
    console.timeEnd("temp05");
    console.time("temp06");
    retval += this._httpBuilder.createTitleLineWithStrongText("Se Marinogram", "<a target='_blank' href='https://www.yr.no/sted/hav/" + feature._internalPosition[1] + "_" + feature._internalPosition[0] + "'" + ">Marinogram</a>");
    console.timeEnd("temp06");
    console.time("temp07");

    retval += this._httpBuilder.getSelfContainedHeading(6, "Redskap");
    console.timeEnd("temp07");
    console.time("temp08");

    //TODO: FIXME: DONT EVER DO THISS!!!
    if (aisSearchModule.getVessel(feature._name).hasOwnProperty("tools")) {
        // Showing only the first 3 tools - showing all can crash the app and make the UI unusable
        var allTools = aisSearchModule.getVessel(feature._name).tools;
        var someTools = [];
        var i;
        for (i = 0; (i < allTools.length) && (i < 3); i++) {
            someTools[i] = allTools[i];
        }
        retval += this._httpBuilder.buildCollapsible(someTools);
        if (someTools.length < allTools.length) {
            retval += this._httpBuilder.createTitleLineWithStrongText("Ikke vist", allTools.length-someTools.length.toString() + " ytteligere redskap");
        }
    }
    console.timeEnd("temp08");
    console.time("temp09");

    //  if(aisSearchModule.getVessel(feature._name).hasOwnProperty("tools")) {
    //      retval += this._httpBuilder.buildCollectionWithHeaderAndLinks("Mine redskaper", aisSearchModule.getVessel(feature._name), "");
    //  }

    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    console.timeEnd("temp09");
    console.time("temp10");
    retval += "<div class='divider'></div>";
    console.timeEnd("temp10");
    console.time("temp11");
    retval += this._httpBuilder.createTitleLineWithStrongText("Fiskerimeldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/Fiskerimeldinger'>Fiskerimeldinger</a>");
    console.timeEnd("temp11");
    console.time("temp12");
    retval += this._httpBuilder.createTitleLineWithStrongText("J-meldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/J-meldinger/Gjeldende-J-meldinger/'>J-meldinger</a>");
    console.timeEnd("temp12");
    console.timeEnd("createAISBottomsheet function");
    return retval;
};

AndroidBackend.prototype._createJMessageBottomsheetContent = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createTitleLineWithStrongText("Stengt fra dato", feature._closedDate);
    retval += this._httpBuilder.createTitleLineWithStrongText("Stengt for", feature._closedFor);
    retval += this._httpBuilder.createTitleLineWithStrongText("Fiskegruppe", feature._fishingGroup);
    retval += this._httpBuilder.createTitleLineWithStrongText("Område", feature._area);
    retval += this._httpBuilder.createTitleLineWithStrongText("J-melding", feature._jmessageName);
    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createTitleLineWithStrongText("Fiskerimeldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/Fiskerimeldinger'>Fiskerimeldinger</a>");
    retval += this._httpBuilder.createTitleLineWithStrongText("J-meldinger", "<a target='_blank' href='https://www.fiskeridir.no/Yrkesfiske/Regelverk-og-reguleringer/J-meldinger/Gjeldende-J-meldinger/'>J-meldinger</a>");
    return retval;
};

AndroidBackend.prototype._createCoralReefBottomsheet = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createModalIconLine("highlight_off", "Info", feature._info);
    return retval;
};

AndroidBackend.prototype._showSubsurfaceFacilityBottomsheet = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createTitleLineWithStrongText("Type", feature._installationType);
    retval += this._httpBuilder.createTitleLineWithStrongText("Funksjon", feature._functionality);
    retval += this._httpBuilder.createTitleLineWithStrongText("Dybde", feature._depth);
    retval += this._httpBuilder.createTitleLineWithStrongText("Tilhører felt", feature._belongsToField);
    retval += this._httpBuilder.createTitleLineWithStrongText("Operatør", feature._operator);
    retval += this._httpBuilder.createTitleLineWithStrongText("Posisjon", feature.getCoordinates());
    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createTitleLineWithStrongText("Oljedirektoratets faktasider", feature._oilDirectorateFactPageURL); //TODO: Make it look like URL
    retval += this._httpBuilder.createTitleLineWithStrongText("Oljedirektoratets kart", feature._oildirectoryMapURL);
    return retval;
};

AndroidBackend.prototype._buildSeismicBottomsheetText = function (feature) {
    var retval = "";
    retval += this._httpBuilder.createTitleLineWithStrongText("Område", feature._areaSubheader);
    retval += this._httpBuilder.createTitleLineWithStrongText("Seismikkfartøy", feature._seismicVessel);
    retval += this._httpBuilder.createTitleLineWithStrongText("Type", feature._operationType);
    retval += this._httpBuilder.createTitleLineWithStrongText("Undertype", feature._underType);
    retval += this._httpBuilder.createTitleLineWithStrongText("Periode", feature.getPeriod());
    retval += this._httpBuilder.createTitleLineWithStrongText("Ansvarlig selskap", feature._responsibleCompany);
    retval += this._httpBuilder.createTitleLineWithStrongText("Kilde", feature._sourceType);
    retval += this._httpBuilder.createTitleLineWithStrongText("Sensortype", feature._sensorType);
    retval += this._httpBuilder.createTitleLineWithStrongText("Sensorantall", feature._numberOfSensors);
    retval += this._httpBuilder.createTitleLineWithStrongText("Sensorlengde", feature._sensorLength);
    retval += this._httpBuilder.getSelfContainedHeading(6, "MER INFO");
    retval += "<div class='divider'></div>";
    retval += this._httpBuilder.createTitleLineWithStrongText("Oljedirektoratets faktasider", feature._factPage); //TODO: Ma  ke it look like URL
    retval += this._httpBuilder.createTitleLineWithStrongText("Oljedirektoratets kart", feature._mapUrl);
    return retval;
};
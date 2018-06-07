class BarentswatchLayersTranslator { // TODO IMPLEMENT THIS AS A JSON OBJECT WITH MULTIPLE STRINGS PER field
    constructor(languageCode) { // nb_NO
        // TODO: Implement language code to support different languages
        this._norwegianBasemap = "Norges grunnkart";
        this._ais = "Ais";
        this._waveForcast = "Bølgevarsel";
        this._iceEdge = "Iskant";
        this._seaBottomInstallation = "Havbunninstallasjon";
        this._coastlinesCod = "Fjordlinjer - kysttorsk";
        this._coralReef = "Forbudsområde - Korallrev";
        this._iceConsentration = "Iskonsentrasjon";
        this._jMessage = "J-melding";
        this._ongoingSeismic = "Pågående seismikk";
        this._plannedSeismic = "Planlagt seismikk";
        this._coastalcodregulations = "Stengte felt";
        this._tools = "Redskaper";
    }

    translateFromLayerToSaneName(layerName) {
        switch (layerName) {
            case "Norges grunnkart":
                return this._norwegianBasemap;
            case "bw:waveforecast_area_iso_latest":
                return this._waveForcast;
            case "bwdev:iceedge_latest":
                return this._iceEdge;
            case "icechart":
                return this._iceConsentration;
            case "npdsurveyongoing":
                return this._ongoingSeismic;
            case "npdsurveyplanned":
                return this._plannedSeismic;
            case "npdfacility":
                return this._seaBottomInstallation;
            case "jmelding":
                return this._jMessage;
            case "coastalcodregulations":
                return this._coastalcodregulations;
            case "coralreef":
                return this._coralReef;
            case "Tools-nets":
                return this._tools;
            case "Tools-crabpot":
                return this._tools;
            case "Tools-mooring":
                return this._tools;
            case "Tools-longLine":
                return this._tools;
            case "Tools-danishPurseSeine":
                return this._tools;
            case "Tools-sensorcables":
                return this._tools;
            case "Tools-unknown":
                return this._tools;
            case "AIS":
                return this._ais;
            default:
                return "Invalid input layer: " + layerName;
        }
    }

    translateFromSaneNameToLayername(saneName) {
        switch (saneName) {
            case this._norwegianBasemap:
                return "Norges grunnkart";
            case this._waveForcast:
                return "bw:waveforecast_area_iso_latest";
            case this._iceEdge:
                return "bwdev:iceedge_latest";
            case this._iceConsentration:
                return "icechart";
            case this._ongoingSeismic:
                return "npdsurveyongoing";
            case this._plannedSeismic:
                return "npdsurveyplanned";
            case this._seaBottomInstallation:
                return "npdfacility";
            case this._jMessage:
                return "jmelding";
            case this._coastalcodregulations:
                return "coastalcodregulations";
            case this._coralReef:
                return "coralreef";
            case this._tools:
                return ["Tools-nets", "Tools-crabpot", "Tools-mooring", "Tools-longLine", "Tools-danishPurseSeine", "Tools-sensorcables", "Tools-unknown"];
            case this._ais:
                return "AIS";
            default:
                console.log("Invalid sane name: " + saneName);
        }
    }


}
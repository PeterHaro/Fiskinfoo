function errorBox(errorMessage) {
    var html = "<div class=\"error\">";
    html += "<div class=\"error-icon\"><i class=\"fas fa-exclamation-triangle\" ></i></div>";
    html += errorMessage;
    html += "</div>";
    return html;
}

function getPositionFromGeometry(geometry) {
    return ol.proj.transform(ol.extent.getCenter(geometry.getExtent()), 'EPSG:3857', 'EPSG:4326');
}

function nFormatter(num, digits) {
    var si = [
        { value: 1, symbol: "" },
        { value: 1E3, symbol: "k" },
        { value: 1E6, symbol: "M" },
        { value: 1E9, symbol: "G" },
        { value: 1E12, symbol: "T" },
        { value: 1E15, symbol: "P" },
        { value: 1E18, symbol: "E" }
    ];
    var rx = /\.0+$|(\.[0-9]*[1-9])0+$/;
    var i;
    for (i = si.length - 1; i > 0; i--) {
        if (num >= si[i].value) {
            break;
        }
    }
    return (num / si[i].value).toFixed(digits).replace(rx, "$1") + si[i].symbol;
}

function degreesToRadians(degrees) {
    var pi = Math.PI;
    return parseFloat(degrees) * (pi / 180);
}

function marinogramError() {
    var marinogramDiv = document.getElementById("marinogram");
    if (marinogramDiv == null) return;
    marinogramDiv.innerHTML = errorBox("Marinogram er ikke tilgjengelig for valg punkt.")
}

function unsetSelectedFeature()  {
    selectedFeature.removeText();
    selectedFeature = null;
}

function getMaxThreeToolsFromCallSign(callsign) {
    if (vesselMap[callsign] === undefined) return [];
    if (vesselMap[callsign].tools === undefined) return [];
    return vesselMap[callsign].tools.slice(0, 3);
}

function vesselCodeToShipTypeName(record) {
    var number = record.get("ShipType");
    switch (number) {
        case 30: return "Fiskefartøy";
        case 31:
        case 32: return "Taubåt";
        case 33: return "Mudringsfartøy";
        case 34: return "Dykkerfartøy";
        case 35: return "Militært fartøy";
        case 36: return "Seilbåt";
        case 37: return "Fritidsbåt";
        case 51: return "Søk og redningsfartøy";
        default:
            return "Ukjent type";
    }
}

function landCodeToCountryName(value) {
    var countryName = "";
    var landCodeArray = value.split(";");
    for (var i = 0; i < landCodeArray.length; i++) {
        switch(landCodeArray[i].toLowerCase()) {
            case "is":
                countryName += "Island, ";
                break;
            case "no":
                countryName += "Norge, ";
                break;
            case "gb":
                countryName += "Storbritannia, ";
                break;
            case "gl":
                countryName += "Grønland, ";
                break;
            case "dk":
                countryName += "Danmark, ";
                break;
            case "sj":
                countryName += "Svalberg og Jan Mayen, ";
                break;
            case "ru":
                countryName += "Russland, ";
                break;
        }
    }

    if (countryName.length > 2)
        countryName = countryName.substring(0, countryName.length - 2);

    return countryName;
}

function formatDateDifference(dateString) {
    var date = new Date(dateString);
    var totalSeconds = (new Date().getTime() - date.getTime())/1000;
    var days = Math.floor(totalSeconds/(3600*24));
    totalSeconds -= days*3600*24;
    var hours = Math.floor(totalSeconds/3600);
    totalSeconds -= hours*3600;
    var minutes = Math.round(totalSeconds/60);
    return days + " døgn, " + hours + " timer, " + minutes + " minutter";
}

function missingIfNull(value) {
    return !value ? "Mangler" : value;
}

function formattedDate(dateString) {
    if (dateString == null) return "Mangler";

    var months = ['januar', 'februar', 'mars', 'april', 'mai', 'juni', 'juli', 'august', 'september', 'oktober', 'november', 'desember'];
    var date = new Date(dateString);
    var year = date.getFullYear();
    var dayOfMonth = date.getDate();
    var month = months[date.getMonth()];
    return dayOfMonth + ". " + month + " " + year;
}

function formattedTimePeriod(fromDate, toDate) {
    return formattedDate(fromDate) + " - " + formattedDate(toDate);
}

function formatLocation(coordinates) {
    var lon = coordinates[0];
    var lat = coordinates[1];
    var convertLat = Math.abs(lat);
    var latDegree = Math.floor(convertLat);
    var latMinutes = Math.floor(((convertLat - latDegree) * 60)*1000)/1000;
    var latCardinal = ((lat > 0) ? "N" : "S");

    var convertLon = Math.abs(lon);
    var lonDegree = Math.floor(convertLon);
    var lonMinute = Math.floor(((convertLon - lonDegree) * 60)*1000)/1000;
    var lonCardinal = ((lon > 0) ? "E" : "W");
    return latDegree + "° " + latMinutes + " " + latCardinal + " " + lonDegree  + "° " + lonMinute + " " + lonCardinal;
}

function marinogramLink(coordinate) {
    var lon = coordinate[0];
    var lat = coordinate[1];
    var link = "<a href=\"https://www.yr.no/sted/Hav/";
    link += lat + "_" + lon;
    link += "\">Se marinogram fra yr.no</a>";
    return link;
}

function showVesselLink(ircs, vesselName) {
    return "<a target='_blank' href='javascript:showVesselAndBottomsheet(" + "\"" + ircs + "\"" + ")'>" + vesselName + "</a>";
}

function formatToolType(toolTypeCode) {
    if (toolTypeCode)
        toolTypeCode = toolTypeCode.toLowerCase();
    switch (toolTypeCode) {
        case "mooring": return "Fortøyningssystem";
        case "longline": return "Line";
        case "crabpot": return "Teine";
        case "nets": return "Garn";
        case "sensorcable": return "Sensor / kabel";
        case "danpurseine": return "Snurpenot";
        case "unk":
        default:
            return "Ukjent redskap";
    }
}
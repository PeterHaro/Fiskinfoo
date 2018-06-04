var FiskInfoUtility = function () {
    "use strict";

    /**
     * Make a X-Domain request to url and callback.
     *
     * @param url {String}
     * @param method {String} HTTP verb ("GET", "POST", "DELETE", etc.)
     * @param data {String} request body
     * @param callback {Function} to callback on completion
     * @param errback {Function} to callback on error
     */
    function corsRequest(url, method, data, callback, errback, authToken) {
        var req;
        var token = null;
        if (authToken !== null) {
            if (typeof authToken !== "undefined") {
                token = authToken;
            }
        }
        if (XMLHttpRequest) {
            req = new XMLHttpRequest();
            if ("withCredentials" in req) {
                req.open(method, url, true);
                req.setRequestHeader("Authorization",
                    "Bearer " + token);
                req.onerror = errback;
                req.onreadystatechange = function () {
                    if (req.readyState === 4) {
                        if (req.status >= 200 && req.status < 400) {
                            callback(req.responseText);
                        } else {
                            errback(new Error("Response returned with non-OK status " + req.status));
                        }
                    }
                };
                req.send(data);
            }
        } else if (XDomainRequest) {
            req = new XDomainRequest();
            req.open(method, url);
            req.onerror = errback;
            req.onload = function () {
                callback(req.responseText);
            };
            req.send(data);
        } else {
            errback(new Error("CORS not supported"));
        }
    }

    function ddToDms(lat, lng) {
        var lat = lat;
        var lng = lng;
        var latResult, lngResult, dmsResult;
        lat = parseFloat(lat);
        lng = parseFloat(lng);
        latResult = (lat >= 0) ? 'N' : 'S';

        latResult += _getDms(lat);
        lngResult = (lng >= 0) ? 'E' : 'W';
        lngResult += _getDms(lng);

        dmsResult = latResult + ' ' + lngResult;
        return dmsResult;
    }

    function _getDms(val) {
        var valDeg, valMin, valSec, result;
        val = Math.abs(val);
        valDeg = Math.floor(val);
        result = valDeg + "º";

        valMin = Math.floor((val - valDeg) * 60);
        result += valMin + "'";

        valSec = Math.round((val - valDeg - valMin / 60) * 3600 * 1000) / 1000;
        result += valSec + '"';

        return result;
    }

    function formatDate(date) {
        var monthNames = [
            "Januar", "Februar", "Mars",
            "April", "Mai", "Juni", "Juli",
            "August", "September", "Oktober",
            "November", "Desember"
        ];

        var day = date.getDate();
        var monthIndex = date.getMonth();
        var year = date.getFullYear();

        return day + '.' + monthNames[monthIndex] + ' ' + year;
    }

    var httpClient = {
        get: function (url, data, callback) {
            var xhr = new XMLHttpRequest();

            xhr.onreadystatechange = function () {
                var readyState = xhr.readyState;

                if (readyState == 4) {
                    callback(xhr);
                }
            };

            var queryString = '';
            if (typeof data === 'object') {
                for (var propertyName in data) {
                    queryString += (queryString.length === 0 ? '' : '&') + propertyName + '=' + encodeURIComponent(data[propertyName]);
                }
            }

            if (queryString.length !== 0) {
                url += (url.indexOf('?') === -1 ? '?' : '&') + queryString;
            }
            xhr.open('GET', url, true);
            xhr.send(null);
        },

        synchronized_get: function (url, data, callback) {
            var xhr = new XMLHttpRequest();

            xhr.onreadystatechange = function () {
                var readyState = xhr.readyState;

                if (readyState == 4) {
                    callback(xhr);
                }
            };

            var queryString = '';
            if (typeof data === 'object') {
                for (var propertyName in data) {
                    queryString += (queryString.length === 0 ? '' : '&') + propertyName + '=' + encodeURIComponent(data[propertyName]);
                }
            }

            if (queryString.length !== 0) {
                url += (url.indexOf('?') === -1 ? '?' : '&') + queryString;
            }

            xhr.open('GET', url, false);
            xhr.send(null);
        },

        post: function (url, data, callback) {
            var xhr = new XMLHttpRequest();

            xhr.onreadystatechange = function () {
                var readyState = xhr.readyState;

                if (readyState == 4) {
                    callback(xhr);
                }
            };

            var queryString = '';
            if (typeof data === 'object') {
                for (var propertyName in data) {
                    queryString += (queryString.length === 0 ? '' : '&') + propertyName + '=' + encodeURIComponent(data[propertyName]);
                }
            }

            xhr.open('POST', url, true);
            xhr.setRequestHeader('content-type', 'application/x-www-form-urlencoded');
            xhr.send(queryString);
        }
    };

    return {
        corsRequest: corsRequest,
        formatDate: formatDate,
        ddToDms: ddToDms,
        httpClient: httpClient
    }
}();

var selectedFeature;

var tokenApiURL = "https://www.barentswatch.no/api/token";
var token = Android.getToken();
var authenticator = Sintium.OAuthAuthenticator(tokenApiURL, token);

var unrollAtZoom = 12;

vesselMap = {};
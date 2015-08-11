package fiskinfoo.no.sintef.fiskinfoo.Http;

/**
 * OK HTTPLIB
 */
public class BarentswatchApi {
    private static final String BARENTSWATCH_API_AUTH_URL = "https://www.barentswatch.no/api/token";
    private static final String BARENTSWATCH_API_BASE_URL = "https://www.barentswatch.no/api/v1/geodata/";
    private static final String BARENTSWATCH_API_SUBSCRIBABLE_NORWEGIAN = "subscribable/";
    private static final String BARENTSWATCH_API_SUBSCRIBABLE_ENGLISH = "subscribable/?lang={en}";
    private static final String BARENTSWATCH_API_SUBSCRIPTION = "PropertyDescription/";
    private static String accessToken;
    private static boolean isAuthenticated;  //Probably remove this

    public BarentswatchApi(String accessToken) {
        this.accessToken = accessToken;
        isAuthenticated = true;
    }

    public BarentswatchApi() {
        isAuthenticated = false;
    }


}

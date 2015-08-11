package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

public class BarentswatchApi {
    private static final String BARENTSWATCH_API_ENDPOINT = "https://www.barentswatch.no/api/v1/geodata";
    private static String accessToken;
    private final IBarentswatchApi barentswatchApi;

    /**
     * Oauth2 interceptor, adds the header token to every request
     */
    //TODO: If we add more external API's which requires OAuth2 authentication, this can be moved to a "httplib", as it is generic in reality
    private class BarentswatchAuthenticationInterceptor implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
            if (accessToken != null) {
                request.addHeader("Authorization", "Bearer " + accessToken);
            }
        }
    }

    private IBarentswatchApi initializeBarentswatchAPI(Executor httpExecutor, Executor callbackExecutor) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setExecutors(httpExecutor, callbackExecutor)
                .setEndpoint(BARENTSWATCH_API_ENDPOINT)
                .setRequestInterceptor(new BarentswatchAuthenticationInterceptor())
                .build();
        return restAdapter.create(IBarentswatchApi.class);
    }

    public BarentswatchApi(Executor httpExecutor, Executor callbackExecutor) {
        barentswatchApi = initializeBarentswatchAPI(httpExecutor, callbackExecutor);
    }

    public BarentswatchApi() {
        Executor httpExecutor = Executors.newSingleThreadExecutor();
        MainThreadExecutor callbackExecutor = new MainThreadExecutor();
        barentswatchApi = initializeBarentswatchAPI(httpExecutor, callbackExecutor);
    }

    public BarentswatchApi setAccesToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public IBarentswatchApi getApi() {
        return barentswatchApi;
    }
}

package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

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

    public static Request getRequestForAuthentication(String mEmail, String mPassword) {
        final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("grant_type", "password")
                .add("username", mEmail)
                .add("password", mPassword)
                .build();
         return new Request.Builder()
                .url("https://www.barentswatch.no/api/token")
                .header("content-type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

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

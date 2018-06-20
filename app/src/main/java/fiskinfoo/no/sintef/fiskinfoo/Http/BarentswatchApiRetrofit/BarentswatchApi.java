/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

import android.os.Environment;
import android.util.JsonReader;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

public class BarentswatchApi {
    private final static String barentsWatchPilotAddress = "https://pilot.barentswatch.net";
    private final static String barentsWatchProdAddress = "https://www.barentswatch.no";
    private static String currentPath = barentsWatchProdAddress;
    private static String BARENTSWATCH_API_ENDPOINT = currentPath + "/api/v1/geodata";
    private static String accessToken;
    private final IBarentswatchApi barentswatchApi;
    private boolean targetProd = true;

    /**
     * Oauth2 interceptor, adds the header token to every request
     */
    //TODO: If we add more external API's which requires OAuth2 authentication, this can be moved to a "httplib", as it is generic in reality
    private class BarentswatchAuthenticationInterceptor implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
            if (accessToken != null) {
                request.addHeader("Authorization", "Bearer " + accessToken);
                request.addHeader("User-Agent", "FiskInfo/2.0 (Android)");
            }
        }
    }

    public Request getRequestForAuthentication(String mEmail, String mPassword) {
        final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("grant_type", "password")
                .add("username", mEmail)
                .add("password", mPassword)
                .build();
         return new Request.Builder()
                .url(currentPath + "/api/token")
                .header("content-type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();
    }

    public Request getRequestForAuthenticationClientCredentialsFlow(String mEmail, String mPassword) {
        final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("grant_type", "client_credentials")
                .add("client_id", mEmail)
                .add("client_secret", mPassword)
                .build();
        return new Request.Builder()
                .url(currentPath + "/api/token")
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
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        String fileName = directoryPath + "/FiskInfo/api_setting.json";
        File file = new File(fileName);
        String environment = null;

        if(file.exists()) {
            InputStream inputStream;
            InputStreamReader streamReader;
            JsonReader jsonReader;

            try {
                inputStream = new BufferedInputStream(new FileInputStream(file));
                streamReader = new InputStreamReader(inputStream, "UTF-8");
                jsonReader = new JsonReader(streamReader);

                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    if (name.equals("environment")) {
                        environment = jsonReader.nextString();
                    } else {
                        jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        targetProd = !"pilot".equals(environment);
        currentPath = targetProd ? barentsWatchProdAddress : barentsWatchPilotAddress;
        BARENTSWATCH_API_ENDPOINT = currentPath + "/api/v1/geodata";

        Executor httpExecutor = Executors.newSingleThreadExecutor();
        MainThreadExecutor callbackExecutor = new MainThreadExecutor();
        barentswatchApi = initializeBarentswatchAPI(httpExecutor, callbackExecutor);
    }

    public BarentswatchApi setAccesToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public void usePilotApi(boolean usePilot) {
        currentPath = usePilot ? barentsWatchPilotAddress : barentsWatchProdAddress;
        BARENTSWATCH_API_ENDPOINT = currentPath + "/api/v1/geodata";
    }

    public IBarentswatchApi getApi() {
        return barentswatchApi;
    }

    public boolean isTargetProd() {
        return targetProd;
    }
}

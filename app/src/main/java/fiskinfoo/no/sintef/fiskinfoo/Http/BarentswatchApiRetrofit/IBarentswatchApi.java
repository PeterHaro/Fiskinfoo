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

import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.SubscriptionSubmitObject;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface IBarentswatchApi {
    String subscribable = "/service/subscribable/";
    String geoDataSubscription = "/subscription/";
    String geoDataSubscriptionManagement = "/subscription/{Id}";
    String geoDataDownload = "/download/{ApiName}";
    String authorization = "/authorization";

    @GET(subscribable)
    void getSubscribable(Callback<List<PropertyDescription>> callback);

    @GET(subscribable)
    List<PropertyDescription> getSubscribable();

    @GET(geoDataSubscription)
    void getSubscriptions(Callback<List<Subscription>> callback);

    @GET(geoDataSubscription)
    List<Subscription> getSubscriptions();

    @POST(geoDataSubscription)
    void setSubscription(Callback<Subscription> callback);

    @POST(geoDataSubscription)
    Subscription setSubscription(@Body SubscriptionSubmitObject subscription);

    @PUT(geoDataSubscriptionManagement)
    void updateSubscription(Callback<Subscription> callback);

    @PUT(geoDataSubscriptionManagement)
    Subscription updateSubscription(@Path("Id")String id, @Body SubscriptionSubmitObject subscription);

    @DELETE(geoDataSubscriptionManagement)
    void deleteSubscription(Callback<Subscription> callback);

    @DELETE(geoDataSubscriptionManagement)
    Response deleteSubscription(@Path("Id")String id);

    @GET(geoDataDownload) //TODO: ASYNC
    Response geoDataDownload(@Path("ApiName")String apiName, @Query("format") String format);

    @GET(authorization)
    List<Authorization> getAuthorization();

}

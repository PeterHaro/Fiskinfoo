package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface IBarentswatchApi {
    String subscribable = "/service/subscribable/";
    String geoDataSubscription = "/subscription/";
    String geoDataDownload = "/download/{ApiName}";

    @GET(subscribable)
    void getSubscribable(Callback<List<PropertyDescription>> callback);

    @GET(subscribable)
    List<PropertyDescription> getSubscribable();

    @GET(geoDataSubscription)
    void getSubscriptions(Callback<List<Subscription>> callback);

    @GET(geoDataSubscription)
    List<Subscription> getSubscriptions();

    @GET(geoDataDownload) //TODO: Figure out how to do this async
    Response geoDataDownload(@Path("ApiName")String apiName, @Query("format") String format);

}

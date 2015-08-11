package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import retrofit.Callback;
import retrofit.http.GET;

public interface IBarentswatchApi {
    String subscribable = "/service/subscribable/";
    String geoDataSubscription = "/subscription/";
    String geoDataDownload = "/download/{ApiName}?format={Format}";

    @GET(subscribable)
    List<PropertyDescription> getSubscribable(Callback<List<PropertyDescription>> callback);

    @GET(geoDataSubscription)
    List<Subscription> getSubscriptions(Callback<List<Subscription>> callback);

    //@Get(geoDataDownload)
    // TODO: Add bw:file object and such
    //File<T> geoDataDownload(@Path("ApiName")String apiName, @Path("Format") String format

}

package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

import retrofit.Callback;
import retrofit.RetrofitError;

public abstract class BarentswatchApiCallback<T> implements Callback<T> {
    public abstract void failure(BarentswatchApiError error);

    @Override
    public void failure(RetrofitError error) {
        failure(BarentswatchApiError.fromRetrofitError(error));
    }
}

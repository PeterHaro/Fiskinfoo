package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.ErrorDetails;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.ErrorResponse;
import retrofit.RetrofitError;

public class BarentswatchApiError extends Exception{
    private final RetrofitError retrofitError;
    private final ErrorDetails errorDetails;

    public static BarentswatchApiError fromRetrofitError(RetrofitError error) {
        ErrorResponse errorResponse = (ErrorResponse) error.getBodyAs(ErrorResponse.class);
        if (errorResponse != null && errorResponse.error != null) {
            String message = errorResponse.error.status + " " + errorResponse.error.message;
            return new BarentswatchApiError(error, errorResponse.error, message);
        } else {
            return new BarentswatchApiError(error);
        }
    }

    public BarentswatchApiError(RetrofitError retrofitError, ErrorDetails errorDetails, String message) {
        super(message, retrofitError);
        this.retrofitError = retrofitError;
        this.errorDetails = errorDetails;
    }

    public BarentswatchApiError(RetrofitError retrofitError) {
        super(retrofitError);
        this.retrofitError = retrofitError;
        errorDetails = null;
    }

    public RetrofitError getRetrofitError() {
        return retrofitError;
    }

    public boolean hasErrorDetails() {
        return errorDetails != null;
    }

    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }
}

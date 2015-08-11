package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Populated from the barentswatch API when an error occurs
 */
public class ErrorResponse implements Parcelable{
    public ErrorDetails error;

    public ErrorResponse() {

    }

    protected ErrorResponse(Parcel in) {
        error = in.readParcelable(ErrorDetails.class.getClassLoader());
    }

    public static final Creator<ErrorResponse> CREATOR = new Creator<ErrorResponse>() {
        @Override
        public ErrorResponse createFromParcel(Parcel in) {
            return new ErrorResponse(in);
        }

        @Override
        public ErrorResponse[] newArray(int size) {
            return new ErrorResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(error, flags);
    }
}

package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is a wrapper class for the RetrofitError class to extract the error message itself.
 */
public class ErrorDetails implements Parcelable{
    public int status;
    public String message;

    public ErrorDetails() {

    }

    protected ErrorDetails(Parcel in) {
        status = in.readInt();
        message = in.readString();
    }

    public static final Creator<ErrorDetails> CREATOR = new Creator<ErrorDetails>() {
        @Override
        public ErrorDetails createFromParcel(Parcel in) {
            return new ErrorDetails(in);
        }

        @Override
        public ErrorDetails[] newArray(int size) {
            return new ErrorDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(message);
    }
}

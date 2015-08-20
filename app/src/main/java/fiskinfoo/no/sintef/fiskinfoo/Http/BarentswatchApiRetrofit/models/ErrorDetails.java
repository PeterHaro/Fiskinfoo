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

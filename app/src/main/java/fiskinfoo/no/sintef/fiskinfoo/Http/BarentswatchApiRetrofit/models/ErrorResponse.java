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

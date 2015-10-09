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

// Note: Naming convention changed to fit API
public class SubscriptionSubmitObject implements Parcelable{
    public String GeoDataServiceName;
    public String FileFormatType;
    public String UserEmail;
    public String SubscriptionEmail;
    public String SubscriptionIntervalName;

    protected SubscriptionSubmitObject(Parcel in) {
        GeoDataServiceName = in.readString();
        FileFormatType = in.readString();
        UserEmail = in.readString();
        SubscriptionEmail = in.readString();
        SubscriptionIntervalName = in.readString();
    }

    public SubscriptionSubmitObject(String geoDataServiceName, String fileFormatType, String userEmail, String subscriptionEmail, String subscriptionIntervalName) {
        GeoDataServiceName = geoDataServiceName;
        FileFormatType = fileFormatType;
        UserEmail = userEmail;
        SubscriptionEmail = subscriptionEmail;
        SubscriptionIntervalName = subscriptionIntervalName;
    }

    public static final Creator<SubscriptionSubmitObject> CREATOR = new Creator<SubscriptionSubmitObject>() {
        @Override
        public SubscriptionSubmitObject createFromParcel(Parcel in) {
            return new SubscriptionSubmitObject(in);
        }

        @Override
        public SubscriptionSubmitObject[] newArray(int size) {
            return new SubscriptionSubmitObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(GeoDataServiceName);
        dest.writeString(FileFormatType);
        dest.writeString(UserEmail);
        dest.writeString(SubscriptionEmail);
        dest.writeString(SubscriptionIntervalName);
    }
}

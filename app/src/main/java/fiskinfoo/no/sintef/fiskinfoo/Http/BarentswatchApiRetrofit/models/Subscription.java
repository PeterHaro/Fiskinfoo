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

public class Subscription implements Parcelable{
    public int Id;
    public String GeoDataServiceName;
    public String FileFormatType;
    public String UserEmail;
    public String SubscriptionEmail;
    public String SubscriptionIntervalName;
    public String Created;
    public String LastModified;

    protected Subscription(Parcel in) {
        Id = in.readInt();
        GeoDataServiceName = in.readString();
        FileFormatType = in.readString();
        UserEmail = in.readString();
        SubscriptionEmail = in.readString();
        SubscriptionIntervalName = in.readString();
        Created = in.readString();
        LastModified = in.readString();
    }

    public static final Creator<Subscription> CREATOR = new Creator<Subscription>() {
        @Override
        public Subscription createFromParcel(Parcel in) {
            return new Subscription(in);
        }

        @Override
        public Subscription[] newArray(int size) {
            return new Subscription[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(GeoDataServiceName);
        dest.writeString(FileFormatType);
        dest.writeString(UserEmail);
        dest.writeString(SubscriptionEmail);
        dest.writeString(SubscriptionIntervalName);
        dest.writeString(Created);
        dest.writeString(LastModified);
    }
}

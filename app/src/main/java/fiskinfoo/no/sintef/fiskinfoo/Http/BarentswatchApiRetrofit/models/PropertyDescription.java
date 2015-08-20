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
public class PropertyDescription implements Parcelable{
    public int Id;
    public int ServiceTypeId;
    public String Name;
    public String LayerName;
    public String ApiName;
    public String UpdateFrequencyText;
    public String Description;
    public String LongDescription;
    public String DataOwner;
    public String DataOwnerLink;
    public boolean Status;
    public String[] Formats;
    public String[] SubscriptionInterval;
    public String Created;
    public String LastUpdated;
    public String Role;
    public String ErrorType;
    public String ErrorText;

    protected PropertyDescription(Parcel in) {
        Id = in.readInt();
        ServiceTypeId = in.readInt();
        Name = in.readString();
        LayerName = in.readString();
        ApiName = in.readString();
        UpdateFrequencyText = in.readString();
        Description = in.readString();
        LongDescription = in.readString();
        DataOwner = in.readString();
        DataOwnerLink = in.readString();
        Status = in.readByte() != 0;
        Formats = in.createStringArray();
        SubscriptionInterval = in.createStringArray();
        Created = in.readString();
        LastUpdated = in.readString();
        Role = in.readString();
        ErrorType = in.readString();
        ErrorText = in.readString();
    }

    public static final Creator<PropertyDescription> CREATOR = new Creator<PropertyDescription>() {
        @Override
        public PropertyDescription createFromParcel(Parcel in) {
            return new PropertyDescription(in);
        }

        @Override
        public PropertyDescription[] newArray(int size) {
            return new PropertyDescription[size];
        }
    };

    public String getFormatsAsString() {
        StringBuilder sb = new StringBuilder();
        for (String format:
             Formats) {
            sb.append(format);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public String getSubscriptionIntervalAsString() {
        StringBuilder sb = new StringBuilder();
        for (String interval:
                SubscriptionInterval) {
            sb.append(interval);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(ServiceTypeId);
        dest.writeString(Name);
        dest.writeString(LayerName);
        dest.writeString(ApiName);
        dest.writeString(UpdateFrequencyText);
        dest.writeString(Description);
        dest.writeString(LongDescription);
        dest.writeString(DataOwner);
        dest.writeString(DataOwnerLink);
        dest.writeByte((byte) (Status ? 1 : 0));
        dest.writeStringArray(Formats);
        dest.writeStringArray(SubscriptionInterval);
        dest.writeString(Created);
        dest.writeString(LastUpdated);
        dest.writeString(Role);
        dest.writeString(ErrorType);
        dest.writeString(ErrorText);
    }
}

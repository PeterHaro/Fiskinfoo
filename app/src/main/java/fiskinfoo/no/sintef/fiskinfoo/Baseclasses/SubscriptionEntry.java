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

package fiskinfoo.no.sintef.fiskinfoo.Baseclasses;

import android.os.Parcel;
import android.os.Parcelable;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;

public class SubscriptionEntry implements Parcelable {
    public String mName;
    public String mLastUpdated;
    public boolean mOfflineActive;
    public boolean mIsAuthorized;
    public PropertyDescription mSubscribable;
    public Subscription mSubscription;

    public SubscriptionEntry(PropertyDescription subscribable, boolean isAuthorized) {
        mName = subscribable.Name;
        mLastUpdated = "n.a.";
        mOfflineActive = false;
        mIsAuthorized = isAuthorized;
        mSubscribable = subscribable;
    }

    public SubscriptionEntry(PropertyDescription subscribable, Subscription subscription, boolean isAuthorized) {
        mName = subscribable.Name;
        mLastUpdated = "n.a.";
        mOfflineActive = subscription != null;
        mIsAuthorized = isAuthorized;
        mSubscribable = subscribable;
        mSubscription = subscription;
    }

    protected SubscriptionEntry(Parcel in) {
        mName = in.readString();
        mLastUpdated = in.readString();
        mOfflineActive = in.readByte() != 0;
        mIsAuthorized = in.readByte() != 0;
        mSubscribable = in.readParcelable(PropertyDescription.class.getClassLoader());
        mSubscription = in.readParcelable(Subscription.class.getClassLoader());
    }

    public static final Creator<SubscriptionEntry> CREATOR = new Creator<SubscriptionEntry>() {
        @Override
        public SubscriptionEntry createFromParcel(Parcel in) {
            return new SubscriptionEntry(in);
        }

        @Override
        public SubscriptionEntry[] newArray(int size) {
            return new SubscriptionEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mLastUpdated);
        dest.writeByte((byte) (mOfflineActive ? 1 : 0));
        dest.writeByte((byte) (mIsAuthorized ? 1 : 0));
        dest.writeParcelable(mSubscribable, flags);
        dest.writeParcelable(mSubscription, flags);
    }
}

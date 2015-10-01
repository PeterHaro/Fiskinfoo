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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OfflineCache implements Parcelable {
    private Map<String, String> cache;
    private boolean isActive = false;

    public OfflineCache() {
        cache = new HashMap<>();
        isActive = false;
    }

    public OfflineCache(Parcel in) {
        Bundle bundle = in.readBundle();

        cache = (Map<String, String>) bundle.getSerializable("cache");
        isActive = in.readByte() != 0;
    }


    public static final Creator<OfflineCache> CREATOR = new Creator<OfflineCache>() {
        @Override
        public OfflineCache createFromParcel(Parcel in) {
            return new OfflineCache(in);
        }

        @Override
        public OfflineCache[] newArray(int size) {
            return new OfflineCache[size];
        }
    };

    public String getLatestVersion(String name) {
        return cache.get(name);
    }

    public void setLatestVersion(String name, String lastUpdated) {
        cache.put(name, lastUpdated);
    }

    public Set<Map.Entry<String, String>> getEntries() {
        return cache.entrySet();
    }

    public boolean getisActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("cache", (Serializable) cache);
        dest.writeBundle(bundle);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }
}


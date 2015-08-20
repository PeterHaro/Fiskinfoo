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

public class Authorization implements Parcelable {
    public int Id;
    public boolean hasAccess;

    // TODO: cannot write boolean to parcel so needs to be rewritten.

    protected Authorization(Parcel in) {
        Id = in.readInt();
    }

    public static final Creator<Authorization> CREATOR = new Creator<Authorization>() {
        @Override
        public Authorization createFromParcel(Parcel in) {
            return new Authorization(in);
        }

        @Override
        public Authorization[] newArray(int size) {
            return new Authorization[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
    }
}

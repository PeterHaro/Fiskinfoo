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

import com.google.gson.annotations.SerializedName;

public class Feature {
    @SerializedName("type")
    public String type;
    @SerializedName("id")
    public String id;
//    @SerializedName("geometry")
//    public Geometry geometry;
    @SerializedName("geometry_name")
    public String geometry_name;
    @SerializedName("version")
    public int version;
    @SerializedName("properties")
    public Properties properties;

    public Feature() {

    }
}

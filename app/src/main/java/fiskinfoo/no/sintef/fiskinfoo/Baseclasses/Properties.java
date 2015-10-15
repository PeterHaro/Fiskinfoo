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

public class Properties {
    @SerializedName("id")
    public int id;
    @SerializedName("version")
    public int version;
    @SerializedName("vesselname")
    public String vesselname;
    @SerializedName("vesselphone")
    public String vesselphone;
    @SerializedName("tooltypecode")
    public String tooltypecode;
    @SerializedName("setupdatetime")
    public String setupdatetime;
    @SerializedName("toolid")
    public String toolid;
    @SerializedName("mmsi")
    public String mmsi;
    @SerializedName("imo")
    public String imo;
    @SerializedName("vesselemail")
    public String vesselemail;
    @SerializedName("tooltypename")
    public String tooltypename;
    @SerializedName("toolcolor")
    public String toolcolor;
    @SerializedName("source")
    public String source;
    @SerializedName("comment")
    public String comment;
    @SerializedName("removeddatetime")
    public String removeddatetime;
    @SerializedName("lastchangeddatetime")
    public String lastchangeddatetime;

    public Properties() {
    }
}

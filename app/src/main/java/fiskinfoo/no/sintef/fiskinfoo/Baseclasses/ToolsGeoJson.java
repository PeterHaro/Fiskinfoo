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

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class ToolsGeoJson {
    private JSONObject tools;
    private String versionNumber;
    public static final String INVALID_VERSION = "INVALID VERSION";

    public ToolsGeoJson(Context mContext) {
        //check if exists
        SharedPreferences prefs = mContext.getSharedPreferences("no.barentswatch.fiskinfo", Context.MODE_PRIVATE);
        Boolean informationExists = prefs.getBoolean("geoJson", false);
        if (informationExists) {
            versionNumber = prefs.getString("toolsVersionNumber", null);
            String tmp = prefs.getString("toolsGeoJson", null);
            if (versionNumber == null || tmp == null) {
                tools = null;
                versionNumber = INVALID_VERSION;
            }
        } else {
            versionNumber = INVALID_VERSION;
        }
    }

    public ToolsGeoJson(JSONObject tools, String versionNumber) {
        this.tools = tools;
        this.versionNumber = versionNumber;
    }

    public JSONObject getTools() {
        return tools;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setTools(JSONObject tools, String versionNumber, Context mContext) {
        Boolean updated = false;
        if (this.versionNumber == INVALID_VERSION) {
            this.tools = tools;
            this.versionNumber = versionNumber;
            updated = true;
        } else {
            //Fuck versions. Forgot to do it unix-time booooh
            this.tools = tools;
            this.versionNumber = versionNumber;
            updated = true;

        }
        if (updated) {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("no.barentswatch.fiskinfo", Context.MODE_PRIVATE).edit();
            editor.putString("toolsGeoJson", tools.toString());
            editor.putString("toolsVersionNumber", this.versionNumber);
            editor.putBoolean("geoJson", true);
            editor.commit();
        }
    }
}

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

package fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit;

public enum ApiErrorType {
    NONE, WARNING, ERROR;

    @Override
    public String toString() {
        String retVal = "";
        switch(this) {
            case NONE:
                retVal = "Ingen feil";
                break;
            case WARNING:
                retVal = "Advarsel";
                break;
            case ERROR:
                retVal = "Error";
                break;
            default:
                throw new UnsupportedOperationException("Error type does not exist in the system");
        }
        return retVal;
    }

    public static ApiErrorType getType(String type) {
        ApiErrorType retVal = null;
        switch(type.toLowerCase()) {
            case "none":
                retVal = NONE;
                break;
            case "warning":
                retVal = WARNING;
                break;
            case "error":
                retVal = ERROR;
                break;
            default:
                throw new UnsupportedOperationException("Error type does not exist in the system");
        }
        return retVal;
    }
}

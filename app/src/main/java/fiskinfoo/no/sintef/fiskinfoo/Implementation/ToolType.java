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

package fiskinfoo.no.sintef.fiskinfoo.Implementation;

public enum ToolType {
    LONG_LINE, NETS, CRAB_POTS, SEISMIC, DANISH_PURSE_SEINE, SENSOR_CABLE, MOORING_SYSTEM, UNKNOWN;

    @Override
    public String toString() {
        String retVal;
        switch(this) {
            case LONG_LINE:
                retVal = "Long line";
                break;
            case NETS:
                retVal = "Nets";
                break;
            case CRAB_POTS:
                retVal = "Crab pots";
                break;
            case SEISMIC:
                retVal = "Seismic";
                break;
            case DANISH_PURSE_SEINE:
                retVal = "Danish - / purse-seine";
                break;
            case SENSOR_CABLE:
                retVal = "Sensor / cable";
                break;
            case MOORING_SYSTEM:
                retVal = "Mooring system";
                break;
            case UNKNOWN:
                retVal = "Unknown";
                break;
            default:
                throw new UnsupportedOperationException("Tool type does not exist in the system");
        }
        return retVal;
    }

    public int getHexValue() {
        int retVal;
        switch(this) {
            case LONG_LINE:
                retVal = 0xFFF30F0F;
                break;
            case NETS:
                retVal = 0xFF0881D7;
                break;
            case CRAB_POTS:
                retVal = 0xFFFF6500;
                break;
            case SEISMIC:
                retVal = 0xFFA06C49;
                break;
            case DANISH_PURSE_SEINE:
                retVal = 0xFF9933CC;
                break;
            case SENSOR_CABLE:
                retVal = 0xFF75A103;
                break;
            case MOORING_SYSTEM:
                retVal = 0xFFFF42E5;
                break;
            case UNKNOWN:
                retVal = 0xff000000;
                break;
            default:
                throw new UnsupportedOperationException("Message type does not exist in the system");
        }
        return retVal;
    }
}

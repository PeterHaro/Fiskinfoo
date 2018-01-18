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

public enum ToolType {
    LONG_LINE, NETS, CRAB_POTS, SEISMIC, SENSOR_CABLE, DANISH_PURSE_SEINE, MOORING_SYSTEM, UNKNOWN;

    public static String[] getValues() {
        String[] retval = {"Line", "Garn", "Teine", "Snurpenot", "Ukjent redskap"};
        return retval;
    }

    @Override
    public String toString() {
        String retVal;
        switch(this) {
            case LONG_LINE:
                retVal = "Line";
                break;
            case NETS:
                retVal = "Garn";
                break;
            case CRAB_POTS:
                retVal = "Teine";
                break;
            case SEISMIC:
                retVal = "Sensorkabel";
                break;
            case DANISH_PURSE_SEINE:
                retVal = "Snurpenot";
                break;
            case SENSOR_CABLE:
                retVal = "Sensor / kabel";
                break;
            case MOORING_SYSTEM:
                retVal = "Fortøyningssystem";
                break;
            case UNKNOWN:
                retVal = "Ukjent redskap";
                break;
            default:
                throw new UnsupportedOperationException("Tool type does not exist in the system");
        }
        return retVal;
    }

    public static ToolType createFromValue(String value) {
        ToolType retval = null;

        switch(value.toLowerCase()) {
            case "longline":
            case "long line":
            case "line":
                retval = ToolType.LONG_LINE;
                break;
            case "nets":
            case "garn":
                retval = ToolType.NETS;
                break;
            case "crabpot":
            case "teine":
            case "crab pot":
                retval = ToolType.CRAB_POTS;
                break;
            case "seismic":
            case "sensorkabel":
                retval = ToolType.SEISMIC;
                break;
            case "danpurseine":
            case "snurpenot":
            case "danish- / purse- seine":
                retval = ToolType.DANISH_PURSE_SEINE;
                break;
            case "sensorcable":
            case "sensor / kabel":
                retval = ToolType.SENSOR_CABLE;
                break;
            case "mooring":
            case "fortøyningssystem":
                retval = ToolType.MOORING_SYSTEM;
                break;
            case "unk":
            case "ukjent redskap":
            case "unknown":
                retval = ToolType.UNKNOWN;
                break;
            default:
                throw new UnsupportedOperationException("Tool type does not exist in the system: " + value);
        }
        return retval;
    }

    public String getToolCode() {
        String retval;
        switch(this) {
            case LONG_LINE:
                retval = "LONGLINE";
                break;
            case NETS:
                retval = "NETS";
                break;
            case CRAB_POTS:
                retval = "CRABPOT";
                break;
            case SEISMIC:
                retval = "SEISMIC";
                break;
            case DANISH_PURSE_SEINE:
                retval = "DANPURSEINE";
                break;
            case SENSOR_CABLE:
                retval = "SENSORCABLE";
                break;
            case MOORING_SYSTEM:
                retval = "MOORING";
                break;
            case UNKNOWN:
                retval = "UNK";
                break;
            default:
                throw new UnsupportedOperationException("Tool type does not exist in the system");
        }
        return retval;
    }

    public int getHexColorValue() {
        int retVal;
        switch(this) {
            case LONG_LINE:
                retVal = 0xFFDA0E0E;
                break;
            case NETS:
                retVal = 0xFF0874C1;
                break;
            case CRAB_POTS:
                retVal = 0xFFEA5D00;
                break;
            case SEISMIC:
                retVal = 0xFFA06C49;
                break;
            case DANISH_PURSE_SEINE:
                retVal = 0xFF8100C1;
                break;
            case SENSOR_CABLE:
                retVal = 0xFF73A000;
                break;
            case MOORING_SYSTEM:
                retVal = 0xFFFF42E5;
                break;
            case UNKNOWN:
                retVal = 0xff000000;
                break;
            default:
                throw new UnsupportedOperationException("Tool type does not exist in the system");
        }
        return retVal;
    }
}
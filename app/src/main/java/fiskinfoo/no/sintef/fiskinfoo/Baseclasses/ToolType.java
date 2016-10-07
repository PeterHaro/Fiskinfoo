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
        String[] retval = {"Line", "Garn", "Teine", "Sensorkabel", "Sensor / kabel", "Snurpenot", "Fortøyningssystem", "Ukjent redskap"};
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
        if (value.equalsIgnoreCase("Line")) {
            return ToolType.LONG_LINE;
        } else if (value.equalsIgnoreCase("garn")) {
            return ToolType.NETS;
        } else if (value.equalsIgnoreCase("Teine")) {
            return ToolType.CRAB_POTS;
        } else if (value.equalsIgnoreCase("Sensorkabel")) {
            return ToolType.SEISMIC;
        } else if (value.equalsIgnoreCase("Sensor / kabel")) {
            return ToolType.SENSOR_CABLE;
        } else if (value.equalsIgnoreCase("Snurpenot")) {
            return ToolType.DANISH_PURSE_SEINE;
        } else if (value.equalsIgnoreCase("Fortøyningssystem")) {
            return ToolType.MOORING_SYSTEM;
        } else if (value.equalsIgnoreCase("Ukjent redskap")) {
            return ToolType.UNKNOWN;
        }
        return null;
    }

    public String getToolCode() {
        String retVal;
        switch(this) {
            case LONG_LINE:
                retVal = "LONGLINE";
                break;
            case NETS:
                retVal = "NETS";
                break;
            case CRAB_POTS:
                retVal = "CRABPOT";
                break;
            case SEISMIC:
                retVal = "SEISMIC";
                break;
            case DANISH_PURSE_SEINE:
                retVal = "DANPURSEINE";
                break;
            case SENSOR_CABLE:
                retVal = "SENSORCABLE";
                break;
            case MOORING_SYSTEM:
                retVal = "MOORING";
                break;
            case UNKNOWN:
                retVal = "UNK";
                break;
            default:
                throw new UnsupportedOperationException("Tool type does not exist in the system");
        }
        return retVal;
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

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

public enum ToolEntryStatus {
    STATUS_UNSENT, STATUS_SENT_UNCONFIRMED, STATUS_RECEIVED, STATUS_REMOVED_UNCONFIRMED, STATUS_REMOVED;


    @Override
    public String toString() {
        String retVal;
        switch(this) {
            case STATUS_UNSENT:
                retVal = "Ikke rapportert";
                break;
            case STATUS_SENT_UNCONFIRMED:
                retVal = "Rapportert, ikke bekreftet";
                break;
            case STATUS_RECEIVED:
                retVal = "Innrapportert";
                break;
            case STATUS_REMOVED_UNCONFIRMED:
                retVal = "Utrapportert, ikke bekreftet";
                break;
            case STATUS_REMOVED:
                retVal = "Utrapportert";
                break;
            default:
                throw new UnsupportedOperationException("Tool type does not exist in the system");
        }
        return retVal;
    }

    public static ToolEntryStatus createFromValue(String value) {
        if (value.equalsIgnoreCase("Ikke rapportert")) {
            return ToolEntryStatus.STATUS_UNSENT;
        } else if (value.equalsIgnoreCase("Rapportert, ikke bekreftet")) {
            return ToolEntryStatus.STATUS_SENT_UNCONFIRMED;
        } else if (value.equalsIgnoreCase("Innrapportert")) {
            return ToolEntryStatus.STATUS_RECEIVED;
        } else if (value.equalsIgnoreCase("Utrapportert, ikke bekreftet")) {
            return ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED;
        } else if (value.equalsIgnoreCase("Utrapportert")) {
            return ToolEntryStatus.STATUS_REMOVED;
        }
        return null;
    }
}

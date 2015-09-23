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

package fiskinfoo.no.sintef.fiskinfoo.Interface;

import android.content.Context;
import android.view.View;

import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CardViewInformationRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.RadioButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SettingsButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolLegendRow;

public interface UtilityRowsInterface {
    ToolLegendRow getToolLegendRow(Context context, int toolColor, String toolName);
    SettingsButtonRow getSettingsButtonRow(Context context, String buttonText);
    SettingsButtonRow getSettingsButtonRow(Context context, String buttonText, View.OnClickListener onClickListener);
    CheckBoxRow getCheckBoxRow(Context context, String text);
    CheckBoxRow getCheckBoxRow(Context context, String text, boolean isChecked);
    CardViewInformationRow getCardViewInformationRow(Context context, String fieldName, String fieldData, boolean indentData);
    CardViewInformationRow getCardViewInformationRow(Context context, String fieldName, String fieldData, boolean indentData, int textColor);
    RadioButtonRow getRadioButtonRow(Context context, String item);
}

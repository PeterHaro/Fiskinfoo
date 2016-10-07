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
import android.widget.CompoundButton;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Feature;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CardViewInformationRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.RadioButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.OptionsButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SwitchAndButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolLegendRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolSearchResultRow;

public interface UtilityRowsInterface {
    /**
     *
     * @param context
     * @param toolColor
     * @param toolName
     * @return
     */
    ToolLegendRow getToolLegendRow(Context context, int toolColor, String toolName);

    /**
     *
     * @param context
     * @param buttonText
     * @return
     */
    OptionsButtonRow getSettingsButtonRow(Context context, String buttonText);

    /**
     *
     * @param context
     * @param buttonText
     * @param onClickListener
     * @return
     */
    OptionsButtonRow getSettingsButtonRow(Context context, String buttonText, View.OnClickListener onClickListener);

    /**
     *
     * @param context
     * @param text
     * @return
     */
    CheckBoxRow getCheckBoxRow(Context context, String text, boolean checkBoxOnRightSide);

    /**
     *
     * @param context
     * @param text
     * @param isChecked
     * @return
     */
    CheckBoxRow getCheckBoxRow(Context context, String text, boolean checkBoxOnRightSide, boolean isChecked);

    /**
     *
     * @param context
     * @param fieldName
     * @param fieldData
     * @param indentData
     * @return
     */
    CardViewInformationRow getCardViewInformationRow(Context context, String fieldName, String fieldData, boolean indentData);

    /**
     *
     * @param context
     * @param fieldName
     * @param fieldData
     * @param indentData
     * @param textColor
     * @return
     */
    CardViewInformationRow getCardViewInformationRow(Context context, String fieldName, String fieldData, boolean indentData, int textColor);

    /**
     *
     * @param context
     * @param item
     * @return
     */
    RadioButtonRow getRadioButtonRow(Context context, String item);

    /**
     *
     * @param context
     * @param titleId
     * @return
     */
    SwitchAndButtonRow getSwitchAndButtonRow(Context context, int titleId);

    /**
     *
     * @param context
     * @param title
     * @return
     */
    SwitchAndButtonRow getSwitchAndButtonRow(Context context, String title);

    /**
     *
     * @param context
     * @param titleId
     * @param onCheckedChangeListener
     * @param onClickListener
     * @return
     */
    SwitchAndButtonRow getSwitchAndButtonRow(Context context, int titleId, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, View.OnClickListener onClickListener);

    /**
     *
     * @param context
     * @param title
     * @param onCheckedChangeListener
     * @param onClickListener
     * @return
     */
    SwitchAndButtonRow getSwitchAndButtonRow(Context context, String title, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, View.OnClickListener onClickListener);

    /**
     *
     * @param context
     * @return
     */
    ToolSearchResultRow getToolSearchResultRow(Context context);

    /**
     *
     * @param context
     * @param toolImageId
     * @param vesselName
     * @param toolType
     * @param phoneNumber
     * @param date
     * @param position
     * @return
     */
    ToolSearchResultRow getToolSearchResultRow(Context context, int toolImageId, String vesselName, String toolType, String phoneNumber, String date, String position);

    ToolSearchResultRow getToolSearchResultRow(Context context, int toolImageId, Feature feature);
}

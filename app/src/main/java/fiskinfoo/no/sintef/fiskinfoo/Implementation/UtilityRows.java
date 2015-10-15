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

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Feature;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CardViewInformationRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.RadioButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SettingsButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SwitchAndButtonRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolLegendRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolSearchResultRow;

public class UtilityRows implements UtilityRowsInterface {
    @Override
    public ToolLegendRow getToolLegendRow(Context context, int toolColor, String toolName) {
        return new ToolLegendRow(context, toolColor, toolName);
    }

    @Override
    public SettingsButtonRow getSettingsButtonRow(Context context, String buttonText) {
        return new SettingsButtonRow(context, buttonText);
    }

    @Override
    public SettingsButtonRow getSettingsButtonRow(Context context, String buttonText, View.OnClickListener onClickListener) {
        return new SettingsButtonRow(context, buttonText, onClickListener);
    }

    @Override
    public CheckBoxRow getCheckBoxRow(Context context, String text) {
        return new CheckBoxRow(context, text);
    }

    @Override
    public CheckBoxRow getCheckBoxRow(Context context, String text, boolean isChecked) {
        return new CheckBoxRow(context, text, isChecked);
    }

    @Override
    public CardViewInformationRow getCardViewInformationRow(Context context, String fieldName, String fieldData, boolean indentData) {
        return new CardViewInformationRow(context, fieldName, fieldData, indentData);
    }

    @Override
    public CardViewInformationRow getCardViewInformationRow(Context context, String fieldName, String fieldData, boolean indentData, int textColor) {
        return new CardViewInformationRow(context, fieldName, fieldData, indentData, textColor);
    }

    @Override
    public RadioButtonRow getRadioButtonRow(Context context, String item) {
        return new RadioButtonRow(context, item);
    }

    @Override
    public SwitchAndButtonRow getSwitchAndButtonRow(Context context, int titleId) {
        return new SwitchAndButtonRow(context, titleId);
    }

    @Override
    public SwitchAndButtonRow getSwitchAndButtonRow(Context context, String title) {
        return new SwitchAndButtonRow(context, title);
    }

    @Override
    public SwitchAndButtonRow getSwitchAndButtonRow(Context context, int titleId, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, View.OnClickListener onClickListener) {
        return new SwitchAndButtonRow(context, titleId, onCheckedChangeListener, onClickListener);
    }

    @Override
    public SwitchAndButtonRow getSwitchAndButtonRow(Context context, String title, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, View.OnClickListener onClickListener) {
        return new SwitchAndButtonRow(context, title, onCheckedChangeListener, onClickListener);
    }

    @Override
    public ToolSearchResultRow getToolSearchResultRow(Context context) {
        return new ToolSearchResultRow(context);
    }

    @Override
    public ToolSearchResultRow getToolSearchResultRow(Context context, int toolImageId, String vesselName, String toolType, String phoneNumber, String date, String position) {
        return new ToolSearchResultRow(context, toolImageId, vesselName, toolType, phoneNumber, date, position);
    }

    @Override
    public ToolSearchResultRow getToolSearchResultRow(Context context, int toolImageId, Feature feature) {
        return new ToolSearchResultRow(context, toolImageId, feature);
    }
}


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

package fiskinfoo.no.sintef.fiskinfoo.UtilityRows;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class SwitchAndButtonRow extends BaseTableRow {
    private Switch mSwitch;
    private Button mInfoButton;

    public SwitchAndButtonRow(Context context, int titleId) {
        super(context, R.layout.utility_row_switch_and_button_row);

        mSwitch = (Switch) getView().findViewById(R.id.switch_and_button_row_switch);
        mInfoButton = (Button) getView().findViewById(R.id.switch_and_button_row_button);

        mSwitch.setText(titleId);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mSwitch.setEnabled(enabled);
        mInfoButton.setEnabled(enabled);
    }

    public SwitchAndButtonRow(Context context, String title) {
        super(context, R.layout.utility_row_switch_and_button_row);

        mSwitch = (Switch) getView().findViewById(R.id.switch_and_button_row_switch);
        mInfoButton = (Button) getView().findViewById(R.id.switch_and_button_row_button);

        mSwitch.setText(title);
    }

    public SwitchAndButtonRow(Context context, int titleId, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, View.OnClickListener buttonOnClickListener) {
        super(context, R.layout.utility_row_switch_and_button_row);

        mSwitch = (Switch) getView().findViewById(R.id.switch_and_button_row_switch);
        mInfoButton = (Button) getView().findViewById(R.id.switch_and_button_row_button);

        mSwitch.setText(titleId);
        mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        mInfoButton.setOnClickListener(buttonOnClickListener);
    }

    public SwitchAndButtonRow(Context context, String title, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, View.OnClickListener buttonOnClickListener) {
        super(context, R.layout.utility_row_switch_and_button_row);

        mSwitch = (Switch) getView().findViewById(R.id.switch_and_button_row_switch);
        mInfoButton = (Button) getView().findViewById(R.id.switch_and_button_row_button);

        mSwitch.setText(title);
        mSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        mInfoButton.setOnClickListener(buttonOnClickListener);
    }

    public String getTitle() {
        return mSwitch.getText().toString();
    }

    public void setTitle(String title) {
        mSwitch.setText(title);
    }

    public boolean isSet() {
        return mSwitch.isChecked();
    }

    public void setChecked(boolean set) {
        mSwitch.setChecked(set);
    }

    public void setButtonOnClickListener(View.OnClickListener onClickListener) {
        mInfoButton.setOnClickListener(onClickListener);
    }

    public void setSwitchOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener onCheckedChangedListener) {
        mSwitch.setOnCheckedChangeListener(onCheckedChangedListener);
    }
}

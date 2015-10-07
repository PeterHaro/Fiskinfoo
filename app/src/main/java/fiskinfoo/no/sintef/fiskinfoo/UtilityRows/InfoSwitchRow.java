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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class InfoSwitchRow extends BaseTableRow {
    private TextView mTextView;
    private Switch mSwitch;

    public InfoSwitchRow(Context context, int titleId) {
        super(context, R.layout.utility_row_info_switch_row);

        mTextView = (TextView) getView().findViewById(R.id.info_switch_row_text_view);
        mSwitch = (Switch) getView().findViewById(R.id.info_switch_row_switch);

        mTextView.setText(titleId);
    }

    public InfoSwitchRow(Context context, String title) {
        super(context, R.layout.utility_row_info_switch_row);

        mTextView = (TextView) getView().findViewById(R.id.info_switch_row_text_view);
        mSwitch = (Switch) getView().findViewById(R.id.info_switch_row_switch);

        mTextView.setText(title);
    }

    public InfoSwitchRow(Context context, int titleId, int switchTextId) {
        super(context, R.layout.utility_row_info_switch_row);

        mTextView = (TextView) getView().findViewById(R.id.info_switch_row_text_view);
        mSwitch = (Switch) getView().findViewById(R.id.info_switch_row_switch);

        mTextView.setText(titleId);
        mSwitch.setText(switchTextId);
    }

    public InfoSwitchRow(Context context, String title, String switchTextId) {
        super(context, R.layout.utility_row_info_switch_row);

        mTextView = (TextView) getView().findViewById(R.id.info_switch_row_text_view);
        mSwitch = (Switch) getView().findViewById(R.id.info_switch_row_switch);

        mTextView.setText(title);
        mSwitch.setText(switchTextId);
    }

    public InfoSwitchRow(Context context, int titleId, View.OnClickListener onClickListener) {
        super(context, R.layout.utility_row_info_switch_row);

        mTextView = (TextView) getView().findViewById(R.id.info_switch_row_text_view);
        mSwitch = (Switch) getView().findViewById(R.id.info_switch_row_switch);

        getView().setOnClickListener(onClickListener);
        mTextView.setText(titleId);
    }

    public InfoSwitchRow(Context context, String title, View.OnClickListener onClickListener) {
        super(context, R.layout.utility_row_info_switch_row);

        mTextView = (TextView) getView().findViewById(R.id.info_switch_row_text_view);
        mSwitch = (Switch) getView().findViewById(R.id.info_switch_row_switch);

        getView().setOnClickListener(onClickListener);
        mTextView.setText(title);
    }

    public String getTitle() {
        return mTextView.getText().toString();
    }

    public void setTitle(String title) {
        mTextView.setText(title);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    public void setChecked(boolean set) {
        mSwitch.setChecked(set);
    }

    public void setSwitchText(String text) {
        mSwitch.setText(text);
    }

    public String getSwitchText(){
        return mSwitch.getText().toString();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        getView().setOnClickListener(onClickListener);
    }

    public void setOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener onCheckChangedListener) {
        mSwitch.setOnCheckedChangeListener(onCheckChangedListener);
    }
}

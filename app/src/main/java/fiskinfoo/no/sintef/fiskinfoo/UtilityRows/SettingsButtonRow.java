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

import fiskinfoo.no.sintef.fiskinfoo.R;

public class SettingsButtonRow extends BaseTableRow {
    private Button mButton;

    public SettingsButtonRow(Context context, String buttonText) {
        super(context, R.layout.utility_row_settings_button_row);

        mButton = (Button) getView().findViewById(R.id.settings_button_row_button);

        mButton.setText(buttonText);
    }

    public SettingsButtonRow(Context context, String buttonText, View.OnClickListener onClickListener) {
        super(context, R.layout.utility_row_settings_button_row);

        mButton = (Button) getView().findViewById(R.id.settings_button_row_button);

        mButton.setText(buttonText);
        mButton.setOnClickListener(onClickListener);

    }

    public String getButtonText() {
        return mButton.getText().toString();
    }

    public void setButtonText(String buttonText) {
        mButton.setText(buttonText);
    }

    public void setButtonOnClickListener(View.OnClickListener oncClickListener)
    {
        mButton.setOnClickListener(oncClickListener);
    }

    public Button getButton() {
        return mButton;
    }

    public void setButton(Button button) {
        mButton.setText(button.getText().toString());
    }
}

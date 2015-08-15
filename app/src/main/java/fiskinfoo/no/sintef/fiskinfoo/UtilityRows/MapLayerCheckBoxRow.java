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
import android.widget.CheckBox;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class MapLayerCheckBoxRow extends BaseTableRow {
    private CheckBox mCheckBox;
    private TextView mTextView;

    public MapLayerCheckBoxRow(Context context, boolean isChecked, String text) {
        super(context, R.layout.utility_row_map_layer_checkbox_row);

        mCheckBox = (CheckBox) getView().findViewById(R.id.map_layer_row_check_box);
        mTextView = (TextView) getView().findViewById(R.id.map_layer_row_text_view);

        mCheckBox.setChecked(isChecked);
        mTextView.setText(text);
    }

    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    public void setChecked(boolean isChecked) {
        mCheckBox.setChecked(isChecked);
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
}

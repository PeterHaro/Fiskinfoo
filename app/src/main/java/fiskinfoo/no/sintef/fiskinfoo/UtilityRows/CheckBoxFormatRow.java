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
import android.widget.CheckBox;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class CheckBoxFormatRow extends BaseTableRow {
    private TextView mTextView;
    private CheckBox mCheckBox;

    public CheckBoxFormatRow(Context context, String format) {
        super(context, R.layout.utility_row_format_check_box_row);

        mTextView = (TextView) getView().findViewById(R.id.format_row_text_view);
        mCheckBox = (CheckBox) getView().findViewById(R.id.format_row_check_box);

        mTextView.setText(format);
    }

    public CheckBoxFormatRow(Context context, String buttonText, boolean isChecked) {
        super(context, R.layout.utility_row_format_text_view_row);

        mTextView = (TextView) getView().findViewById(R.id.format_row_text_view);
        mCheckBox = (CheckBox) getView().findViewById(R.id.format_row_check_box);

        mTextView.setText(buttonText);
        mCheckBox.setChecked(isChecked);
    }

    public void setText(String buttonText) {
        mTextView.setText(buttonText);
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mTextView.setOnClickListener(onClickListener);
    }

    public void setChecked(boolean isChecked) {
        if(mCheckBox != null) {
            mCheckBox.setChecked(isChecked);
        }
    }

    public boolean isChecked() {
        if(mCheckBox != null ) {
            return mCheckBox.isChecked();
        } else {
            return false;
        }
    }
}

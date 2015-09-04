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
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class RadioButtonRow extends BaseTableRow {
    private RadioButton mRadioButton;
    private TextView mTextView;

    public RadioButtonRow(Context context, String format) {
        super(context, R.layout.utility_row_radio_button_row);

        mTextView = (TextView) getView().findViewById(R.id.radio_button_row_text_view);
        mRadioButton = (RadioButton) getView().findViewById(R.id.radio_button_row_radio_button);

        mTextView.setText(format);

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < ((ViewGroup)v.getParent()).getChildCount(); i++) {
                    ((RadioButton) ((ViewGroup)v.getParent()).getChildAt(i).findViewById(R.id.radio_button_row_radio_button)).setChecked(false);
                }
                ((RadioButton)v.findViewById(R.id.radio_button_row_radio_button)).setChecked(true);
            }
        });
    }

    public void setText(String buttonText) {
        mTextView.setText(buttonText);
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public boolean isSelected( ) {
        return mRadioButton.isSelected();
    }

    public void setSelected(boolean selected) {
        mRadioButton.setChecked(selected);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        getView().setOnClickListener(onClickListener);
    }
}

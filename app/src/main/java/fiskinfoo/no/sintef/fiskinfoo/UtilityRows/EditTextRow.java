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
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class EditTextRow extends BaseTableRow  {
    private TextView header;
    private Button helpButton;
    private TextView helpTextView;
    private EditText editText;

    public EditTextRow(Context context, String fieldName, String fieldHint) {
        super(context, R.layout.utility_row_edit_text_row);

        header = (TextView) getView().findViewById(R.id.utility_edit_text_row_header_text_view);
        helpButton = (Button) getView().findViewById(R.id.utility_edit_text_row_help_button);
        helpTextView = (TextView) getView().findViewById(R.id.utility_edit_text_row_help_text_view);
        editText = (EditText) getView().findViewById(R.id.utility_edit_text_row_edit_text);

        header.setText(fieldName);
        editText.setHint(fieldHint);
    }

    public EditTextRow(Context context, String fieldName, String fieldHint, int textColor) {
        super(context, R.layout.utility_row_card_view_information_row);

        header = (TextView) getView().findViewById(R.id.utility_edit_text_row_header_text_view);
        editText = (EditText) getView().findViewById(R.id.utility_edit_text_row_edit_text);

        header.setText(fieldName);
        editText.setHint(fieldHint);

        header.setTextColor(textColor);
        editText.setTextColor(textColor);
    }

    public void setTextColor(int textColor) {
        if(header == null || editText == null) {
            System.out.println("Cannot set color, view is null");
            return;
        }

        header.setTextColor(textColor);
        editText.setTextColor(textColor);
    }

    @SuppressWarnings("unused")
    public void setRowHeader(String header) {
        this.header.setText(header);
    }

    @SuppressWarnings("unused")
    public String getRowHeader() {
        return header.getText().toString();
    }

    @SuppressWarnings("unused")
    public void setFieldTextHint(String fieldText) {
        editText.setHint(fieldText);
    }

    @SuppressWarnings("unused")
    public void setText(String fieldText) {
        editText.setText(fieldText);
    }

    @SuppressWarnings("unused")
    public void setInputType(int inputType) {
        editText.setInputType(inputType);
    }

    @SuppressWarnings("unused")
    public String getFieldText() {
        return editText.getText().toString();
    }

    public void setHelpText(String helpText) {
        helpTextView.setText(helpText);
        helpButton.setVisibility(helpText != null ? View.VISIBLE : View.GONE);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpTextView.setVisibility(helpTextView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void setInputFilters(InputFilter[] filters) {
        editText.setFilters(filters);
    }

    public void setError(String error) {
        editText.setError(error);
    }

    public void requestFocus() {
        editText.requestFocus();
    }

    public void setEnabled(boolean enabled) {
        editText.setEnabled(enabled);
    }
}

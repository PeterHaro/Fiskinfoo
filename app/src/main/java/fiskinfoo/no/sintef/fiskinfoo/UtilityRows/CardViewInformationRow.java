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
import android.text.Html;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;

public class CardViewInformationRow extends BaseTableRow {
    private TextView fieldNameTextView;
    private TextView fieldDataTextView;

    public CardViewInformationRow(Context context, String fieldName, String fieldInfo, boolean indentData) {
        super(context, R.layout.utility_row_card_view_information_row);

        fieldNameTextView = (TextView) getView().findViewById(R.id.card_view_field_name_text_view);
        fieldDataTextView = (TextView) getView().findViewById(R.id.card_view_field_data_text_view);

        fieldNameTextView.setText(fieldName);
        fieldDataTextView.setText(indentData ? "\t\t" + fieldInfo : fieldInfo);
    }

    public CardViewInformationRow(Context context, String fieldName, String fieldInfo, boolean indentData, int textColor) {
        super(context, R.layout.utility_row_card_view_information_row);

        fieldNameTextView = (TextView) getView().findViewById(R.id.card_view_field_name_text_view);
        fieldDataTextView = (TextView) getView().findViewById(R.id.card_view_field_data_text_view);

        fieldNameTextView.setText(fieldName);
        fieldDataTextView.setText(indentData ? "\t\t" + fieldInfo : fieldInfo);

        fieldNameTextView.setTextColor(textColor);
        fieldDataTextView.setTextColor(textColor);
    }

    public void setTextColor(int textColor) {
        if(fieldNameTextView == null || fieldDataTextView == null) {
            System.out.println("Cannot set color, view is null");
            return;
        }

        fieldNameTextView.setTextColor(textColor);
        fieldDataTextView.setTextColor(textColor);
    }

    @SuppressWarnings("unused")
    public void setFieldName(String fieldName) {
        fieldNameTextView.setText(fieldName);
    }

    @SuppressWarnings("unused")
    public String getFieldName() {
        return fieldNameTextView.getText().toString();
    }

    @SuppressWarnings("unused")
    public void setFieldData(String fieldData) {
        String text = "\t\t" + fieldData;
        fieldDataTextView.setText(text);
    }

    @SuppressWarnings("unused")
    public String getFieldData() {
        return fieldDataTextView.getText().toString();
    }

    public void setHyperlink(String hyperlink) {
        fieldDataTextView.setTextSize(fieldDataTextView.getResources().getInteger(R.integer.hyperlinkTextSize));
        fieldDataTextView.setText(Html.fromHtml(hyperlink));
    }

    public TextView getFieldDataTextView() {
        return fieldDataTextView;
    }

    @Override
    public void setEnabled(boolean enabled) {
        fieldNameTextView.setEnabled(enabled);
        fieldDataTextView.setEnabled(enabled);
    }
}

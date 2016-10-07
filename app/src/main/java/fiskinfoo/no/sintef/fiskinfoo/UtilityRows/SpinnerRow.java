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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.R;


public class SpinnerRow extends BaseTableRow {
    private TextView fieldNameTextView;
    private Spinner spinner;
    private ArrayAdapter adapter;

    public SpinnerRow(Context context, String fieldName, String[] spinnerItems) {
        super(context, R.layout.utility_row_spinner_row);

        fieldNameTextView = (TextView) getView().findViewById(R.id.utility_spinner_row_header_text_view);
        spinner = (Spinner) getView().findViewById(R.id.utility_spinner_row_spinner);

        fieldNameTextView.setText(fieldName);

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinner.setAdapter(adapter);
    }

    public SpinnerRow(Context context, String fieldName, ArrayAdapter adapter) {
        super(context, R.layout.utility_row_spinner_row);

        fieldNameTextView = (TextView) getView().findViewById(R.id.utility_spinner_row_header_text_view);
        spinner = (Spinner) getView().findViewById(R.id.utility_spinner_row_spinner);
        this.adapter = adapter;

        fieldNameTextView.setText(fieldName);
        spinner.setAdapter(adapter);
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
    public ArrayAdapter<String> getAdapter() {
        return spinner != null ? (ArrayAdapter<String>)spinner.getAdapter() : null;
    }

    @SuppressWarnings("unused")
    public void setAdapter(ArrayAdapter adapter) {
        if(spinner != null) {
            spinner.setAdapter(adapter);
        }
    }

    @SuppressWarnings("unused")
    public String getCurrentSpinnerItem () {
        return spinner.getSelectedItem().toString();
    }

    @SuppressWarnings("unused")
    public void setSelectedSpinnerItem (int index) {
        spinner.setSelection(index);
    }

    @SuppressWarnings("unused")
    public void setSelectedSpinnerItem (String spinnerText) {
        spinner.setSelection(adapter.getPosition(spinnerText));
    }
}

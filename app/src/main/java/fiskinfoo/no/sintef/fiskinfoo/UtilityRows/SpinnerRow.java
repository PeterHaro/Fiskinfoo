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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.R;


public class SpinnerRow extends BaseTableRow {
    private TextView fieldNameTextView;
    private TextView errorTextView;
    private Spinner spinner;

    public SpinnerRow(Context context, String fieldName, String[] spinnerItems) {
        super(context, R.layout.utility_row_spinner_row);

        fieldNameTextView = (TextView) getView().findViewById(R.id.utility_spinner_row_header_text_view);
        errorTextView = (TextView) getView().findViewById(R.id.utility_spinner_row_error_text_view);
        spinner = (Spinner) getView().findViewById(R.id.utility_spinner_row_spinner);
        ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, spinnerItems);

        fieldNameTextView.setText(fieldName);
        spinner.setAdapter(adapter);
    }

    public SpinnerRow(Context context, String fieldName, ArrayAdapter adapter) {
        super(context, R.layout.utility_row_spinner_row);

        fieldNameTextView = (TextView) getView().findViewById(R.id.utility_spinner_row_header_text_view);
        spinner = (Spinner) getView().findViewById(R.id.utility_spinner_row_spinner);

        fieldNameTextView.setText(fieldName);
        spinner.setAdapter(adapter);
    }

    public void setFieldName(String fieldName) {
        fieldNameTextView.setText(fieldName);
    }

    public String getFieldName() {
        return fieldNameTextView.getText().toString();
    }

    public ArrayAdapter<String> getAdapter() {
        return spinner != null ? (ArrayAdapter<String>)spinner.getAdapter() : null;
    }

    public void setAdapter(ArrayAdapter adapter) {
        if(spinner != null) {
            spinner.setAdapter(adapter);
        }
    }

    public String getCurrentSpinnerItem () {
        return spinner.getSelectedItem().toString();
    }

    public void setSelectedSpinnerItem (int index) {
        spinner.setSelection(index);
    }

    public void setSelectedSpinnerItem (String spinnerText) {
        spinner.setSelection(((ArrayAdapter)spinner.getAdapter()).getPosition(spinnerText));
    }

    public int getCurrentSpinnerIndex() {
        return spinner.getSelectedItemPosition();
    }

    public void setCurrentSpinnerIndex(int index) {
        if(index < 0 || index >= spinner.getAdapter().getCount()) {
            throw new IndexOutOfBoundsException();
        }

        spinner.setSelection(index);
    }

    public void add(Object object, int index) {
        List<Object> objects = new ArrayList<>();
        for(int i = 0; i < spinner.getAdapter().getCount(); i++) {
            objects.add(spinner.getAdapter().getItem(i));
        }

        objects.add(index, object);
        spinner.setAdapter(new ArrayAdapter<>(getView().getContext(), android.R.layout.simple_spinner_dropdown_item, objects));
    }

    public void setError(String error) {
        errorTextView.setError(error);
        errorTextView.setText(error);
        errorTextView.setVisibility(error != null ? View.VISIBLE : View.GONE);
    }
}

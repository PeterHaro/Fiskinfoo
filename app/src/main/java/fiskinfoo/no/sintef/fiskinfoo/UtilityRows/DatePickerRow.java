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
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import fiskinfoo.no.sintef.fiskinfoo.Fragments.ActiveToolsFragment;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class DatePickerRow extends BaseTableRow {
    private TextView header;
    private Button datePickerButton;
    private TextView dateTextView;

    public DatePickerRow(Context context, String rowTitle, final FragmentManager fragmentManager) {
        super(context, R.layout.utility_row_date_picker_row);

        header = (TextView) getView().findViewById(R.id.date_picker_row_header);
        datePickerButton = (Button) getView().findViewById(R.id.date_picker_row_date_picker_button);
        dateTextView = (TextView) getView().findViewById(R.id.date_picker_row_date_text_view);

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        header.setText(rowTitle);
        dateTextView.setText(sdf.format(date));

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new ActiveToolsFragment.DatePickerFragment(dateTextView);
                dateFragment.show(fragmentManager, "datePicker");
            }
        };

        dateTextView.setOnClickListener(onClickListener);
        datePickerButton.setOnClickListener(onClickListener);
    }

    public String getDate() {
        return dateTextView.getText().toString();
    }

    public void setDate(String date) {
        dateTextView.setText(date);
    }

    public void setHeader(String headerText) {
        header.setText(headerText);
    }

    public void setIconVisibility(boolean isVisible) {
        this.datePickerButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    // TODO: Should be abstract in base class so different rows can implement differently.
    public void setEnabled(boolean enabled) {
        dateTextView.setEnabled(enabled);
        datePickerButton.setEnabled(enabled);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(enabled) {
                datePickerButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_active_tint_color));
            } else {
                datePickerButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_disabled_tint_color));
            }
        }
    }

    public TextView getDateTextView() {
        return this.dateTextView;
    }
}
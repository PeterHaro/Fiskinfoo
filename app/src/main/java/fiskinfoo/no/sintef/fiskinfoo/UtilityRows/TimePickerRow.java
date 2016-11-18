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

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import fiskinfoo.no.sintef.fiskinfoo.MyToolsFragment;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class TimePickerRow extends BaseTableRow {
    private TextView header;
    private Button timePickerButton;
    private TextView timeTextView;

    public TimePickerRow(Context context, final FragmentManager fragmentManager, final boolean hasMaxTime) {
        super(context, R.layout.utility_row_time_picker_row);

        header = (TextView) getView().findViewById(R.id.date_picker_row_header);
        timePickerButton = (Button) getView().findViewById(R.id.time_picker_row_time_picker_button);
        timeTextView = (TextView) getView().findViewById(R.id.time_picker_row_time_text_view);

        Date time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        timeTextView.setText(sdf.format(time));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyToolsFragment.TimePickerFragment(timeTextView, hasMaxTime);
                newFragment.show(fragmentManager, "timePicker");
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyToolsFragment.TimePickerFragment(timeTextView, hasMaxTime);
                newFragment.show(fragmentManager, "timePicker");
            }
        });
    }

    public TimePickerRow(Context context, String rowTitle, final FragmentManager fragmentManager, final boolean hasMaxTime) {
        super(context, R.layout.utility_row_time_picker_row);

        header = (TextView) getView().findViewById(R.id.utility_row_header);
        timePickerButton = (Button) getView().findViewById(R.id.time_picker_row_time_picker_button);
        timeTextView = (TextView) getView().findViewById(R.id.time_picker_row_time_text_view);

        Date time = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        header.setText(rowTitle);
        timeTextView.setText(sdf.format(time).substring(0, 5));
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyToolsFragment.TimePickerFragment(timeTextView, hasMaxTime);
                newFragment.show(fragmentManager, "timePicker");
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyToolsFragment.TimePickerFragment(timeTextView, hasMaxTime);
                newFragment.show(fragmentManager, "timePicker");
            }
        });
    }

    public String getTime() {
        return timeTextView.getText().toString();
    }

    public void setTime(String time) {
        timeTextView.setText(time);
    }

    public void setHeader(String headerText) {
        header.setText(headerText);
    }
}

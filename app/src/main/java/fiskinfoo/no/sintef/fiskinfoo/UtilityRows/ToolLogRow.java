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
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntryStatus;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class ToolLogRow extends BaseTableRow {
    TextView dateHeader;
    TextView toolTypeTextView;
    TextView toolPositionTextView;
    ImageView toolNotificationImageView;
    Button editToolButton;

    public ToolLogRow(Context context, ToolEntry tool) {
        super(context, R.layout.utility_row_tool_log_row);

        dateHeader = (TextView) getView().findViewById(R.id.tool_log_row_latest_date_text_view);
        toolTypeTextView = (TextView) getView().findViewById(R.id.tool_log_row_tool_type_text_view);
        toolPositionTextView = (TextView) getView().findViewById(R.id.tool_log_row_tool_position_text_view);
        toolNotificationImageView = (ImageView) getView().findViewById(R.id.tool_log_row_reported_image_view);
        editToolButton = (Button) getView().findViewById(R.id.tool_log_row_edit_tool_button);

        dateHeader.setText(tool.getSetupDateTime());
        toolTypeTextView.setText(tool.getToolType().toString());
        String toolPosition = Double.toString(tool.getCoordinates().get(0).getLatitude()) + ", " + Double.toString(tool.getCoordinates().get(0).getLongitude());
        toolPositionTextView.setText(toolPosition);

        if(tool.getId() == null) {
            toolNotificationImageView.setVisibility(View.VISIBLE);
            toolNotificationImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Toast(v.getContext()).makeText(v.getContext(), R.string.notification_tool_not_reported, Toast.LENGTH_LONG).show();
                }
            });
        }

        highlightOldTool(true);
    }

    public ToolLogRow(Context context, ToolEntry tool, View.OnClickListener onClickListener) {
        super(context, R.layout.utility_row_tool_log_row);

        dateHeader = (TextView) getView().findViewById(R.id.tool_log_row_latest_date_text_view);
        toolTypeTextView = (TextView) getView().findViewById(R.id.tool_log_row_tool_type_text_view);
        toolPositionTextView = (TextView) getView().findViewById(R.id.tool_log_row_tool_position_text_view);
        toolNotificationImageView = (ImageView) getView().findViewById(R.id.tool_log_row_reported_image_view);
        editToolButton = (Button) getView().findViewById(R.id.tool_log_row_edit_tool_button);
        StringBuilder sb = new StringBuilder();

        dateHeader.setText(tool.getSetupDateTime().replace("T", " ").substring(0, 16));
        toolTypeTextView.setText(tool.getToolType().toString());

        sb.append(String.format(Locale.ENGLISH, "%.8f", tool.getCoordinates().get(0).getLatitude()));
        sb.append(", ");
        sb.append(String.format(Locale.ENGLISH, "%.8f", tool.getCoordinates().get(0).getLongitude()));

        String coordinateString = sb.toString();
        coordinateString = tool.getCoordinates().size() < 2 ? coordinateString.substring(0, sb.toString().length()) : coordinateString.substring(0, coordinateString.length()) + "\n..";

        toolPositionTextView.setText(coordinateString);
        editToolButton.setOnClickListener(onClickListener);
        editToolButton.setVisibility(View.VISIBLE);

        if(tool.getToolStatus() != ToolEntryStatus.STATUS_RECEIVED && tool.getToolStatus() != ToolEntryStatus.STATUS_REMOVED) {
            toolNotificationImageView.setVisibility(View.VISIBLE);
            toolNotificationImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Toast(v.getContext()).makeText(v.getContext(), R.string.notification_tool_not_reported, Toast.LENGTH_LONG).show();
                }
            });
        }

        highlightOldTool(true);
    }

    public String getDate() {
        return dateHeader.getText().toString();
    }

    public void setDate(String date) {
        dateHeader.setText(date);
    }

    public ToolType getToolType() {
        return ToolType.createFromValue(toolTypeTextView.getText().toString());
    }

    public void setToolType(ToolType toolType) {
        dateHeader.setText(toolType.toString());
    }

    public String getToolPosition() {
        return dateHeader.getText().toString();
    }

    public void setToolPosition(String position) {
        dateHeader.setText(position);
    }

    public void highlightOldTool(boolean highlight) {
        if(highlight) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date toolDate;
            Date currentDate = new Date();
            try {
                toolDate = sdf.parse(dateHeader.getText().toString());

                long diff = currentDate.getTime() - toolDate.getTime();
                double days = diff / super.getView().getContext().getResources().getInteger(R.integer.milliseconds_in_a_day);

                if(days > 14) {
                    dateHeader.setTextColor(ContextCompat.getColor(super.getView().getContext(), (R.color.error_red)));
                } else {
                    dateHeader.setTextColor(dateHeader.getTextColors().getDefaultColor());
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
        } else{
            dateHeader.setTextColor(dateHeader.getTextColors().getDefaultColor());
        }
    }

    public void setNotificationOnClickListener(View.OnClickListener onClickListener) {
        toolNotificationImageView.setOnClickListener(onClickListener);
    }

    public void setToolNotificationImageViewVisibility(boolean visible) {
        if(visible) {
            toolNotificationImageView.setVisibility(View.VISIBLE);
        } else {
            toolNotificationImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void setEditToolButtonOnClickListener(View.OnClickListener onClickListener) {
        editToolButton.setOnClickListener(onClickListener);
        editToolButton.setVisibility(onClickListener == null ? View.INVISIBLE : View.VISIBLE);
    }
}

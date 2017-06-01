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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntryStatus;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class ToolLogRow extends BaseTableRow {
    private TextView dateHeader;
    private TextView toolTypeTextView;
    private TextView toolPositionTextView;
    private ImageView toolNotificationImageView;
    private ImageView editToolImageView;
    private RelativeLayout relativeLayout;

    public ToolLogRow(Context context, final ToolEntry tool, View.OnClickListener onClickListener) {
        super(context, R.layout.utility_row_tool_log_row);

        dateHeader = (TextView) getView().findViewById(R.id.tool_log_row_latest_date_text_view);
        toolTypeTextView = (TextView) getView().findViewById(R.id.tool_log_row_tool_type_text_view);
        toolPositionTextView = (TextView) getView().findViewById(R.id.tool_log_row_tool_position_text_view);
        toolNotificationImageView = (ImageView) getView().findViewById(R.id.tool_log_row_reported_image_view);
        editToolImageView = (ImageView) getView().findViewById(R.id.tool_log_row_edit_image_view);
        relativeLayout = (RelativeLayout) getView().findViewById(R.id.tool_log_row_relative_layout);
        StringBuilder sb = new StringBuilder(tool.getCoordinates().get(0).getLatitude() < 0 ? "S" : "N");

        sb.append(FiskInfoUtility.decimalToDMS((tool.getCoordinates().get(0).getLatitude())));
        sb.append(", ");
        sb.append(tool.getCoordinates().get(0).getLongitude() < 0 ? "W" : "E");
        sb.append(FiskInfoUtility.decimalToDMS((tool.getCoordinates().get(0).getLongitude())));

        String coordinateString = sb.toString();
        coordinateString = tool.getCoordinates().size() < 2 ? coordinateString : coordinateString + "\n..";

        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss), Locale.getDefault());
        Date setupDate;
        String setupDateTime = "";
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            setupDate = sdf.parse(tool.getSetupDateTime());
            sdf.setTimeZone(TimeZone.getDefault());
            setupDateTime = sdf.format(setupDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        toolPositionTextView.setText(coordinateString);
        editToolImageView.setVisibility(View.VISIBLE);
        relativeLayout.setOnClickListener(onClickListener);
        toolTypeTextView.setText(tool.getToolType().toString());
        dateHeader.setText(setupDateTime.replace("T", " ").substring(0, 16));

        if(tool.getToolStatus() != ToolEntryStatus.STATUS_RECEIVED && tool.getToolStatus() != ToolEntryStatus.STATUS_REMOVED) {
            toolNotificationImageView.setVisibility(View.VISIBLE);
            toolNotificationImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int errorMessageId = -1;
                    switch(tool.getToolStatus()) {
                        case STATUS_REMOVED_UNCONFIRMED:
                        case STATUS_SENT_UNCONFIRMED:
                        case STATUS_TOOL_LOST_UNCONFIRMED:
                            errorMessageId = R.string.notification_tool_sent_unconfirmed_changes;
                            break;
                        case STATUS_UNREPORTED:
                        case STATUS_TOOL_LOST_UNREPORTED:
                            errorMessageId = R.string.notification_tool_not_reported;
                            break;
                        case STATUS_UNSENT:
                            errorMessageId = R.string.notification_tool_unreported_changes;
                        default:
                            break;
                    }

                    if(errorMessageId != -1) {
                        Toast.makeText(v.getContext(), errorMessageId, Toast.LENGTH_LONG).show();
                    }
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        toolNotificationImageView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setEditToolImageViewVisibility(boolean visible) {
        editToolImageView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setEditToolOnClickListener(View.OnClickListener onClickListener) {
        relativeLayout.setOnClickListener(onClickListener);
        editToolImageView.setVisibility(onClickListener == null ? View.INVISIBLE : View.VISIBLE);
    }
}
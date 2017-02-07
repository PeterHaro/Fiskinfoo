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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Locale;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class ToolConfirmationRow extends BaseTableRow{
    private TextView coordinatesTextView;
    private TextView ircsTextView;
    private TextView mmsiTextView;
    private TextView imoTextView;
    private TextView vesselNameTextView;
    private TextView vesselPhoneTextView;
    private TextView toolTypeNameTextView;
    private TextView commentTextView;
    private TextView setupTimeTextView;
    private TextView lastChangedDateTimeTextView;
    private TextView lastChangedBySourceTextView;
    private ImageView expandViewImageView;
    private CheckBox addToolCheckBox;
    private ToolEntry toolEntry;

    public ToolConfirmationRow(Context context, JSONObject tool) {
        super(context, R.layout.utility_row_tool_confirmation_row);

        coordinatesTextView = (TextView) getView().findViewById(R.id.tool_row_tool_position_text_view);
        ircsTextView = (TextView) getView().findViewById(R.id.tool_row_ircs_text_view);
        mmsiTextView = (TextView) getView().findViewById(R.id.tool_row_mmsi_text_view);
        imoTextView = (TextView) getView().findViewById(R.id.tool_row_imo_text_view);
        vesselNameTextView = (TextView) getView().findViewById(R.id.tool_row_vessel_name_text_view);
        vesselPhoneTextView = (TextView) getView().findViewById(R.id.tool_row_vessel_phone_text_view);
        toolTypeNameTextView = (TextView) getView().findViewById(R.id.tool_row_tool_type_text_view);
        commentTextView = (TextView) getView().findViewById(R.id.tool_row_comment_text_view);
        setupTimeTextView = (TextView) getView().findViewById(R.id.tool_row_setup_time_text_view);
        lastChangedDateTimeTextView = (TextView) getView().findViewById(R.id.tool_row_last_changed_text_view);
        lastChangedBySourceTextView = (TextView) getView().findViewById(R.id.tool_row_last_changed_by_source_text_view);
        expandViewImageView = (ImageView) getView().findViewById(R.id.tool_row_expand_view_image_view);
        addToolCheckBox = (CheckBox) getView().findViewById(R.id.tool_row_check_box);
        toolEntry = new ToolEntry(tool, context);

        setupTimeTextView.setText(toolEntry.getSetupDateTime().replace("T", " ").substring(0, 19));
        toolTypeNameTextView.setText(toolEntry.getToolType().toString());

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < toolEntry.getCoordinates().size() && i < 2; i++) {
            sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLatitude())));
            sb.append(", ");
            sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLongitude())));
            sb.append("\n");
        }

        String coordinateString = sb.toString();
        coordinateString = toolEntry.getCoordinates().size() < 2 ? coordinateString.substring(0, sb.toString().length() - 1) : coordinateString.substring(0, coordinateString.length() - 1) + "\n..";
        String lastChangedBySourceString = (toolEntry.getLastChangedBySource().equals("") ? "" : toolEntry.getLastChangedBySource().replace("T", " ").substring(0, toolEntry.getLastChangedBySource().length() - 5));


        coordinatesTextView.setText(coordinateString);
        vesselNameTextView.setText(context.getString(R.string.vessel_name) + ": " + toolEntry.getVesselName());
        ircsTextView.setText(context.getString(R.string.ircs) + ": " + toolEntry.getIRCS());
        mmsiTextView.setText(context.getString(R.string.mmsi) + ": " + toolEntry.getMMSI());
        imoTextView.setText(context.getString(R.string.imo) + ": " + toolEntry.getIMO());
        vesselPhoneTextView.setText(context.getString(R.string.vessel_phone) + ": " + toolEntry.getVesselPhone());
        commentTextView.setText(context.getString(R.string.comment_field_header) + ": " + toolEntry.getComment());
        lastChangedDateTimeTextView.setText(context.getString(R.string.last_updated) + ": " + toolEntry.getLastChangedDateTime().replace("T", " ").substring(0, toolEntry.getLastChangedDateTime().length() - 5));
        lastChangedBySourceTextView.setText(context.getString(R.string.last_updated_by_source) + ": " + lastChangedBySourceString);

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if(ircsTextView.getVisibility() == View.GONE) {
                    for(Point point : toolEntry.getCoordinates()) {
                        sb.append(FiskInfoUtility.decimalToDMS((point.getLatitude())));
                        sb.append(", ");
                        sb.append(FiskInfoUtility.decimalToDMS((point.getLongitude())));
                        sb.append("\n");
                    }

                    coordinatesTextView.setText(sb.toString().substring(0, sb.toString().length() - 1));

                    ircsTextView.setVisibility(!toolEntry.getIRCS().isEmpty() ? View.VISIBLE : View.GONE);
                    mmsiTextView.setVisibility(!toolEntry.getMMSI().isEmpty() ? View.VISIBLE : View.GONE);
                    imoTextView.setVisibility(!toolEntry.getIMO().isEmpty() ? View.VISIBLE : View.GONE);
                    vesselPhoneTextView.setVisibility(!toolEntry.getVesselPhone().isEmpty() ? View.VISIBLE : View.GONE);
                    commentTextView.setVisibility(!toolEntry.getComment().isEmpty() ? View.VISIBLE : View.GONE);
                    lastChangedDateTimeTextView.setVisibility(!toolEntry.getLastChangedDateTime().isEmpty() ? View.VISIBLE : View.GONE);
                    lastChangedBySourceTextView.setVisibility(!toolEntry.getLastChangedBySource().isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    ircsTextView.setVisibility(View.GONE);
                    mmsiTextView.setVisibility(View.GONE);
                    imoTextView.setVisibility(View.GONE);
                    vesselPhoneTextView.setVisibility(View.GONE);
                    commentTextView.setVisibility(View.GONE);
                    lastChangedDateTimeTextView.setVisibility(View.GONE);
                    lastChangedBySourceTextView.setVisibility(View.GONE);

                    for(int i = 0; i < toolEntry.getCoordinates().size() && i < 2; i++) {
                        sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLatitude())));
                        sb.append(", ");
                        sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLongitude())));
                        sb.append("\n");
                    }

                    String coordinateString = sb.toString();
                    coordinateString = toolEntry.getCoordinates().size() < 2 ? coordinateString.substring(0, sb.toString().length() - 1) : coordinateString.substring(0, coordinateString.length() - 1) + "\n..";
                    coordinatesTextView.setText(coordinateString);
                }
            }
        });
    }

    public ToolConfirmationRow(Context context, ToolEntry tool) {
        super(context, R.layout.utility_row_tool_confirmation_row);

        coordinatesTextView = (TextView) getView().findViewById(R.id.tool_row_tool_position_text_view);
        ircsTextView = (TextView) getView().findViewById(R.id.tool_row_ircs_text_view);
        mmsiTextView = (TextView) getView().findViewById(R.id.tool_row_mmsi_text_view);
        imoTextView = (TextView) getView().findViewById(R.id.tool_row_imo_text_view);
        vesselNameTextView = (TextView) getView().findViewById(R.id.tool_row_vessel_name_text_view);
        vesselPhoneTextView = (TextView) getView().findViewById(R.id.tool_row_vessel_phone_text_view);
        toolTypeNameTextView = (TextView) getView().findViewById(R.id.tool_row_tool_type_text_view);
        commentTextView = (TextView) getView().findViewById(R.id.tool_row_comment_text_view);
        setupTimeTextView = (TextView) getView().findViewById(R.id.tool_row_setup_time_text_view);
        lastChangedDateTimeTextView = (TextView) getView().findViewById(R.id.tool_row_last_changed_text_view);
        lastChangedBySourceTextView = (TextView) getView().findViewById(R.id.tool_row_last_changed_by_source_text_view);
        expandViewImageView = (ImageView) getView().findViewById(R.id.tool_row_expand_view_image_view);
        addToolCheckBox = (CheckBox) getView().findViewById(R.id.tool_row_check_box);
        toolEntry = tool;

        addToolCheckBox.setVisibility(View.GONE);

        setupTimeTextView.setText(toolEntry.getSetupDateTime().replace("T", " ").substring(0, 19));
        toolTypeNameTextView.setText(toolEntry.getToolType().toString());

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < toolEntry.getCoordinates().size() && i < 2; i++) {
            sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLatitude())));
            sb.append(", ");
            sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLongitude())));
            sb.append("\n");
        }

        String coordinateString = sb.toString();
        coordinateString = toolEntry.getCoordinates().size() < 2 ? coordinateString.substring(0, sb.toString().length() - 1) : coordinateString.substring(0, coordinateString.length() - 1) + "\n..";
        String lastChangedBySourceString = (toolEntry.getLastChangedBySource().equals("") ? "" : toolEntry.getLastChangedBySource().replace("T", " ").substring(0, toolEntry.getLastChangedBySource().length() - 5));


        coordinatesTextView.setText(coordinateString);
        vesselNameTextView.setText(context.getString(R.string.vessel_name) + ": " + toolEntry.getVesselName());
        ircsTextView.setText(context.getString(R.string.ircs) + ": " + toolEntry.getIRCS());
        mmsiTextView.setText(context.getString(R.string.mmsi) + ": " + toolEntry.getMMSI());
        imoTextView.setText(context.getString(R.string.imo) + ": " + toolEntry.getIMO());
        vesselPhoneTextView.setText(context.getString(R.string.vessel_phone) + ": " + toolEntry.getVesselPhone());
        commentTextView.setText(context.getString(R.string.comment_field_header) + ": " + toolEntry.getComment());
        lastChangedDateTimeTextView.setText(context.getString(R.string.last_updated) + ": " + toolEntry.getLastChangedDateTime().replace("T", " ").substring(0, toolEntry.getLastChangedDateTime().length() - 5));
        lastChangedBySourceTextView.setText(context.getString(R.string.last_updated_by_source) + ": " + lastChangedBySourceString);

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                if(ircsTextView.getVisibility() == View.GONE) {
                    for(Point point : toolEntry.getCoordinates()) {
                        sb.append(FiskInfoUtility.decimalToDMS((point.getLatitude())));
                        sb.append(", ");
                        sb.append(FiskInfoUtility.decimalToDMS((point.getLongitude())));
                        sb.append("\n");
                    }

                    coordinatesTextView.setText(sb.toString().substring(0, sb.toString().length() - 1));

                    ircsTextView.setVisibility(!toolEntry.getIRCS().isEmpty() ? View.VISIBLE : View.GONE);
                    mmsiTextView.setVisibility(!toolEntry.getMMSI().isEmpty() ? View.VISIBLE : View.GONE);
                    imoTextView.setVisibility(!toolEntry.getIMO().isEmpty() ? View.VISIBLE : View.GONE);
                    vesselPhoneTextView.setVisibility(!toolEntry.getVesselPhone().isEmpty() ? View.VISIBLE : View.GONE);
                    commentTextView.setVisibility(!toolEntry.getComment().isEmpty() ? View.VISIBLE : View.GONE);
                    lastChangedDateTimeTextView.setVisibility(!toolEntry.getLastChangedDateTime().isEmpty() ? View.VISIBLE : View.GONE);
                    lastChangedBySourceTextView.setVisibility(!toolEntry.getLastChangedBySource().isEmpty() ? View.VISIBLE : View.GONE);

                } else {
                    ircsTextView.setVisibility(View.GONE);
                    mmsiTextView.setVisibility(View.GONE);
                    imoTextView.setVisibility(View.GONE);
                    vesselPhoneTextView.setVisibility(View.GONE);
                    commentTextView.setVisibility(View.GONE);
                    lastChangedDateTimeTextView.setVisibility(View.GONE);
                    lastChangedBySourceTextView.setVisibility(View.GONE);

                    for(int i = 0; i < toolEntry.getCoordinates().size() && i < 2; i++) {
                        sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLatitude())));
                        sb.append(", ");
                        sb.append(FiskInfoUtility.decimalToDMS((toolEntry.getCoordinates().get(i).getLongitude())));
                        sb.append("\n");
                    }

                    String coordinateString = sb.toString();
                    coordinateString = toolEntry.getCoordinates().size() < 2 ? coordinateString.substring(0, sb.toString().length() - 1) : coordinateString.substring(0, coordinateString.length() - 1) + "\n..";
                    coordinatesTextView.setText(coordinateString);
                }
            }
        });
    }

    public ToolEntry getToolEntry() {
        return toolEntry;
    }

    public boolean isChecked() {
        return addToolCheckBox.isChecked();
    }
}

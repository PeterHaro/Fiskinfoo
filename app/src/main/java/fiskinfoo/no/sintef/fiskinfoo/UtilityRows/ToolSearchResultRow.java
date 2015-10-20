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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Feature;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.LineFeature;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.PointFeature;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class ToolSearchResultRow extends BaseTableRow {
    private ImageView toolTypeImageView;
    private TextView vesselNameTextView;
    private TextView toolTypeTextView;
    private TextView phoneNumberTextView;
    private TextView dateTextView;
    private TextView positionTextView;

    public ToolSearchResultRow(Context context) {
        super(context, R.layout.utility_row_tool_search_result);

        toolTypeImageView = (ImageView) getView().findViewById(R.id.tool_search_result_row_tool_type_image_view);
        vesselNameTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_vessel_name_text_view);
        toolTypeTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_tool_type_text_view);
        phoneNumberTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_phone_number_text_view);
        dateTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_date_text_view);
        positionTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_position_text_view);
    }

    public ToolSearchResultRow(Context context, int toolImageId, String vesselName, String toolType, String phoneNumber, String date, String position) {
        super(context, R.layout.utility_row_tool_search_result);

        toolTypeImageView = (ImageView) getView().findViewById(R.id.tool_search_result_row_tool_type_image_view);
        vesselNameTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_vessel_name_text_view);
        toolTypeTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_tool_type_text_view);
        phoneNumberTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_phone_number_text_view);
        dateTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_date_text_view);
        positionTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_position_text_view);

        toolTypeImageView.setImageResource(toolImageId);
        vesselNameTextView.setText(vesselName);
        toolTypeTextView.setText(toolType);
        phoneNumberTextView.setText(phoneNumber);
        dateTextView.setText(date);
        positionTextView.setText(position);
    }

    public ToolSearchResultRow(Context context, int toolImageId, Feature feature) {
        super(context, R.layout.utility_row_tool_search_result);


        toolTypeImageView = (ImageView) getView().findViewById(R.id.tool_search_result_row_tool_type_image_view);
        vesselNameTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_vessel_name_text_view);
        toolTypeTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_tool_type_text_view);
        phoneNumberTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_phone_number_text_view);
        dateTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_date_text_view);
        positionTextView = (TextView) getView().findViewById(R.id.tool_search_result_row_position_text_view);

        StringBuilder sb = new StringBuilder();
        String vesselName = feature.properties.vesselname != null ? feature.properties.vesselname : context.getString(R.string.vessel_name_na);
        String date = feature.properties.setupdatetime != null ? context.getString(R.string.tool_set_date) + " " + feature.properties.setupdatetime.replace("T", " ").replace("Z", "") : context.getString(R.string.tool_set_date_na);
        String toolType = feature.properties.tooltypename != null ? context.getString(R.string.tool_type_with_colon) + " " + feature.properties.tooltypename : context.getString(R.string.tool_type_na);
        String vesselNumber = feature.properties.vesselphone != null ? context.getString(R.string.vessel_phone) + " " + feature.properties.vesselphone : context.getString(R.string.vessel_phone_na);

        if(feature instanceof PointFeature) {
            sb.append("\n");
            sb.append(((PointFeature)feature).geometry.coordinates[0]);
            sb.append(", ");
            sb.append(((PointFeature)feature).geometry.coordinates[1]);
        } else {
            for(List<Double> coordinates : ((LineFeature)feature).geometry.coordinates) {
                sb.append("\n(");
                sb.append(coordinates.get(0));
                sb.append(", ");
                sb.append(coordinates.get(1));
                sb.append("),");
            }
        }

        if(!date.equals(context.getString(R.string.tool_set_date_na)) && date.contains(".")) {
            date = date.substring(0, date.indexOf('.'));
        }

        if(vesselNumber.contains(";")) {
            vesselNumber.replace(';', '\n');
        }

        vesselNameTextView.setText(vesselName);
        toolTypeTextView.setText(toolType);
        phoneNumberTextView.setText(vesselNumber);
        dateTextView.setText(date);
        positionTextView.setText(context.getString(R.string.tool_pos) + sb.toString());
    }

    public String getVesselName() {
        return vesselNameTextView.getText().toString();
    }

    public void setVesselName(String vesselName) {
        vesselNameTextView.setText(vesselName);
    }

    public String getToolType() {
        return toolTypeTextView.getText().toString();
    }

    public void setToolType(String toolType) {
        toolTypeTextView.setText(toolType);
    }

    public String getPhoneNumber() {
        return phoneNumberTextView.getText().toString();
    }

    public void setPhoneNumber(String phoneNumber) {
        phoneNumberTextView.setText(phoneNumber);
    }

    public String getDate() {
        return dateTextView.getText().toString();
    }

    public void setDate(String date) {
        phoneNumberTextView.setText(date);
    }

    public String getPosition() {
        return positionTextView.getText().toString();
    }

    public void setPosition(String position) {
        phoneNumberTextView.setText(position);
    }

    public void setDateTextViewTextColor(int colorId) {
        dateTextView.setTextColor(colorId);
    }
}

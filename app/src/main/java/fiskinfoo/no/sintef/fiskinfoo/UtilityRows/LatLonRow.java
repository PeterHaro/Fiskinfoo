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
import android.widget.EditText;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class LatLonRow extends BaseTableRow {
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button setPositionButton;
    private GpsLocationTracker locationTracker;

    public LatLonRow(Context context, GpsLocationTracker gpsLocationTracker) {
        super(context, R.layout.utility_row_lat_lon_row);

        latitudeEditText = (EditText) super.getView().findViewById(R.id.utility_lat_lon_row_latitude_edit_text);
        longitudeEditText = (EditText) super.getView().findViewById(R.id.utility_lat_lon_row_longitude_edit_text);
        setPositionButton = (Button) super.getView().findViewById(R.id.utility_lat_lon_row_set_position_button);
        locationTracker = gpsLocationTracker;

        setPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitudeEditText.setText(Double.toString(locationTracker.getLatitude()));
                longitudeEditText.setText(Double.toString(locationTracker.getLongitude()));
            }
        });
        setPositionButton.setVisibility(gpsLocationTracker == null ? View.GONE : View.VISIBLE);
    }

    public void setEditable(boolean editable) {
        latitudeEditText.setClickable(editable);
        latitudeEditText.setFocusable(editable);

        longitudeEditText.setClickable(editable);
        longitudeEditText.setFocusable(editable);
    }

    public void setCoordinates(Point position) {
        latitudeEditText.setText(Double.toString(position.getLatitude()));
        longitudeEditText.setText(Double.toString(position.getLongitude()));
    }

    public String getLatitude() {
        return latitudeEditText.getText().toString();
    }

    public void setLatitude(String latitude) {
        latitudeEditText.setText(latitude);
    }

    public String getLongitude() {
        return longitudeEditText.getText().toString();
    }

    public void setLongitude(String longitude) {
        longitudeEditText.setText(longitude);
    }

    public Point getCoordinates() {
        Point point;
        double latitude = Double.NaN;
        double longitude = Double.NaN;

        try {
            Double.parseDouble(latitudeEditText.getText().toString().trim());
            Double.parseDouble(longitudeEditText.getText().toString().trim());
        } catch(NumberFormatException e) {
            if(Double.isNaN(latitude)) {
                latitudeEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
            } else if(Double.isNaN(longitude)) {
                longitudeEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            }

            return null;
        }

        point = new Point(Double.parseDouble(latitudeEditText.getText().toString().trim()), Double.parseDouble(longitudeEditText.getText().toString().trim()));

        return point;
    }
}

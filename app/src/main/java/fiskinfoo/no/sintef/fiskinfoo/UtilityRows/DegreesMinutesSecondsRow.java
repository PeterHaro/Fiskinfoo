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

import android.app.Activity;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Interface.LocationProviderInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class DegreesMinutesSecondsRow extends BaseTableRow  {
    private EditText latitudeDegreesEditText;
    private EditText latitudeMinutesEditText;
    private EditText latitudeSecondsEditText;
    private EditText longitudeDegreesEditText;
    private EditText longitudeMinutesEditText;
    private EditText longitudeSecondsEditText;
    private Button setPositionButton;
    private LocationProviderInterface mLocationProvider;

    public DegreesMinutesSecondsRow(final Activity activity, LocationProviderInterface gpsLocationTracker) {
        super(activity, R.layout.utility_row_degrees_minutes_seconds_row);



        latitudeDegreesEditText = (EditText) super.getView().findViewById(R.id.utility_dms_row_latitude_degrees_edit_text);
        latitudeMinutesEditText = (EditText) super.getView().findViewById(R.id.utility_dms_row_latitude_minutes_edit_text);
        latitudeSecondsEditText = (EditText) super.getView().findViewById(R.id.utility_dms_row_latitude_seconds_edit_text);
        longitudeDegreesEditText = (EditText) super.getView().findViewById(R.id.utility_dms_row_longitude_degrees_edit_text);
        longitudeMinutesEditText = (EditText) super.getView().findViewById(R.id.utility_dms_row_longitude_minutes_edit_text);
        longitudeSecondsEditText = (EditText) super.getView().findViewById(R.id.utility_dms_row_longitude_seconds_edit_text);
        setPositionButton = (Button) super.getView().findViewById(R.id.utility_lat_lon_row_set_position_button);
        mLocationProvider = gpsLocationTracker;

        setPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = mLocationProvider.getLatitude();
                double longitude = mLocationProvider.getLongitude();

                double[] latitudeDMS = FiskInfoUtility.decimalToDMSArray(latitude);
                double[] longitudeDMS = FiskInfoUtility.decimalToDMSArray(longitude);

                latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
                latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
                latitudeSecondsEditText.setText(String.valueOf(latitudeDMS[2]));
                longitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
                longitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
                longitudeSecondsEditText.setText(String.valueOf(longitudeDMS[2]));
            }
        });
        setPositionButton.setVisibility(gpsLocationTracker == null ? View.GONE : View.VISIBLE);
    }

    public void setEditable(boolean editable) {
        latitudeDegreesEditText.setClickable(editable);
        latitudeDegreesEditText.setFocusable(editable);
        latitudeMinutesEditText.setClickable(editable);
        latitudeMinutesEditText.setFocusable(editable);
        latitudeSecondsEditText.setClickable(editable);
        latitudeSecondsEditText.setFocusable(editable);
        longitudeDegreesEditText.setClickable(editable);
        longitudeDegreesEditText.setFocusable(editable);
        longitudeMinutesEditText.setClickable(editable);
        longitudeMinutesEditText.setFocusable(editable);
        longitudeSecondsEditText.setClickable(editable);
        longitudeSecondsEditText.setFocusable(editable);
    }

    public void setCoordinates(Point position) {
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();

        double[] latitudeDMS = FiskInfoUtility.decimalToDMSArray(latitude);
        double[] longitudeDMS = FiskInfoUtility.decimalToDMSArray(longitude);

        latitudeDegreesEditText.setText(String.valueOf((int) latitudeDMS[0]));
        latitudeMinutesEditText.setText(String.valueOf((int) latitudeDMS[1]));
        latitudeSecondsEditText.setText(String.valueOf((int) latitudeDMS[2]));
        longitudeDegreesEditText.setText(String.valueOf((int) longitudeDMS[0]));
        longitudeMinutesEditText.setText(String.valueOf((int) longitudeDMS[1]));
        longitudeSecondsEditText.setText(String.valueOf((int) longitudeDMS[2]));
    }

    public String getLatitude() {
        double latitudeDegrees = Double.NaN;
        double latitudeMinutes = Double.NaN;
        double latitudeSeconds;
        double latitude;

        try {
            latitudeDegrees = Double.parseDouble(latitudeDegreesEditText.getText().toString().trim());
            latitudeMinutes = Double.parseDouble(latitudeMinutesEditText.getText().toString().trim());
            latitudeSeconds = Double.parseDouble(latitudeSecondsEditText.getText().toString().trim());

            int minLatDegree = latitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxLatDegree = latitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minLatMinute = latitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxLatMinute = latitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);
            int minLatSecond = latitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_seconds_value_min);
            int maxLatSecond = latitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_seconds_value_max);

            if(minLatDegree > latitudeDegrees || maxLatDegree < latitudeDegrees) {
                latitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
                return null;
            } else if(minLatMinute > latitudeMinutes || maxLatMinute < latitudeMinutes) {
                latitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
                return null;
            } else if(minLatSecond > latitudeSeconds || maxLatSecond < latitudeSeconds) {
                latitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
                return null;
            }

            latitude = FiskInfoUtility.DMSToDecimal(new double[] { latitudeDegrees, latitudeMinutes, latitudeSeconds });
        } catch(NumberFormatException e) {
            if(Double.isNaN(latitudeDegrees)) {
                latitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
            } else if(Double.isNaN(latitudeMinutes)) {
                latitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            } else {
                latitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            }

            return null;
        }

        return Double.toString(latitude);
    }

    public void setLatitude(String latitude) {
        double[] latitudeDMS = FiskInfoUtility.decimalToDMSArray(Double.valueOf(latitude));

        latitudeDegreesEditText.setText(String.valueOf((int) latitudeDMS[0]));
        latitudeMinutesEditText.setText(String.valueOf((int) latitudeDMS[1]));
        latitudeSecondsEditText.setText(String.valueOf((int) latitudeDMS[2]));
    }

    public String getLongitude() {
        double longitudeDegrees = Double.NaN;
        double longitudeMinutes = Double.NaN;
        double longitudeSeconds;
        double longitude;

        try {
            longitudeDegrees = Double.parseDouble(longitudeDegreesEditText.getText().toString().trim());
            longitudeMinutes = Double.parseDouble(longitudeMinutesEditText.getText().toString().trim());
            longitudeSeconds = Double.parseDouble(longitudeSecondsEditText.getText().toString().trim());

            int minLonDegree = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxLonDegree = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minLonMinute = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxLonMinute = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);
            int minLonSecond = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_seconds_value_min);
            int maxLonSecond = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_seconds_value_max);

            if(minLonDegree > longitudeDegrees || maxLonDegree < longitudeDegrees) {
                latitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
                return null;
            } else if(minLonMinute > longitudeMinutes || maxLonMinute < longitudeMinutes) {
                latitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
                return null;
            } else if(minLonSecond > longitudeSeconds || maxLonSecond < longitudeSeconds) {
                latitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
                return null;
            }

            longitude = FiskInfoUtility.DMSToDecimal(new double[] { longitudeDegrees, longitudeMinutes, longitudeSeconds });
        } catch(NumberFormatException e) {
            if(Double.isNaN(longitudeDegrees)) {
                latitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
            } else if(Double.isNaN(longitudeMinutes)) {
                latitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            } else {
                latitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            }

            return null;
        }

        return Double.toString(longitude);
    }

    public void setLongitude(String longitude) {
        double[] longitudeDMS = FiskInfoUtility.decimalToDMSArray(Double.valueOf(longitude));

        latitudeDegreesEditText.setText(String.valueOf((int) longitudeDMS[0]));
        latitudeMinutesEditText.setText(String.valueOf((int) longitudeDMS[1]));
        latitudeSecondsEditText.setText(String.valueOf((int) longitudeDMS[2]));
    }

    public Point getCoordinates() {
        Point point;
        double latitude;
        double longitude;

        double latitudeDegrees = Double.NaN;
        double latitudeMinutes = Double.NaN;
        double latitudeSeconds = Double.NaN;
        double longitudeDegrees = Double.NaN;
        double longitudeMinutes = Double.NaN;
        double longitudeSeconds;

        try {
            latitudeDegrees = Double.parseDouble(latitudeDegreesEditText.getText().toString().trim());
            latitudeMinutes = Double.parseDouble(latitudeMinutesEditText.getText().toString().trim());
            latitudeSeconds = Double.parseDouble(latitudeSecondsEditText.getText().toString().trim());
            longitudeDegrees = Double.parseDouble(longitudeDegreesEditText.getText().toString().trim());
            longitudeMinutes = Double.parseDouble(longitudeMinutesEditText.getText().toString().trim());
            longitudeSeconds = Double.parseDouble(longitudeSecondsEditText.getText().toString().trim());

            int minDegree = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxDegree = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minMinute = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxMinute = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);
            int minSecond = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_seconds_value_min);
            int maxSecond = longitudeDegreesEditText.getContext().getResources().getInteger(R.integer.valid_seconds_value_max);

            if(minDegree > latitudeDegrees || maxDegree < latitudeDegrees) {
                latitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_format));
                return null;
            } else if(minMinute > latitudeMinutes || maxMinute < latitudeMinutes) {
                latitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_format));
                return null;
            } else if(minSecond > latitudeSeconds || maxSecond < latitudeSeconds) {
                latitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_format));
                return null;
            } else if(minDegree > longitudeDegrees || maxDegree < longitudeDegrees) {
                longitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_format));
                return null;
            } else if(minMinute > longitudeMinutes || maxMinute < longitudeMinutes) {
                longitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_format));
                return null;
            } else if(minSecond > longitudeSeconds || maxSecond < longitudeSeconds) {
                longitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_format));
                return null;
            }

            latitude = FiskInfoUtility.DMSToDecimal(new double[] { latitudeDegrees, latitudeMinutes, latitudeSeconds });
            longitude = FiskInfoUtility.DMSToDecimal(new double[] { longitudeDegrees, longitudeMinutes, longitudeSeconds });
        } catch(NumberFormatException e) {
            if(Double.isNaN(latitudeDegrees)) {
                latitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
            } else if(Double.isNaN(latitudeMinutes)) {
                latitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            } else if(Double.isNaN(latitudeSeconds)) {
                latitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            } else if(Double.isNaN(longitudeDegrees)) {
                longitudeDegreesEditText.setError(getView().getContext().getString(R.string.error_invalid_latitude));
            } else if(Double.isNaN(longitudeMinutes)) {
                longitudeMinutesEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            } else {
                longitudeSecondsEditText.setError(getView().getContext().getString(R.string.error_invalid_longitude));
            }

            return null;
        }

        point = new Point(latitude, longitude);

        return point;
    }

    public void SetPositionButtonOnClickListener(View.OnClickListener onClickListener) {
        if(onClickListener != null) {
            setPositionButton.setOnClickListener(onClickListener);
        } else {
            setPositionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double latitude = mLocationProvider.getLatitude();
                    double longitude = mLocationProvider.getLongitude();

                    double[] latitudeDMS = FiskInfoUtility.decimalToDMSArray(latitude);
                    double[] longitudeDMS = FiskInfoUtility.decimalToDMSArray(longitude);

                    latitudeDegreesEditText.setText(String.valueOf((int) latitudeDMS[0]));
                    latitudeMinutesEditText.setText(String.valueOf((int) latitudeDMS[1]));
                    latitudeSecondsEditText.setText(String.valueOf((int) latitudeDMS[2]));
                    longitudeDegreesEditText.setText(String.valueOf((int) longitudeDMS[0]));
                    longitudeMinutesEditText.setText(String.valueOf((int) longitudeDMS[1]));
                    longitudeSecondsEditText.setText(String.valueOf((int) longitudeDMS[2]));
                }
            });
        }
    }

    public void setTextWatcher(TextWatcher watcher) {
        latitudeDegreesEditText.addTextChangedListener(watcher);
        latitudeMinutesEditText.addTextChangedListener(watcher);
        latitudeSecondsEditText.addTextChangedListener(watcher);
        longitudeDegreesEditText.addTextChangedListener(watcher);
        longitudeMinutesEditText.addTextChangedListener(watcher);
        longitudeSecondsEditText.addTextChangedListener(watcher);
    }
}

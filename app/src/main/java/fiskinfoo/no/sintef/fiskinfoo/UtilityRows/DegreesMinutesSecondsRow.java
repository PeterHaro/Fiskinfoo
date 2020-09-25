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
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.CoordinateFormat;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ICoordinateRow;
import fiskinfoo.no.sintef.fiskinfoo.Interface.LocationProviderInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class DegreesMinutesSecondsRow extends CoordinateRow implements ICoordinateRow{
    private EditText latitudeDegreesEditText;
    private EditText latitudeMinutesEditText;
    private EditText latitudeSecondsEditText;
    private EditText longitudeDegreesEditText;
    private EditText longitudeMinutesEditText;
    private EditText longitudeSecondsEditText;
    private SwitchCompat latitudeCardinalDirectionSwitch;
    private SwitchCompat longitudeCardinalDirectionSwitch;
    private Button setPositionButton;
    private LocationProviderInterface mLocationProvider;

    public DegreesMinutesSecondsRow(final Activity activity, LocationProviderInterface gpsLocationTracker) {
        super(activity, R.layout.utility_row_degrees_minutes_seconds_row);

        latitudeDegreesEditText = super.getView().findViewById(R.id.utility_dms_row_latitude_degrees_edit_text);
        latitudeMinutesEditText = super.getView().findViewById(R.id.utility_dms_row_latitude_minutes_edit_text);
        latitudeSecondsEditText = super.getView().findViewById(R.id.utility_dms_row_latitude_seconds_edit_text);
        longitudeDegreesEditText = super.getView().findViewById(R.id.utility_dms_row_longitude_degrees_edit_text);
        longitudeMinutesEditText = super.getView().findViewById(R.id.utility_dms_row_longitude_minutes_edit_text);
        longitudeSecondsEditText = super.getView().findViewById(R.id.utility_dms_row_longitude_seconds_edit_text);
        latitudeCardinalDirectionSwitch = super.getView().findViewById(R.id.utility_dms_row_latitude_cardinal_direction_switch);
        longitudeCardinalDirectionSwitch = super.getView().findViewById(R.id.utility_dms_row_longitude_cardinal_direction_switch);
        setPositionButton = super.getView().findViewById(R.id.utility_lat_lon_row_set_position_button);
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
                latitudeCardinalDirectionSwitch.setChecked(latitude < 0);
                longitudeCardinalDirectionSwitch.setChecked(longitude >= 0);
            }
        });
        setPositionButton.setVisibility(gpsLocationTracker == null ? View.GONE : View.VISIBLE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPositionButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_active_tint_color));
        }
    }

    @Override
    public DegreesMinutesSecondsRow initRow(Activity activity, LocationProviderInterface gpsLocationTracker) {
        return new DegreesMinutesSecondsRow(activity, gpsLocationTracker);
    }

    @Override
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

    @Override
    public CoordinateFormat getCoordinateFormat() {
        return CoordinateFormat.DEGREES_MINUTES_SECONDS;
    }

    @Override
    public void setCoordinates(Point position) {
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();

        double[] latitudeDMS = FiskInfoUtility.decimalToDMSArray(latitude);
        double[] longitudeDMS = FiskInfoUtility.decimalToDMSArray(longitude);

        latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
        latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
        latitudeSecondsEditText.setText(String.valueOf(latitudeDMS[2]));
        longitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
        longitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
        longitudeSecondsEditText.setText(String.valueOf(longitudeDMS[2]));
        latitudeCardinalDirectionSwitch.setChecked(latitude < 0);
        longitudeCardinalDirectionSwitch.setChecked(longitude >= 0);
    }

    @Override
    public String getLatitude() {
        double latitudeDegrees = Double.NaN;
        double latitudeMinutes = Double.NaN;
        double latitudeSeconds = Double.NaN;
        boolean fieldValid;
        boolean valid;

        try {
            latitudeDegrees = Double.parseDouble(latitudeDegreesEditText.getText().toString().trim());
            latitudeMinutes = Double.parseDouble(latitudeMinutesEditText.getText().toString().trim());
            latitudeSeconds = Double.parseDouble(latitudeSecondsEditText.getText().toString().trim());

            int minLatDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxLatDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minLatMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxLatMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);
            int minLatSecond = super.getContext().getResources().getInteger(R.integer.valid_seconds_value_min);
            int maxLatSecond = super.getContext().getResources().getInteger(R.integer.valid_seconds_value_max);

            valid = fieldValid = !(minLatDegree > latitudeDegrees || maxLatDegree < latitudeDegrees);
            latitudeDegreesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minLatMinute > latitudeMinutes || maxLatMinute < latitudeMinutes)) && valid;
            latitudeMinutesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minLatSecond > latitudeSeconds || maxLatSecond < latitudeSeconds)) && valid;
            latitudeSecondsEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            if(!valid) {
                return null;
            }

            return Double.toString(FiskInfoUtility.DMSToDecimal(new double[] { latitudeDegrees, latitudeMinutes, latitudeSeconds }) * (latitudeCardinalDirectionSwitch.isChecked() ? -1 : 1));
        } catch(NumberFormatException e) {
            if(Double.isNaN(latitudeDegrees)) {
                latitudeDegreesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }
            if(Double.isNaN(latitudeMinutes)) {
                latitudeMinutesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }
            if(Double.isNaN(latitudeSeconds)){
                latitudeSecondsEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }

            return null;
        }
    }

    @Override
    public void setLatitude(String latitude) {
        double[] latitudeDMS = FiskInfoUtility.decimalToDMSArray(Double.valueOf(latitude));

        latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
        latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
        latitudeSecondsEditText.setText(String.valueOf(latitudeDMS[2]));
        latitudeCardinalDirectionSwitch.setChecked(Double.valueOf(latitude) < 0);
    }

    @Override
    public String getLongitude() {
        double longitudeDegrees = Double.NaN;
        double longitudeMinutes = Double.NaN;
        double longitudeSeconds = Double.NaN;
        boolean fieldValid;
        boolean valid;

        try {
            longitudeDegrees = Double.parseDouble(longitudeDegreesEditText.getText().toString().trim());
            longitudeMinutes = Double.parseDouble(longitudeMinutesEditText.getText().toString().trim());
            longitudeSeconds = Double.parseDouble(longitudeSecondsEditText.getText().toString().trim());

            int minLonDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxLonDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minLonMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxLonMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);
            int minLonSecond = super.getContext().getResources().getInteger(R.integer.valid_seconds_value_min);
            int maxLonSecond = super.getContext().getResources().getInteger(R.integer.valid_seconds_value_max);

            valid = fieldValid = !(minLonDegree > longitudeDegrees || maxLonDegree < longitudeDegrees);
            longitudeDegreesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minLonMinute > longitudeMinutes || maxLonMinute < longitudeMinutes)) && valid;
            longitudeMinutesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minLonSecond > longitudeSeconds || maxLonSecond < longitudeSeconds)) && valid;

            longitudeSecondsEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            if(!valid) {
                return null;
            }

            return Double.toString(FiskInfoUtility.DMSToDecimal(new double[] { longitudeDegrees, longitudeMinutes, longitudeSeconds }) * (latitudeCardinalDirectionSwitch.isChecked() ? -1 : 1));
        } catch(NumberFormatException e) {
            if(Double.isNaN(longitudeDegrees)) {
                longitudeDegreesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }
            if(Double.isNaN(longitudeMinutes)) {
                longitudeMinutesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }
            if(Double.isNaN(longitudeSeconds)) {
                longitudeSecondsEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }

            return null;
        }
    }

    @Override
    public void setLongitude(String longitude) {
        double[] longitudeDMS = FiskInfoUtility.decimalToDMSArray(Double.valueOf(longitude));

        latitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
        latitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
        latitudeSecondsEditText.setText(String.valueOf(longitudeDMS[2]));
        longitudeCardinalDirectionSwitch.setChecked(Double.valueOf(longitude) >= 0);
    }

    @Override
    public Point getCoordinates() {
        double latitude;
        double longitude;

        double latitudeDegrees = Double.NaN;
        double latitudeMinutes = Double.NaN;
        double latitudeSeconds = Double.NaN;
        double longitudeDegrees = Double.NaN;
        double longitudeMinutes = Double.NaN;
        double longitudeSeconds = Double.NaN;
        boolean fieldValid;
        boolean valid;

        try {
            latitudeDegrees = !latitudeDegreesEditText.getText().toString().equals("") ? Double.parseDouble(latitudeDegreesEditText.getText().toString().trim()) : 0;
            latitudeMinutes = !latitudeMinutesEditText.getText().toString().equals("") ? Double.parseDouble(latitudeMinutesEditText.getText().toString().trim()) : 0;
            latitudeSeconds = !latitudeSecondsEditText.getText().toString().equals("") ? Double.parseDouble(latitudeSecondsEditText.getText().toString().trim()) : 0;
            longitudeDegrees = !longitudeDegreesEditText.getText().toString().equals("") ? Double.parseDouble(longitudeDegreesEditText.getText().toString().trim()) : 0;
            longitudeMinutes = !longitudeMinutesEditText.getText().toString().equals("") ? Double.parseDouble(longitudeMinutesEditText.getText().toString().trim()) : 0;
            longitudeSeconds = !longitudeSecondsEditText.getText().toString().equals("") ? Double.parseDouble(longitudeSecondsEditText.getText().toString().trim()) : 0;

            int minDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);
            int minSecond = super.getContext().getResources().getInteger(R.integer.valid_seconds_value_min);
            int maxSecond = super.getContext().getResources().getInteger(R.integer.valid_seconds_value_max);

            valid = fieldValid = !(minDegree > latitudeDegrees || maxDegree < latitudeDegrees);
            latitudeDegreesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minMinute > latitudeMinutes || maxMinute < latitudeMinutes)) && valid;
            latitudeMinutesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minSecond > latitudeSeconds || maxSecond < latitudeSeconds)) && valid;
            latitudeSecondsEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minDegree > longitudeDegrees || maxDegree < longitudeDegrees)) && valid;
            longitudeDegreesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minMinute > longitudeMinutes || maxMinute < longitudeMinutes)) && valid;
            longitudeMinutesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minSecond > longitudeSeconds || maxSecond < longitudeSeconds)) && valid;
            longitudeSecondsEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            if(!valid) {
                return null;
            }

            latitude = FiskInfoUtility.DMSToDecimal(new double[] { latitudeDegrees, latitudeMinutes, latitudeSeconds }) * (latitudeCardinalDirectionSwitch.isChecked() ? -1 : 1);
            longitude = FiskInfoUtility.DMSToDecimal(new double[] { longitudeDegrees, longitudeMinutes, longitudeSeconds }) * (longitudeCardinalDirectionSwitch.isChecked() ? 1 : -1);

            return new Point(latitude, longitude);
        } catch(NumberFormatException e) {
            latitudeDegreesEditText.setError(Double.isNaN(latitudeDegrees) ? super.getContext().getString(R.string.error_invalid_format) : null);
            latitudeMinutesEditText.setError(Double.isNaN(latitudeMinutes) ? super.getContext().getString(R.string.error_invalid_format) : null);
            latitudeSecondsEditText.setError(Double.isNaN(latitudeSeconds) ? super.getContext().getString(R.string.error_invalid_format) : null);
            longitudeDegreesEditText.setError(Double.isNaN(longitudeDegrees) ? super.getContext().getString(R.string.error_invalid_format) : null);
            longitudeMinutesEditText.setError(Double.isNaN(longitudeMinutes) ? super.getContext().getString(R.string.error_invalid_format) : null);
            longitudeSecondsEditText.setError(Double.isNaN(longitudeSeconds) ? super.getContext().getString(R.string.error_invalid_format) : null);

            return null;
        }
    }

    @Override
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

                    latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
                    latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
                    latitudeSecondsEditText.setText(String.valueOf(latitudeDMS[2]));
                    longitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
                    longitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
                    longitudeSecondsEditText.setText(String.valueOf(longitudeDMS[2]));
                    latitudeCardinalDirectionSwitch.setChecked(latitude < 0);
                    longitudeCardinalDirectionSwitch.setChecked(longitude >= 0);
                }
            });
        }
    }

    @Override
    public void setTextWatcher(TextWatcher watcher) {
        latitudeDegreesEditText.addTextChangedListener(watcher);
        latitudeMinutesEditText.addTextChangedListener(watcher);
        latitudeSecondsEditText.addTextChangedListener(watcher);
        longitudeDegreesEditText.addTextChangedListener(watcher);
        longitudeMinutesEditText.addTextChangedListener(watcher);
        longitudeSecondsEditText.addTextChangedListener(watcher);
    }

    @Override
    public void setCardinalDirectionSwitchOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        latitudeCardinalDirectionSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        longitudeCardinalDirectionSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        latitudeDegreesEditText.setEnabled(enabled);
        latitudeMinutesEditText.setEnabled(enabled);
        latitudeSecondsEditText.setEnabled(enabled);
        longitudeDegreesEditText.setEnabled(enabled);
        longitudeMinutesEditText.setEnabled(enabled);
        longitudeSecondsEditText.setEnabled(enabled);
        latitudeCardinalDirectionSwitch.setEnabled(enabled);
        longitudeCardinalDirectionSwitch.setEnabled(enabled);
        setPositionButton.setEnabled(enabled);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(enabled) {
                setPositionButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_active_tint_color));
            } else {
                setPositionButton.setBackgroundTintList(ContextCompat.getColorStateList(getView().getContext(), R.color.material_icon_black_disabled_tint_color));
            }
        }
    }
}

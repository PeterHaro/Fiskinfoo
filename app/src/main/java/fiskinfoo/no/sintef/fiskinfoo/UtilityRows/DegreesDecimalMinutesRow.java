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
import fiskinfoo.no.sintef.fiskinfoo.Interface.LocationProviderInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;

public class DegreesDecimalMinutesRow extends CoordinateRow {
    private EditText latitudeDegreesEditText;
    private EditText latitudeMinutesEditText;
    private EditText longitudeDegreesEditText;
    private EditText longitudeMinutesEditText;
    private SwitchCompat latitudeCardinalDirectionSwitch;
    private SwitchCompat longitudeCardinalDirectionSwitch;
    private Button setPositionButton;
    private LocationProviderInterface mLocationProvider;


    public DegreesDecimalMinutesRow(final Activity activity, LocationProviderInterface gpsLocationTracker) {
        super(activity, R.layout.utility_row_degrees_decimal_minutes_row);

        latitudeDegreesEditText = super.getView().findViewById(R.id.utility_ddm_row_latitude_degrees_edit_text);
        latitudeMinutesEditText = super.getView().findViewById(R.id.utility_ddm_row_latitude_minutes_edit_text);
        longitudeDegreesEditText = super.getView().findViewById(R.id.utility_ddm_row_longitude_degrees_edit_text);
        longitudeMinutesEditText = super.getView().findViewById(R.id.utility_ddm_row_longitude_minutes_edit_text);
        latitudeCardinalDirectionSwitch = super.getView().findViewById(R.id.utility_ddm_row_latitude_cardinal_direction_switch);
        longitudeCardinalDirectionSwitch = super.getView().findViewById(R.id.utility_ddm_row_longitude_cardinal_direction_switch);
        setPositionButton = super.getView().findViewById(R.id.utility_lat_lon_row_set_position_button);
        mLocationProvider = gpsLocationTracker;

        setPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = mLocationProvider.getLatitude();
                double longitude = mLocationProvider.getLongitude();

//                double[] latitudeDMS = FiskInfoUtility.decimalToDDMArray(latitude);
//                double[] longitudeDMS = FiskInfoUtility.decimalToDDMArray(longitude);

//                latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
//                latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
//                longitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
//                longitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
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
    public CoordinateRow initRow(Activity activity, LocationProviderInterface gpsLocationTracker) {
        return new DegreesDecimalMinutesRow(activity, gpsLocationTracker);
    }

    @Override
    public Point getCoordinates() {
        double latitude;
        double longitude;

        double latitudeDegrees = Double.NaN;
        double latitudeMinutes = Double.NaN;
        double longitudeDegrees = Double.NaN;
        double longitudeMinutes = Double.NaN;
        boolean fieldValid;
        boolean valid;

        try {
            latitudeDegrees = !latitudeDegreesEditText.getText().toString().equals("") ? Double.parseDouble(latitudeDegreesEditText.getText().toString().trim()) : 0;
            latitudeMinutes = !latitudeMinutesEditText.getText().toString().equals("") ? Double.parseDouble(latitudeMinutesEditText.getText().toString().trim()) : 0;
            longitudeDegrees = !longitudeDegreesEditText.getText().toString().equals("") ? Double.parseDouble(longitudeDegreesEditText.getText().toString().trim()) : 0;
            longitudeMinutes = !longitudeMinutesEditText.getText().toString().equals("") ? Double.parseDouble(longitudeMinutesEditText.getText().toString().trim()) : 0;

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

            valid = (fieldValid = !(minDegree > longitudeDegrees || maxDegree < longitudeDegrees)) && valid;
            longitudeDegreesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minMinute > longitudeMinutes || maxMinute < longitudeMinutes)) && valid;
            longitudeMinutesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            if(!valid) {
                return null;
            }

//            latitude = FiskInfoUtility.DDMToDecimal(new double[] { latitudeDegrees, latitudeMinutes }) * (latitudeCardinalDirectionSwitch.isChecked() ? -1 : 1);
//            longitude = FiskInfoUtility.DDMToDecimal(new double[] { longitudeDegrees, longitudeMinutes }) * (longitudeCardinalDirectionSwitch.isChecked() ? 1 : -1);

//            return new Point(latitude, longitude);
            return null;
        } catch(NumberFormatException e) {
            latitudeDegreesEditText.setError(Double.isNaN(latitudeDegrees) ? super.getContext().getString(R.string.error_invalid_format) : null);
            latitudeMinutesEditText.setError(Double.isNaN(latitudeMinutes) ? super.getContext().getString(R.string.error_invalid_format) : null);
            longitudeDegreesEditText.setError(Double.isNaN(longitudeDegrees) ? super.getContext().getString(R.string.error_invalid_format) : null);
            longitudeMinutesEditText.setError(Double.isNaN(longitudeMinutes) ? super.getContext().getString(R.string.error_invalid_format) : null);

            return null;
        }
    }

    @Override
    public void setCoordinates(Point position) {
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();
//
//        double[] latitudeDMS = FiskInfoUtility.decimalToDDMArray(latitude);
//        double[] longitudeDMS = FiskInfoUtility.decimalToDDMArray(longitude);
//
//        latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
//        latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
//        longitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
//        longitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
        latitudeCardinalDirectionSwitch.setChecked(latitude < 0);
        longitudeCardinalDirectionSwitch.setChecked(longitude >= 0);
    }

    @Override
    public String getLatitude() {
        double latitudeDegrees = Double.NaN;
        double latitudeMinutes = Double.NaN;
        boolean fieldValid;
        boolean valid;

        try {
            latitudeDegrees = Double.parseDouble(latitudeDegreesEditText.getText().toString().trim());
            latitudeMinutes = Double.parseDouble(latitudeMinutesEditText.getText().toString().trim());

            int minLatDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxLatDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minLatMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxLatMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);

            valid = fieldValid = !(minLatDegree > latitudeDegrees || maxLatDegree < Math.floor(latitudeDegrees));
            latitudeDegreesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minLatMinute > latitudeMinutes || maxLatMinute < Math.floor(latitudeMinutes))) && valid;
            latitudeMinutesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            return null;
//            return valid ? Double.toString(FiskInfoUtility.DDMToDecimal(new double[] { latitudeDegrees, latitudeMinutes }) * (latitudeCardinalDirectionSwitch.isChecked() ? -1 : 1)) : null;
        } catch(NumberFormatException e) {
            if(Double.isNaN(latitudeDegrees)) {
                latitudeDegreesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }
            if(Double.isNaN(latitudeMinutes)) {
                latitudeMinutesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }

            return null;
        }
    }

    @Override
    public void setLatitude(String latitude) {
//        double[] latitudeDMS = FiskInfoUtility.decimalToDDMArray(Double.valueOf(latitude));

//        latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
//        latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
        latitudeCardinalDirectionSwitch.setChecked(Double.valueOf(latitude) < 0);
    }

    @Override
    public String getLongitude() {
        double longitudeDegrees = Double.NaN;
        double longitudeMinutes = Double.NaN;
        boolean fieldValid;
        boolean valid;

        try {
            longitudeDegrees = Double.parseDouble(longitudeDegreesEditText.getText().toString().trim());
            longitudeMinutes = Double.parseDouble(longitudeMinutesEditText.getText().toString().trim());

            int minLonDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_min);
            int maxLonDegree = super.getContext().getResources().getInteger(R.integer.valid_degrees_value_max);
            int minLonMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_min);
            int maxLonMinute = super.getContext().getResources().getInteger(R.integer.valid_minutes_value_max);

            valid = fieldValid = !(minLonDegree > longitudeDegrees || maxLonDegree < Math.floor(longitudeDegrees));
            longitudeDegreesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            valid = (fieldValid = !(minLonMinute > longitudeMinutes || maxLonMinute < Math.floor(longitudeMinutes))) && valid;
            longitudeMinutesEditText.setError(fieldValid ? null : super.getContext().getString(R.string.error_invalid_format));

            if(!valid) {
                return null;
            }

            return null; // Double.toString(FiskInfoUtility.DDMToDecimal(new double[] { longitudeDegrees, longitudeMinutes }) * (longitudeCardinalDirectionSwitch.isChecked() ? -1 : 1));
        } catch(NumberFormatException e) {
            if(Double.isNaN(longitudeDegrees)) {
                longitudeDegreesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }
            if(Double.isNaN(longitudeMinutes)) {
                longitudeMinutesEditText.setError(super.getContext().getString(R.string.error_invalid_format));
            }

            return null;
        }
    }

    @Override
    public void setLongitude(String longitude) {
//        double[] longitudeDMS = FiskInfoUtility.decimalToDDMArray(Double.valueOf(longitude));

//        latitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
//        latitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
        longitudeCardinalDirectionSwitch.setChecked(Double.valueOf(longitude) >= 0);
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

//                    double[] latitudeDMS = FiskInfoUtility.decimalToDDMArray(latitude);
//                    double[] longitudeDMS = FiskInfoUtility.decimalToDDMArray(longitude);
//
//                    latitudeDegreesEditText.setText(String.valueOf(latitudeDMS[0]));
//                    latitudeMinutesEditText.setText(String.valueOf(latitudeDMS[1]));
//                    longitudeDegreesEditText.setText(String.valueOf(longitudeDMS[0]));
//                    longitudeMinutesEditText.setText(String.valueOf(longitudeDMS[1]));
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
        longitudeDegreesEditText.addTextChangedListener(watcher);
        longitudeMinutesEditText.addTextChangedListener(watcher);
    }

    @Override
    public void setCardinalDirectionSwitchOnCheckedChangedListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        latitudeCardinalDirectionSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        longitudeCardinalDirectionSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void setEditable(boolean editable) {
        latitudeDegreesEditText.setClickable(editable);
        latitudeDegreesEditText.setFocusable(editable);
        latitudeMinutesEditText.setClickable(editable);
        latitudeMinutesEditText.setFocusable(editable);
        longitudeDegreesEditText.setClickable(editable);
        longitudeDegreesEditText.setFocusable(editable);
        longitudeMinutesEditText.setClickable(editable);
        longitudeMinutesEditText.setFocusable(editable);
    }

    @Override
    public CoordinateFormat getCoordinateFormat() {
        return CoordinateFormat.DEGREES_DECIMAL_MINUTES;
    }

    @Override
    public void setEnabled(boolean enabled) {
        latitudeDegreesEditText.setEnabled(enabled);
        latitudeMinutesEditText.setEnabled(enabled);
        longitudeDegreesEditText.setEnabled(enabled);
        longitudeMinutesEditText.setEnabled(enabled);
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

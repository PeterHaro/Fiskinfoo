package fiskinfoo.no.sintef.fiskinfoo.UtilityRows;

import android.content.Context;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.R;

/**
 * Created by bardh on 26.05.2017.
 */

public class RegistrationNumberRow extends BaseTableRow {
    private TextView titleTextView;
    private Button helpButton;
    private TextView helpTextView;
    private EditText countyEditText;
    private EditText vesselNumberEditText;
    private EditText municipalityEditText;

    public RegistrationNumberRow(Context context) {
        super(context, R.layout.utility_row_registration_number_row);

        titleTextView = (TextView) getView().findViewById(R.id.utility_registration_number_row_header_text_view);
        helpButton = (Button) getView().findViewById(R.id.utility_registration_number_row_help_button);
        helpTextView = (TextView) getView().findViewById(R.id.utility_registration_number_row_help_text_view);
        countyEditText = (EditText) getView().findViewById(R.id.utility_registration_number_row_county_code_edit_text);
        vesselNumberEditText = (EditText) getView().findViewById(R.id.utility_registration_number_row_vessel_number_edit_text);
        municipalityEditText = (EditText) getView().findViewById(R.id.utility_registration_number_row_municipality_code_edit_text);

        countyEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.utility_row_registration_number_county_code_max_length)), new InputFilter.AllCaps()});
        vesselNumberEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.utility_row_registration_number_vessel_number_max_length))});
        municipalityEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.utility_row_registration_number_municipality_code_max_length)), new InputFilter.AllCaps()});

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpTextView.setVisibility(helpTextView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void setCountyText(String fieldText) {
        countyEditText.setText(fieldText);
    }

    public String getCountyText() {
        return countyEditText.getText().toString();
    }

    public void setVesselNumberText(String fieldText) {
        vesselNumberEditText.setText(fieldText);
    }

    public String getVesselNumberText() {
        return vesselNumberEditText.getText().toString();
    }

    public void setMunicipalityText(String municipalityCode) {
        municipalityEditText.setText(municipalityCode);
    }

    public String getMunicipalityText() {
        return municipalityEditText.getText().toString();
    }

    public void setHelpText(String helpText) {
        helpTextView.setText(helpText);
        helpButton.setVisibility(helpText != null ? View.VISIBLE : View.GONE);

    }

    public void setRegistrationNumber(String registrationNumber) {
        if(registrationNumber == null || !FiskInfoUtility.validateRegistrationNumber(registrationNumber)) {
            return;
        }

        countyEditText.setText(registrationNumber.substring(0, 2));
        vesselNumberEditText.setText(registrationNumber.substring(2, 6));
        municipalityEditText.setText(registrationNumber.substring(6, registrationNumber.length()));
    }

    public void setCountyCodeError(String error) {
        countyEditText.setError(error);

        if(error != null) {
            countyEditText.requestFocus();
        }
    }

    public void setVesselNumberError(String error) {
        vesselNumberEditText.setError(error);

        if(error != null) {
            vesselNumberEditText.requestFocus();
        }
    }

    public void setMunicipalityCodeError(String error) {
        municipalityEditText.setError(error);

        if(error != null) {
            municipalityEditText.requestFocus();
        }
    }

    public void setEnabled(boolean enabled) {
        countyEditText.setEnabled(enabled);
        vesselNumberEditText.setEnabled(enabled);
        municipalityEditText.setEnabled(enabled);
    }

    public String getRegistrationNumber() {
        StringBuilder registrationNumber = new StringBuilder();
        String countyCode = countyEditText.getText().toString().trim();
        String vesselNumber = vesselNumberEditText.getText().toString().trim();
        String municipalityCode = municipalityEditText.getText().toString().trim();
        int vesselNumberLength = vesselNumber.length();
        boolean valid = true;

        countyEditText.setError(null);
        vesselNumberEditText.setError(null);
        municipalityEditText.setError(null);

        if(!FiskInfoUtility.isValidCountyCode(countyCode)) {
            countyEditText.setError(getView().getContext().getString(R.string.error_invalid_county_code));
            countyEditText.requestFocus();
            valid = false;
        }
        if(!FiskInfoUtility.isValidVesselNumber(vesselNumber)) {
            vesselNumberEditText.setError(getView().getContext().getString(R.string.error_invalid_vessel_number));
            vesselNumberEditText.requestFocus();
            valid = false;
        }
        if(!FiskInfoUtility.isValidMunicipalityCode(municipalityCode)) {
            municipalityEditText.setError(getView().getContext().getString(R.string.error_invalid_municipality_code));
            municipalityEditText.requestFocus();
            valid = false;
        }

        if(!valid) {
            return "";
        }

        while(vesselNumber.length() < 4) {
            vesselNumber = "0" + vesselNumber;
        }

        registrationNumber.append(countyCode.length() == 2 ? countyCode : countyCode + " ");
        registrationNumber.append(vesselNumber);
        registrationNumber.append(municipalityCode);

        return registrationNumber.toString();
    }
}

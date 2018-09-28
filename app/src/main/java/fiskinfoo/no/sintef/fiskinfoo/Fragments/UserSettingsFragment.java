package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.CoordinateFormat;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Tool;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.BaseTableRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CoordinateRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.EditTextRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.RegistrationNumberRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SpinnerRow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterface} interface
 * to handle interaction events.
 * Use the {@link UserSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingsFragment extends Fragment {
    private static final String ARG_PARAM_USER = "user";

    private UserSettings userSettings;
    private UserInterface userInterface;
    private LinearLayout fieldsContainer;
    private EditTextRow contactPersonNameRow;
    private EditTextRow contactPersonPhoneRow;
    private EditTextRow contactPersonEmailRow;
    private SpinnerRow toolRow;
    private EditTextRow vesselNameRow;
    private EditTextRow vesselPhoneNumberRow;
    private EditTextRow vesselIrcsNumberRow;
    private EditTextRow vesselMmsiNumberRow;
    private EditTextRow vesselImoNumberRow;
    private EditTextRow vesselRegistrationNumberRow;
    private Tracker tracker;

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userSettings
     * @return A new instance of fragment UserDetailsFragment.
     */
    public static UserSettingsFragment newInstance(UserSettings userSettings) {
        UserSettingsFragment fragment = new UserSettingsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_USER, userSettings != null ? userSettings : new UserSettings());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        if (getArguments() != null) {
            userSettings = getArguments().getParcelable(ARG_PARAM_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);

        fieldsContainer = rootView.findViewById(R.id.user_settings_fragment_fields_container);

        generateAndPopulateFields();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void generateAndPopulateFields() {

        contactPersonNameRow = new EditTextRow(getContext(), getString(R.string.contact_person_name), getString(R.string.contact_person_name));
        contactPersonPhoneRow = new EditTextRow(getContext(), getString(R.string.contact_person_phone), getString(R.string.contact_person_phone));
        contactPersonEmailRow = new EditTextRow(getContext(), getString(R.string.contact_person_email), getString(R.string.contact_person_email));

        toolRow = new SpinnerRow(getContext(), getString(R.string.tool_type), ToolType.getValues());
        vesselNameRow = new EditTextRow(getContext(), getString(R.string.vessel_name), getString(R.string.vessel_name));
        vesselPhoneNumberRow = new EditTextRow(getContext(), getString(R.string.vessel_phone_number), getString(R.string.vessel_phone_number));
        vesselIrcsNumberRow = new EditTextRow(getContext(), getString(R.string.ircs_number), getString(R.string.ircs_number));
        vesselMmsiNumberRow = new EditTextRow(getContext(), getString(R.string.mmsi_number), getString(R.string.mmsi_number));
        vesselImoNumberRow = new EditTextRow(getContext(), getString(R.string.imo_number), getString(R.string.imo_number));
        vesselRegistrationNumberRow = new EditTextRow(getContext(), getString(R.string.registration_number), getString(R.string.registration_number));

        contactPersonNameRow.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        contactPersonPhoneRow.setInputType(InputType.TYPE_CLASS_PHONE);
        contactPersonEmailRow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        contactPersonNameRow.setHelpText(getString(R.string.contact_person_name_help_description));
        contactPersonPhoneRow.setHelpText(getString(R.string.contact_person_phone_help_description));
        contactPersonEmailRow.setHelpText(getString(R.string.contact_person_email_help_description));

        vesselNameRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselPhoneNumberRow.setInputType(InputType.TYPE_CLASS_PHONE);

        vesselIrcsNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselIrcsNumberRow.setInputFilters(new InputFilter[] { new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_ircs)), new InputFilter.AllCaps()});
        vesselIrcsNumberRow.setHelpText(getString(R.string.ircs_help_description));

        vesselMmsiNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselMmsiNumberRow.setInputFilters(new InputFilter[] { new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_mmsi))});
        vesselMmsiNumberRow.setHelpText(getString(R.string.mmsi_help_description));

        vesselImoNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselImoNumberRow.setInputFilters(new InputFilter[] { new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_imo))});
        vesselImoNumberRow.setHelpText(getString(R.string.imo_help_description));

        vesselRegistrationNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselRegistrationNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.input_length_registration_number)), new InputFilter.AllCaps(), new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                if (i1 > i) {

                    char[] acceptedChars = getString(R.string.registration_number_allowed_characters).toCharArray();

                    for (int index = i; index < i1; index++) {
                        if (!new String(acceptedChars).contains(String.valueOf(charSequence.charAt(index)))) {
                            return "";
                        }
                    }
                }
                return null;
            }
        }});
        vesselRegistrationNumberRow.setHelpText(getString(R.string.registration_number_help_description));

        fieldsContainer.addView(contactPersonNameRow.getView());
        fieldsContainer.addView(contactPersonPhoneRow.getView());
        fieldsContainer.addView(contactPersonEmailRow.getView());
        fieldsContainer.addView(vesselNameRow.getView());
        fieldsContainer.addView(toolRow.getView());
        fieldsContainer.addView(vesselPhoneNumberRow.getView());
        fieldsContainer.addView(vesselIrcsNumberRow.getView());
        fieldsContainer.addView(vesselMmsiNumberRow.getView());
        fieldsContainer.addView(vesselImoNumberRow.getView());
        fieldsContainer.addView(vesselRegistrationNumberRow.getView());

        if (userSettings != null) {
            ArrayAdapter<String> toolRowAdapter = toolRow.getAdapter();
            toolRow.setSelectedSpinnerItem(toolRowAdapter.getPosition(userSettings.getToolType() != null ? userSettings.getToolType().toString() : Tool.BUNNTRÅL.toString()));
            contactPersonNameRow.setText(userSettings.getContactPersonName());
            contactPersonPhoneRow.setText(userSettings.getContactPersonPhone());
            contactPersonEmailRow.setText(userSettings.getContactPersonEmail());
            vesselNameRow.setText(userSettings.getVesselName());
            vesselPhoneNumberRow.setText(userSettings.getVesselPhone());
            vesselIrcsNumberRow.setText(userSettings.getIrcs());
            vesselMmsiNumberRow.setText(userSettings.getMmsi());
            vesselImoNumberRow.setText(userSettings.getImo());
            vesselRegistrationNumberRow.setText(userSettings.getRegistrationNumber().replace(" ", ""));
        }
    }

    public void highlightInvalidField(final BaseTableRow row) {
        ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView)fieldsContainer.getParent()).scrollTo(0, row.getView().getBottom());
                row.getView().requestFocus();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_user_settings:
                validateFieldsAndUpdateUserSettings();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validateFieldsAndUpdateUserSettings() {
        boolean validated;

        validated = FiskInfoUtility.validateName(contactPersonNameRow.getFieldText().trim()) || contactPersonNameRow.getFieldText().trim().equals("");
        contactPersonNameRow.setError(validated ? null : getString(R.string.error_invalid_name));
        if(!validated) {
            highlightInvalidField(contactPersonNameRow);

            return;
        }

        validated = FiskInfoUtility.validatePhoneNumber(contactPersonPhoneRow.getFieldText().trim()) || contactPersonPhoneRow.getFieldText().trim().equals("");
        contactPersonPhoneRow.setError(validated ? null : getString(R.string.error_invalid_phone_number));
        if(!validated) {
            highlightInvalidField(contactPersonPhoneRow);

            return;
        }
        validated = FiskInfoUtility.isEmailValid(contactPersonEmailRow.getFieldText().trim()) || contactPersonEmailRow.getFieldText().trim().equals("");
        contactPersonEmailRow.setError(validated ? null : getString(R.string.error_invalid_email));
        if(!validated) {
            highlightInvalidField(contactPersonEmailRow);

            return;
        }

        validated = FiskInfoUtility.validateIRCS(vesselIrcsNumberRow.getFieldText().trim()) || vesselIrcsNumberRow.getFieldText().trim().equals("");
        vesselIrcsNumberRow.setError(validated ? null : getString(R.string.error_invalid_ircs));
        if(!validated) {
            highlightInvalidField(vesselIrcsNumberRow);

            return;
        }

        validated = FiskInfoUtility.validateMMSI(vesselMmsiNumberRow.getFieldText().trim()) || vesselMmsiNumberRow.getFieldText().trim().equals("");
        vesselMmsiNumberRow.setError(validated ? null : getString(R.string.error_invalid_mmsi));
        if(!validated) {
            highlightInvalidField(vesselMmsiNumberRow);

            return;
        }

        validated = FiskInfoUtility.validateIMO(vesselImoNumberRow.getFieldText().trim()) || vesselImoNumberRow.getFieldText().trim().equals("");
        vesselImoNumberRow.setError(validated ? null : getString(R.string.error_invalid_imo));
        if(!validated) {
            highlightInvalidField(vesselImoNumberRow);

            return;
        }

        validated = FiskInfoUtility.validateRegistrationNumber(vesselRegistrationNumberRow.getFieldText().trim()) || vesselRegistrationNumberRow.getFieldText().trim().isEmpty();
        vesselRegistrationNumberRow.setError(validated ? null : getString(R.string.error_invalid_registration_number));
        if(!validated) {
            highlightInvalidField(vesselRegistrationNumberRow);

            return;
        }

        String registrationNumber = FiskInfoUtility.formatRegistrationNumber(vesselRegistrationNumberRow.getFieldText());

        userSettings.setToolType(ToolType.createFromValue(toolRow.getCurrentSpinnerItem()));
        userSettings.setVesselName(vesselNameRow.getFieldText().trim());
        userSettings.setVesselPhone(vesselPhoneNumberRow.getFieldText().trim());
        userSettings.setIrcs(vesselIrcsNumberRow.getFieldText().trim());
        userSettings.setMmsi(vesselMmsiNumberRow.getFieldText().trim());
        userSettings.setImo(vesselImoNumberRow.getFieldText().trim());
        userSettings.setRegistrationNumber(registrationNumber);
        userSettings.setContactPersonEmail(contactPersonEmailRow.getFieldText().toLowerCase().trim());
        userSettings.setContactPersonName(contactPersonNameRow.getFieldText().trim());
        userSettings.setContactPersonPhone(contactPersonPhoneRow.getFieldText().trim());

        userInterface.updateUserSettings(userSettings);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserInterface) {
            userInterface = (UserInterface) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement userInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        userInterface = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() != null) {
            getView().refreshDrawableState();
        }

        if(tracker != null){

            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

        MainActivity activity = (MainActivity) getActivity();
        String title = getResources().getString(R.string.user_information);
        activity.refreshTitle(title);
    }
}

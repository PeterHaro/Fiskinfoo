package Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Tool;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntryStatus;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Interface.LocationProviderInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ActionRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.BaseTableRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CoordinatesRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.DatePickerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.EditTextRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ErrorRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SpinnerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.TimePickerRow;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static fiskinfoo.no.sintef.fiskinfoo.MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditToolFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditToolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditToolFragment extends DialogFragment implements LocationProviderInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TOOL_PARAM = "tool";

    private OnFragmentInteractionListener mListener;
    private UserInterface userInterface;

    private GpsLocationTracker locationTracker;
    private ToolEntry tool;
    private LinearLayout fieldsContainer;
    private DatePickerRow setupDateRow;
    private TimePickerRow setupTimeRow;
    private CoordinatesRow coordinatesRow;
    private SpinnerRow toolRow;
    private CheckBoxRow toolRemovedRow;
    private EditTextRow commentRow;
    private EditTextRow contactPersonNameRow;
    private EditTextRow contactPersonPhoneRow;
    private EditTextRow contactPersonEmailRow;
    private EditTextRow vesselNameRow;
    private EditTextRow vesselPhoneNumberRow;
    private EditTextRow vesselIrcsNumberRow;
    private EditTextRow vesselMmsiNumberRow;
    private EditTextRow vesselImoNumberRow;
    private EditTextRow vesselRegistrationNumberRow;
    private ActionRow archiveRow;
    private ActionRow deleteRow;
    private ErrorRow errorRow;

    public EditToolFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tool .
     * @return A new instance of fragment EditToolFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditToolFragment newInstance(ToolEntry tool) {
        EditToolFragment fragment = new EditToolFragment();
        Bundle args = new Bundle();
        args.putParcelable(TOOL_PARAM, tool);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tool = getArguments().getParcelable(TOOL_PARAM);
        }

        locationTracker = new GpsLocationTracker(getContext());

        if (!locationTracker.canGetLocation()) {
            locationTracker.showSettingsAlert();
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_tool, container, false);

        fieldsContainer = (LinearLayout) rootView.findViewById(R.id.dialog_fragment_edit_tool_linear_layout);

        generateFields();

        if(tool != null) {
            populateFieldsFromTool();
        } else {
            populateFieldsFromSettings();
        }
        
        return rootView;
    }

    private void generateFields() {
        setupDateRow = new DatePickerRow(getContext(), getString(R.string.tool_set_date_colon), getFragmentManager());
        setupTimeRow = new TimePickerRow(getContext(), getString(R.string.tool_set_time_colon), getFragmentManager(), true);
        coordinatesRow = new CoordinatesRow(getActivity(), this);
        toolRow = new SpinnerRow(getContext(), getString(R.string.tool_type_colon), ToolType.getValues());
        toolRemovedRow = new CheckBoxRow(getContext(), getString(R.string.tool_removed_row_text), true);
        commentRow = new EditTextRow(getContext(), getString(R.string.comment_field_header), getString(R.string.comment_field_hint));
        contactPersonNameRow = new EditTextRow(getContext(), getString(R.string.contact_person_name), getString(R.string.contact_person_name));
        contactPersonPhoneRow = new EditTextRow(getContext(), getString(R.string.contact_person_phone), getString(R.string.contact_person_phone));
        contactPersonEmailRow = new EditTextRow(getContext(), getString(R.string.contact_person_email), getString(R.string.contact_person_email));
        vesselNameRow = new EditTextRow(getContext(), getString(R.string.vessel_name), getString(R.string.vessel_name));
        vesselPhoneNumberRow = new EditTextRow(getContext(), getString(R.string.vessel_phone_number), getString(R.string.vessel_phone_number));
        vesselIrcsNumberRow = new EditTextRow(getContext(), getString(R.string.ircs_number), getString(R.string.ircs_number));
        vesselMmsiNumberRow = new EditTextRow(getContext(), getString(R.string.mmsi_number), getString(R.string.mmsi_number));
        vesselImoNumberRow = new EditTextRow(getContext(), getString(R.string.imo_number), getString(R.string.imo_number));
        vesselRegistrationNumberRow = new EditTextRow(getContext(), getString(R.string.registration_number), getString(R.string.registration_number));
        errorRow = new ErrorRow(getContext(), getString(R.string.error_minimum_identification_factors_not_met), false);

        archiveRow = new ActionRow(getContext(), getString(R.string.archive_tool), R.drawable.ic_archive_black_24dp, getArchiveToolRowOnClickListener());
        deleteRow = new ActionRow(getContext(), getString(R.string.delete_tool), R.drawable.ic_delete_black_24dp, getDeleteToolRowOnClickListener());

        commentRow.setInputType(InputType.TYPE_CLASS_TEXT);
        commentRow.setHelpText(getString(R.string.comment_help_description));
        vesselNameRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselPhoneNumberRow.setInputType(InputType.TYPE_CLASS_PHONE);
        vesselIrcsNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselIrcsNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_ircs)), new InputFilter.AllCaps()});
        vesselIrcsNumberRow.setHelpText(getString(R.string.ircs_help_description));
        vesselMmsiNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselMmsiNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_mmsi))});
        vesselMmsiNumberRow.setHelpText(getString(R.string.mmsi_help_description));
        vesselImoNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselImoNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_imo))});
        vesselImoNumberRow.setHelpText(getString(R.string.imo_help_description));
        vesselRegistrationNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselRegistrationNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_registration_number)), new InputFilter.AllCaps()});
        vesselRegistrationNumberRow.setHelpText(getString(R.string.registration_number_help_description));
        contactPersonNameRow.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        contactPersonPhoneRow.setInputType(InputType.TYPE_CLASS_PHONE);
        contactPersonEmailRow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        contactPersonNameRow.setHelpText(getString(R.string.contact_person_name_help_description));
        contactPersonPhoneRow.setHelpText(getString(R.string.contact_person_phone_help_description));
        contactPersonEmailRow.setHelpText(getString(R.string.contact_person_email_help_description));

        fieldsContainer.addView(coordinatesRow.getView());
        fieldsContainer.addView(setupDateRow.getView());
        fieldsContainer.addView(setupTimeRow.getView());
        fieldsContainer.addView(toolRow.getView());
        fieldsContainer.addView(commentRow.getView());
        fieldsContainer.addView(contactPersonNameRow.getView());
        fieldsContainer.addView(contactPersonPhoneRow.getView());
        fieldsContainer.addView(contactPersonEmailRow.getView());
        fieldsContainer.addView(vesselNameRow.getView());
        fieldsContainer.addView(vesselPhoneNumberRow.getView());
        fieldsContainer.addView(vesselIrcsNumberRow.getView());
        fieldsContainer.addView(vesselMmsiNumberRow.getView());
        fieldsContainer.addView(vesselImoNumberRow.getView());
        fieldsContainer.addView(vesselRegistrationNumberRow.getView());
        fieldsContainer.addView(errorRow.getView());

        if (tool != null) {
            fieldsContainer.addView(toolRemovedRow.getView(), 4);

            if (tool.getToolStatus() != ToolEntryStatus.STATUS_UNREPORTED) {
                fieldsContainer.addView(archiveRow.getView());
            }

            fieldsContainer.addView(deleteRow.getView());
        }
    }

    private void populateFieldsFromTool() {
        coordinatesRow.setCoordinates(getActivity(), tool.getCoordinates(), this);
        setupDateRow.setDate(tool.getSetupDate());
        setupTimeRow.setTime(tool.getSetupDateTime().substring(tool.getSetupDateTime().indexOf('T') + 1, tool.getSetupDateTime().indexOf('T') + 6));

        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            coordinatesRow.setPositionButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions(new String[] { ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }
            });
        }

        ArrayAdapter<String> currentAdapter = toolRow.getAdapter();
        toolRow.setSelectedSpinnerItem(currentAdapter.getPosition(tool.getToolType() != null ? tool.getToolType().toString() : Tool.BUNNTRÅL.toString()));
        toolRemovedRow.setChecked(!tool.getRemovedTime().isEmpty());
        contactPersonNameRow.setText(tool.getContactPersonName());
        contactPersonPhoneRow.setText(tool.getContactPersonPhone());
        contactPersonEmailRow.setText(tool.getContactPersonEmail());
        vesselNameRow.setText(tool.getVesselName());
        vesselPhoneNumberRow.setText(tool.getVesselPhone());
        vesselIrcsNumberRow.setText(tool.getIRCS());
        vesselMmsiNumberRow.setText(tool.getMMSI());
        vesselImoNumberRow.setText(tool.getIMO());
        vesselRegistrationNumberRow.setText(tool.getRegNum());
    }

    private void populateFieldsFromSettings() {
        UserSettings settings = userInterface.getUser().getSettings() != null ? userInterface.getUser().getSettings() : new UserSettings();

        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            coordinatesRow.setPositionButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions(new String[] { ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }
            });
        }

        if (settings != null) {
            ArrayAdapter<String> currentAdapter = toolRow.getAdapter();
            toolRow.setSelectedSpinnerItem(currentAdapter.getPosition(settings.getToolType() != null ? settings.getToolType().toString() : Tool.BUNNTRÅL.toString()));
            contactPersonNameRow.setText(settings.getContactPersonName());
            contactPersonPhoneRow.setText(settings.getContactPersonPhone());
            contactPersonEmailRow.setText(settings.getContactPersonEmail());
            vesselNameRow.setText(settings.getVesselName());
            vesselPhoneNumberRow.setText(settings.getVesselPhone());
            vesselIrcsNumberRow.setText(settings.getIrcs());
            vesselMmsiNumberRow.setText(settings.getMmsi());
            vesselImoNumberRow.setText(settings.getImo());
            vesselRegistrationNumberRow.setText(settings.getRegistrationNumber());
        }
    }

    private void validateAndCreateToolEntry() {
        List<Point> coordinates = coordinatesRow.getCoordinates();
        ToolType toolType = ToolType.createFromValue(toolRow.getCurrentSpinnerItem());
        String vesselName = vesselNameRow.getFieldText().trim();
        String vesselPhoneNumber = vesselPhoneNumberRow.getFieldText().trim();
        String toolSetupDate = setupDateRow.getDate().trim();
        String toolSetupTime = setupTimeRow.getTime().trim();
        String toolSetupDateTime = toolSetupDate + "T" + toolSetupTime + ":00.000";
        String commentString = commentRow.getFieldText().trim();
        String vesselIrcsNumber = vesselIrcsNumberRow.getFieldText().trim();
        String vesselMmsiNumber = vesselMmsiNumberRow.getFieldText().trim();
        String vesselImoNumber = vesselImoNumberRow.getFieldText().trim();
        String registrationNumber = vesselRegistrationNumberRow.getFieldText().trim();
        String contactPersonName = contactPersonNameRow.getFieldText().trim();
        String contactPersonPhone = contactPersonPhoneRow.getFieldText().trim();
        String contactPersonEmail = contactPersonEmailRow.getFieldText().trim();
        FiskInfoUtility utility = new FiskInfoUtility();
        boolean validated;
        boolean ircsValidated;
        boolean mmsiValidated;
        boolean imoValidated;
        boolean regNumValidated;
        boolean minimumIdentificationFactorsMet;

        validated = coordinates != null;
        if(!validated) {
            return;
        }

        validated = utility.validateName(contactPersonName);
        contactPersonNameRow.setError(validated ? null : getString(R.string.error_invalid_name));
        if(!validated) {
            highlightInvalidField(contactPersonNameRow);

            return;
        }

        validated = utility.validatePhoneNumber(contactPersonPhone);
        contactPersonPhoneRow.setError(validated ? null : getString(R.string.error_invalid_phone_number));
        if(!validated) {
            highlightInvalidField(contactPersonPhoneRow);

            return;
        }

        validated = utility.isEmailValid(contactPersonEmail) || contactPersonEmail.isEmpty();
        contactPersonEmailRow.setError(validated ? null : getString(R.string.error_invalid_email));
        if(!validated) {
            highlightInvalidField(contactPersonEmailRow);

            return;
        }

        validated = !vesselNameRow.getFieldText().isEmpty();
        vesselNameRow.setError(validated ? null : getString(R.string.error_invalid_vessel_name));
        if(!validated) {
            highlightInvalidField(vesselNameRow);

            return;
        }

        validated = !vesselPhoneNumberRow.getFieldText().isEmpty();
        vesselPhoneNumberRow.setError(validated ? null : getString(R.string.error_invalid_phone_number));
        if(!validated) {
            highlightInvalidField(vesselPhoneNumberRow);

            return;
        }

        validated = (ircsValidated = utility.validateIRCS(vesselIrcsNumber)) || vesselIrcsNumber.isEmpty();
        vesselIrcsNumberRow.setError(validated ? null : getString(R.string.error_invalid_ircs));
        if(!validated) {
            highlightInvalidField(vesselIrcsNumberRow);

            return;
        }

        validated = (mmsiValidated = utility.validateMMSI(vesselMmsiNumber)) || vesselMmsiNumber.isEmpty();
        vesselMmsiNumberRow.setError(validated ? null : getString(R.string.error_invalid_mmsi));
        if(!validated) {
            highlightInvalidField(vesselMmsiNumberRow);

            return;
        }

        validated = (imoValidated = utility.validateIMO(vesselImoNumber)) || vesselImoNumber.isEmpty();
        vesselImoNumberRow.setError(validated ? null : getString(R.string.error_invalid_imo));
        if(!validated) {
            highlightInvalidField(vesselImoNumberRow);

            return;
        }

        validated = (regNumValidated = utility.validateRegistrationNumber(registrationNumber)) || registrationNumber.isEmpty();
        vesselRegistrationNumberRow.setError(validated ? null : getString(R.string.error_invalid_registration_number));
        if(!validated) {
            highlightInvalidField(vesselRegistrationNumberRow);

            return;
        }

        minimumIdentificationFactorsMet = !vesselName.isEmpty() && (ircsValidated || mmsiValidated || imoValidated || regNumValidated);

        errorRow.setVisibility(!minimumIdentificationFactorsMet);

        if(!minimumIdentificationFactorsMet) {
            ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
                @Override
                public void run() {
                    ((ScrollView)fieldsContainer.getParent()).scrollTo(0, errorRow.getView().getBottom());
                    vesselRegistrationNumberRow.requestFocus();
                }
            });

            return;
        }

        ToolEntry toolEntry = new ToolEntry(coordinates, vesselName, vesselPhoneNumber, contactPersonEmail,
                toolType, toolSetupDateTime, registrationNumber, contactPersonName, contactPersonPhone, contactPersonEmail);

        toolEntry.setIRCS(vesselIrcsNumber);
        toolEntry.setMMSI(vesselMmsiNumber);
        toolEntry.setIMO(vesselImoNumber);
        toolEntry.setComment(commentString);

        mListener.updateTool(toolEntry);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }

    private void validateAndUpdateTool() {
        final SimpleDateFormat sdfMilliSeconds = new SimpleDateFormat(getContext().getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss_sss), Locale.getDefault());
        final SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss), Locale.getDefault());

        FiskInfoUtility utility = new FiskInfoUtility();
        List<Point> coordinates = coordinatesRow.getCoordinates();
        ToolType toolType = ToolType.createFromValue(toolRow.getCurrentSpinnerItem());
        String vesselName = vesselNameRow.getFieldText().trim();
        String vesselPhoneNumber = vesselPhoneNumberRow.getFieldText().trim();
        String toolSetupDate = setupDateRow.getDate().trim();
        String toolSetupTime = setupTimeRow.getTime().trim();
        String toolSetupDateTime;
        String commentString = commentRow.getFieldText().trim();
        String vesselIrcsNumber = vesselIrcsNumberRow.getFieldText().trim();
        String vesselMmsiNumber = vesselMmsiNumberRow.getFieldText().trim();
        String vesselImoNumber = vesselImoNumberRow.getFieldText().trim();
        String registrationNumber = vesselRegistrationNumberRow.getFieldText().trim();
        String contactPersonName = contactPersonNameRow.getFieldText().trim();
        String contactPersonPhone = contactPersonPhoneRow.getFieldText().trim();
        String contactPersonEmail = contactPersonEmailRow.getFieldText().trim();
        boolean toolRemoved = toolRemovedRow.isChecked();
        boolean validated;
        boolean edited = false;
        boolean ircsValidated;
        boolean mmsiValidated;
        boolean imoValidated;
        boolean regNumValidated;
        boolean minimumIdentificationFactorsMet;

        validated = coordinates != null;
        if(!validated) {
            return;
        }

        validated = utility.validateName(contactPersonName);
        contactPersonNameRow.setError(validated ? null : getString(R.string.error_invalid_name));
        if(!validated) {
            highlightInvalidField(contactPersonNameRow);

            return;
        }

        validated = utility.validatePhoneNumber(contactPersonPhone);
        contactPersonPhoneRow.setError(validated ? null : getString(R.string.error_invalid_phone_number));
        if(!validated) {
            highlightInvalidField(contactPersonPhoneRow);

            return;
        }

        validated = utility.isEmailValid(contactPersonEmail) || contactPersonEmail.isEmpty();
        contactPersonEmailRow.setError(validated ? null : getString(R.string.error_invalid_email));
        if(!validated) {
            highlightInvalidField(contactPersonEmailRow);

            return;
        }

        validated = !vesselNameRow.getFieldText().isEmpty();
        vesselNameRow.setError(validated ? null : getString(R.string.error_invalid_vessel_name));
        if(!validated) {
            highlightInvalidField(vesselNameRow);

            return;
        }

        validated = !vesselPhoneNumberRow.getFieldText().isEmpty();
        vesselPhoneNumberRow.setError(validated ? null : getString(R.string.error_invalid_phone_number));
        if(!validated) {
            highlightInvalidField(vesselPhoneNumberRow);

            return;
        }

        validated = (ircsValidated = utility.validateIRCS(vesselIrcsNumber)) || vesselIrcsNumber.isEmpty();
        vesselIrcsNumberRow.setError(validated ? null : getString(R.string.error_invalid_ircs));
        if(!validated) {
            highlightInvalidField(vesselIrcsNumberRow);

            return;
        }

        validated = (mmsiValidated = utility.validateMMSI(vesselMmsiNumber)) || vesselMmsiNumber.isEmpty();
        vesselMmsiNumberRow.setError(validated ? null : getString(R.string.error_invalid_mmsi));
        if(!validated) {
            highlightInvalidField(vesselMmsiNumberRow);

            return;
        }

        validated = (imoValidated = utility.validateIMO(vesselImoNumber)) || vesselImoNumber.isEmpty();
        vesselImoNumberRow.setError(validated ? null : getString(R.string.error_invalid_imo));
        if(!validated) {
            highlightInvalidField(vesselImoNumberRow);

            return;
        }

        validated = (regNumValidated = utility.validateRegistrationNumber(registrationNumber)) || registrationNumber.isEmpty();
        vesselRegistrationNumberRow.setError(validated ? null : getString(R.string.error_invalid_registration_number));
        if(!validated) {
            highlightInvalidField(vesselRegistrationNumberRow);

            return;
        }

        minimumIdentificationFactorsMet = !vesselName.isEmpty() && (ircsValidated || mmsiValidated || imoValidated || regNumValidated);
        errorRow.setVisibility(!minimumIdentificationFactorsMet);

        if(!minimumIdentificationFactorsMet) {
            highlightInvalidField(errorRow);

            return;
        }

        sdf.setTimeZone(TimeZone.getDefault());
        Date setupDate = null;
        String setupDateString = toolSetupDate + "T" + toolSetupTime + ":00Z";

        try {
            setupDate = sdf.parse(setupDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));
        setupDateString = sdfMilliSeconds.format(setupDate);
        toolSetupDateTime = setupDateString.substring(0, setupDateString.indexOf('.')).concat("Z");


        if((coordinates.size() != tool.getCoordinates().size()) ||
                toolType != tool.getToolType() ||
                (toolRemoved) == (tool.getRemovedTime().isEmpty()) ||
                (!vesselName.equals(tool.getVesselName())) ||
                (!vesselPhoneNumber.equals(tool.getVesselPhone())) ||
                (!toolSetupDateTime.equals(tool.getSetupDateTime())) ||
                (!vesselIrcsNumber.equals(tool.getIRCS())) ||
                (!vesselMmsiNumber.equals(tool.getMMSI())) ||
                (!vesselImoNumber.equals(tool.getIMO())) ||
                (!registrationNumber.equals(tool.getRegNum())) ||
                (!contactPersonName.equals(tool.getContactPersonName())) ||
                (!contactPersonPhone.equals(tool.getContactPersonPhone())) ||
                (!contactPersonEmail.equals(tool.getContactPersonEmail())) ||
                (!commentString.equals(tool.getComment())))
        {
            edited = true;
        } else {
            List<Point> points = tool.getCoordinates();
            for(int i = 0; i < coordinates.size(); i++) {
                if(coordinates.get(i).getLatitude() != points.get(i).getLatitude() ||
                        coordinates.get(i).getLongitude() != points.get(i).getLongitude()) {
                    edited = true;
                    break;
                }
            }
        }

        if(edited) {
            Date lastChangedDate = new Date();
            Date previousSetupDate = null;
            SimpleDateFormat sdfSetupCompare = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

            String lastChangedDateString = sdfMilliSeconds.format(lastChangedDate)
                    .concat("Z");
            sdfSetupCompare.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                previousSetupDate = sdf.parse(tool.getSetupDateTime().substring(0, tool.getSetupDateTime().length() - 1).concat(".000"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(!sdfSetupCompare.format(previousSetupDate).equals(sdfSetupCompare.format(setupDate))) {
                // TODO: setup date is changed, tool needs to be moved in log so the app does not crash when trying to delete tool.
//                user.getToolLog().removeTool(toolEntry.getSetupDate(), toolEntry.getToolLogId());
//                user.getToolLog().addTool(toolEntry, toolEntry.getSetupDateTime().substring(0, 10));
//                user.writeToSharedPref(editButton.getContext());
            }

            ToolEntryStatus toolStatus = (toolRemoved ? ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED : (tool.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED ? ToolEntryStatus.STATUS_UNREPORTED : ToolEntryStatus.STATUS_UNSENT));
//
            tool.setToolStatus(toolStatus);
            tool.setCoordinates(coordinates);
            tool.setToolType(toolType);
            tool.setVesselName(vesselName);
            tool.setVesselPhone(vesselPhoneNumber);
            tool.setSetupDateTime(toolSetupDateTime);
            tool.setRemovedTime(toolRemoved ? lastChangedDateString : null);
            tool.setComment(commentString);
            tool.setIRCS(vesselIrcsNumber);
            tool.setMMSI(vesselMmsiNumber);
            tool.setIMO(vesselImoNumber);
            tool.setRegNum(registrationNumber);
            tool.setLastChangedDateTime(lastChangedDateString);
            tool.setLastChangedBySource(lastChangedDateString);
            tool.setContactPersonName(contactPersonName);
            tool.setContactPersonPhone(contactPersonPhone);
            tool.setContactPersonEmail(contactPersonEmail);

            userInterface.getUser().writeToSharedPref(getContext());
        } else {
            Toast.makeText(getContext(), R.string.no_changes_made, Toast.LENGTH_LONG).show();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                for(int i = 0; i < permissions.length; i++) {
                    if(permissions[i].equals(ACCESS_FINE_LOCATION) && results[i] == PackageManager.PERMISSION_GRANTED) {
                        locationTracker = new GpsLocationTracker(getActivity());
                        coordinatesRow.setPositionButtonOnClickListener(null);
                    }
                }

                break;
            default:

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId = tool != null ? R.menu.edit_tool_menu : R.menu.new_tool_menu;
        inflater.inflate(menuId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.action_add_new_tool:
                validateAndCreateToolEntry();
                break;
            case R.id.action_update_tool:
                validateAndUpdateTool();
                break;
            case android.R.id.home:
                break;
        }

        if (id == R.id.action_add_new_tool) {
        } else if (id == android.R.id.home) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();

        if(getView() != null) {
            getView().refreshDrawableState();
        }

        MainActivity activity = (MainActivity) getActivity();
        String title = getResources().getString(tool != null ? R.string.edit_tool_fragment_edit_title : R.string.edit_tool_fragment_new_tool_title);
        activity.refreshTitle(title);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            userInterface = (UserInterface) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        userInterface = null;
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

    public View.OnClickListener getDeleteToolRowOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmationText;

                switch (tool.getToolStatus()) {
                    case STATUS_RECEIVED:
                    case STATUS_SENT_UNCONFIRMED:
                        confirmationText = v.getContext().getString(R.string.confirm_registered_tool_deletion_text);
                        break;
                    case STATUS_UNSENT:
                    case STATUS_UNREPORTED:
                        confirmationText = v.getContext().getString(R.string.confirm_tool_deletion_text);
                        break;
                    case STATUS_REMOVED_UNCONFIRMED:
                        confirmationText = v.getContext().getString(R.string.confirm_registered_tool_with_local_changes_deletion_text);
                        break;
                    default:
                        confirmationText = v.getContext().getString(R.string.confirm_tool_deletion_text_general);
                        break;
                }
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(getString(R.string.delete_tool))
                        .setMessage(confirmationText)
                        .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mListener.deleteToolLogEntry(tool);

                                Toast.makeText(getContext(), R.string.tool_deleted, Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.popBackStackImmediate();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
            }
        };
    }

    public View.OnClickListener getArchiveToolRowOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmationText;

                switch (tool.getToolStatus()) {
                    case STATUS_RECEIVED:
                        confirmationText = v.getContext().getString(R.string.confirm_registered_tool_archiving);
                        break;
                    case STATUS_UNSENT:
                        if(!tool.getId().isEmpty()) {
                            confirmationText = v.getContext().getString(R.string.confirm_tool_archiving_text);
                        } else {
                            confirmationText = v.getContext().getString(R.string.confirm_registered_tool_with_local_changes_archiving_text);
                        }
                        break;
                    default:
                        confirmationText = v.getContext().getString(R.string.confirm_tool_archiving_text_general);
                        break;
                }

                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(getString(R.string.archive_tool))
                        .setMessage(confirmationText)
                        .setPositiveButton(getString(R.string.archive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tool.setToolStatus(ToolEntryStatus.STATUS_REMOVED);
                                userInterface.getUser().writeToSharedPref(getContext());

                                Toast.makeText(getContext(), R.string.tool_archived, Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.popBackStackImmediate();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
            }
        };
    }

    @Override
    public double getLatitude() {
        return locationTracker.getLatitude();
    }

    @Override
    public double getLongitude() {
        return locationTracker.getLongitude();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void updateTool(ToolEntry tool);

        void deleteToolLogEntry(ToolEntry tool);
    }
}

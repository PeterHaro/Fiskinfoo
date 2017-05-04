package Fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Tool;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CoordinatesRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.DatePickerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.EditTextRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ErrorRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SpinnerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.TimePickerRow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditToolFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditToolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditToolFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TOOL_PARAM = "tool";

    private OnFragmentInteractionListener mListener;
    private GpsLocationTracker locationTracker;
    private ToolEntry tool;
    private User user;
    private LinearLayout fieldsContainer;

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
            user = mListener.getUser();
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

        if(tool != null) {
            populateFieldsFromTool();
        } else {
            populateFieldsFromSettings();
        }
        
        return rootView;
    }

    private void populateFieldsFromTool() {
        
    }

    private void populateFieldsFromSettings() {
        if(FiskInfoUtility.shouldAskPermission()) {
            String[] perms = { "android.permission.ACCESS_FINE_LOCATION" };
            int permsRequestCode = MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    permsRequestCode);
        }

//        final Dialog dialog = dialogInterface.getDialog(getContext(), R.layout.dialog_register_new_tool, R.string.tool_registration);
        final UserSettings settings = user.getSettings() != null ? user.getSettings() : new UserSettings();

        final DatePickerRow setupDateRow = new DatePickerRow(getContext(), getContext().getString(R.string.tool_set_date_colon), getFragmentManager());
        final TimePickerRow setupTimeRow = new TimePickerRow(getContext(), getContext().getString(R.string.tool_set_time_colon), getFragmentManager(), true);
        final CoordinatesRow coordinatesRow = new CoordinatesRow(getActivity(), locationTracker);
        final SpinnerRow toolRow = new SpinnerRow(getContext(), getContext().getString(R.string.tool_type), ToolType.getValues());
        final EditTextRow commentRow = new EditTextRow(getContext(), getString(R.string.comment_field_header), getString(R.string.comment_field_hint));

        final EditTextRow contactPersonNameRow = new EditTextRow(getContext(), getContext().getString(R.string.contact_person_name), getContext().getString(R.string.contact_person_name));
        final EditTextRow contactPersonPhoneRow = new EditTextRow(getContext(), getContext().getString(R.string.contact_person_phone), getContext().getString(R.string.contact_person_phone));
        final EditTextRow contactPersonEmailRow = new EditTextRow(getContext(), getContext().getString(R.string.contact_person_email), getContext().getString(R.string.contact_person_email));
        final EditTextRow vesselNameRow = new EditTextRow(getContext(), getContext().getString(R.string.vessel_name), getContext().getString(R.string.vessel_name));
        final EditTextRow vesselPhoneNumberRow = new EditTextRow(getContext(), getContext().getString(R.string.vessel_phone_number), getContext().getString(R.string.vessel_phone_number));
        final EditTextRow vesselIrcsNumberRow = new EditTextRow(getContext(), getContext().getString(R.string.ircs_number), getContext().getString(R.string.ircs_number));
        final EditTextRow vesselMmsiNumberRow = new EditTextRow(getContext(), getContext().getString(R.string.mmsi_number), getContext().getString(R.string.mmsi_number));
        final EditTextRow vesselImoNumberRow = new EditTextRow(getContext(), getContext().getString(R.string.imo_number), getContext().getString(R.string.imo_number));
        final EditTextRow vesselRegistrationNumberRow = new EditTextRow(getContext(), getContext().getString(R.string.registration_number), getContext().getString(R.string.registration_number));
        final ErrorRow errorRow = new ErrorRow(getContext(), getString(R.string.error_minimum_identification_factors_not_met), false);

        commentRow.setInputType(InputType.TYPE_CLASS_TEXT);
        commentRow.setHelpText(getString(R.string.comment_help_description));
        vesselNameRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselPhoneNumberRow.setInputType(InputType.TYPE_CLASS_PHONE);
        vesselIrcsNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselIrcsNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_ircs)), new InputFilter.AllCaps()});
        vesselIrcsNumberRow.setHelpText(getContext().getString(R.string.ircs_help_description));
        vesselMmsiNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselMmsiNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_mmsi))});
        vesselMmsiNumberRow.setHelpText(getContext().getString(R.string.mmsi_help_description));
        vesselImoNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselImoNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_imo))});
        vesselImoNumberRow.setHelpText(getContext().getString(R.string.imo_help_description));
        vesselRegistrationNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselRegistrationNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getContext().getResources().getInteger(R.integer.input_length_registration_number)), new InputFilter.AllCaps()});
        vesselRegistrationNumberRow.setHelpText(getContext().getString(R.string.registration_number_help_description));
        contactPersonNameRow.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        contactPersonPhoneRow.setInputType(InputType.TYPE_CLASS_PHONE);
        contactPersonEmailRow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        contactPersonNameRow.setHelpText(getContext().getString(R.string.contact_person_name_help_description));
        contactPersonPhoneRow.setHelpText(getContext().getString(R.string.contact_person_phone_help_description));
        contactPersonEmailRow.setHelpText(getContext().getString(R.string.contact_person_email_help_description));

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

    private void validateAndUpdateToolValues() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

//                List<Point> coordinates = coordinatesRow.getCoordinates();
//                ToolType toolType = ToolType.createFromValue(toolRow.getCurrentSpinnerItem());
//                String vesselName = vesselNameRow.getFieldText().trim();
//                String vesselPhoneNumber = vesselPhoneNumberRow.getFieldText().trim();
//                String toolSetupDate = setupDateRow.getDate().trim();
//                String toolSetupTime = setupTimeRow.getTime().trim();
//                String toolSetupDateTime = toolSetupDate + "T" + toolSetupTime + ":00.000";
//                String commentString = commentRow.getFieldText().trim();
//                String vesselIrcsNumber = vesselIrcsNumberRow.getFieldText().trim();
//                String vesselMmsiNumber = vesselMmsiNumberRow.getFieldText().trim();
//                String vesselImoNumber = vesselImoNumberRow.getFieldText().trim();
//                String registrationNumber = vesselRegistrationNumberRow.getFieldText().trim();
//                String contactPersonName = contactPersonNameRow.getFieldText().trim();
//                String contactPersonPhone = contactPersonPhoneRow.getFieldText().trim();
//                String contactPersonEmail = contactPersonEmailRow.getFieldText().trim();
//                FiskInfoUtility utility = new FiskInfoUtility();
//                boolean validated;
//                boolean ircsValidated;
//                boolean mmsiValidated;
//                boolean imoValidated;
//                boolean regNumValidated;
//                boolean minimumIdentificationFactorsMet;
//
//                validated = coordinates != null;
//                if(!validated) {
//                    return;
//                }
//
//                validated = utility.validateName(contactPersonName);
//                contactPersonNameRow.setError(validated ? null : getContext().getString(R.string.error_invalid_name));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, contactPersonNameRow.getView().getBottom());
//                            contactPersonNameRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = utility.validatePhoneNumber(contactPersonPhone);
//                contactPersonPhoneRow.setError(validated ? null : getContext().getString(R.string.error_invalid_phone_number));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, contactPersonPhoneRow.getView().getBottom());
//                            contactPersonPhoneRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = utility.isEmailValid(contactPersonEmail) || contactPersonEmail.isEmpty();
//                contactPersonEmailRow.setError(validated ? null : getContext().getString(R.string.error_invalid_email));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, contactPersonEmailRow.getView().getBottom());
//                            contactPersonEmailRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = vesselNameRow.getFieldText().trim() != null && !vesselNameRow.getFieldText().isEmpty();
//                vesselNameRow.setError(validated ? null : getContext().getString(R.string.error_invalid_vessel_name));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, vesselNameRow.getView().getBottom());
//                            vesselNameRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = vesselPhoneNumberRow.getFieldText().trim() != null && !vesselPhoneNumberRow.getFieldText().isEmpty();
//                vesselPhoneNumberRow.setError(validated ? null : getContext().getString(R.string.error_invalid_phone_number));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, vesselPhoneNumberRow.getView().getBottom());
//                            vesselPhoneNumberRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = (ircsValidated = utility.validateIRCS(vesselIrcsNumber)) || vesselIrcsNumber.isEmpty();
//                vesselIrcsNumberRow.setError(validated ? null : getContext().getString(R.string.error_invalid_ircs));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, vesselIrcsNumberRow.getView().getBottom());
//                            vesselIrcsNumberRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = (mmsiValidated = utility.validateMMSI(vesselMmsiNumber)) || vesselMmsiNumber.isEmpty();
//                vesselMmsiNumberRow.setError(validated ? null : getContext().getString(R.string.error_invalid_mmsi));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, vesselMmsiNumberRow.getView().getBottom());
//                            vesselMmsiNumberRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = (imoValidated = utility.validateIMO(vesselImoNumber)) || vesselImoNumber.isEmpty();
//                vesselImoNumberRow.setError(validated ? null : getContext().getString(R.string.error_invalid_imo));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, vesselImoNumberRow.getView().getBottom());
//                            vesselImoNumberRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                validated = (regNumValidated = utility.validateRegistrationNumber(registrationNumber)) || registrationNumber.isEmpty();
//                vesselRegistrationNumberRow.setError(validated ? null : getContext().getString(R.string.error_invalid_registration_number));
//                if(!validated) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, vesselRegistrationNumberRow.getView().getBottom());
//                            vesselRegistrationNumberRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                minimumIdentificationFactorsMet = !vesselName.isEmpty() && (ircsValidated || mmsiValidated || imoValidated || regNumValidated);
//
//                errorRow.setVisibility(!minimumIdentificationFactorsMet);
//
//                if(!minimumIdentificationFactorsMet) {
//                    ((ScrollView)fieldsContainer.getParent()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((ScrollView)fieldsContainer.getParent()).scrollTo(0, errorRow.getView().getBottom());
//                            vesselRegistrationNumberRow.requestFocus();
//                        }
//                    });
//
//                    return;
//                }
//
//                ToolEntry toolEntry = new ToolEntry(coordinates, vesselName, vesselPhoneNumber, contactPersonEmail,
//                        toolType, toolSetupDateTime, registrationNumber, contactPersonName, contactPersonPhone, contactPersonEmail);
//
//                toolEntry.setIRCS(vesselIrcsNumber);
//                toolEntry.setMMSI(vesselMmsiNumber);
//                toolEntry.setIMO(vesselImoNumber);
//                toolEntry.setComment(commentString);
//
////                ToolLog toolLog = user.getToolLog();
////                toolLog.addTool(toolEntry, toolSetupDate);
////                user.writeToSharedPref(getContext());
//                mListener.updateTool(toolEntry);
//
////                View.OnClickListener onClickListener = utilityOnClickListeners.getToolEntryEditDialogOnClickListener(getActivity(), getFragmentManager(), mGpsLocationTracker, toolEntry, user);
////                ToolLogRow row = new ToolLogRow(getActivity(), toolEntry, onClickListener);
//
////                row.getView().setTag(toolEntry.getToolId());
////                toolContainer.addView(row.getView());
            }
        };

        mListener.updateTool(tool);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
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
            case R.id.action_update_tool:
                validateAndUpdateToolValues();
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
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        User getUser();
        void updateTool(ToolEntry tool);
    }
}

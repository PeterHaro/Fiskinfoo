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

package fiskinfoo.no.sintef.fiskinfoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Tool;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntryStatus;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ToolLog;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CoordinatesRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.DatePickerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.EditTextRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ErrorRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SpinnerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.TimePickerRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolConfirmationRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolLogRow;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterToolsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterToolsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private FloatingActionButton newToolButton = null;
    private UtilityOnClickListeners utilityOnClickListeners = new UtilityOnClickListeners();
    private static final String USER_PARAM = "user";
    private User user;
    private GpsLocationTracker mGpsLocationTracker;

    private OnFragmentInteractionListener mListener;

    public static final String TAG = "REGTOOLSF";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user The user instance.
     * @return A new instance of fragment RegisterToolsFragment.
     */
    public static RegisterToolsFragment newInstance(User user) {
        RegisterToolsFragment fragment = new RegisterToolsFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_PARAM, user);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterToolsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER_PARAM);
        }

        mGpsLocationTracker = new GpsLocationTracker(getActivity());

        if (!mGpsLocationTracker.canGetLocation()) {
            mGpsLocationTracker.showSettingsAlert();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        for (int i = 0; i < menu.size(); i++) {
            menu.removeItem(i);
        }
        inflater.inflate(R.menu.menu_tool_registration, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_register_tools, container, false);
        final TextView headerDate = (TextView) rootView.findViewById(R.id.register_tool_header_date_field);
        final LinearLayout toolContainer = (LinearLayout) rootView.findViewById(R.id.register_tool_current_tools_table_layout);
        final DialogInterface dialogInterface = new UtilityDialogs();
        final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();

        final String currentDate = getCurrentDate();
        headerDate.setText(currentDate);
        Set<Map.Entry<String, ArrayList<ToolEntry>>> tools = user.getToolLog().myLog.entrySet();

        if(fiskInfoUtility.isNetworkAvailable(getActivity())) {

            List<ToolEntry> localTools = new ArrayList<>();

            for(final Map.Entry<String, ArrayList<ToolEntry>> dateEntry : tools) {
                for (final ToolEntry toolEntry : dateEntry.getValue()) {
                    localTools.add(toolEntry);
                }
            }

            BarentswatchApi barentswatchApi = new BarentswatchApi();
            barentswatchApi.setAccesToken(user.getToken());

            Response response = barentswatchApi.getApi().geoDataDownload("fishingfacility", "JSON");

            if (response == null) {
                Log.d(TAG, "RESPONSE == NULL");
            }

            byte[] toolData = new byte[0];
            try {
                toolData = FiskInfoUtility.toByteArray(response.getBody().in());
                JSONObject featureCollection = new JSONObject(new String(toolData));
                JSONArray jsonTools = featureCollection.getJSONArray("features");
                JSONArray matchedTools = new JSONArray();

                for(int i = 0; i < jsonTools.length(); i++) {
                    UserSettings settings = user.getSettings();
                    JSONObject tool = jsonTools.getJSONObject(i);
                    boolean hasCopy = false;

                    for(ToolEntry localTool : localTools) {
                        if(localTool.getToolId().equals(tool.getJSONObject("properties").getString("toolid"))) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                            SimpleDateFormat sdfJson = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Date localLastUpdatedDateTime;
                            Date localUpdatedBySourceDateTime;
                            Date serverUpdatedDateTime;
                            Date serverUpdatedBySourceDateTime = null;

                            try {
                                localLastUpdatedDateTime = sdf.parse(localTool.getLastChangedDateTime());
                                localUpdatedBySourceDateTime = sdf.parse(localTool.getLastChangedDateTime());
                                serverUpdatedDateTime = tool.getJSONObject("properties").getString("lastchangeddatetime").length() == 20 ?
                                        sdfJson.parse(tool.getJSONObject("properties").getString("lastchangeddatetime")) : sdf.parse(tool.getJSONObject("properties").getString("lastchangeddatetime"));

                                if(tool.getJSONObject("properties").has("lastchangedbysource")) {
                                    serverUpdatedBySourceDateTime = tool.getJSONObject("properties").getString("lastchangedbysource").length() == 20 ?
                                            sdfJson.parse(tool.getJSONObject("properties").getString("lastchangedbysource")) : sdf.parse(tool.getJSONObject("properties").getString("lastchangedbysource"));
                                }


                                if((localLastUpdatedDateTime.equals(serverUpdatedDateTime) || localLastUpdatedDateTime.before(serverUpdatedDateTime)) && localUpdatedBySourceDateTime.equals(serverUpdatedBySourceDateTime)) {
                                    localTool.updateFromGeoJson(tool);
                                } else {
                                    // TODO: Handle updates from KV if there are unreported local changes.


                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            hasCopy = true;
                            break;
                        }
                    }

                    if(!hasCopy && settings != null) {
                        if((!settings.getVesselName().isEmpty() && settings.getVesselName().toUpperCase().equals(tool.getJSONObject("properties").getString("vesselname"))) &&
                                (!settings.getIrcs().isEmpty() && settings.getIrcs().toUpperCase().equals(tool.getJSONObject("properties").getString("ircs"))) ||
                                (!settings.getMmsi().isEmpty() && settings.getMmsi().equals(tool.getJSONObject("properties").getString("mmsi"))) ||
                                (!settings.getImo().isEmpty() && settings.getImo().equals(tool.getJSONObject("properties").getString("imo"))))
                        {
                            matchedTools.put(tool);
                        }
                    }
                }

                if(matchedTools.length() > 0) {
                    final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_confirm_tools_from_api, R.string.tool_confirmation);
                    Button cancelButton = (Button) dialog.findViewById(R.id.dialog_bottom_cancel_button);
                    Button addToolsButton = (Button) dialog.findViewById(R.id.dialog_bottom_add_button);
                    final LinearLayout linearLayoutToolContainer = (LinearLayout) dialog.findViewById(R.id.dialog_confirm_tool_main_container_linear_layout);
                    final List<ToolConfirmationRow> matchedToolsList = new ArrayList<>();

                    for(int i = 0; i < matchedTools.length(); i++) {
                        ToolConfirmationRow confirmationRow = new ToolConfirmationRow(getActivity(), matchedTools.getJSONObject(i));
                        linearLayoutToolContainer.addView(confirmationRow.getView());
                        matchedToolsList.add(confirmationRow);
                    }

                    addToolsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(ToolConfirmationRow row : matchedToolsList) {
                                if(row.isChecked()) {
                                    ToolEntry newTool = row.getToolEntry();
                                    user.getToolLog().addTool(newTool, newTool.getSetupDateTime().substring(0, 10));
                                    ToolLogRow newRow = new ToolLogRow(v.getContext(), newTool, utilityOnClickListeners.getToolEntryEditDialogOnClickListener(getFragmentManager(), mGpsLocationTracker, newTool, user));
                                    toolContainer.addView(newRow.getView());
                                }
                            }

                            user.writeToSharedPref(v.getContext());
                            dialog.dismiss();
                        }
                    });

                    cancelButton.setOnClickListener(utilityOnClickListeners.getDismissDialogListener(dialog));
                    dialog.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        newToolButton = (FloatingActionButton) rootView.findViewById(R.id.register_tool_layout_add_tool_material_button);
        newToolButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dialog = dialogInterface.getDialog(v.getContext(), R.layout.dialog_register_new_tool, R.string.tool_registration);
                final UserSettings settings = user.getSettings() != null ? user.getSettings() : new UserSettings();

                final Button createToolButton = (Button) dialog.findViewById(R.id.dialog_register_tool_create_tool_button);
                final Button cancelButton = (Button) dialog.findViewById(R.id.dialog_register_tool_cancel_button);
                final LinearLayout fieldContainer = (LinearLayout) dialog.findViewById(R.id.dialog_register_tool_main_container);
                final DatePickerRow setupDateRow = new DatePickerRow(v.getContext(), v.getContext().getString(R.string.tool_set_date_colon), getFragmentManager());
                final TimePickerRow setupTimeRow = new TimePickerRow(v.getContext(), v.getContext().getString(R.string.tool_set_time_colon), getFragmentManager(), true);
                final CoordinatesRow coordinatesRow = new CoordinatesRow(v.getContext(), mGpsLocationTracker);
                final SpinnerRow toolRow = new SpinnerRow(v.getContext(), v.getContext().getString(R.string.tool_type), ToolType.getValues());
                final EditTextRow commentRow = new EditTextRow(v.getContext(), getString(R.string.comment_field_header), getString(R.string.comment_field_hint));

                final EditTextRow contactPersonNameRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.contact_person_name), v.getContext().getString(R.string.contact_person_name));
                final EditTextRow contactPersonPhoneRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.contact_person_phone), v.getContext().getString(R.string.contact_person_phone));
                final EditTextRow contactPersonEmailRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.contact_person_email), v.getContext().getString(R.string.contact_person_email));
                final EditTextRow vesselNameRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.vessel_name), v.getContext().getString(R.string.vessel_name));
                final EditTextRow vesselPhoneNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.vessel_phone_number), v.getContext().getString(R.string.vessel_phone_number));
                final EditTextRow vesselIrcsNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.ircs_number), v.getContext().getString(R.string.ircs_number));
                final EditTextRow vesselMmsiNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.mmsi_number), v.getContext().getString(R.string.mmsi_number));
                final EditTextRow vesselImoNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.imo_number), v.getContext().getString(R.string.imo_number));
                final EditTextRow vesselRegistrationNumberRow = new EditTextRow(v.getContext(), v.getContext().getString(R.string.registration_number), v.getContext().getString(R.string.registration_number));
                final ErrorRow errorRow = new ErrorRow(v.getContext(), getString(R.string.error_minimum_identification_factors_not_met), false);

                commentRow.setInputType(InputType.TYPE_CLASS_TEXT);
                commentRow.setHelpText(getString(R.string.comment_help_description));
                vesselNameRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselPhoneNumberRow.setInputType(InputType.TYPE_CLASS_PHONE);
                vesselIrcsNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselIrcsNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_ircs)), new InputFilter.AllCaps()});
                vesselIrcsNumberRow.setHelpText(v.getContext().getString(R.string.ircs_help_description));
                vesselMmsiNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
                vesselMmsiNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_mmsi))});
                vesselMmsiNumberRow.setHelpText(v.getContext().getString(R.string.mmsi_help_description));
                vesselImoNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
                vesselImoNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_imo))});
                vesselImoNumberRow.setHelpText(v.getContext().getString(R.string.imo_help_description));
                vesselRegistrationNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
                vesselRegistrationNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(v.getContext().getResources().getInteger(R.integer.input_length_registration_number)), new InputFilter.AllCaps()});
                vesselRegistrationNumberRow.setHelpText(v.getContext().getString(R.string.registration_help_description));
                contactPersonNameRow.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                contactPersonPhoneRow.setInputType(InputType.TYPE_CLASS_PHONE);
                contactPersonEmailRow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                contactPersonNameRow.setHelpText(v.getContext().getString(R.string.contact_person_name_help_description));
                contactPersonPhoneRow.setHelpText(v.getContext().getString(R.string.contact_person_phone_help_description));
                contactPersonEmailRow.setHelpText(v.getContext().getString(R.string.contact_person_email_help_description));

                fieldContainer.addView(coordinatesRow.getView());
                fieldContainer.addView(setupDateRow.getView());
                fieldContainer.addView(setupTimeRow.getView());
                fieldContainer.addView(toolRow.getView());
                fieldContainer.addView(commentRow.getView());
                fieldContainer.addView(contactPersonNameRow.getView());
                fieldContainer.addView(contactPersonPhoneRow.getView());
                fieldContainer.addView(contactPersonEmailRow.getView());
                fieldContainer.addView(vesselNameRow.getView());
                fieldContainer.addView(vesselPhoneNumberRow.getView());
                fieldContainer.addView(vesselIrcsNumberRow.getView());
                fieldContainer.addView(vesselMmsiNumberRow.getView());
                fieldContainer.addView(vesselImoNumberRow.getView());
                fieldContainer.addView(vesselRegistrationNumberRow.getView());
                fieldContainer.addView(errorRow.getView());

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

                createToolButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        List<Point> coordinates = coordinatesRow.getCoordinates();
                        ToolType toolType = ToolType.createFromValue(toolRow.getCurrentSpinnerItem());
                        String vesselName = vesselNameRow.getFieldText().trim();
                        String vesselPhoneNumber = vesselPhoneNumberRow.getFieldText().trim();
                        String toolSetupDate = setupDateRow.getDate().trim();
                        String toolSetupTime = setupTimeRow.getTime().trim();
                        String toolSetupDateTime = toolSetupDate + "T" + toolSetupTime + ":00.000Z";
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

                        /*
                         *  TODO: Validation of the following:
                         *      Coordinates
                         *      toolType
                         *      VesselName
                         *      VesselPhone
                         *      setupDateTime
                         *
                         */
                        validated = coordinates != null;
                        if(!validated) {
                            return;
                        }

                        validated = utility.validateName(contactPersonName);
                        contactPersonNameRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_name));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, contactPersonNameRow.getView().getBottom());
                                    contactPersonNameRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.validatePhoneNumber(contactPersonPhone);
                        contactPersonPhoneRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_phone_number));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, contactPersonPhoneRow.getView().getBottom());
                                    contactPersonPhoneRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = utility.isEmailValid(contactPersonEmail) || contactPersonEmail.isEmpty();
                        contactPersonEmailRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_email));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, contactPersonEmailRow.getView().getBottom());
                                    contactPersonEmailRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (ircsValidated = utility.validateIRCS(vesselIrcsNumber)) || vesselIrcsNumber.isEmpty();
                        vesselIrcsNumberRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_ircs));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselIrcsNumberRow.getView().getBottom());
                                    vesselIrcsNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (mmsiValidated = utility.validateMMSI(vesselMmsiNumber)) || vesselMmsiNumber.isEmpty();
                        vesselMmsiNumberRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_mmsi));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselMmsiNumberRow.getView().getBottom());
                                    vesselMmsiNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (imoValidated = utility.validateIMO(vesselImoNumber)) || vesselImoNumber.isEmpty();
                        vesselImoNumberRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_imo));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselImoNumberRow.getView().getBottom());
                                    vesselImoNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        validated = (regNumValidated = utility.validateRegistrationNumber(registrationNumber)) || registrationNumber.isEmpty();
                        vesselRegistrationNumberRow.setError(validated ? null : v.getContext().getString(R.string.error_invalid_registration_number));
                        if(!validated) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, vesselRegistrationNumberRow.getView().getBottom());
                                    vesselRegistrationNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        minimumIdentificationFactorsMet = (ircsValidated || mmsiValidated || imoValidated || regNumValidated);

                        errorRow.setVisibility(!minimumIdentificationFactorsMet);

                        if(!minimumIdentificationFactorsMet) {
                            ((ScrollView)fieldContainer.getParent()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)fieldContainer.getParent()).scrollTo(0, errorRow.getView().getBottom());
                                    vesselRegistrationNumberRow.requestFocus();
                                }
                            });

                            return;
                        }

                        ToolEntry toolEntry = new ToolEntry(coordinates, vesselName, vesselPhoneNumber,
                                toolType, toolSetupDateTime, registrationNumber, contactPersonName, contactPersonPhone, contactPersonEmail);

                        toolEntry.setIRCS(vesselIrcsNumber);
                        toolEntry.setMMSI(vesselMmsiNumber);
                        toolEntry.setIMO(vesselImoNumber);
                        toolEntry.setComment(commentString);

                        ToolLog toolLog = user.getToolLog();
                        toolLog.addTool(toolEntry, toolSetupDate);
                        user.writeToSharedPref(v.getContext());

                        View.OnClickListener onClickListener = utilityOnClickListeners.getToolEntryEditDialogOnClickListener(getFragmentManager(), mGpsLocationTracker, toolEntry, user);
                        ToolLogRow row = new ToolLogRow(getActivity(), toolEntry, onClickListener);

                        toolContainer.addView(row.getView());

                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        //Arrow logic
        final Button arrowRightButton = (Button) rootView.findViewById(R.id.register_tool_header_arrow_right);
        setupRightArrowButton(headerDate, currentDate, arrowRightButton);

        Button arrowLeftButton = (Button) rootView.findViewById(R.id.register_tool_header_arrow_left);
        setupLeftArrowButton(headerDate, currentDate, arrowRightButton, arrowLeftButton);

        final Button dateButton = (Button) rootView.findViewById(R.id.register_tool_calendar_picker);
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new DatePickerFragment(headerDate);
                dateFragment.show(getFragmentManager(), "datePicker");
            }
        });

        setupTextChangedListenerOnHeaderDate(headerDate, currentDate, arrowRightButton);

        for(final Map.Entry<String, ArrayList<ToolEntry>> dateEntry : tools) {
            for(final ToolEntry toolEntry : dateEntry.getValue()) {
                if(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED) {
                    continue;
                }

                View.OnClickListener onClickListener = utilityOnClickListeners.getToolEntryEditDialogOnClickListener(getFragmentManager(), mGpsLocationTracker, toolEntry, user);

                ToolLogRow row = new ToolLogRow(getActivity(), toolEntry, onClickListener);
                toolContainer.addView(row.getView());
            }
        }

        return rootView;
    }


    private void setupTextChangedListenerOnHeaderDate(final TextView headerDate, final String currentDate, final Button arrowRightButton) {
        headerDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //generateDiaryTableDataOnCurrentlySelectedDate(rootView, headerDate, haulLogSpecies, speciesHeader, haulLogLayout);
                if (!headerDate.getText().toString().equals(currentDate)) {
                    arrowRightButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupRightArrowButton(final TextView headerDate, final String currentDate, final Button arrowRightButton) {
        arrowRightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (headerDate.getText().toString().equals(currentDate)) {
                    arrowRightButton.setVisibility(View.INVISIBLE);
                } else {
                    String next = user.getToolLog().myLog.higherKey(headerDate.getText().toString());
                    if (next != null) {
                        headerDate.setText(next);
                        if (headerDate.getText().toString().equals(currentDate)) {
                            arrowRightButton.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        headerDate.setText(currentDate);
                        arrowRightButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        if (headerDate.getText().toString().equals(currentDate)) {
            if (arrowRightButton.getVisibility() == View.VISIBLE) {
                arrowRightButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupLeftArrowButton(final TextView headerDate, final String currentDate, final Button arrowRightButton, Button arrowLeftButton) {
        arrowLeftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (headerDate.toString().equals(currentDate)) {
                    if (user.getToolLog().myLog.isEmpty()) {
                        Log.d(TAG, "Decrementing date by one day, because the haul log is empty");
                        decrementDateByOneInHeader(headerDate.getText().toString(), headerDate);
                    } else {
                        String currentKey = user.getToolLog().myLog.lastKey();
                        if (currentKey.equals(currentDate)) {
                            // Skip
                            currentKey = user.getToolLog().myLog.lowerKey(currentKey);
                            if (currentKey != null) {
                                headerDate.setText(currentKey);
                            } else {
                                decrementDateByOneInHeader(headerDate.getText().toString(), headerDate);
                            }
                        }
                    }
                } else {
                    String currentKey = headerDate.getText().toString();
                    currentKey = user.getToolLog().myLog.lowerKey(currentKey);
                    if (currentKey != null) {
                        headerDate.setText(currentKey);
                    } else {
                        decrementDateByOneInHeader(headerDate.getText().toString(), headerDate);
                    }

                }

                if (arrowRightButton.getVisibility() != View.VISIBLE && !headerDate.getText().toString().equals(currentDate)) {
                    arrowRightButton.setVisibility(View.VISIBLE);
                } else {
                    if (headerDate.getText().toString().equals(currentDate)) {
                        arrowRightButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
            private void decrementDateByOneInHeader(String currentKey, final TextView headerDate) {
                final int DECREMENT_VALUE = -1;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date selectedDate = null;
                try {
                    selectedDate = sdf.parse(currentKey);
                } catch (ParseException e) {
                    Log.d(TAG, "Could not parse the given date in decrementByOneInHeader");
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.setTime(selectedDate);
                cal.add(Calendar.DATE, DECREMENT_VALUE);
                Date updatedDate = cal.getTime();
                String mDate = sdf.format(updatedDate);
                headerDate.setText(mDate);
            }
        });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private String getCurrentDate() {
        String time = getCurrentDateTime();
        return time.split("\\s+")[0];
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    @SuppressLint("ValidFragment")
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        TextView mTextView;
        boolean hasMaxTime;

        @SuppressLint("ValidFragment")
        public TimePickerFragment(TextView tx, boolean maxTime) {
            mTextView = tx;
            hasMaxTime = maxTime;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String toolTime = sdf.format(calendar.getTime());
            String currentTime = sdf.format(new Date());

            if (hasMaxTime && FiskInfoUtility.compareDates(toolTime, currentTime, "HH:mm:ss") > 0 ) {
                Toast.makeText(getActivity(), getString(R.string.error_cannot_set_time_to_future), Toast.LENGTH_LONG).show();
            } else {
                mTextView.setText(toolTime.substring(0, 5));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_tool_report:
                generateAndSendGeoJsonToolReport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void generateAndSendGeoJsonToolReport() {
        FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();
        JSONObject featureCollection = new JSONObject();
        try {
            Set<Map.Entry<String, ArrayList<ToolEntry>>> tools = user.getToolLog().myLog.entrySet();
            JSONArray featureList = new JSONArray();

            for(final Map.Entry<String, ArrayList<ToolEntry>> dateEntry : tools) {
                for(final ToolEntry toolEntry : dateEntry.getValue()) {
                    if (toolEntry.getToolStatus() == ToolEntryStatus.STATUS_RECEIVED ||
                            toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED) {
                        continue;
                    }

                    JSONObject gjsonTool = toolEntry.toGeoJson();
                    featureList.put(gjsonTool);
                }
            }

            if(featureList.length() == 0) {
                Toast.makeText(getActivity(), getString(R.string.no_changes_to_report), Toast.LENGTH_LONG).show();

                return;
            }

            featureCollection.put("features", featureList);
            featureCollection.put("type", "FeatureCollection");
            featureCollection.put("crs", JSONObject.NULL);
            featureCollection.put("bbox", JSONObject.NULL);

            String toolString = featureCollection.toString(4);

            if (fiskInfoUtility.isExternalStorageWritable()) {
                fiskInfoUtility.writeMapLayerToExternalStorage(getActivity(), toolString.getBytes(), getString(R.string.tool_report_file_name), getString(R.string.format_geojson), null, false);

                boolean found = false;
                String[] clients = new String[] { "inbox", "gmail", "outlook", "mail",  };
                String[] recipients = new String[] { getString(R.string.tool_report_recipient_email) };
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/plain");

//                List<ResolveInfo> resInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
//                if (!resInfo.isEmpty()){
//                    for (ResolveInfo info : resInfo) {
//                        for(String emailClient : clients) {
//                            if (info.activityInfo.packageName.toLowerCase().contains(emailClient) ||
//                                    info.activityInfo.name.toLowerCase().contains(emailClient)) {

//                                info.activityInfo.packageName.toLowerCase().contains(type) ||
//                                info.activityInfo.name.toLowerCase().contains(type) ) {
//                                intent.setPackage(info.activityInfo.packageName);
//                                break;
//                            }
//                        }
//                    }
//                }

                String toolIds;
                StringBuilder sb = new StringBuilder();

                sb.append("Redskapskoder:\n");

                for(int i = 0; i < featureList.length(); i++) {
                    sb.append(Integer.toString(i + 1));
                    sb.append(": ");
                    sb.append(featureList.getJSONObject(i).getJSONObject("properties").getString("ToolId"));
                    sb.append("\n");
                }

                toolIds = sb.toString();

                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_TEXT, toolIds);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.tool_report_email_header));
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                        + "/FiskInfo/Redskapsrapport.geojson");
                Uri uri = Uri.fromFile(file);
                intent.putExtra(Intent.EXTRA_STREAM, uri);

                startActivity(Intent.createChooser(intent, getString(R.string.send_tool_report_intent_header)));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ValidFragment")
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        TextView mTextView;

        @SuppressLint("ValidFragment")
        public DatePickerFragment(TextView tx) {
            mTextView = tx;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog d = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker dp = d.getDatePicker();
            dp.setMaxDate(new Date().getTime());  //Deprecated 5.0
            return d;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(c.getTime());
            String today = sdf.format(new Date());

            if (FiskInfoUtility.compareDates(date, today, "yyyy-MM-dd") > 0 ) {
                Toast.makeText(getActivity(), getString(R.string.error_cannot_set_time_to_future), Toast.LENGTH_LONG).show();
            } else {
                mTextView.setText(date);
            }
        }
    }

}

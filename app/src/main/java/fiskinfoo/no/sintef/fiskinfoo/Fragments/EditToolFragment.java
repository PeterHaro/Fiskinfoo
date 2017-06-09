package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
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
    private CheckBoxRow toolLostRow;
    private SpinnerRow toolLostCauseRow;
    private SpinnerRow toolLostConditionsRow;
    private WebView toolMapPreviewWebView;
    private RelativeLayout mapPreviewContainer;
    private Button mapPreviewZoomButton;
    private EditTextRow numberOfToolsLostRow;
    private EditTextRow lostToolLengthRow;


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

        if(tool != null && !(tool.getToolStatus() == ToolEntryStatus.STATUS_REMOVED || tool.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_CONFIRMED)) {
            setHasOptionsMenu(true);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_tool, container, false);

        fieldsContainer = (LinearLayout) rootView.findViewById(R.id.dialog_fragment_edit_tool_linear_layout);
        generateFields();

        if(tool != null) {
            populateFieldsFromTool();
        } else {
            populateFieldsFromSettings();
        }

        setHasOptionsMenu(true);
        
        return rootView;
    }

    private class barentswatchFiskInfoWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:populateMap();");

            if(tool != null) {
                JSONObject jsonTool = tool.toGeoJson(locationTracker);
                view.loadUrl("javascript:highlightTool(" + jsonTool + ");");

            }
        }
    }

    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context context) {
            mContext = context;
        }

        @SuppressWarnings("unused")
        @android.webkit.JavascriptInterface
        public String getToken() {
            return userInterface.getUser().getToken();
        }

        @SuppressWarnings("unused")
        @android.webkit.JavascriptInterface
        public String getGeoJSONFile(String fileName) {
            String directoryFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";

            File file = new File(directoryFilePath + fileName + ".JSON");
            StringBuilder jsonString = new StringBuilder();
            BufferedReader bufferReader = null;

            if(file.exists()) {
                try {
                    bufferReader = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = bufferReader.readLine()) != null) {
                        jsonString.append(line);
                        jsonString.append('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferReader != null) {
                        try {
                            bufferReader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Sent following layer: " + fileName);
            }

            return file.exists() ? jsonString.toString() : null;
        }

        @SuppressWarnings("unused")
        @android.webkit.JavascriptInterface
        public String getToolFeatureCollection() {
//            return toolsFeatureCollection.toString();
            return null;
        }
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

        toolLostRow = new CheckBoxRow(getContext(), getString(R.string.tool_lost_row_text), true);
        toolLostCauseRow = new SpinnerRow(getContext(), getString(R.string.tool_lost_cause_row_text), getResources().getStringArray(R.array.tool_lost_causes));
        toolLostConditionsRow = new SpinnerRow(getContext(), getString(R.string.tool_lost_conditions_row_text), getResources().getStringArray(R.array.tool_lost_conditions));
        toolLostCauseRow.addSpinnerOption(getString(R.string.not_selected), 0);
        toolLostConditionsRow.addSpinnerOption(getString(R.string.not_selected), 0);
        toolLostRow.setVisibility(true);
        toolLostCauseRow.setVisibility(false);
        toolLostConditionsRow.setVisibility(false);

        numberOfToolsLostRow = new EditTextRow(getContext(), getString(R.string.number_of_crab_pots_lost), "");
        lostToolLengthRow = new EditTextRow(getContext(), getString(R.string.number_of_meters_of_line_lost), "");
        numberOfToolsLostRow.setVisibility(false);
        lostToolLengthRow.setVisibility(false);

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        mapPreviewContainer = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.utility_tool_map_preview, relativeLayout, false);
        toolMapPreviewWebView = (WebView) mapPreviewContainer.findViewById(R.id.utility_tool_map_preview_web_view);
        mapPreviewZoomButton = (Button) mapPreviewContainer.findViewById(R.id.utility_tool_map_preview_zoom_button);
        mapPreviewZoomButton.setTag(null);

        mapPreviewZoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getTag() == null) {
                    toolMapPreviewWebView.loadUrl("javascript:zoomMap(8);");
                    view.setBackgroundResource(R.drawable.ic_zoom_in_black_24dp);
                    view.setTag("");
                } else {
                    toolMapPreviewWebView.loadUrl("javascript:zoomToToolExtent();");
                    view.setBackgroundResource(R.drawable.ic_zoom_out_black_24dp);
                    view.setTag(null);
                }
            }
        });

        toolMapPreviewWebView.loadUrl("file:///android_asset/tool_map_preview.html");
        toolMapPreviewWebView.getSettings().setJavaScriptEnabled(true);
        toolMapPreviewWebView.getSettings().setDomStorageEnabled(true);
        toolMapPreviewWebView.addJavascriptInterface(new JavaScriptInterface(getActivity()), "Android");
        toolMapPreviewWebView.setWebViewClient(new barentswatchFiskInfoWebClient());
        toolMapPreviewWebView.setWebChromeClient(new WebChromeClient() {

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                Log.d("geolocation permission", "permission >>>" + origin);
                callback.invoke(origin, true, false);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateMapPreview();
            }
        };

        coordinatesRow.setTextWatcher(textWatcher);
        coordinatesRow.setCardinalDirectionSwitchOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateMapPreview();
            }
        });

        setupTimeRow.setDateTextView(setupDateRow.getDateTextView());

        toolLostRow.setOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                toggleToolLostRows(checked);
            }
        });

        toolRow.setOnSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(toolLostRow.isChecked()) {
                    toggleToolLostRows(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        archiveRow = new ActionRow(getContext(), getString(R.string.archive_tool), R.drawable.ic_archive_black_24dp, getArchiveToolRowOnClickListener());
        deleteRow = new ActionRow(getContext(), getString(R.string.delete_tool), R.drawable.ic_delete_black_24dp, getDeleteToolRowOnClickListener());

        commentRow.setInputType(InputType.TYPE_CLASS_TEXT);
        commentRow.setHelpText(getString(R.string.comment_help_description));
        vesselNameRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselPhoneNumberRow.setInputType(InputType.TYPE_CLASS_PHONE);
        vesselIrcsNumberRow.setInputType(InputType.TYPE_CLASS_TEXT);
        vesselIrcsNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.input_length_ircs)), new InputFilter.AllCaps()});
        vesselIrcsNumberRow.setHelpText(getString(R.string.ircs_help_description));
        vesselMmsiNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselMmsiNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.input_length_mmsi))});
        vesselMmsiNumberRow.setHelpText(getString(R.string.mmsi_help_description));
        vesselImoNumberRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        vesselImoNumberRow.setInputFilters(new InputFilter[]{new InputFilter.LengthFilter(getResources().getInteger(R.integer.input_length_imo))});
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
        contactPersonNameRow.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        contactPersonPhoneRow.setInputType(InputType.TYPE_CLASS_PHONE);
        contactPersonEmailRow.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        contactPersonNameRow.setHelpText(getString(R.string.contact_person_name_help_description));
        contactPersonPhoneRow.setHelpText(getString(R.string.contact_person_phone_help_description));
        contactPersonEmailRow.setHelpText(getString(R.string.contact_person_email_help_description));
        numberOfToolsLostRow.setInputType(InputType.TYPE_CLASS_NUMBER);
        lostToolLengthRow.setInputType(InputType.TYPE_CLASS_NUMBER);

        toolRemovedRow.setVisibility(false);

        fieldsContainer.addView(coordinatesRow.getView());
        fieldsContainer.addView(mapPreviewContainer);
        fieldsContainer.addView(setupDateRow.getView());
        fieldsContainer.addView(setupTimeRow.getView());
        fieldsContainer.addView(toolRow.getView());
        fieldsContainer.addView(toolLostRow.getView());
        fieldsContainer.addView(toolLostCauseRow.getView());
        fieldsContainer.addView(toolLostConditionsRow.getView());
        fieldsContainer.addView(lostToolLengthRow.getView());
        fieldsContainer.addView(numberOfToolsLostRow.getView());
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
        fieldsContainer.addView(toolRemovedRow.getView(), 4);

        if (tool != null) {
            if (tool.hasBeenRegistered()) {
                fieldsContainer.addView(archiveRow.getView());
            }

            fieldsContainer.addView(deleteRow.getView());

            // TODO: If archived, set fields as uneditable
            if(tool.getToolStatus() == ToolEntryStatus.STATUS_REMOVED || tool.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_CONFIRMED) {
                setEnabled(false);

                if(!tool.getRemovedTime().isEmpty()) {
                    DatePickerRow dateRemovedRow = new DatePickerRow(getContext(), getString(R.string.date_removed), getFragmentManager());
                    dateRemovedRow.setEnabled(false);
                    dateRemovedRow.setIconVisibility(false);
                    dateRemovedRow.setDate(tool.getRemovedTime().substring(0, 10));
                    fieldsContainer.addView(dateRemovedRow.getView(), 5);
                }
            }
        }
    }

    private void updateMapPreview() {
        List<Point> coordinates = coordinatesRow.getCoordinates();
        if(coordinates != null) {
            if(tool != null) {
                JSONObject jsonTool = tool.toGeoJson(locationTracker);
                JSONArray toolCoordinates = new JSONArray();

                try {
                    if(coordinates.size() == 1) {
                        toolCoordinates.put(coordinates.get(0).getLongitude());
                        toolCoordinates.put(coordinates.get(0).getLatitude());
                        jsonTool.getJSONObject("geometry").put("type", "Point");
                    } else {
                        for(Point currentPosition : coordinates) {
                            JSONArray position = new JSONArray();
                            position.put(currentPosition.getLongitude());
                            position.put(currentPosition.getLatitude());

                            toolCoordinates.put(position);
                            jsonTool.getJSONObject("geometry").put("type", "LineString");
                        }
                    }

                    jsonTool.getJSONObject("geometry").put("coordinates", toolCoordinates);
                    toolMapPreviewWebView.loadUrl("javascript:highlightTool(" + jsonTool + ");");
                    mapPreviewZoomButton.setBackgroundResource(R.drawable.ic_zoom_out_black_24dp);
                    mapPreviewZoomButton.setTag(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONObject jsonTool = new JSONObject();
                JSONArray toolCoordinates = new JSONArray();

                try {
                    JSONObject geometry = new JSONObject();
                    JSONObject properties = new JSONObject();

                    if(coordinates.size() == 1) {
                        toolCoordinates.put(coordinates.get(0).getLongitude());
                        toolCoordinates.put(coordinates.get(0).getLatitude());
                        geometry.put("type", "Point");
                    } else {
                        for(Point currentPosition : coordinatesRow.getCoordinates()) {
                            JSONArray position = new JSONArray();
                            position.put(currentPosition.getLongitude());
                            position.put(currentPosition.getLatitude());

                            toolCoordinates.put(position);
                        }

                        geometry.put("type", "LineString");
                    }

                    geometry.put("coordinates", toolCoordinates);
                    geometry.put("crs", JSONObject.NULL);
                    geometry.put("bbox", JSONObject.NULL);
                    jsonTool.put("geometry", geometry);

                    properties.put("ToolTypeCode", ToolType.createFromValue(toolRow.getCurrentSpinnerItem()).getToolCode());
                    properties.put("ToolTypeName", ToolType.createFromValue(toolRow.getCurrentSpinnerItem()).toString());
                    properties.put("ToolColor", "#" + Integer.toHexString(ToolType.createFromValue(toolRow.getCurrentSpinnerItem()).getHexColorValue()).toUpperCase());

                    jsonTool.put("properties", properties);
                    jsonTool.put("type", "Feature");
                    jsonTool.put("crs", JSONObject.NULL);
                    jsonTool.put("bbox", JSONObject.NULL);

                    toolMapPreviewWebView.loadUrl("javascript:highlightTool(" + jsonTool + ");");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void toggleToolLostRows(boolean checked) {

        if(checked) {
            if(toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_crab_pot))) {
                lostToolLengthRow.setRowHeader(getString(R.string.number_of_meters_of_line_lost));
                numberOfToolsLostRow.setRowHeader(getString(R.string.number_of_crab_pots_lost));
                lostToolLengthRow.setVisibility(true);
                numberOfToolsLostRow.setVisibility(true);
            } else if(toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_net))) {
                lostToolLengthRow.setRowHeader(getString(R.string.size_of_nets_in_meters));
                numberOfToolsLostRow.setRowHeader(getString(R.string.number_of_nets_lost));
                lostToolLengthRow.setVisibility(true);
                numberOfToolsLostRow.setVisibility(true);
            } else if(toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_line))) {
                lostToolLengthRow.setRowHeader(getString(R.string.number_of_meters_of_line_lost));
                lostToolLengthRow.setVisibility(true);
                numberOfToolsLostRow.setVisibility(false);
            } else if(toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_purseine))){
                lostToolLengthRow.setRowHeader(getString(R.string.number_of_meters_of_net_lost));
                lostToolLengthRow.setVisibility(true);
                numberOfToolsLostRow.setVisibility(false);
            } else if(toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_unknown))) {
                lostToolLengthRow.setRowHeader(getString(R.string.size_of_lost_tool));
                lostToolLengthRow.setVisibility(true);
                numberOfToolsLostRow.setVisibility(false);
            }

            toolLostCauseRow.setVisibility(true);
            toolLostConditionsRow.setVisibility(true);
        } else {
            numberOfToolsLostRow.setVisibility(false);
            lostToolLengthRow.setVisibility(false);
            toolLostCauseRow.setVisibility(false);
            toolLostConditionsRow.setVisibility(false);
        }
    }

    private void populateFieldsFromTool() {

        final SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss), Locale.getDefault());
        SimpleDateFormat sdfDisplay = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss), Locale.getDefault());

        sdfDisplay.setTimeZone(TimeZone.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date setupDateTime;
        String setupDateTimeString;
        String setupDateString;
        String setupTimeString;

        try {
            setupDateTime = sdf.parse(tool.getSetupDateTime());
            setupDateTimeString = sdfDisplay.format(setupDateTime);
            setupDateString = setupDateTimeString.substring(0, 10);
            setupTimeString = setupDateTimeString.substring(setupDateTimeString.indexOf("T") + 1, setupDateTimeString.indexOf("T") + 6);
        } catch (ParseException e) {
            e.printStackTrace();
            setupDateString = tool.getSetupDate();
            setupTimeString = tool.getSetupDateTime().substring(tool.getSetupDateTime().indexOf('T') + 1, tool.getSetupDateTime().indexOf('T') + 6);
        }




        coordinatesRow.setCoordinates(getActivity(), tool.getCoordinates(), this);
        setupDateRow.setDate(setupDateString);
        setupTimeRow.setTime(setupTimeString);

        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            coordinatesRow.setPositionButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissions(new String[] { ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }
            });
        }

        if(tool.hasBeenRegistered() || !tool.getRemovedTime().isEmpty()) {
            toolRemovedRow.setVisibility(true);
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
        vesselRegistrationNumberRow.setText(tool.getRegNum().replace(" ", ""));
        toolRemovedRow.setChecked(!tool.getRemovedTime().isEmpty());

        if(tool.isToolLost()) {
            toolLostRow.setChecked(true);
            toolLostCauseRow.setVisibility(true);
            toolLostCauseRow.setSelectedSpinnerItem(tool.getToolLostReason());
            toolLostConditionsRow.setSelectedSpinnerItem(tool.getToolLostWeather());
            lostToolLengthRow.setText(String.valueOf(tool.getToolLostLength()));

            if(tool.getToolType() == ToolType.CRAB_POTS ||
                    tool.getToolType() == ToolType.NETS) {
                numberOfToolsLostRow.setText(String.valueOf(tool.getNumberOfLostTools()));
            }
        }
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
            vesselRegistrationNumberRow.setText(settings.getRegistrationNumber().replace(" ", ""));
        }
    }

    private void createOrUpdateToolEntry() {
        final SimpleDateFormat sdfMilliSeconds = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss_sss), Locale.getDefault());
        final SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss), Locale.getDefault());

        List<Point> coordinates = coordinatesRow.getCoordinates();
        final ToolType toolType = ToolType.createFromValue(toolRow.getCurrentSpinnerItem());
        final String vesselName = vesselNameRow.getFieldText().trim();
        final String vesselPhoneNumber = vesselPhoneNumberRow.getFieldText().trim();
        String toolSetupDate = setupDateRow.getDate().trim();
        String toolSetupTime = setupTimeRow.getTime().trim();
        String toolSetupDateTime = toolSetupDate + "T" + toolSetupTime + ":00.000";
        String commentString = commentRow.getFieldText().trim();
        final String vesselIrcsNumber = vesselIrcsNumberRow.getFieldText().trim();
        final String vesselMmsiNumber = vesselMmsiNumberRow.getFieldText().trim();
        final String vesselImoNumber = vesselImoNumberRow.getFieldText().trim();
        final String contactPersonName = contactPersonNameRow.getFieldText().trim();
        final String contactPersonPhone = contactPersonPhoneRow.getFieldText().trim();
        final String contactPersonEmail = contactPersonEmailRow.getFieldText().trim();
        String toolLostReason = toolLostCauseRow.getCurrentSpinnerItem();
        String toolLostWeather = toolLostConditionsRow.getCurrentSpinnerItem();
        int lostToolLength = lostToolLengthRow.getFieldText().isEmpty() ? 0 : Integer.valueOf(lostToolLengthRow.getFieldText());
        int numberOfToolsLost = numberOfToolsLostRow.getFieldText().isEmpty() ? 0 : Integer.valueOf(numberOfToolsLostRow.getFieldText());
        boolean toolLost = toolLostRow.isChecked();
        boolean toolRemoved = toolRemovedRow.isChecked();
        boolean edited = false;

        if(!validateFields()) {
            return;
        }

        final String registrationNumber = FiskInfoUtility.validateRegistrationNumber(vesselRegistrationNumberRow.getFieldText().trim()) ? FiskInfoUtility.formatRegistrationNumber(vesselRegistrationNumberRow.getFieldText().trim()) : "";

        if(tool == null) {
            ToolEntry toolEntry = new ToolEntry(coordinates, vesselName, vesselPhoneNumber, contactPersonEmail,
                    toolType, toolSetupDateTime, registrationNumber, contactPersonName, contactPersonPhone, contactPersonEmail);

            toolEntry.setIRCS(vesselIrcsNumber);
            toolEntry.setMMSI(vesselMmsiNumber);
            toolEntry.setIMO(vesselImoNumber);
            toolEntry.setComment(commentString);
            toolEntry.setToolLost(toolLost);

            if(toolLost) {
                toolEntry.setRemovedTime(toolEntry.getSetupDateTime());
                toolEntry.setToolLost(true);
                toolEntry.setToolLostReason(toolLostReason);
                toolEntry.setToolLostWeather(toolLostWeather);
                toolEntry.setToolStatus(ToolEntryStatus.STATUS_TOOL_LOST_UNREPORTED);
                toolEntry.setToolLostLength(lostToolLength);

                if(toolType == ToolType.CRAB_POTS ||
                        toolType == ToolType.NETS) {
                    toolEntry.setNumberOfLostTools(numberOfToolsLost);
                } else {
                    toolEntry.setNumberOfLostTools(0);
                }
            }

            mListener.updateTool(toolEntry);
        } else {
            sdf.setTimeZone(TimeZone.getDefault());
            Date setupDate = null;
            String setupDateString = toolSetupDate + "T" + toolSetupTime + ":00";

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
                    (!commentString.equals(tool.getComment())) ||
                    (toolLost != tool.isToolLost()) ||
                    (numberOfToolsLost != tool.getNumberOfLostTools()) ||
                    (lostToolLength != tool.getToolLostLength()))
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
//                user.writeToSharedPref(getContext());
                }

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
                tool.setToolLost(toolLost);

                if(toolLost) {
                    tool.setToolLostReason(toolLostReason);
                    tool.setToolLostWeather(toolLostWeather);
                    ToolEntryStatus toolStatus = ((tool.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_UNREPORTED || tool.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED) ?
                            ToolEntryStatus.STATUS_TOOL_LOST_UNREPORTED : ToolEntryStatus.STATUS_TOOL_LOST_UNSENT);
                    tool.setToolStatus(toolStatus);
                    tool.setToolLostLength(lostToolLength);

                    if(toolType == ToolType.CRAB_POTS ||
                            toolType == ToolType.NETS) {
                        tool.setNumberOfLostTools(numberOfToolsLost);
                    } else {
                        tool.setNumberOfLostTools(0);
                    }
                } else {
                    ToolEntryStatus toolStatus = (toolRemoved ? ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED : ((tool.getToolStatus() == ToolEntryStatus.STATUS_UNREPORTED || tool.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_UNREPORTED) ?
                            ToolEntryStatus.STATUS_UNREPORTED : ToolEntryStatus.STATUS_UNSENT));
                    tool.setToolStatus(toolStatus);
                }

                final User user = userInterface.getUser();

                if(!compareSettingsValues() && user.showUpdateUserSettingsDialog()) {

                    View checkBoxView = View.inflate(getActivity(), R.layout.dialog_alert_checkbox, null);
                    final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox_dialog_checkbox);

                    checkBox.setText(R.string.do_not_show_message_again);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.update_settings);
                    builder.setMessage(R.string.update_settings_explanation)
                            .setView(checkBoxView)
                            .setCancelable(true)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    user.setShowUpdateUserSettingsDialog(!checkBox.isChecked());

                                    UserSettings settings = new UserSettings();

                                    settings.setToolType(toolType);
                                    settings.setVesselName(vesselName);
                                    settings.setVesselPhone(vesselPhoneNumber);
                                    settings.setIrcs(vesselIrcsNumber);
                                    settings.setMmsi(vesselMmsiNumber);
                                    settings.setImo(vesselImoNumber);
                                    settings.setRegistrationNumber(registrationNumber);
                                    settings.setContactPersonEmail(contactPersonEmail);
                                    settings.setContactPersonName(contactPersonName);
                                    settings.setContactPersonPhone(contactPersonPhone);

                                    user.setSettings(settings);
                                    user.writeToSharedPref(getContext());
                                    Toast.makeText(getContext(), R.string.tool_updated, Toast.LENGTH_LONG).show();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.popBackStackImmediate();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    user.setShowUpdateUserSettingsDialog(!checkBox.isChecked());
                                    user.writeToSharedPref(getContext());
                                    Toast.makeText(getContext(), R.string.tool_updated, Toast.LENGTH_LONG).show();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.popBackStackImmediate();
                                }
                            }).show();
                } else {
                    user.writeToSharedPref(getContext());
                    Toast.makeText(getContext(), R.string.tool_updated, Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStackImmediate();
                }
            } else {
                Toast.makeText(getContext(), R.string.no_changes_made, Toast.LENGTH_LONG).show();
            }
        }

        if(!userInterface.getUser().showUpdateUserSettingsDialog() || !edited) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStackImmediate();
        }
    }

    /**
     * Compares the tool values submitted to the values saved in user settings.
     * @return Returns true if one of the following is true:
     *      - All values are equal
     *      - Tool values and settings values are equal or settings values are non-empty and their tool value counterparts are empty
     */
    private boolean compareSettingsValues() {
        UserSettings settings = userInterface.getUser().getSettings();
        ToolType toolType = ToolType.createFromValue(toolRow.getCurrentSpinnerItem());
        String vesselName = vesselNameRow.getFieldText().trim();
        String vesselPhone = vesselPhoneNumberRow.getFieldText().trim();
        String ircs = vesselIrcsNumberRow.getFieldText().trim();
        String mmsi = vesselMmsiNumberRow.getFieldText().trim();
        String imo = vesselImoNumberRow.getFieldText().trim();
        String registrationNumber = FiskInfoUtility.formatRegistrationNumber(vesselRegistrationNumberRow.getFieldText().trim());
        String ContactPersonEmail = contactPersonEmailRow.getFieldText().trim();
        String ContactPersonPhone = contactPersonPhoneRow.getFieldText().trim();
        String ContactPersonName = contactPersonNameRow.getFieldText().trim();

        if(settings == null) {
            return false;
        }

        if(!settings.getIrcs().equals(ircs) || ircs.isEmpty()) {
            return false;
        }

        if(!settings.getMmsi().equals(mmsi) || mmsi.isEmpty()) {
            return false;
        }

        if(!settings.getImo().equals(imo) || imo.isEmpty()) {
            return false;
        }

        if(!settings.getRegistrationNumber().equals(registrationNumber) || registrationNumber.isEmpty()) {
            return false;
        }

        if(!settings.getContactPersonEmail().equals(ContactPersonEmail) || ContactPersonEmail.isEmpty()) {
            return false;
        }

        if(!settings.getContactPersonPhone().equals(ContactPersonPhone) || ContactPersonPhone.isEmpty()) {
            return false;
        }

        if(!settings.getVesselPhone().equals(vesselPhone) || vesselPhone.isEmpty()) {
            return false;
        }

        if(!settings.getVesselName().equals(vesselName) || vesselName.isEmpty()) {
            return false;
        }

        if(settings.getToolType() != toolType) {
            return false;
        }

        if(!settings.getContactPersonName().equals(ContactPersonName) || ContactPersonName.isEmpty()) {
            return false;
        }

//        return (settings.getToolType() == toolType &&
//                (settings.getVesselName().equals(vesselName) || vesselName.isEmpty()) &&
//                (settings.getVesselPhone().equals(vesselPhone) || vesselPhone.isEmpty()) &&
//                (settings.getIrcs().equals(ircs) || ircs.isEmpty()) &&
//                (settings.getMmsi().equals(mmsi) || mmsi.isEmpty()) &&
//                (settings.getImo().equals(imo) || imo.isEmpty()) &&
//                (settings.getRegistrationNumber().equals(registrationNumber) || registrationNumber.isEmpty()) &&
//                (settings.getContactPersonEmail().equals(ContactPersonEmail) || ContactPersonEmail.isEmpty()) &&
//                (settings.getContactPersonPhone().equals(ContactPersonPhone) || ContactPersonPhone.isEmpty()) &&
//                (settings.getContactPersonName().equals(ContactPersonName) || ContactPersonName.isEmpty())
//
//
//        );

        return true;
    }

    private boolean validateFields() {
        List<Point> coordinates = coordinatesRow.getCoordinates();
        String vesselName = vesselNameRow.getFieldText().trim();
        String vesselIrcsNumber = vesselIrcsNumberRow.getFieldText().trim();
        String vesselMmsiNumber = vesselMmsiNumberRow.getFieldText().trim();
        String vesselImoNumber = vesselImoNumberRow.getFieldText().trim();
        String registrationNumber = vesselRegistrationNumberRow.getFieldText().trim();
        String contactPersonName = contactPersonNameRow.getFieldText().trim();
        String contactPersonPhone = contactPersonPhoneRow.getFieldText().trim();
        String contactPersonEmail = contactPersonEmailRow.getFieldText().trim();
        boolean toolLost = toolLostRow.isChecked();
        boolean toolRemoved = toolRemovedRow.isChecked();
        boolean validated;
        boolean ircsValidated;
        boolean mmsiValidated;
        boolean imoValidated;
        boolean regNumValidated;
        boolean minimumIdentificationFactorsMet;

        validated = coordinates != null;
        if(!validated) {
            return false;
        }

        validated = toolLost != toolRemoved || (!toolLost);

        if(!validated) {
            highlightInvalidField(toolRemovedRow);
            Toast.makeText(getContext(), R.string.error_lost_tool_cannot_be_marked_removed, Toast.LENGTH_LONG).show();

            return false;
        }

        if(toolLost) {
            validated = toolLostCauseRow.getCurrentSpinnerIndex() > 0;
            toolLostCauseRow.setError(validated ? null : getString(R.string.error_field_required_short));
            if(!validated) {
                highlightInvalidField(toolLostCauseRow);

                return false;
            }

            validated = toolLostConditionsRow.getCurrentSpinnerIndex() > 0;
            toolLostConditionsRow.setError(validated ? null : getString(R.string.error_field_required_short));
            if(!validated) {
                highlightInvalidField(toolLostConditionsRow);

                return false;
            }

            if(toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_crab_pot)) ||
                    toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_net))) {

                validated = !lostToolLengthRow.getFieldText().isEmpty();
                lostToolLengthRow.setError(validated ? null : getString(R.string.error_field_required_short));
                if(!validated) {
                    highlightInvalidField(lostToolLengthRow);

                    return false;
                }

                validated = !numberOfToolsLostRow.getFieldText().isEmpty();
                numberOfToolsLostRow.setError(validated ? null : getString(R.string.error_field_required_short));
                if(!validated) {
                    highlightInvalidField(numberOfToolsLostRow);

                    return false;
                }
            } else if(toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_line)) ||
                    toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_purseine)) ||
                    toolRow.getCurrentSpinnerItem().equals(getString(R.string.tool_type_unknown))) {

                validated = !lostToolLengthRow.getFieldText().isEmpty();
                lostToolLengthRow.setError(validated ? null : getString(R.string.error_field_required_short));
                if(!validated) {
                    highlightInvalidField(lostToolLengthRow);

                    return false;
                }
            } else {
                return false;
            }
        }

        validated = FiskInfoUtility.validateName(contactPersonName);
        contactPersonNameRow.setError(validated ? null : getString(R.string.error_invalid_name));
        if(!validated) {
            highlightInvalidField(contactPersonNameRow);

            return false;
        }

        validated = FiskInfoUtility.validatePhoneNumber(contactPersonPhone);
        contactPersonPhoneRow.setError(validated ? null : getString(R.string.error_invalid_phone_number));
        if(!validated) {
            highlightInvalidField(contactPersonPhoneRow);

            return false;
        }

        validated = FiskInfoUtility.isEmailValid(contactPersonEmail) || contactPersonEmail.isEmpty();
        contactPersonEmailRow.setError(validated ? null : getString(R.string.error_invalid_email));
        if(!validated) {
            highlightInvalidField(contactPersonEmailRow);

            return false;
        }

        validated = !vesselNameRow.getFieldText().isEmpty();
        vesselNameRow.setError(validated ? null : getString(R.string.error_invalid_vessel_name));
        if(!validated) {
            highlightInvalidField(vesselNameRow);

            return false;
        }

        validated = !vesselPhoneNumberRow.getFieldText().isEmpty();
        vesselPhoneNumberRow.setError(validated ? null : getString(R.string.error_invalid_phone_number));
        if(!validated) {
            highlightInvalidField(vesselPhoneNumberRow);

            return false;
        }

        validated = (ircsValidated = FiskInfoUtility.validateIRCS(vesselIrcsNumber)) || vesselIrcsNumber.isEmpty();
        vesselIrcsNumberRow.setError(validated ? null : getString(R.string.error_invalid_ircs));
        if(!validated) {
            highlightInvalidField(vesselIrcsNumberRow);

            return false;
        }

        validated = (mmsiValidated = FiskInfoUtility.validateMMSI(vesselMmsiNumber)) || vesselMmsiNumber.isEmpty();
        vesselMmsiNumberRow.setError(validated ? null : getString(R.string.error_invalid_mmsi));
        if(!validated) {
            highlightInvalidField(vesselMmsiNumberRow);

            return false;
        }

        validated = (imoValidated = FiskInfoUtility.validateIMO(vesselImoNumber)) || vesselImoNumber.isEmpty();
        vesselImoNumberRow.setError(validated ? null : getString(R.string.error_invalid_imo));
        if(!validated) {
            highlightInvalidField(vesselImoNumberRow);

            return false;
        }

        validated = (regNumValidated = FiskInfoUtility.validateRegistrationNumber(registrationNumber)) || registrationNumber.isEmpty();
        vesselRegistrationNumberRow.setError(validated ? null : getString(R.string.error_invalid_registration_number));
        if(!validated) {
            highlightInvalidField(vesselRegistrationNumberRow);

            return false;
        }

        minimumIdentificationFactorsMet = !vesselName.isEmpty() && (ircsValidated || mmsiValidated || imoValidated || regNumValidated);
        errorRow.setVisibility(!minimumIdentificationFactorsMet);

        if(!minimumIdentificationFactorsMet) {
            highlightInvalidField(errorRow);

            return false;
        }

        return true;
    }

    private void setEnabled(boolean enabled) {
        setupDateRow.setEnabled(enabled);
        setupTimeRow.setEnabled(enabled);
        coordinatesRow.setEnabled(enabled);
        toolRow.setEnabled(enabled);
        toolRemovedRow.setEnabled(enabled);
        commentRow.setEnabled(enabled);
        contactPersonNameRow.setEnabled(enabled);
        contactPersonPhoneRow.setEnabled(enabled);
        contactPersonEmailRow.setEnabled(enabled);
        vesselNameRow.setEnabled(enabled);
        vesselPhoneNumberRow.setEnabled(enabled);
        vesselIrcsNumberRow.setEnabled(enabled);
        vesselMmsiNumberRow.setEnabled(enabled);
        vesselImoNumberRow.setEnabled(enabled);
        vesselRegistrationNumberRow.setEnabled(enabled);
        archiveRow.setEnabled(enabled);
        errorRow.setEnabled(enabled);
        toolLostRow.setEnabled(enabled);
        toolLostCauseRow.setEnabled(enabled);
        toolLostConditionsRow.setEnabled(enabled);
        toolMapPreviewWebView.setEnabled(enabled);
        numberOfToolsLostRow.setEnabled(enabled);
        lostToolLengthRow.setEnabled(enabled);
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
            case R.id.action_update_tool:
                createOrUpdateToolEntry();
                break;
            case android.R.id.home:
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
     * >Communicating with Other fiskinfoo.no.sintef.fiskinfoo.Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void updateTool(ToolEntry tool);

        void deleteToolLogEntry(ToolEntry tool);
    }
}

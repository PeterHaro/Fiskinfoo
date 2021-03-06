package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntryStatus;
import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskinfoConnectivityManager;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolConfirmationRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolLogRow;
import retrofit.client.Response;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static fiskinfoo.no.sintef.fiskinfoo.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterface} interface
 * to handle interaction events.
 * Use the {@link ActiveToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveToolsFragment extends Fragment {
    public static final String FRAGMENT_TAG = "MyActiveToolsFragment";
    private final static String SCREEN_NAME = "ActiveToolsFragment";
    private final static String LOG_TAG = "ActiveToolsFragmentLogTag";

    private final DialogInterface dialogInterface = new UtilityDialogs();
    private final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton newToolButton = null;
    private LinearLayout toolContainer;
    private User user;
    private GpsLocationTracker mGpsLocationTracker;
    private BarentswatchApi barentswatchApi;
    private ActiveToolsFragment.CheckToolsStatusAsyncTask checkToolsStatusTask;
    private List<ToolEntry> unconfirmedRemovedTools;
    private List<ToolEntry> synchedTools;
    private JSONArray matchedTools;

    private UserInterface userInterface;
    private Tracker tracker;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ActiveToolsFragment.
     */
    public static ActiveToolsFragment newInstance() {
        ActiveToolsFragment fragment = new ActiveToolsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ActiveToolsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        mGpsLocationTracker = new GpsLocationTracker(getActivity());
        barentswatchApi = new BarentswatchApi();
        user = userInterface.getUser();

        if (!mGpsLocationTracker.canGetLocation()) {
            mGpsLocationTracker.showSettingsAlert();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tool_registration, menu);

        /*
        MenuItem menuItem = menu.findItem(R.id.send_tool_report);

        if(menuItem == null) {
            inflater.inflate(R.menu.menu_tool_registration, menu);
        } else if(!menuItem.isVisible()){
            menuItem.setVisible(true);
            getActivity().invalidateOptionsMenu();
        }*/
    }

    private boolean canSendReportOption = false;

    private void setCanSendReportOption(boolean canSendReportOption) {
        this.canSendReportOption = canSendReportOption;
        getActivity().invalidateOptionsMenu();
    }

    private boolean canShowSendReportOption() {
        return canSendReportOption;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.send_tool_report);
        if(menuItem != null) {
            menuItem.setVisible(canShowSendReportOption());
            //getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_active_tools, container, false);
        toolContainer = (LinearLayout) rootView.findViewById(R.id.active_tools_main_container);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.active_tools_swipe_refresh_layout);
        newToolButton = (FloatingActionButton) rootView.findViewById(R.id.register_tool_layout_add_tool_material_button);

        if (false) { // TODO: Revert
//        if(!user.getIsFishingFacilityAuthenticated()) {
            newToolButton.setEnabled(false);
            newToolButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.unavailable_grey)));

            Dialog dialog = dialogInterface.getHyperlinkAlertDialog(getActivity(), getString(R.string.register_tool_not_authenticated_title), getString(R.string.register_tool_not_authenticated_info_text));
            dialog.show();
        } else {
            final List<ArrayList<ToolEntry>> tools = new ArrayList(user.getToolLog().myLog.values());

            for(final List<ToolEntry> dateEntry : tools) {
                for(final ToolEntry toolEntry : dateEntry) {
                    if(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED) {
                        continue;
                    }

                    View.OnClickListener onClickListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            EditToolFragment fragment = EditToolFragment.newInstance(toolEntry);

                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_activity_fragment_container, fragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack(getString(R.string.edit_tool_fragment_edit_title))
                                    .commit();
                        }
                    };

                    ToolLogRow row = new ToolLogRow(getActivity(), toolEntry, onClickListener, user.getSettings().getCoordinateFormat());
                    row.getView().setTag(toolEntry.getToolId());
                    toolContainer.addView(row.getView());
                }
            }

            checkToolsStatusTask = new ActiveToolsFragment.CheckToolsStatusAsyncTask();
            checkToolsStatusTask.execute(tools);


            if(user.getShowToolExplanation()) {
                final Dialog dialog = dialogInterface.getCheckboxInformationDialog(getActivity(), getString(R.string.tool_fragement_explanation_title), getString(R.string.tool_fragment_explanation));

                Button okButton = (Button) dialog.findViewById(R.id.dialog_bottom_ok_button);
                final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.checkbox_dialog_checkbox);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkBox.isChecked()) {
                            user.setShowToolExplanation(false);
                            user.writeToSharedPref(getActivity());
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // TODO: Will not work if token has expired, should look into fixing this in general
                    if(fiskInfoUtility.isNetworkAvailable(getActivity()) ) {
                        List<ArrayList<ToolEntry>> localTools = new ArrayList(user.getToolLog().myLog.values());

                        ((MainActivity) getActivity()).toggleNetworkErrorTextView(true);

                        if(checkToolsStatusTask != null && checkToolsStatusTask.getStatus() != AsyncTask.Status.RUNNING) {
                            checkToolsStatusTask = new ActiveToolsFragment.CheckToolsStatusAsyncTask();
                            checkToolsStatusTask.execute(localTools);
                        }
                    }else {
                        ((MainActivity) getActivity()).toggleNetworkErrorTextView(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

            newToolButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    EditToolFragment fragment = EditToolFragment.newInstance(null);

                    fragmentManager.beginTransaction()
                            .replace(R.id.main_activity_fragment_container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(getString(R.string.edit_tool_fragment_new_tool_title))
                            .commit();
                }
            });
        }

        return rootView;
    }

    private boolean updateToolList(List<ArrayList<ToolEntry>> tools) {
        if (FiskinfoConnectivityManager.hasValidNetworkConnection(getActivity())) { //(fiskInfoUtility.isNetworkAvailable(getActivity())) {
            List<ToolEntry> localTools = new ArrayList<>();
            unconfirmedRemovedTools = new ArrayList<>();
            synchedTools = new ArrayList<>();

            for(final ArrayList<ToolEntry> dateEntry : tools) {
                for (final ToolEntry toolEntry : dateEntry) {
                    if(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED ||
                            toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_CONFIRMED) {
                        continue;
                    } else if(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_RECEIVED) {
                        synchedTools.add(toolEntry);
                    } else if(!(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED) ||
                            toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_UNCONFIRMED) {
                        localTools.add(toolEntry);
                    } else {
                        unconfirmedRemovedTools.add(toolEntry);
                    }
                }
            }

            Response response;
            try {
                barentswatchApi.setAccesToken(user.getToken());

                response = barentswatchApi.getApi().geoDataDownload("fishingfacility", "JSON");
            }
            catch (Exception ex) {
                // Added catch of network error. Should we notify the user somehow?
                response = null;
            }

            if (response == null) {
                Log.d(FRAGMENT_TAG, "RESPONSE == NULL");
                return false;
            }

            byte[] toolData;
            try {
                toolData = FiskInfoUtility.toByteArray(response.getBody().in());
                JSONObject featureCollection = new JSONObject(new String(toolData));
                JSONArray jsonTools = featureCollection.getJSONArray("features");
                matchedTools = new JSONArray();
                UserSettings settings = user.getSettings();

                for(int i = 0; i < jsonTools.length(); i++) {
                    JSONObject tool = jsonTools.getJSONObject(i);
                    boolean hasCopy = false;

                    for(int j = 0; j < localTools.size(); j++) {
                        if(localTools.get(j).getToolId().equals(tool.getJSONObject("properties").getString("toolid"))) {
                            localTools.get(j).setHasBeenRegistered(true);
                            SimpleDateFormat sdfMilliSeconds = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss_sss), Locale.getDefault());
                            SimpleDateFormat sdfMilliSecondsRemote = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss_sss), Locale.getDefault());
                            SimpleDateFormat sdfRemote = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd_t_hh_mm_ss), Locale.getDefault());

                            sdfMilliSeconds.setTimeZone(TimeZone.getTimeZone("UTC"));
                            /* Timestamps from BW seem to be one hour earlier than UTC/GMT?  */
                            sdfMilliSecondsRemote.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                            sdfRemote.setTimeZone(TimeZone.getTimeZone("GMT-1"));
                            Date localLastUpdatedDateTime;
                            Date localUpdatedBySourceDateTime;
                            Date serverUpdatedDateTime;
                            Date serverUpdatedBySourceDateTime = null;
                            try {
                                localLastUpdatedDateTime = sdfMilliSeconds.parse(localTools.get(j).getLastChangedDateTime());
                                localUpdatedBySourceDateTime = sdfMilliSeconds.parse(localTools.get(j).getLastChangedBySource());

                                serverUpdatedDateTime = tool.getJSONObject("properties").getString("lastchangeddatetime").length() == getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                                        sdfRemote.parse(tool.getJSONObject("properties").getString("lastchangeddatetime")) : sdfMilliSecondsRemote.parse(tool.getJSONObject("properties").getString("lastchangeddatetime"));

                                if(tool.getJSONObject("properties").has("lastchangedbysource")) {
                                    serverUpdatedBySourceDateTime = tool.getJSONObject("properties").getString("lastchangedbysource").length() == getResources().getInteger(R.integer.datetime_without_milliseconds_length) ?
                                            sdfRemote.parse(tool.getJSONObject("properties").getString("lastchangedbysource")) : sdfMilliSecondsRemote.parse(tool.getJSONObject("properties").getString("lastchangedbysource"));
                                }

                                if((localLastUpdatedDateTime.equals(serverUpdatedDateTime) || localLastUpdatedDateTime.before(serverUpdatedDateTime)) &&
                                        serverUpdatedBySourceDateTime != null &&
                                        (localUpdatedBySourceDateTime.equals(serverUpdatedBySourceDateTime) || localUpdatedBySourceDateTime.before(serverUpdatedBySourceDateTime))) {
                                    localTools.get(j).updateFromGeoJson(tool, getActivity());

                                    localTools.get(j).setToolStatus(ToolEntryStatus.STATUS_RECEIVED);
                                } else if(serverUpdatedBySourceDateTime != null && localUpdatedBySourceDateTime.after(serverUpdatedBySourceDateTime)) {
                                    // TODO: Do nothing, local changes should be reported.


                                } else {
                                    // TODO: what gives?
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            localTools.remove(j);
                            j--;

                            hasCopy = true;
                            break;
                        }
                    }

                    for(int j = 0; j < unconfirmedRemovedTools.size(); j++) {
                        if(unconfirmedRemovedTools.get(j).getToolId().equals(tool.getJSONObject("properties").getString("toolid"))) {
                            hasCopy = true;
                            unconfirmedRemovedTools.remove(j);
                            j--;
                        }
                    }

                    for(int j = 0; j < synchedTools.size(); j++) {
                        if(synchedTools.get(j).getToolId().equals(tool.getJSONObject("properties").getString("toolid"))) {
                            hasCopy = true;
                            synchedTools.remove(j);
                            j--;
                        }
                    }

                    if(!hasCopy && settings != null) {
                        if((!settings.getVesselName().isEmpty() && (FiskInfoUtility.ReplaceRegionalCharacters(settings.getVesselName()).equalsIgnoreCase(tool.getJSONObject("properties").getString("vesselname")) ||
                                settings.getVesselName().equalsIgnoreCase(tool.getJSONObject("properties").getString("vesselname")))) &&
                                ((!settings.getIrcs().isEmpty() && settings.getIrcs().toUpperCase().equals(tool.getJSONObject("properties").getString("ircs"))) ||
                                        (!settings.getMmsi().isEmpty() && settings.getMmsi().equals(tool.getJSONObject("properties").getString("mmsi"))) ||
                                        (!settings.getImo().isEmpty() && settings.getImo().equals(tool.getJSONObject("properties").getString("imo")))))
                        {
                            matchedTools.put(tool);
                        }
                    }
                }

                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserInterface) {
            userInterface = (UserInterface) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        userInterface = null;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(checkToolsStatusTask != null && checkToolsStatusTask.getStatus() == AsyncTask.Status.RUNNING) {
            checkToolsStatusTask.cancel(true);
        }
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
        String title = getResources().getString(R.string.my_tools_fragment_title);
        activity.refreshTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_tool_report:
                validateUserSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                for(int i = 0; i < permissions.length; i++) {
                    if(permissions[i].equals(WRITE_EXTERNAL_STORAGE) && results[i] == PackageManager.PERMISSION_GRANTED) {
                        generateAndSendGeoJsonToolReport();
                        break; // Should not need to continue once we have found a match
                    }
                }
                break;
            default:
                break;
        }
    }

    private void validateUserSettings() {
        if(user.getIsFishingFacilityAuthenticated()) {
            if (FiskInfoUtility.validateUserSettings(user.getSettings())) {
                if (tracker != null) {
                    tracker.send(new HitBuilders.EventBuilder().setCategory("Send tool report").build());
                    if (tracker != null) {
                        tracker.setScreenName(SCREEN_NAME + ActiveToolsFragment.class.getSimpleName());
                        tracker.send(new HitBuilders.ScreenViewBuilder().build());
                    } else {
                        Log.wtf(LOG_TAG, "TRACKER IS NULL IN ON RESUME");
                    }
                }

                generateAndSendGeoJsonToolReport();
            } else {
                Dialog dialog = dialogInterface.getHyperlinkAlertDialog(getActivity(), getString(R.string.send_tool_report_not_authorized_error_title), getString(R.string.summary_user_profile_incomplete));
                //TODO: Add link from dialog to jump to editing profile
                dialog.show();
            }
        } else {
            Dialog dialog = dialogInterface.getHyperlinkAlertDialog(getActivity(), getString(R.string.send_tool_report_not_authorized_error_title), getString(R.string.send_tool_report_not_authorized_error_message));
            dialog.show();
        }
    }

    private void generateAndSendGeoJsonToolReport() {
        FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();
        JSONObject featureCollection = new JSONObject();

        if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        try {
            Set<Map.Entry<String, ArrayList<ToolEntry>>> tools = user.getToolLog().myLog.entrySet();
            JSONArray featureList = new JSONArray();

            for(final Map.Entry<String, ArrayList<ToolEntry>> dateEntry : tools) {
                for(final ToolEntry toolEntry : dateEntry.getValue()) {
                    if (toolEntry.getToolStatus() == ToolEntryStatus.STATUS_RECEIVED ||
                            toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED ||
                            toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_CONFIRMED) {
                        continue;
                    }

                    toolEntry.setToolStatus(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED ? ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED :
                            ((toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_UNREPORTED || toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_UNSENT || toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_UNCONFIRMED) ?
                                    ToolEntryStatus.STATUS_TOOL_LOST_UNCONFIRMED : ToolEntryStatus.STATUS_SENT_UNCONFIRMED));
                    JSONObject gjsonTool = toolEntry.toGeoJson(mGpsLocationTracker);

                    // Copy user profile info into the tool
                    UserSettings settings = user.getSettings();

                    toolEntry.setContactPersonEmail(settings.getContactPersonEmail());
                    toolEntry.setContactPersonName(settings.getContactPersonName());
                    toolEntry.setContactPersonPhone(settings.getContactPersonPhone());

                    toolEntry.setVesselName(settings.getVesselName());
                    toolEntry.setVesselEmail(settings.getContactPersonEmail());
                    toolEntry.setVesselPhone(settings.getVesselPhone());

                    toolEntry.setRegNum(settings.getRegistrationNumber());

                    toolEntry.setIRCS(settings.getIrcs());
                    toolEntry.setMMSI(settings.getMmsi());
                    toolEntry.setIMO(settings.getImo());

                    featureList.put(gjsonTool);
                }
            }

            if(featureList.length() == 0) {
                Toast.makeText(getActivity(), getString(R.string.no_changes_to_report), Toast.LENGTH_LONG).show();

                return;
            }

            user.writeToSharedPref(getActivity());
            featureCollection.put("features", featureList);
            featureCollection.put("type", "FeatureCollection");
            featureCollection.put("crs", JSONObject.NULL);
            featureCollection.put("bbox", JSONObject.NULL);

            String toolString = featureCollection.toString(4);

            if (fiskInfoUtility.isExternalStorageWritable()) {
                File reportPath = new File(getActivity().getFilesDir(), "reports");
                if (!reportPath.exists()) {
                    if (!reportPath.mkdirs())
                        return;
                }
                File reportFile = new File(reportPath, getString(R.string.tool_report_file_name) + "." + getString(R.string.format_geojson));
                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(reportFile);
                    outputStream.write(toolString.getBytes());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //fiskInfoUtility.writeDataToExternalStorage(getActivity(), toolString.getBytes(), getString(R.string.tool_report_file_name), getString(R.string.format_geojson), null, false);
                String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                String fileName = directoryPath + "/FiskInfo/api_setting.json";
                File apiSettingsFile = new File(fileName);
                String recipient = null;

                if(apiSettingsFile.exists()) {
                    InputStream inputStream;
                    InputStreamReader streamReader;
                    JsonReader jsonReader;

                    try {
                        inputStream = new BufferedInputStream(new FileInputStream(apiSettingsFile));
                        streamReader = new InputStreamReader(inputStream, "UTF-8");
                        jsonReader = new JsonReader(streamReader);

                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String name = jsonReader.nextName();
                            if (name.equals("email")) {
                                recipient = jsonReader.nextString();
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        jsonReader.endObject();
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }
                }

                recipient = recipient == null ? getString(R.string.tool_report_recipient_email) : recipient;
                //recipient = recipient == null ? "erlend.stav@sintef.no" : recipient;
                String[] recipients = new String[] { recipient };
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/plain");

                String toolIds;
                StringBuilder sb = new StringBuilder();

                sb.append("Redskapskoder:\n");

                for(int i = 0; i < featureList.length(); i++) {
                    sb.append(Integer.toString(i + 1));
                    sb.append(": ");
                    sb.append(featureList.getJSONObject(i).getJSONObject("properties").getString("ToolId"));
                    sb.append("\n");
                }

                PackageManager packageManager = getActivity().getPackageManager();
                PackageInfo packageInfo;
                sb.append("\nAppversjon (Android): ");

                try {
                    packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
                    sb.append(packageInfo.versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    sb.append("Error retrieving version number");
                }

                sb.append("\nAndroidversjon: ");
                sb.append(Build.VERSION.RELEASE);

                toolIds = sb.toString();

                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_TEXT, toolIds);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.tool_report_email_header));
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                        + "/FiskInfo/Redskapsrapport.geojson");
                //Uri uri = Uri.fromFile(file);
                Uri uri = FileProvider.getUriForFile(getContext(), "fiskinfoo.no.sintef.fiskinfoo.fileprovider", reportFile);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, getString(R.string.send_tool_report_intent_header)));

                toolContainer.removeAllViews();

                for(final Map.Entry<String, ArrayList<ToolEntry>> dateEntry : tools) {
                    for(final ToolEntry toolEntry : dateEntry.getValue()) {
                        if (toolEntry.getToolStatus() == ToolEntryStatus.STATUS_RECEIVED ||
                                toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED ||
                                toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_CONFIRMED) {
                            continue;
                        }

                        View.OnClickListener onClickListener = new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                EditToolFragment fragment = EditToolFragment.newInstance(toolEntry);

                                fragmentManager.beginTransaction()
                                        .replace(R.id.main_activity_fragment_container, fragment)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .addToBackStack(getString(R.string.edit_tool_fragment_edit_title))
                                        .commit();
                            }
                        };

                        ToolLogRow row = new ToolLogRow(getActivity(), toolEntry, onClickListener, user.getSettings().getCoordinateFormat());
                        toolContainer.addView(row.getView());
                    }
                }

                if(featureList.length() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.no_changes_to_report), Toast.LENGTH_LONG).show();

                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class CheckToolsStatusAsyncTask extends AsyncTask<List<ArrayList<ToolEntry>>, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(List<ArrayList<ToolEntry>>... tools) {
            return updateToolList(tools[0]);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSwipeRefreshLayout.setRefreshing(false);

            ((MainActivity) getActivity()).toggleNetworkErrorTextView(success);

            if(!success) {
                Toast.makeText(getContext(), R.string.error_could_not_validate_tool_status, Toast.LENGTH_LONG).show();
                return;
            }

            if(matchedTools.length() > 0) {
                final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_confirm_tools_from_api, R.string.tool_confirmation);
                Button cancelButton = dialog.findViewById(R.id.dialog_bottom_cancel_button);
                Button addToolsButton = dialog.findViewById(R.id.dialog_bottom_add_button);
                final LinearLayout linearLayoutToolContainer = dialog.findViewById(R.id.dialog_confirm_tool_main_container_linear_layout);
                final List<ToolConfirmationRow> matchedToolsList = new ArrayList<>();

                for(int i = 0; i < matchedTools.length(); i++) {
                    try {
                        ToolConfirmationRow confirmationRow = new ToolConfirmationRow(getActivity(), matchedTools.getJSONObject(i));
                        linearLayoutToolContainer.addView(confirmationRow.getView());
                        matchedToolsList.add(confirmationRow);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                addToolsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(ToolConfirmationRow row : matchedToolsList) {
                            if(row.isChecked()) {
                                final ToolEntry newTool = row.getToolEntry();
                                user.getToolLog().addTool(newTool, newTool.getSetupDateTime().substring(0, 10));


                                View.OnClickListener onClickListener = new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        EditToolFragment fragment = EditToolFragment.newInstance(newTool);

                                        fragmentManager.beginTransaction()
                                                .replace(R.id.main_activity_fragment_container, fragment)
                                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                                .addToBackStack(getString(R.string.edit_tool_fragment_edit_title))
                                                .commit();
                                    }
                                };

                                ToolLogRow newRow = new ToolLogRow(v.getContext(), newTool, onClickListener, user.getSettings().getCoordinateFormat());
                                row.getView().setTag(newTool.getToolId());
                                toolContainer.addView(newRow.getView());
                            }
                        }

//                            user.writeToSharedPref(v.getContext());
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

            if(synchedTools.size() > 0) {
//                    // TODO: Prompt user: Tool was confirmed, now is no longer at BW, remove or archive?

                final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_confirm_tools_from_api, R.string.tool_confirmation);
                TextView informationTextView = (TextView) dialog.findViewById(R.id.dialog_description_text_view);
                Button cancelButton = (Button) dialog.findViewById(R.id.dialog_bottom_cancel_button);
                Button archiveToolsButton = (Button) dialog.findViewById(R.id.dialog_bottom_add_button);
                final LinearLayout linearLayoutToolContainer = (LinearLayout) dialog.findViewById(R.id.dialog_confirm_tool_main_container_linear_layout);

                informationTextView.setText(getString(R.string.unexpected_tool_removal_info_text));
                archiveToolsButton.setText(getString(R.string.ok));

                for(ToolEntry toolEntry : synchedTools) {
                    ToolConfirmationRow confirmationRow = new ToolConfirmationRow(getActivity(), toolEntry);
                    linearLayoutToolContainer.addView(confirmationRow.getView());
                }

                archiveToolsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(ToolEntry toolEntry : synchedTools) {
                            toolEntry.setToolStatus(ToolEntryStatus.STATUS_REMOVED);
                            toolEntry.setHasBeenRegistered(true);

                            for(int i = 0; i < toolContainer.getChildCount(); i++) {
                                if(toolEntry.getToolId().equals(toolContainer.getChildAt(i).getTag().toString())) {
                                    toolContainer.removeViewAt(i);
                                    break;
                                }
                            }
                        }

                        if(toolContainer.getChildCount() == 0) {
                            //setHasOptionsMenu(false);
                            setCanSendReportOption(false);
                        }

                        user.writeToSharedPref(v.getContext());
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            if(unconfirmedRemovedTools.size() > 0) {
                // TODO: If not found server side, tool is assumed to be removed. Inform user.

                final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_confirm_tools_from_api, R.string.reported_tools_removed_title);
                TextView informationTextView = (TextView) dialog.findViewById(R.id.dialog_description_text_view);
                Button cancelButton = (Button) dialog.findViewById(R.id.dialog_bottom_cancel_button);
                Button archiveToolsButton = (Button) dialog.findViewById(R.id.dialog_bottom_add_button);
                final LinearLayout linearLayoutToolContainer = (LinearLayout) dialog.findViewById(R.id.dialog_confirm_tool_main_container_linear_layout);

                informationTextView.setText(getString(R.string.removed_tools_information_text));
                cancelButton.setVisibility(View.GONE);
                archiveToolsButton.setText(getString(R.string.ok));

                for(ToolEntry toolEntry : unconfirmedRemovedTools) {
                    ToolConfirmationRow confirmationRow = new ToolConfirmationRow(getActivity(), toolEntry);
                    linearLayoutToolContainer.addView(confirmationRow.getView());
                    toolEntry.setToolStatus(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED_UNCONFIRMED ? ToolEntryStatus.STATUS_REMOVED : ToolEntryStatus.STATUS_TOOL_LOST_CONFIRMED);
                }

                archiveToolsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            user.writeToSharedPref(getActivity());

            final List<ArrayList<ToolEntry>> tools = new ArrayList(user.getToolLog().myLog.values());
            boolean localChanges = false;
            toolContainer.removeAllViews();

            for(final List<ToolEntry> dateEntry : tools) {
                for(final ToolEntry toolEntry : dateEntry) {
                    if(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED) {
                        continue;
                    }

                    View.OnClickListener onClickListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            EditToolFragment fragment = EditToolFragment.newInstance(toolEntry);

                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_activity_fragment_container, fragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack(getString(R.string.edit_tool_fragment_edit_title))
                                    .commit();
                        }
                    };

                    if(!(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_RECEIVED) && !(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED)) {
                        localChanges = true;
                    }

                    ToolLogRow row = new ToolLogRow(getActivity(), toolEntry, onClickListener, user.getSettings().getCoordinateFormat());
                    row.getView().setTag(toolEntry.getToolId());
                    toolContainer.addView(row.getView());
                }
            }

            //setHasOptionsMenu(localChanges);
            setCanSendReportOption(localChanges);
        }

        @Override
        protected void onCancelled() {
        }
    }

    @SuppressLint("ValidFragment")
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        TextView timeTextView;
        TextView dateTextView;
        boolean hasMaxTime;

        @SuppressLint("ValidFragment")
        public TimePickerFragment(TextView textView, boolean maxTime) {
            timeTextView = textView;
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
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.datetime_format_hh_mm_ss), Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            String toolTime = sdf.format(calendar.getTime());
            Date currentDate = new Date();
            String currentTime = sdf.format(currentDate);

            if(dateTextView != null) {
                String setupDateString = dateTextView.getText().toString();
                String setupTimeString = toolTime.substring(0, 5);
                String setupDateTime = setupDateString + "T" + setupTimeString + ":00.000";
                Date setupDate;
                sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());

                try {
                    setupDate = sdf.parse(setupDateTime);

                    if(setupDate.before(currentDate)) {
                        timeTextView.setText(toolTime.substring(0, 5));
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_cannot_set_time_to_future), Toast.LENGTH_LONG).show();
                    }

                    return;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (hasMaxTime && FiskInfoUtility.compareDates(toolTime, currentTime, getString(R.string.datetime_format_hh_mm_ss)) > 0 ) {
                Toast.makeText(getActivity(), getString(R.string.error_cannot_set_time_to_future), Toast.LENGTH_LONG).show();
            } else {
                timeTextView.setText(toolTime.substring(0, 5));
            }
        }

        public void setDateTextView(TextView dateTextView) {
            this.dateTextView = dateTextView;
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
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker datePicker = dialog.getDatePicker();
            datePicker.setMaxDate(new Date().getTime());  //Deprecated 5.0
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.datetime_format_yyyy_mm_dd), Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            String date = sdf.format(c.getTime());
            String today = sdf.format(new Date());

            if (FiskInfoUtility.compareDates(date, today, getString(R.string.datetime_format_yyyy_mm_dd)) > 0 ) {
                Toast.makeText(getActivity(), getString(R.string.error_cannot_set_time_to_future), Toast.LENGTH_LONG).show();
            } else {
                mTextView.setText(date);
            }
        }
    }
}

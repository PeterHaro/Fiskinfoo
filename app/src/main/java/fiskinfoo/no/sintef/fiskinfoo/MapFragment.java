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
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Feature;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.FiskInfoPolygon2D;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.LayerAndVisibility;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Line;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.LineFeature;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.PointFeature;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Polygon;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskinfoScheduledTaskExecutor;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolSearchResultRow;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

public class MapFragment extends Fragment {
    FragmentActivity listener;
    public static final String TAG = "Map";
    private WebView browser;
    private BarentswatchApi barentswatchApi;
    private User user;
    private UtilityRowsInterface rowsInterface;
    private FiskInfoUtility fiskInfoUtility;
    private UtilityDialogs dialogInterface;
    private UtilityOnClickListeners onClickListenerInterface;
    private ScheduledFuture proximityAlertWatcher;
    private GpsLocationTracker mGpsLocationTracker;

    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private FiskInfoPolygon2D tools = null;
    private boolean cacheDeserialized = false;
    private boolean alarmFiring = false;
    protected double cachedLat;
    protected double cachedLon;
    protected double cachedDistance;
    private JSONArray layersAndVisibility = null;
    private Button searchToolsButton;
    private Button clearHighlightingButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (FragmentActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = getArguments().getParcelable("user");
        if (user == null) {
            Log.d(TAG, "did not receive user");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        for (int i = 0; i < menu.size(); i++) {
            menu.removeItem(i);
        }
        inflater.inflate(R.menu.menu_map, menu);
    }

    // This event fires 3rd, and is the first time views are available in the fragment
    // The onCreateView method is called when Fragment should create its View object hierarchy.
    // Use onCreateView to get a handle to views as soon as they are freshly inflated
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        return inf.inflate(R.layout.fragment_map, parent, false);
    }

    // This fires 4th, and this is the first time the Activity is fully created.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fiskInfoUtility = new FiskInfoUtility();
        barentswatchApi = new BarentswatchApi();
        dialogInterface = new UtilityDialogs();
        onClickListenerInterface = new UtilityOnClickListeners();
        rowsInterface = new UtilityRows();
        searchToolsButton = (Button) (getView() != null ? getView().findViewById(R.id.map_search_button) : null);
        searchToolsButton = (Button) getView().findViewById(R.id.map_search_button);
        clearHighlightingButton = (Button) getView().findViewById(R.id.map_clear_highlighting_button);
        configureWebParametersAndLoadDefaultMapApplication();

        if(user.getIsFishingFacilityAuthenticated()) {
            searchToolsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createSearchDialog();
                }
            });
        } else {
            searchToolsButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        browser.reload();
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void configureWebParametersAndLoadDefaultMapApplication() {
        if(getView() == null) {
            throw new NullPointerException();
        }
        browser = (WebView) getView().findViewById(R.id.map_fragment_web_view);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        browser.getSettings().setGeolocationEnabled(true);
        browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        browser.addJavascriptInterface(new JavaScriptInterface(getActivity()), "Android");
        browser.setWebViewClient(new barentswatchFiskInfoWebClient());
        browser.setWebChromeClient(new WebChromeClient() {

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                Log.d("geolocation permission", "permission >>>" + origin);
                callback.invoke(origin, true, false);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, message);
                return super.onJsAlert(view, url, message, result);
            }

        });

        if((new FiskInfoUtility().isNetworkAvailable(getActivity())) && !user.getOfflineMode()) {
            browser.loadUrl("file:///android_asset/mapApplication.html");
        } else {
            browser.loadUrl("file:///android_asset/mapApplicationOfflineMode.html");
            if((!new FiskInfoUtility().isNetworkAvailable(getActivity()))) {
                dialogInterface.getAlertDialog(getActivity(), R.string.offline_mode_map_used_title, R.string.offline_mode_map_used_info, -1).show();
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
            return user.getToken();
        }

        @android.webkit.JavascriptInterface
        public void setMessage(String message) {
            Log.d(TAG, message);
            try {
                layersAndVisibility = new JSONArray(message);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO
            }
        }

        // Note: Would pass a JSONObject, but for some reason the mapApplication fails at receiving so sending as string instead.
        @SuppressWarnings("unused")
        @android.webkit.JavascriptInterface
        public String getGeoJSONFile(String fileName) {
            String directoryFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";

            File file = new File(directoryFilePath + fileName + ".JSON");
            StringBuilder jsonString = new StringBuilder();
            BufferedReader bufferReader = null;

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

            return jsonString.toString();
        }

    }

    @SuppressWarnings("unused")
    private void getLayers() {
        browser.loadUrl("javascript:alert(getLayers())");
    }

    private void getLayersAndVisibility() {
        browser.loadUrl("javascript:getLayersAndState()");
    }

    private class barentswatchFiskInfoWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            List<String> layers = user.getActiveLayers();
            if (user.isTokenValid() && user.getIsFishingFacilityAuthenticated()) {
                Log.d(TAG, "USER IS AUTHENTICATED");
                view.loadUrl("javascript:populateMap(1);");

                JSONArray json = new JSONArray(layers);
                Log.d("TAG", json.toString());
                view.loadUrl("javascript:toggleLayers(" + json + ")");
            } else {
                JSONArray json = new JSONArray(layers);
                Log.d(TAG, json.toString());
                view.loadUrl("javascript:populateMap(2)");
                view.loadUrl("javascript:toggleLayers(" + json + ")");
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    getLayersAndVisibility();
                }
            }, 3000);
        }
    }

    //
    private void createMapLayerSelectionDialog() {
        if(layersAndVisibility == null) {
            getLayersAndVisibility();
        }

        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_map_layers, R.string.choose_map_layers);
        Button okButton = (Button) dialog.findViewById(R.id.select_map_layers_update_map_button);
        final List<CheckBoxRow> rows = new ArrayList<>();
        final LinearLayout mapLayerLayout = (LinearLayout) dialog.findViewById(R.id.map_layers_checkbox_layout);
        final Button cancelButton = (Button) dialog.findViewById(R.id.select_map_layers_cancel_button);
        LayerAndVisibility[] layers = new Gson().fromJson(layersAndVisibility.toString(), LayerAndVisibility[].class);
        for (LayerAndVisibility layer : layers) {
            if (layer.name.equals("Grunnkart")) {
                continue;
            }
            boolean isActive;
            isActive = layer.isVisible;

            CheckBoxRow row = rowsInterface.getCheckBoxRow(getActivity(), layer.name, isActive);
            rows.add(row);
            View mapLayerRow = row.getView();
            mapLayerLayout.addView(mapLayerRow);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> layersList = new ArrayList<>();
                layersList.add("Grunnkart");

                for (int i = 0; i < mapLayerLayout.getChildCount(); i++) {
                    if (rows.get(i).isChecked()) {
                        layersList.add(rows.get(i).getText());
                    }
                }

                user.setActiveLayers(layersList);
                user.writeToSharedPref(getActivity());
                dialog.dismiss();

                if(layersList.contains(getString(R.string.fishing_facility_name)) && !user.getIsFishingFacilityAuthenticated()) {
                    dialogInterface.getHyperlinkAlertDialog(getActivity(), getString(R.string.about_fishing_facility_title), getString(R.string.about_fishing_facility_details)).show();
                }

                JSONArray json = new JSONArray(layersList);
                browser.loadUrl("javascript:toggleLayers(" + json + ")");

                getLayersAndVisibility();
            }
        });

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void createToolSymbolExplanationDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_tool_legend, R.string.tool_legend);

        TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.tool_legend_table_layout);
        Button dismissButton = (Button) dialog.findViewById(R.id.tool_legend_dismiss_button);

        for (ToolType toolType : ToolType.values()) {
            View toolLegendRow = rowsInterface.getToolLegendRow(getActivity(), toolType.getHexValue(), toolType.toString()).getView();
            tableLayout.addView(toolLegendRow);
        }

        dismissButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void createProximityAlertSetupDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_proximity_alert_create, R.string.create_proximity_alert);

        Button setProximityAlertWatcherButton = (Button) dialog.findViewById(R.id.create_proximity_alert_create_alert_watcher_button);
        Button stopCurrentProximityAlertWatcherButton = (Button) dialog.findViewById(R.id.create_proximity_alert_stop_existing_alert_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.create_proximity_alert_cancel_button);
        SeekBar seekbar = (SeekBar) dialog.findViewById(R.id.create_proximity_alert_seekBar);
        final EditText radiusEditText = (EditText) dialog.findViewById(R.id.create_proximity_alert_range_edit_text);
        final Switch formatSwitch = (Switch) dialog.findViewById(R.id.create_proximity_alert_format_switch);

        final double seekBarStepSize = (double) (getResources().getInteger(R.integer.proximity_alert_maximum_warning_range_meters) - getResources().getInteger(R.integer.proximity_alert_minimum_warning_range_meters)) / 100;

        radiusEditText.setText(String.valueOf(getResources().getInteger(R.integer.proximity_alert_minimum_warning_range_meters)));

        formatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    buttonView.setText(getString(R.string.range_format_nautical_miles));
                } else {
                    buttonView.setText(getString(R.string.range_format_meters));
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    String range = String.valueOf((int) (getResources().getInteger(R.integer.proximity_alert_minimum_warning_range_meters) + (seekBarStepSize * progress)));
                    radiusEditText.setText(range);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        setProximityAlertWatcherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastText;

                if (proximityAlertWatcher == null) {
                    toastText = getString(R.string.proximity_alert_set);
                } else {

                    toastText = getString(R.string.proximity_alert_replace);
                }

                if (proximityAlertWatcher != null) {
                    proximityAlertWatcher.cancel(true);
                }

                mGpsLocationTracker = new GpsLocationTracker(getActivity());
                double latitude, longitude;

                if (mGpsLocationTracker.canGetLocation()) {
                    latitude = mGpsLocationTracker.getLatitude();
                    cachedLat = latitude;
                    longitude = mGpsLocationTracker.getLongitude();
                    cachedLon = longitude;
                } else {
                    mGpsLocationTracker.showSettingsAlert();
                    return;
                }

                if(formatSwitch.isChecked()) {
                    cachedDistance = Double.valueOf(radiusEditText.getText().toString()) * getResources().getInteger(R.integer.meters_per_nautical_mile);
                } else {
                    cachedDistance = Double.valueOf(radiusEditText.getText().toString());
                }

                dialog.dismiss();

                Response response;

                try {
                    String apiName = "fishingfacility";
                    String format = "OLEX";
                    String filePath;
                    String fileName = "collisionCheckToolsFile";

                    response = barentswatchApi.getApi().geoDataDownload(apiName, format);

                    if (response == null) {
                        Log.d(TAG, "RESPONSE == NULL");
                        throw new InternalError();
                    }

                    if (fiskInfoUtility.isExternalStorageWritable()) {
                        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        String directoryName = "FiskInfo";
                        filePath = directoryPath + "/" + directoryName + "/";
                        InputStream zippedInputStream = null;

                        try {
                            TypedInput responseInput = response.getBody();
                            zippedInputStream = responseInput.in();
                            zippedInputStream = new GZIPInputStream(zippedInputStream);

                            InputSource inputSource = new InputSource(zippedInputStream);
                            InputStream input = new BufferedInputStream(inputSource.getByteStream());
                            byte data[];
                            data = FiskInfoUtility.toByteArray(input);

                            InputStream inputStream = new ByteArrayInputStream(data);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            FiskInfoPolygon2D serializablePolygon2D = new FiskInfoPolygon2D();

                            String line;
                            boolean startSet = false;
                            String[] convertedLine;
                            List<Point> shape = new ArrayList<>();
                            while ((line = reader.readLine()) != null) {
                                Point currPoint = new Point();
                                if (line.length() == 0 || line.equals("")) {
                                    continue;
                                }
                                if (Character.isLetter(line.charAt(0))) {
                                    continue;
                                }

                                convertedLine = line.split("\\s+");

                                if (line.length() > 150) {
                                    Log.d(TAG, "line " + line);
                                }

                                if (convertedLine[3].equalsIgnoreCase("Garnstart") && startSet) {
                                    if (shape.size() == 1) {
                                        // Point

                                        serializablePolygon2D.addPoint(shape.get(0));
                                        shape = new ArrayList<>();
                                    } else if (shape.size() == 2) {

                                        // line
                                        serializablePolygon2D.addLine(new Line(shape.get(0), shape.get(1)));
                                        shape = new ArrayList<>();
                                    } else {

                                        serializablePolygon2D.addPolygon(new Polygon(shape));
                                        shape = new ArrayList<>();
                                    }
                                    startSet = false;
                                }

                                if (convertedLine[3].equalsIgnoreCase("Garnstart") && !startSet) {
                                    double lat = Double.parseDouble(convertedLine[0]) / 60;
                                    double lon = Double.parseDouble(convertedLine[1]) / 60;
                                    currPoint.setNewPointValues(lat, lon);
                                    shape.add(currPoint);
                                    startSet = true;
                                } else if (convertedLine[3].equalsIgnoreCase("Brunsirkel")) {
                                    double lat = Double.parseDouble(convertedLine[0]) / 60;
                                    double lon = Double.parseDouble(convertedLine[1]) / 60;
                                    currPoint.setNewPointValues(lat, lon);
                                    shape.add(currPoint);
                                }
                            }

                            reader.close();
                            new FiskInfoUtility().serializeFiskInfoPolygon2D(filePath + fileName + "." + format, serializablePolygon2D);

                            tools = serializablePolygon2D;


                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.e(TAG, "Error when trying to serialize file.");
                            Toast error = Toast.makeText(getActivity(), "Ingen redskaper i området du definerte", Toast.LENGTH_LONG);
                            error.show();
                            return;
                        } finally {
                            try {
                                if (zippedInputStream != null) {
                                    zippedInputStream.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(v.getContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }

                } catch (Exception e) {
                    Log.d(TAG, "Could not download tools file");
                    Toast.makeText(getActivity(), R.string.download_failed, Toast.LENGTH_LONG).show();
                }

                runScheduledAlarm(getResources().getInteger(R.integer.zero), getResources().getInteger(R.integer.proximity_alert_interval_time_seconds));

                Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
            }
        });

        if (proximityAlertWatcher != null) {
            TypedValue outValue = new TypedValue();
            stopCurrentProximityAlertWatcherButton.setVisibility(View.VISIBLE);


            getResources().getValue(R.dimen.proximity_alert_dialog_button_text_size_small, outValue, true);
            float textSize = outValue.getFloat();

            setProximityAlertWatcherButton.setTextSize(textSize);
            stopCurrentProximityAlertWatcherButton.setTextSize(textSize);
            cancelButton.setTextSize(textSize);

            stopCurrentProximityAlertWatcherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proximityAlertWatcher.cancel(true);
                    proximityAlertWatcher = null;
                    dialog.dismiss();
                }
            });
        }

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void runScheduledAlarm(int initialDelay, int period) {
        proximityAlertWatcher = new FiskinfoScheduledTaskExecutor(2).scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // Need to get alarm status and handle kill
                if (!cacheDeserialized) {
                    String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    String directoryName = "FiskInfo";
                    String filename = "collisionCheckToolsFile";
                    String format = "OLEX";
                    String filePath = directoryPath + "/" + directoryName + "/" + filename + "." + format;
                    tools = fiskInfoUtility.deserializeFiskInfoPolygon2D(filePath);
                    cacheDeserialized = true;
                    // DEMO: add point here for testing/demo purposes
//                         Point point = new Point(69.650543, 18.956831);
//                         tools.addPoint(point);
                } else {
                    if(alarmFiring) {
                        return;
                    }

                    double latitude, longitude;

                    if (mGpsLocationTracker.canGetLocation()) {
                        latitude = mGpsLocationTracker.getLatitude();
                        cachedLat = latitude;
                        longitude = mGpsLocationTracker.getLongitude();
                        cachedLon = longitude;
                        Log.i("GPS-LocationTracker", String.format("latitude: %s, ", latitude));
                        Log.i("GPS-LocationTracker", String.format("longitude: %s", longitude));
                    } else {
                        mGpsLocationTracker.showSettingsAlert();
                        return;
                    }
                    Point userPosition = new Point(cachedLat, cachedLon);

                    if (tools.checkCollisionWithPoint(userPosition, cachedDistance)) {
                        alarmFiring = true;
                        Looper.prepare();
                        notifyUserOfProximityAlert();
                    }
                }

                System.out.println("Collision check run");
            }

        }, initialDelay, period, TimeUnit.SECONDS);
    }

    private void notifyUserOfProximityAlert() {
        Handler mainHandler = new Handler(getActivity().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_proximity_alert_warning, R.string.proximity_alert_warning);

                Button okButton = (Button) dialog.findViewById(R.id.proximity_alert_warning_ok_button);
                Button showOnMapButton = (Button) dialog.findViewById(R.id.proximity_alert_warning_show_on_map_button);
                Button dismissAlertButton = (Button) dialog.findViewById(R.id.proximity_alert_warning_dismiss_button);

                long[] pattern = {0, 500, 200, 200, 300, 200, 200};

                vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(pattern, 0);

                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.proximity_warning_sound);

                if (mediaPlayer == null) {
                    return;
                }

                mediaPlayer.setLooping(true);
                mediaPlayer.start();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmFiring = false;
                        vibrator.cancel();
                        vibrator = null;
                        proximityAlertWatcher.cancel(true);
                        proximityAlertWatcher = null;
                        mediaPlayer.stop();
                        mediaPlayer.release();

                        dialog.dismiss();
                        runScheduledAlarm(getResources().getInteger(R.integer.sixty), getResources().getInteger(R.integer.proximity_alert_interval_time_seconds));
                    }
                });

                showOnMapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmFiring = false;
                        vibrator.cancel();
                        vibrator = null;
                        proximityAlertWatcher.cancel(true);
                        proximityAlertWatcher = null;
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        browser.loadUrl("javascript:zoomToUserPosition()");

                        dialog.dismiss();
                        runScheduledAlarm(getResources().getInteger(R.integer.sixty), getResources().getInteger(R.integer.proximity_alert_interval_time_seconds));

                    }
                });

                dismissAlertButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmFiring = false;
                        vibrator.cancel();
                        vibrator = null;
                        proximityAlertWatcher.cancel(true);
                        proximityAlertWatcher = null;
                        mediaPlayer.stop();
                        mediaPlayer.release();

                        dialog.dismiss();
                    }
                });

                dialog.show();
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }

    public void updateMap() {
        if((new FiskInfoUtility().isNetworkAvailable(getActivity())) && !user.getOfflineMode()) {
            browser.loadUrl("file:///android_asset/mapApplication.html");
            ((MainActivity)getActivity()).toggleNetworkErrorTextView(true);
        } else {
            browser.loadUrl("file:///android_asset/mapApplicationOfflineMode.html");
            dialogInterface.getAlertDialog(getActivity(), R.string.offline_mode_map_used_title, R.string.offline_mode_map_used_info, -1).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_map:
                updateMap();
                return true;
            case R.id.zoom_to_user_position:
                browser.loadUrl("javascript:zoomToUserPosition()");
                return true;
            case R.id.symbol_explanation:
                createToolSymbolExplanationDialog();
                return true;
            case R.id.setProximityAlert:
                createProximityAlertSetupDialog();
                return true;
            case R.id.choose_map_layers:
                createMapLayerSelectionDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //getAllVesselNames
    private void createSearchDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_search_tools, R.string.search_tools_title);

        final ScrollView scrollView = (ScrollView) dialog.findViewById(R.id.search_tools_dialog_scroll_view);
        final AutoCompleteTextView inputField = (AutoCompleteTextView) dialog.findViewById(R.id.search_tools_input_field);
        final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.search_tools_row_container);
        final Button viewInMapButton = (Button) dialog.findViewById(R.id.search_tools_view_in_map_button);
        final Button jumpToBottomButton = (Button) dialog.findViewById(R.id.search_tools_jump_to_bottom_button);
        Button dismissButton = (Button) dialog.findViewById(R.id.search_tools_dismiss_button);

        List<PropertyDescription> subscribables;
        PropertyDescription newestSubscribable = null;
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date cachedUpdateDateTime;
        Date newestUpdateDateTime;
        SubscriptionEntry cachedEntry;
        Response response;
        final JSONArray toolsArray;
        ArrayAdapter<String> adapter;
        String format = "JSON";
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";
        final JSONObject tools;
        final List<String> vesselNames;
        final Map<String, List<Integer>> toolIdMap =  new HashMap<>();
        byte[] data = new byte[0];

        cachedEntry = user.getSubscriptionCacheEntry(getString(R.string.fishing_facility_api_name));

        if(fiskInfoUtility.isNetworkAvailable(getActivity())) {
            subscribables = barentswatchApi.getApi().getSubscribable();
            for(PropertyDescription subscribable : subscribables) {
                if(subscribable.ApiName.equals(getString(R.string.fishing_facility_api_name))) {
                    newestSubscribable = subscribable;
                    break;
                }
            }
        } else if(cachedEntry == null){
            Dialog infoDialog = dialogInterface.getAlertDialog(getActivity(), R.string.tools_search_no_data_title, R.string.tools_search_no_data, -1);

            infoDialog.show();
            return;
        }

        if(cachedEntry != null) {
            try {
                cachedUpdateDateTime = simpleDateFormat.parse(cachedEntry.mLastUpdated.equals(getActivity().getString(R.string.abbreviation_na)) ? "2000-00-00T00:00:00" : cachedEntry.mLastUpdated);
                newestUpdateDateTime = simpleDateFormat.parse(newestSubscribable != null ? newestSubscribable.LastUpdated : "2000-00-00T00:00:00");

                if(cachedUpdateDateTime.getTime() - newestUpdateDateTime.getTime() < 0) {
                    response = barentswatchApi.getApi().geoDataDownload(newestSubscribable.ApiName, format);
                    try {
                        data = FiskInfoUtility.toByteArray(response.getBody().in());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(new FiskInfoUtility().writeMapLayerToExternalStorage(getActivity(), data, newestSubscribable.Name.replace(",", "").replace(" ", "_"), format, downloadPath, false)) {
                        SubscriptionEntry entry = new SubscriptionEntry(newestSubscribable, true);
                        entry.mLastUpdated = newestSubscribable.LastUpdated;
                        user.setSubscriptionCacheEntry(newestSubscribable.ApiName, entry);
                        user.writeToSharedPref(getActivity());
                    }
                } else {
                    String directoryFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";

                    File file = new File(directoryFilePath + newestSubscribable.Name + ".JSON");
                    StringBuilder jsonString = new StringBuilder();
                    BufferedReader bufferReader = null;

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
                        if(bufferReader != null) {
                            try {
                                bufferReader.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    data = jsonString.toString().getBytes();
                }

            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "Invalid datetime provided");
            }
        } else {
            response = barentswatchApi.getApi().geoDataDownload(newestSubscribable.ApiName, format);
            try {
                data = FiskInfoUtility.toByteArray(response.getBody().in());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(new FiskInfoUtility().writeMapLayerToExternalStorage(getActivity(), data, newestSubscribable.Name.replace(",", "").replace(" ", "_"), format, downloadPath, false)) {
                SubscriptionEntry entry = new SubscriptionEntry(newestSubscribable, true);
                entry.mLastUpdated = newestSubscribable.LastUpdated;
                user.setSubscriptionCacheEntry(newestSubscribable.ApiName, entry);
            }
        }

        try {
            tools = new JSONObject(new String(data));
            toolsArray = tools.getJSONArray("features");
            vesselNames = new ArrayList<>();
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, vesselNames);

            for (int i = 0; i < toolsArray.length(); i++) {
                JSONObject feature = toolsArray.getJSONObject(i);
                String vesselName = (feature.getJSONObject("properties").getString("vesselname") != null && !feature.getJSONObject("properties").getString("vesselname").equals("null")) ? feature.getJSONObject("properties").getString("vesselname") : getString(R.string.vessel_name_unknown);
                List<Integer> toolsIdList = toolIdMap.get(vesselName) != null ? toolIdMap.get(vesselName) : new ArrayList<Integer>();

                if (vesselName != null && !vesselNames.contains(vesselName)) {
                    vesselNames.add(vesselName);
                }

                toolsIdList.add(i);
                toolIdMap.put(vesselName, toolsIdList);
            }

            inputField.setAdapter(adapter);
        } catch (JSONException e) {
            dialogInterface.getAlertDialog(getActivity(), R.string.search_tools_init_error, R.string.search_tools_init_info, -1).show();
            Log.e(TAG, "JSON parse error");
            e.printStackTrace();

            return;
        }

        if(searchToolsButton.getTag() != null) {
            inputField.requestFocus();
            inputField.setText(searchToolsButton.getTag().toString());
            inputField.selectAll();
        }

        inputField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedVesselName = ((TextView) view).getText().toString();
                List<Integer> selectedTools = toolIdMap.get(selectedVesselName);
                Gson gson = new Gson();
                String toolSetDateString;
                Date toolSetDate;

                rowsContainer.removeAllViews();

                for(int toolId : selectedTools) {
                    JSONObject feature;
                    Feature toolFeature;

                    try {
                        feature = toolsArray.getJSONObject(toolId);

                        if(feature.getJSONObject("geometry").getString("type").equals("LineString")) {
                            toolFeature = gson.fromJson(feature.toString(), LineFeature.class);
                        } else {
                            toolFeature = gson.fromJson(feature.toString(), PointFeature.class);
                        }

                        toolSetDateString = toolFeature.properties.setupdatetime != null ? toolFeature.properties.setupdatetime : "2038-00-00T00:00:00";
                        toolSetDate = simpleDateFormat.parse(toolSetDateString);

                    } catch (JSONException | ParseException e) {
                        dialogInterface.getAlertDialog(getActivity(), R.string.search_tools_init_error, R.string.search_tools_init_info, -1).show();
                        e.printStackTrace();

                        return;
                    }

                    ToolSearchResultRow row = rowsInterface.getToolSearchResultRow(getActivity(), R.drawable.ikon_kystfiske, toolFeature);
                    long toolTime = System.currentTimeMillis() - toolSetDate.getTime();
                    long highlightCutoff = ((long)getResources().getInteger(R.integer.milliseconds_in_a_day)) * ((long)getResources().getInteger(R.integer.days_to_highlight_active_tool));

                    if(toolTime > highlightCutoff) {
                        int colorId = ContextCompat.getColor(getActivity(), R.color.error_red);
                        row.setDateTextViewTextColor(colorId);
                    }

                    rowsContainer.addView(row.getView());
                }

                viewInMapButton.setEnabled(true);
                inputField.setTag(selectedVesselName);
                searchToolsButton.setTag(selectedVesselName);
                jumpToBottomButton.setVisibility(View.VISIBLE);
                jumpToBottomButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0, rowsContainer.getBottom());
                            }
                        });
                    }
                });

            }
        });

        viewInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vesselName = inputField.getTag().toString();
                highlightToolsInMap(vesselName);

                dialog.dismiss();
            }
        });

        dismissButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void highlightToolsInMap(String vesselName) {
        browser.loadUrl("javascript:highlightTools(\"" + vesselName + "\")");
        clearHighlightingButton.setVisibility(View.VISIBLE);

        clearHighlightingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nullString = null;
                browser.loadUrl("javascript:highlightTools(" + nullString + ")");
                clearHighlightingButton.setVisibility(View.GONE);
                searchToolsButton.setTag(null);
            }
        });
    }
}

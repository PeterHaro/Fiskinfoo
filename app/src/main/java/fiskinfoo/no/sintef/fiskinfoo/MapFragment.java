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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.FiskInfoPolygon2D;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.LayerAndVisibility;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolsGeoJson;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskinfoScheduledTaskExecutor;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.MapLayerCheckBoxRow;

public class MapFragment extends Fragment{
    FragmentActivity listener;
    public static final String TAG = "Map";
    private WebView browser;
    private BarentswatchApi barentswatchApi;
    private User user;
    private UtilityDialogs dialogInterface;
    private UtilityRowsInterface utilityRowsInterface;
    private UtilityOnClickListeners onClickListenerInterface;
    private ScheduledFuture proximityAlertWatcher;
    private GpsLocationTracker mGpsLocationTracker;

    private Vibrator vibrator;
    private ToolsGeoJson mTools = null;
    private FiskInfoPolygon2D tools = null;
    private boolean cacheDeserialized = false;
    private boolean alarmFiring = false;
    protected AsyncTask<String, String, byte[]> cacheWriter;
    protected double cachedLat;
    protected double cachedLon;
    private String geoJsonFile = null;
    protected String cachedDistance;
    private JSONArray layersAndVisiblity = null;
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
        for(int i = 0; i < menu.size(); i++) {
            menu.removeItem(i);
        }
        inflater.inflate(R.menu.menu_map, menu);
    }

    // This event fires 3rd, and is the first time views are available in the fragment
    // The onCreateView method is called when Fragment should create its View object hierarchy.
    // Use onCreateView to get a handle to views as soon as they are freshly inflated
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inf.inflate(R.layout.fragment_map, parent, false);
        return v;
    }

    // This fires 4th, and this is the first time the Activity is fully created.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated
    // At this point, it is safe to search for activity View objects by their ID, for example.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        barentswatchApi = new BarentswatchApi();
        dialogInterface = new UtilityDialogs();
        onClickListenerInterface = new UtilityOnClickListeners();
        utilityRowsInterface = new UtilityRows();
        mTools = new ToolsGeoJson(getActivity());
        geoJsonFile = null;

        getMapTools();
        configureWebParametersAndLoadDefaultMapApplication();
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void configureWebParametersAndLoadDefaultMapApplication() {
        browser = new WebView(getActivity());
        browser = (WebView) getView().findViewById(R.id.browserWebView);
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

        browser.loadUrl("file:///android_asset/mapApplication.html");
    }

    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context context) {
            mContext = context;
        }

        @android.webkit.JavascriptInterface
        public String getToken() {
            return user.getToken();
        }

        @android.webkit.JavascriptInterface
        public void setMessage(String message) {
            Log.d(TAG, message);
            try{
                layersAndVisiblity = new JSONArray(message);
            } catch (Exception e) {
                //TODO
            }
        }

    }

    private void getLayers() {
        browser.loadUrl("javascript:alert(getLayers())");
    }

    private void getLayersAndVisiblity() {
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
            if(user.isTokenValid()) {
                Log.d(TAG, "USER IS AUTHENTICATED");
                view.loadUrl("javascript:populateMap(1);");

                JSONArray json = new JSONArray(layers);
                Log.d("TAG", json.toString());
                view.loadUrl("javascript:toggleLayers(" + json + ")");
            } else {
                JSONArray json = new JSONArray(layers);
                Log.d(TAG, json.toString());
                view.loadUrl("javascript:populateMap(2)");
                view.loadUrl("javascript:toggleLayers(" + json+ ")");
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    getLayersAndVisiblity();
                }
            }, 3000);

        }

    }

    private void createMapLayerSelectionDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_map_layers, R.string.choose_map_layers);

        Button okButton = (Button) dialog.findViewById(R.id.select_map_layers_update_map_button);
        final List<MapLayerCheckBoxRow> rows = new ArrayList<>();
        final LinearLayout mapLayerLayout = (LinearLayout) dialog.findViewById(R.id.map_layers_checkbox_layout);
        final Button cancelButton = (Button) dialog.findViewById(R.id.select_map_layers_cancel_button);
        LayerAndVisibility[] layers = new Gson().fromJson(layersAndVisiblity.toString(), LayerAndVisibility[].class);
        for(LayerAndVisibility layer : layers) {
            boolean isActive = false;
            isActive = layer.isVisible;

            MapLayerCheckBoxRow row = utilityRowsInterface.getMapLayerCheckBoxRow(getActivity(), isActive, layer.name);
            rows.add(row);
            View mapLayerRow = row.getView();
            mapLayerLayout.addView(mapLayerRow);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> layersList = new ArrayList<>();

                for (int i = 0; i < mapLayerLayout.getChildCount(); i++) {
                    if (rows.get(i).isChecked()) {
                        layersList.add(rows.get(i).getText());
                    }
                }
                user.setActiveLayers(layersList);
                user.writeToSharedPref(getActivity());
                dialog.dismiss();
                JSONArray json = new JSONArray(layersList);
                browser.loadUrl("javascript:toggleLayers(" + json + ")");

                getLayersAndVisiblity();
            }
        });

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void createPolarLowDialog() {
//        Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_polar_low, R.string.polar_low);
//
//        Button closeDialogButton = (Button) dialog.findViewById(R.id.polar_low_ok_button);
//
//        // TODO: implement me
//
//        closeDialogButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));
//
//        dialog.show();
        //getLayers();
       // browser.loadUrl("file:///android_asset/mapApplicationPolarLow.html");
    }

    private void createToolSymbolExplanationDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_tool_legend, R.string.tool_legend);

        TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.message_dialog_mandatory_fields_container);
        Button dismissButton = (Button) dialog.findViewById(R.id.tool_legend_dismiss_button);

        for(ToolType toolType : ToolType.values()) {
            View toolLegendRow = utilityRowsInterface.getToolLegendRow(getActivity(), toolType.getHexValue(), toolType.toString()).getView();
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

        final double seekBarStepSize = (double)(getResources().getInteger(R.integer.proximity_alert_maximum_warning_range_meters) - getResources().getInteger(R.integer.proximity_alert_minimum_warning_range_meters)) / 100;

        radiusEditText.setText(String.valueOf(getResources().getInteger(R.integer.proximity_alert_minimum_warning_range_meters)));

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
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
                // TODO: implement
                String toastText = null;

                if (proximityAlertWatcher == null) {
                    toastText = getString(R.string.proximity_alert_set);
                } else {

                    toastText = getString(R.string.proximity_alert_replace);
                }

                if (proximityAlertWatcher != null) {
                    proximityAlertWatcher.cancel(true);
                }

                mGpsLocationTracker = new GpsLocationTracker(getActivity());
                double latitude, longitude = 0;
                if (mGpsLocationTracker.canGetLocation()) {
                    latitude = mGpsLocationTracker.getLatitude();
                    cachedLat = latitude;
                    longitude = mGpsLocationTracker.getLongitude();
                    cachedLon = longitude;
                } else {
                    mGpsLocationTracker.showSettingsAlert();
                    return;
                }
                String distance = radiusEditText.getText().toString();
                cachedDistance = distance;

//                    cacheWriter = new BarentswatchApi barentswatchApi.getApi().geoDataDownload("")  .execute("fishingfacility", "OLEX", "cachedResults",
//                            String.valueOf(longitude), String.valueOf(latitude), distance, "true");
                runScheduledAlarm();


                Toast.makeText(

                        getActivity(), toastText, Toast

                                .LENGTH_LONG).

                        show();

                dialog.dismiss();

            }
        });

        if(proximityAlertWatcher != null) {
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

        System.out.println("size: " + String.valueOf(stopCurrentProximityAlertWatcherButton.getTextSize()));

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void runScheduledAlarm() {
        proximityAlertWatcher = new FiskinfoScheduledTaskExecutor(2).scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                // Need to get alarm status and handle kill
                if (!cacheDeserialized) {
                    if (checkCacheWriterStatus()) {
                        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        String directoryName = "FiskInfo";
                        String filename = "cachedResults";
                        String filePath = directoryPath + "/" + directoryName + "/" + filename;
                        tools = new FiskInfoUtility().deserializeFiskInfoPolygon2D(filePath);
                        cacheDeserialized = true;
                        // DEMO: add point here for testing/demo purposes
                         Point point = new Point(69.650543, 18.956831);
                         tools.addPoint(point);
                    }
                } else {
                    if (alarmFiring) {
                        notifyUserOfProximityAlert();
                    } else {
                        double latitude, longitude = 0;
                        if (mGpsLocationTracker.canGetLocation()) {
                            latitude = mGpsLocationTracker.getLatitude();
                            cachedLat = latitude;
                            longitude = mGpsLocationTracker.getLongitude();
                            cachedLon = longitude;
                            System.out.println("Lat; " + latitude + "lon: " + longitude);
                            Log.i("GPS-LocationTracker", String.format("latitude: %s", latitude));
                            Log.i("GPS-LocationTracker", String.format("longitude: %s", longitude));
                        } else {
                            mGpsLocationTracker.showSettingsAlert();
                            return;
                        }
                        Point userPosition = new Point(cachedLat, cachedLon);
                        if (!tools.checkCollsionWithPoint(userPosition, Double.parseDouble(cachedDistance))) {
                            return;
                        }

                        alarmFiring = true;
                    }
                }
            }

        }, getResources().getInteger(R.integer.zero), 3, TimeUnit.SECONDS); // <num1> is initial delay,<num2> is the subsequent delay between each call
    }

    private boolean checkCacheWriterStatus() {
        if (cacheWriter != null && cacheWriter.getStatus() == AsyncTask.Status.FINISHED) {
            return true;
        }
        return false;
    }

    private void notifyUserOfProximityAlert() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_proximity_alert_warning, R.string.proximity_alert_warning);

        Button okButton = (Button) dialog.findViewById(R.id.proximity_alert_warning_ok_button);
        Button showOnMapButton = (Button) dialog.findViewById(R.id.proximity_alert_warning_show_on_map_button);
        Button dismissAlertButton = (Button) dialog.findViewById(R.id.proximity_alert_warning_dismiss_button);

        long[] pattern = { 0, 500, 200, 200, 300, 200, 200 };

        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);

//		MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.proximity_warning_sound);
//		if (mediaPlayer == null) {
//			return;
//		}
//		mediaPlayer.start();

        okButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        showOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Zoom map to user position.
                dialog.dismiss();
            }
        });

        dismissAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Kill background task handling proximity checking and set proximityAlertWatcher to null.
                vibrator.cancel();
                vibrator = null;
                proximityAlertWatcher.cancel(true);
                proximityAlertWatcher = null;
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getMapTools() {
            // TODO: place in own thread as task is not asynch, and get tools

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //TODO: REMOVE AT PRODUCTION THIS ALLOWS DEBUGGING ASYNC HTTP-REQUESTS
//        Response response = barentswatchApi.getApi().geoDataDownload("fishingfacility", "JSON");
//        System.out.println("This is the body: " + response);
//
//        InputStream data = null;
//        Reader reader = null;
//        StringWriter writer = null;
//        String charset = "UTF-8";
//        byte[] rawData = null;
//
//        data = new ByteArrayInputStream(((TypedByteArray)response.getBody()).getBytes());
//
//        try {
//            reader = new InputStreamReader(data, charset);
//            writer = new StringWriter();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        char[] buffer = new char[10240];
//        try {
//            for (int length = 0; (length = reader.read(buffer)) > 0;) {
//                writer.write(buffer, 0, length);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(writer.toString());
//        geoJsonFile = writer.toString();
//        try {
//            rawData = new FiskInfoUtility().toByteArray(data);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }




    }

    private void updateMapTools() {
//        String tools = null;
//        tools = geoJsonFile;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date now = new Date();
//        String strDate = sdf.format(now);
//        try {
//            mTools.setTools(new JSONObject(tools), strDate, getActivity());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_map:
                browser.loadUrl("file:///android_asset/mapApplication.html");
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
            case R.id.check_polar_low:
                createPolarLowDialog();
                return true;
            case R.id.choose_map_layers:
                createMapLayerSelectionDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

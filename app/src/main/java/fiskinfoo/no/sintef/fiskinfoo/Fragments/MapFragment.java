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

package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.FiskInfoPolygon2D;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.IceConcentrationType;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.LayerAndVisibility;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Line;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Point;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Polygon;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskinfoConnectivityManager;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskinfoScheduledTaskExecutor;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GeometryType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.GpsLocationTracker;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolLegendRow;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static fiskinfoo.no.sintef.fiskinfoo.MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION;
import static fiskinfoo.no.sintef.fiskinfoo.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

/*

This Fragment does the following calls to JavaScript
  getLayersAndState()
  //populateMap()
  zoomToUserPosition
  toggleLayers (lagt til i html)

? getToolDataFromAndroid (bare tilgjengelig i preview html)

 */


public class MapFragment extends Fragment {
    public static final String FRAGMENT_TAG = "MapFragment";

//    private AutoCompleteTextView searchEditText;
    private LinearLayout bottomSheetLayout;

    private AsynchApiCallTask asynchApiCallTask;
    private WebView browser;
    private BarentswatchApi barentswatchApi;
    private User user;
    private FiskInfoUtility fiskInfoUtility;
    private UtilityDialogs dialogInterface;
    private ScheduledFuture proximityAlertWatcher;
    private GpsLocationTracker mGpsLocationTracker;
    private UserInterface userInterface;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private FiskInfoPolygon2D tools = null;
    private JSONArray layersAndVisibility = null;
    private boolean cacheDeserialized = false;
    private boolean alarmFiring = false;
    protected double cachedLat;
    protected double cachedLon;
    protected double cachedDistance;
    private Map<String, JSONObject> toolMap;
    private JSONObject toolsFeatureCollection;
    private Map<String, List<Integer>> vesselToolIdsMap =  new HashMap<>();
    private boolean pageLoaded = false;
    private Tracker tracker;


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        user = userInterface.getUser();
        if (user == null) {
            Log.d(FRAGMENT_TAG, "Could not get user");
        }

        toolMap = new HashMap<>();
        setHasOptionsMenu(true);
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
        if(asynchApiCallTask != null && asynchApiCallTask.getStatus() != AsyncTask.Status.RUNNING) {
            asynchApiCallTask.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() != null) {
            getView().refreshDrawableState();
        }

        MainActivity activity = (MainActivity) getActivity();
        String title = getResources().getString(R.string.map_fragment_title);
        activity.refreshTitle(title);

        if(tracker != null){

            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
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

        final View rootView = inf.inflate(R.layout.fragment_map, parent, false);;
        bottomSheetLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_bottom_sheet);
        bottomSheetLayout.setVisibility(View.GONE);

//        searchEditText = (AutoCompleteTextView) rootView.findViewById(R.id.map_fragment_tool_search_edit_text);



        // TODO: Disable search if user is not authenticated

//        searchEditText.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.text_white_transparent));

/*        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch(i) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        List<Integer> vesselTools = vesselToolIdsMap.get(textView.getText().toString());
                        highlightVesselInMap(textView.getText().toString());
                        searchEditText.setTag(getString(R.string.map_search_view_tag_clear));
                        searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_black_24dp, 0);
                        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        break;
                    default:
                        return false;
                }

                return true;
            }
        });

        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(getString(R.string.map_search_view_tag_search).equals(searchEditText.getTag())) {
                            searchEditText.setError(null);

                            if(vesselToolIdsMap.get(searchEditText.getText().toString().toUpperCase()) != null) {
                                searchEditText.setTag(getString(R.string.map_search_view_tag_clear));
                                searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_black_24dp, 0);

                                highlightVesselInMap(searchEditText.getText().toString().toUpperCase());
                                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                            } else{
                                searchEditText.setError(getString(R.string.error_no_match_for_vessel_search));
                            }
                        } else{
                            searchEditText.setTag(getString(R.string.map_search_view_tag_search));
                            searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search_black_24dp, 0);

                            String nullString = null;
                            highlightVesselInMap(null);
                            searchEditText.setText("");
                            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                        }


                        return true;
                    }
                }
                return false;
            }
        });*/

        return rootView;
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
        configureWebParametersAndLoadDefaultMapApplication();
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
                Log.d(FRAGMENT_TAG, message);
                return super.onJsAlert(view, url, message, result);
            }

        });

        updateMap();
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
            //Log.d(FRAGMENT_TAG, message);
            try {
                layersAndVisibility = new JSONArray(message);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO
            }
        }

        @SuppressWarnings("unused")
        @android.webkit.JavascriptInterface
        public String getToolFeatureCollection() {
            return toolsFeatureCollection.toString();
        }
    }

    private void getLayersAndVisibility() {
        //browser.loadUrl("javascript:getLayersByNameAndVisibilityState()");
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
            JSONArray json = new JSONArray(layers);

            //view.loadUrl("javascript:populateMap();");
            view.loadUrl("javascript:toggleLayers(" + json + ");");

            if(toolsFeatureCollection != null && (getActivity() != null && (new FiskInfoUtility().isNetworkAvailable(getActivity())) && !user.getOfflineMode())) {
                //TODO: Check with Bård if this is needed now;   view.loadUrl("javascript:getToolDataFromAndroid();");
            }

            pageLoaded = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    getLayersAndVisibility();
                }
            }, 3000);
        }
    }


    public class AsynchApiCallTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            BarentswatchApi barentswatchApi;
            barentswatchApi = new BarentswatchApi();
            String format = "JSON";
            Response response;

            List<PropertyDescription> subscribables;
            PropertyDescription newestSubscribable = null;
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date cachedUpdateDateTime;
            Date newestUpdateDateTime;
            SubscriptionEntry cachedEntry;
            final JSONArray toolsArray;
            final ArrayAdapter<String> adapter;
            String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";
            final List<String> vesselNames;
            byte[] data = new byte[0];
            File file = null;
            String directoryFilePath;

            cachedEntry = user.getSubscriptionCacheEntry(getString(R.string.fishing_facility_api_name));

            if (FiskinfoConnectivityManager.hasValidNetworkConnection(getActivity())) { //(fiskInfoUtility.isNetworkAvailable(getActivity())) {
                try {
                    subscribables = barentswatchApi.getApi().getSubscribable();
                    for (PropertyDescription subscribable : subscribables) {
                        if (subscribable.ApiName.equals(getString(R.string.fishing_facility_api_name))) {
                            newestSubscribable = subscribable;
                            break;
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            } else if(cachedEntry == null){
                return false;
            }

            if(cachedEntry != null) {
                directoryFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";
                file = new File(directoryFilePath + cachedEntry.mSubscribable.Name + ".JSON");
            }

            if(isCancelled()) {
                cancel(true);
            }

            // Added to catch exception that sometimes occour due to null context
            if(getContext() == null) {
                cancel(true);
            }

            if(cachedEntry != null && file.exists() && ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    cachedUpdateDateTime = simpleDateFormat.parse(cachedEntry.mLastUpdated.equals(getActivity().getString(R.string.abbreviation_na)) ? "2000-00-00T00:00:00" : cachedEntry.mLastUpdated);
                    newestUpdateDateTime = simpleDateFormat.parse(newestSubscribable != null ? newestSubscribable.LastUpdated : "2000-00-00T00:00:00");

                    if(newestSubscribable != null && cachedUpdateDateTime.getTime() - newestUpdateDateTime.getTime() < 0) {
                        response = barentswatchApi.getApi().geoDataDownload(newestSubscribable.ApiName, format);
                        try {
                            data = FiskInfoUtility.toByteArray(response.getBody().in());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(getContext() == null) {
                            cancel(true);
                        }
                        if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if(new FiskInfoUtility().writeDataToExternalStorage(getActivity(), data, newestSubscribable.Name.replace(",", "").replace(" ", "_"), format, downloadPath, false)) {
                                SubscriptionEntry entry = new SubscriptionEntry(newestSubscribable, true);
                                entry.mLastUpdated = newestSubscribable.LastUpdated;
                                user.setSubscriptionCacheEntry(newestSubscribable.ApiName, entry);
                                user.writeToSharedPref(getActivity());
                            }
                        }

                    } else {
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
                    Log.e(FRAGMENT_TAG, "Invalid datetime provided");
                }
            } else if (FiskinfoConnectivityManager.hasValidNetworkConnection(getActivity())) {//(fiskInfoUtility.isNetworkAvailable(getActivity())) {
                try {
                    response = barentswatchApi.getApi().geoDataDownload(newestSubscribable.ApiName, format);
                    data = FiskInfoUtility.toByteArray(response.getBody().in());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }

                if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if(new FiskInfoUtility().writeDataToExternalStorage(getActivity(), data, newestSubscribable.Name.replace(",", "").replace(" ", "_"), format, downloadPath, false)) {
                        SubscriptionEntry entry = new SubscriptionEntry(newestSubscribable, true);
                        entry.mLastUpdated = newestSubscribable.LastUpdated;
                        user.setSubscriptionCacheEntry(newestSubscribable.ApiName, entry);
                        user.writeToSharedPref(getActivity());
                    }
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getContext())
                                    .setIcon(R.drawable.ic_warning_black_24dp)
                                    .setTitle(getString(R.string.request_write_access_dialog_title))
                                    .setMessage(getString(R.string.request_write_access_map_rationale))
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // After click on Ok, request the permission.
                                            requestPermissions(new String[] { WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .show();
                        }
                    });
                }
            } else {
                return false;
            }

            try {
                toolsFeatureCollection = new JSONObject(new String(data));
                toolsArray = toolsFeatureCollection.getJSONArray("features");
                vesselNames = new ArrayList<>();
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, vesselNames);

                for (int i = 0; i < toolsArray.length(); i++) {
                    JSONObject feature = toolsArray.getJSONObject(i);
                    String vesselName = (feature.getJSONObject("properties").getString("vesselname") != null && !feature.getJSONObject("properties").getString("vesselname").equals("null")) ? feature.getJSONObject("properties").getString("vesselname") : getString(R.string.vessel_name_unknown);
                    List<Integer> toolsIdList = vesselToolIdsMap.get(vesselName) != null ? vesselToolIdsMap.get(vesselName) : new ArrayList<Integer>();

                    if (vesselName != null && !vesselNames.contains(vesselName)) {
                        vesselNames.add(vesselName);
                    }

                    toolsIdList.add(i);
                    vesselToolIdsMap.put(vesselName, toolsIdList);
                    toolMap.put(feature.getJSONObject("properties").getString("toolid"), feature);
                }
            } catch (JSONException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(getContext())
                                .setTitle(getString(R.string.search_tools_init_error))
                                .setMessage(getString(R.string.search_tools_init_info))
                                .setPositiveButton(getString(R.string.ok), null)
                                .show();
                    }
                });
                Log.e(FRAGMENT_TAG, "JSON parse error");
                e.printStackTrace();

                return false;
            }

/* search removed
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchEditText.setVisibility(View.VISIBLE);
                    searchEditText.setAdapter(adapter);

                    searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String vesselName = ((TextView) view).getText().toString();
                            highlightVesselInMap(vesselName);

                            searchEditText.setTag(getString(R.string.map_search_view_tag_clear));
                            searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_black_24dp, 0);

                            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                        }
                    });
                }
            });
*/

            return true;
        }

        @Override
        protected void onCancelled(Boolean result) {
            Log.d("MapAsync", "Map async cancelled");
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success) {
                if(pageLoaded) {
                    //TODO: Check with Bård if this is needed now; browser.loadUrl("javascript:getToolDataFromAndroid();");
                }
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.tools_search_no_data_title))
                        .setMessage(getString(R.string.tools_search_no_data))
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
            }
        }
    }
    //
    private void createMapLayerSelectionDialog() {
        if(layersAndVisibility == null) {
            return;
        }

        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_map_layers, R.string.choose_map_layers);
        Button okButton = (Button) dialog.findViewById(R.id.select_map_layers_update_map_button);
        final List<CheckBoxRow> rows = new ArrayList<>();
        final LinearLayout mapLayerLayout = (LinearLayout) dialog.findViewById(R.id.map_layers_checkbox_layout);
        final Button cancelButton = (Button) dialog.findViewById(R.id.select_map_layers_cancel_button);
        LayerAndVisibility[] layers = new Gson().fromJson(layersAndVisibility.toString(), LayerAndVisibility[].class);
        for (LayerAndVisibility layer : layers) {
            if (layer.name.equals("Grunnkart") || layer.name.contains("OpenLayers_Control")) {
                continue;
            }
            boolean isActive;
            isActive = layer.visibility;

            CheckBoxRow row = new CheckBoxRow(getActivity(), layer.name, false, isActive);
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void createToolSymbolExplanationDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_tool_legend, R.string.tool_legend);

        TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.tool_legend_table_layout);
        Button dismissButton = (Button) dialog.findViewById(R.id.tool_legend_dismiss_button);

        for (ToolType toolType : ToolType.values()) {
            View toolLegendRow = new ToolLegendRow(getActivity(), toolType.getHexColorValue(), toolType.toString()).getView();
            tableLayout.addView(toolLegendRow);
        }

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

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
                        Log.d(FRAGMENT_TAG, "RESPONSE == NULL");
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
                                    Log.d(FRAGMENT_TAG, "line " + line);
                                }

                                if(convertedLine[0].startsWith("3sl")) {
                                    continue;
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
                            Log.e(FRAGMENT_TAG, "Error when trying to serialize file.");
                            Toast error = Toast.makeText(getActivity(), "Ingen redskaper i området du definerte", Toast.LENGTH_LONG);
                            e.printStackTrace();
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
                    Log.d(FRAGMENT_TAG, "Could not download tools file");
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

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
        pageLoaded = false;
        if((new FiskInfoUtility().isNetworkAvailable(getActivity())) && !user.getOfflineMode()) {
            browser.loadUrl("file:///android_asset/mapApplication.html");

            asynchApiCallTask = new AsynchApiCallTask();
            asynchApiCallTask.execute();
            ((MainActivity)getActivity()).toggleNetworkErrorTextView(true);
        } else {
            browser.loadUrl("file:///android_asset/mapApplicationOfflineMode.html");

            if((!new FiskInfoUtility().isNetworkAvailable(getActivity()))) {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(getString(R.string.offline_mode_map_used_title))
                        .setMessage(getString(R.string.offline_mode_map_used_info))
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
            }

            populateSearchFieldFromLocalFile();
        }
    }

    private void populateSearchFieldFromLocalFile() {
        String directoryFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";

        List<String> vesselNames = new ArrayList<>();
        final JSONArray toolsArray;
        File file = new File(directoryFilePath + "Redskap" + ".JSON");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferReader = null;

        if(file.exists()) {
            try {
                bufferReader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = bufferReader.readLine()) != null) {
                    stringBuilder.append(line);
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
        } else {
            return;
        }

        try {
            toolsFeatureCollection = new JSONObject(stringBuilder.toString());
            toolsArray = toolsFeatureCollection.getJSONArray("features");

            for (int i = 0; i < toolsArray.length(); i++) {
                JSONObject feature = toolsArray.getJSONObject(i);
                String vesselName = (feature.getJSONObject("properties").getString("vesselname") != null && !feature.getJSONObject("properties").getString("vesselname").equals("null")) ? feature.getJSONObject("properties").getString("vesselname") : getString(R.string.vessel_name_unknown);
                List<Integer> toolsIdList = vesselToolIdsMap.get(vesselName) != null ? vesselToolIdsMap.get(vesselName) : new ArrayList<Integer>();

                if (vesselName != null && !vesselNames.contains(vesselName)) {
                    vesselNames.add(vesselName);
                }

                toolsIdList.add(i);
                vesselToolIdsMap.put(vesselName, toolsIdList);
                toolMap.put(feature.getJSONObject("properties").getString("toolid"), feature);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, vesselNames);
/*        searchEditText.setVisibility(View.VISIBLE);
        searchEditText.setAdapter(adapter);

        searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String vesselName = ((TextView) view).getText().toString();
                highlightVesselInMap(vesselName);

                searchEditText.setTag(getString(R.string.map_search_view_tag_clear));
                searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_black_24dp, 0);
                searchEditText.setThreshold(1);

                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });*/
    }

    private void zoomToUserPosition() {
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            browser.loadUrl("javascript:zoomToUserPosition()");
        }
    }

    private void highlightVesselInMap(String vesselName) {
        //TODO: Map Cleanup - Check if this is needed
        /*
        String mmsi = vesselName != null ? getMMSIFromVesselName(vesselName) : null;
        browser.loadUrl("javascript:highlightVessel(\"" + mmsi + "\")");
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        */
    }

    private String getMMSIFromVesselName(String vesselName) {
        List<Integer> vesselTools = vesselToolIdsMap.get(vesselName);
        String mmsi = null;
        for(int toolId : vesselTools) {
            JSONObject tool = null;
            try {
                tool = ((JSONObject)toolsFeatureCollection.getJSONArray("features").get(toolId));
                mmsi = tool.getJSONObject("properties").getString("mmsi");
                break;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mmsi;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                for(int i = 0; i < permissions.length; i++) {
                    if(permissions[i].equals(WRITE_EXTERNAL_STORAGE) && results[i] == PackageManager.PERMISSION_GRANTED) {
                        updateMap();
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                for(int i = 0; i < permissions.length; i++) {
                    if(permissions[i].equals(ACCESS_FINE_LOCATION) && results[i] == PackageManager.PERMISSION_GRANTED && browser != null) {
                        browser.loadUrl("javascript:zoomToUserPosition()");
                    } else if(permissions[i].equals(ACCESS_FINE_LOCATION) && results[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getContext(), R.string.error_cannot_zoom_to_position_without_location_access, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_map:
                updateMap();
                return true;
            case R.id.zoom_to_user_position:
                zoomToUserPosition();
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
}

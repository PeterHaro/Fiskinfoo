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
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolsGeoJson;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.MapLayerCheckBoxRow;
import retrofit.client.Header;
import retrofit.client.Response;

public class MapFragment extends Fragment{
    FragmentActivity listener;
    public static final String TAG = "Map";
    private ToolsGeoJson mTools = null;
    private WebView browser;
    private BarentswatchApi barentswatchApi;
    String geoJsonFile = null;
    private User user;
    private UtilityDialogs dialogInterface;
    private UtilityRowsInterface utilityRowsInterface;
    private UtilityOnClickListeners onClickListenerInterface;

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
                Log.i("my log", "Alert box popped");
                return super.onJsAlert(view, url, message, result);
            }
        });
        // TODO: add tools
        updateMapTools();

        browser.loadUrl("file:///android_asset/mapApplication.html");
    }

    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context context) {
            mContext = context;
        }

        @android.webkit.JavascriptInterface
        public JSONObject getGeoJson() {
            JSONObject jsonObject = null;
            try {

                jsonObject = mTools.getTools();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @android.webkit.JavascriptInterface
        public JSONArray getActiveLayers() {
            JSONArray jsonArray = null;
            try {
                //TODO: remove once user is properly implemented
                if(user != null) {
                    jsonArray = new JSONArray(user.getActiveLayers());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonArray;
        }
    }

    private class barentswatchFiskInfoWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    private void createMapLayerSelectionDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_map_layers, R.string.choose_map_layers);

        Button okButton = (Button) dialog.findViewById(R.id.select_map_layers_update_map_button);
        final List<MapLayerCheckBoxRow> rows = new ArrayList<>();
        final LinearLayout mapLayerLayout = (LinearLayout) dialog.findViewById(R.id.map_layers_checkbox_layout);
        final Button cancelButton = (Button) dialog.findViewById(R.id.select_map_layers_cancel_button);
        String[] mapLayerNames = getResources().getStringArray(R.array.map_layer_names_array);
        final boolean hasUser = user != null;

        for (String mapLayerName : mapLayerNames) {
            boolean isActive = false;

            if (hasUser) {
                isActive = user.getActiveLayers().contains(mapLayerName);
            }

            MapLayerCheckBoxRow row = utilityRowsInterface.getMapLayerCheckBoxRow(getActivity(), isActive, mapLayerName);
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

                if(hasUser) {
                    user.setActiveLayers(layersList);
                }
                dialog.dismiss();
                browser.loadUrl("file:///android_asset/mapApplication.html");
            }
        });

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void createPolarLowDialog() {
        Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_polar_low, R.string.polar_low);

        Button closeDialogButton = (Button) dialog.findViewById(R.id.polar_low_ok_button);

        // TODO: implement me

        closeDialogButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void createProximityAlertSetupDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_create_proximity_alert, R.string.create_proximity_alert);

        Button setProximityAlertWatcherButton = (Button) dialog.findViewById(R.id.create_proximity_alert_create_alert_watcher_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.create_proximity_alert_cancel_button);
        SeekBar seekbar = (SeekBar) dialog.findViewById(R.id.create_proximity_alert_seekBar);
        final EditText radiusEditText = (EditText) dialog.findViewById(R.id.create_proximity_alert_range_edit_text);

        final int stepSizeMeters = (getResources().getInteger(R.integer.maximum_warning_range_meters) - getResources().getInteger(R.integer.minimum_warning_range_meters)) / 100;

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    radiusEditText.setText(String.valueOf((getResources().getInteger(R.integer.minimum_warning_range_meters) + (stepSizeMeters * progress))));
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


                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
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

    private void createDownloadMapLayerDialog() {
        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_download_map_layer, R.string.download_map_layer_dialog_title);

        Button downloadMapLayerButton = (Button) dialog.findViewById(R.id.download_map_layer_download_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.download_map_layer_cancel_button);

        // TODO: add available layers and their available formats.



        downloadMapLayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String apiName = null;
                String format = null;

                //TODO: get selected values
//                apiName = ;
//                format = ;

                if(apiName == null || format == null) {
                    Toast.makeText(getActivity(), getString(R.string.error_no_format_selected), Toast.LENGTH_LONG).show();
                    return;
                }

                // TODO: download map layer and pass to function to write to disk.
                Response response = barentswatchApi.getApi().geoDataDownload(apiName, format);

                if(response.getHeaders().contains("200 OK")) {
                    for(Header header : response.getHeaders()) {
                        System.out.println("Header: " + header.getName() + ", " + header.getValue() + ", " + header.toString());
                    }
                    Toast.makeText(getActivity(), R.string.download_failed, Toast.LENGTH_LONG).show();
                }

                if(isExternalStorageWritable()) {
                    writeMapLayerToExternalStorage(null, null, null, null);
                } else {
                    Toast.makeText(getActivity(), R.string.download_failed, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    return;
                }

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void getMapTools() {
            // TODO: place in own thread as task is not asynch, and get tools

//            barentswatchApi.getApi().geoDataDownload("fishingfacility", "JSON");

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

    /**
     * Checks if external storage is available for read and write.
     *
     * @return True if external storage is available, false otherwise.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }

    private void writeMapLayerToExternalStorage(byte[] data, OutputStream outputStream, String writableName, String format) {
        String filePath = null;
        // TODO: fix once user is properly implemented
        if(user != null) {
            filePath = user.getFilePathForExternalStorage();
        }
        if(filePath == null) {
            String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String directoryName = "FiskInfo";
            filePath = directoryPath + "/" + directoryName + "/";
        }

        File directory = new File(filePath);

        if (!(directory.exists())) {
            directory.mkdirs();
        }

        Toast toast = Toast.makeText(getActivity(), R.string.disk_write_completed, Toast.LENGTH_LONG);
        toast.show();

        try {
            outputStream = new FileOutputStream(new File(filePath + writableName + "." + format));
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            case R.id.export_metadata_to_user:
                createDownloadMapLayerDialog();
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

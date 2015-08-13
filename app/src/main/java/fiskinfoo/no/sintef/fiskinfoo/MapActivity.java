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
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TableLayout;

import org.json.JSONObject;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolsGeoJson;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;

public class MapActivity extends Fragment {
    private ToolsGeoJson mTools = null;
    private WebView browser;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.activity_map, container, false);

        super.onCreate(savedInstanceState);

        configureWebParametersAndLoadDefaultMapApplication();

        return rootView;
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
                JSONObject fnName = mTools.getTools();
                jsonObject = fnName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
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
    }

    private void createPolarLowDialog() {
    }

    private void createProximityAlertSetupDialog(Context currentContext) {
    }

    private void createToolSymbolExplanationDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle(R.string.tool_legend);
        dialog.setContentView(R.layout.dialog_tool_legend);
        dialog.setCanceledOnTouchOutside(false);

        TableLayout tableLayout = (TableLayout) dialog.findViewById(R.id.message_dialog_mandatory_fields_container);
        Button dismissButton = (Button) dialog.findViewById(R.id.tool_legend_dismiss_button);

        for(ToolType toolType : ToolType.values()) {
            View toolLegendRow = new UtilityRows().getToolLegendRow(getActivity(), toolType.toString(), toolType.getHexValue());
            tableLayout.addView(toolLegendRow);
        }

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialog.show();
    }

    private void createDownloadMapLayerDialog(Context currentContext) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context currentContext = getActivity();
        switch (item.getItemId()) {
            case R.id.update_map:
                browser.loadUrl("file:///android_asset/mapApplication.html");
                return true;
            case R.id.zoom_to_user_position:
                browser.loadUrl("javascript:zoomToUserPosition()");
                return true;
            case R.id.export_metadata_to_user:
                createDownloadMapLayerDialog(currentContext);
                return true;
            case R.id.symbol_explanation:
                createToolSymbolExplanationDialog();
                return true;
            case R.id.setProximityAlert:
                createProximityAlertSetupDialog(currentContext);
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
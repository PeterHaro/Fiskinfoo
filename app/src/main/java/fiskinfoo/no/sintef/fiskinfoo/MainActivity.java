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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FileDialog;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.SelectionMode;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.Legacy.LegacyExpandableListAdapter;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SettingsButtonRow;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.this.getClass().getSimpleName();
    private UtilityRowsInterface utilityRowsInterface;
    private UtilityOnClickListeners onClickListenerInterface;
    private UtilityDialogs dialogInterface;
    private FiskInfoUtility fiskInfoUtility;
    private User user;
    private boolean proximityAlerterRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            Log.d(TAG, "did not receive user");
        }

        setupToolbar();
        utilityRowsInterface = new UtilityRows();
        onClickListenerInterface = new UtilityOnClickListeners();
        dialogInterface = new UtilityDialogs();
        fiskInfoUtility = new FiskInfoUtility();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tl = (TabLayout) findViewById(R.id.tabs);
        tl.addTab(tl.newTab().setText(R.string.my_page).setTag(MyPageFragment.TAG));
        tl.addTab(tl.newTab().setText(R.string.map).setTag(MapFragment.TAG));
        setSupportActionBar(toolbar);
        setupTabsInToolbar(tl);
    }

    private void setupTabsInToolbar(TabLayout tl) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_placeholder);
        if(frameLayout.getChildCount() == 0) {
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment_placeholder, createFragment(MyPageFragment.TAG), MyPageFragment.TAG).
                    commit();
        }

        tl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() == MyPageFragment.TAG) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.fragment_placeholder, createFragment(MyPageFragment.TAG), MyPageFragment.TAG).addToBackStack(null).
                            commit();
                } else if (tab.getTag() == MapFragment.TAG) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.fragment_placeholder, createFragment(MapFragment.TAG), MapFragment.TAG).addToBackStack(null).
                            commit();
                } else {
                    Log.d(TAG, "Invalid tab selected");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void createDownloadMapLayerDialog() {
        final Dialog dialog = dialogInterface.getDialogWithTitleIcon(this, R.layout.dialog_download_map_layer, R.string.download_map_layer_dialog_title, R.drawable.ikon_kart_til_din_kartplotter);

        Button downloadMapLayerButton = (Button) dialog.findViewById(R.id.download_map_layer_download_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.download_map_layer_cancel_button);
        final ExpandableListView expListView = (ExpandableListView) dialog.findViewById(R.id.download_map_layer_dialog_expandable_list_layer_container);
        final List<String> listDataHeader = new ArrayList<String>();
        final HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();
        final AtomicReference<String> selectedHeader = new AtomicReference<String>();
        final AtomicReference<String> selectedFormat = new AtomicReference<String>();
        final BarentswatchApi barentswatchApi = new BarentswatchApi();
        final Map<String, String> nameToApi = new HashMap<>();
        barentswatchApi.setAccesToken(user.getToken());
        final IBarentswatchApi api = barentswatchApi.getApi();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //TODO: REMOVE AT PRODUCTION THIS ALLOWS DEBUGGING ASYNC HTTP-REQUESTS
        List<PropertyDescription> availableSubscriptions = null;
        List<Authorization> authorizations = null;
        try {
            availableSubscriptions = api.getSubscribable();
            authorizations = api.getAuthorization();
        } catch(Exception e) {
            Log.d(TAG, "Could not download available subscriptions.\n Exception occured : " + e.toString());
        }
        if (availableSubscriptions == null /*|| authorizations == null*/) {
            return;
        }
        for(int i = 0; i < availableSubscriptions.size(); i++) {
            // TODO: use authorizations to filter out any layers that user does not have access to.
            listDataHeader.add(availableSubscriptions.get(i).Name);
            nameToApi.put(availableSubscriptions.get(i).Name, availableSubscriptions.get(i).ApiName);
            List<String> availableFormats = new ArrayList<>();
            for(String format : availableSubscriptions.get(i).Formats) {
                availableFormats.add(format);
            }
            listDataChild.put(listDataHeader.get(i), availableFormats);
        }
        LegacyExpandableListAdapter legacyExpandableListAdapter = new LegacyExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(legacyExpandableListAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                selectedHeader.set(listDataHeader.get(groupPosition));
                selectedFormat.set(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));

                LinearLayout currentlySelected = (LinearLayout) parent.findViewWithTag("currentlySelectedRow");
                if(currentlySelected != null) {
                    currentlySelected.getChildAt(0).setBackgroundColor(Color.WHITE);
                    currentlySelected.setTag(null);
                }

                ((LinearLayout)v).getChildAt(0).setBackgroundColor(Color.rgb(214, 214, 214));
                v.setTag("currentlySelectedRow");
                return true;
            }
        });

        downloadMapLayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String apiName = nameToApi.get(selectedHeader.get());
                String format = selectedFormat.get();
                Response response = null;

                if(apiName == null || format == null) {
                    Toast.makeText(v.getContext(), R.string.error_no_format_selected, Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    response = api.geoDataDownload(apiName, format);
                    if (response == null) {
                        Log.d(TAG, "RESPONSE == NULL");
                    }
                    byte[] fileData = FiskInfoUtility.toByteArray(response.getBody().in());
                    if(fiskInfoUtility.isExternalStorageWritable()) {
                        fiskInfoUtility.writeMapLayerToExternalStorage(v.getContext(), fileData, selectedHeader.get(), format, user.getFilePathForExternalStorage());
                    } else {
                        Toast.makeText(v.getContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }

                } catch(Exception e) {
                    Log.d(TAG, "Could not download with ApiName: " + apiName + "  and format: " + format);
                }
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void createSettingsDialog() {
        final Dialog dialog = new UtilityDialogs().getDialog(this, R.layout.dialog_settings, R.string.settings);

        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.settings_dialog_fields_container);
        Button closeDialogButton = (Button) dialog.findViewById(R.id.settings_dialog_close_button);

        SettingsButtonRow setDownloadPathButton = utilityRowsInterface.getSettingsButtonRow(this, getString(R.string.set_download_path));
        SettingsButtonRow logOutButton = utilityRowsInterface.getSettingsButtonRow(this, getString(R.string.log_out));

        setDownloadPathButton.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSetFileDownloadPathDialog();
            }
        });

        logOutButton.setButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.log_out))
                        .setMessage(getString(R.string.confirm_log_out))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userLogout();
                            }

                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
            }
        });

        linearLayout.addView(setDownloadPathButton.getView());
        linearLayout.addView(logOutButton.getView());

        closeDialogButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void userLogout() {
        User.forgetUser(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void createSetFileDownloadPathDialog() {
        Intent intent = new Intent(getBaseContext(), FileDialog.class);
        intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

        intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
        intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);

        startActivityForResult(intent, 1);
    }

    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FileDialog.RESULT_PATH) + "/";
            user.setFilePathForExternalStorage(filePath);
            Toast mToast = Toast.makeText(this, "SELECTED FILEPATH AT: " + filePath, Toast.LENGTH_LONG);
            user.writeToSharedPref(this);
            mToast.show();

        } else if (resultCode == Activity.RESULT_CANCELED) {

        }

    }

    private Fragment createFragment(String tag) {
        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user", user);
        switch(tag) {
            case MyPageFragment.TAG:
                MyPageFragment myPageFragment = new MyPageFragment();
                myPageFragment.setArguments(userBundle);
                return myPageFragment;
            case MapFragment.TAG:
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(userBundle);
                return mapFragment;
            default:
                Log.d(TAG, "Trying to create invalid fragment with TAG: " + tag);
        }
        return null;
    }

    public boolean isProximityAlerterRunning() {
        return proximityAlerterRunning;
    }

    public void setProximityAlerterRunning(boolean proximityAlerterRunning) {
        this.proximityAlerterRunning = proximityAlerterRunning;
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                createSettingsDialog();
                break;
            case R.id.export_metadata_to_user:
                createDownloadMapLayerDialog();
                break;
            default:
                return false;
        }

        return true;
    }
}

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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import Fragments.EditToolFragment;
import Fragments.MapFragment;
import Fragments.MyPageFragment;
import Fragments.MyToolsFragment;
import Fragments.SettingsFragment;
import Fragments.SubscriptionDetailsFragment;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FileDialog;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskinfoScheduledTaskExecutor;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.SelectionMode;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.Legacy.LegacyExpandableListAdapter;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.InfoSwitchRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.OptionsButtonRow;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SettingsFragment.OnFragmentInteractionListener,
        EditToolFragment.OnFragmentInteractionListener,
        UserInterface
        {

    public final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0x001;
    public final static int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0x002;
    private final String TAG = MainActivity.this.getClass().getSimpleName();

    private UtilityOnClickListeners utilityOnClickListeners;
    private UtilityDialogs dialogInterface;
    private FiskInfoUtility fiskInfoUtility;
    private User user;
    private ScheduledFuture offlineModeBackGroundThread;
    private RelativeLayout toolbarOfflineModeView;
    boolean offlineModeLooperPrepared = false;
    private TextView mNetworkErrorTextView;
    private boolean networkStateChanged;
    private TextView navigationHeaderUserNameTextView;

    private NavigationView navigationView;
    int currentMenuItemID = -1;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = getIntent().getParcelableExtra("user");
        if (user == null) {
            Log.d(TAG, "did not receive user");
        }

        utilityOnClickListeners = new UtilityOnClickListeners();
        dialogInterface = new UtilityDialogs();
        fiskInfoUtility = new FiskInfoUtility();
//        setupToolbar();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        initializeNavigationView();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.inflateMenu(R.menu.navigation_drawer_menu);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().performIdentifierAction(R.id.navigation_view_subscriptions, 0);
        navigationHeaderUserNameTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navigation_header_user_name_text_view);

        if(user.getSettings() != null && user.getSettings().getContactPersonName() != null) {
            navigationHeaderUserNameTextView.setText(user.getSettings().getContactPersonName());
        }

        mNetworkErrorTextView = (TextView) findViewById(R.id.activity_main_network_error_text_view);

        if(!fiskInfoUtility.isNetworkAvailable(getBaseContext())) {

        }
    }

    public void refreshTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void initializeNavigationView() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
                @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void navigate(int menuItemID) {
        navigationView.getMenu().performIdentifierAction(menuItemID, 0);
    }

//    private void setupToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        TabLayout tl = (TabLayout) findViewById(R.id.tabs);
//        tl.addTab(tl.newTab().setText(R.string.my_page).setTag(MyPageFragment.FRAGMENT_TAG));
//        tl.addTab(tl.newTab().setText(R.string.map).setTag(MapFragment.FRAGMENT_TAG));
//        tl.addTab(tl.newTab().setText(R.string.my_tools).setTag(MyToolsFragment.FRAGMENT_TAG));
//
//        setSupportActionBar(toolbar);
//        setupTabsInToolbar(tl);
//
//        toolbarOfflineModeView = (RelativeLayout) toolbar.findViewById(R.id.toolbar_offline_mode_container);
//
//        if(user.getOfflineMode()) {
//            initAndStartOfflineModeBackgroundThread();
//        }
//
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null) {
//            actionBar.setDisplayShowTitleEnabled(false);
//        }
//
//    }

//    private void setupTabsInToolbar(TabLayout tl) {
//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
//        if(frameLayout.getChildCount() == 0) {
//            getFragmentManager().beginTransaction().
//                    replace(R.id.fragment_container, createFragment(MyPageFragment.FRAGMENT_TAG), MyPageFragment.FRAGMENT_TAG).
//                    commit();
//        }
//
//        tl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                if (tab.getTag() == MyPageFragment.FRAGMENT_TAG) {
//                    getFragmentManager().beginTransaction().
//                            replace(R.id.fragment_container, createFragment(MyPageFragment.FRAGMENT_TAG), MyPageFragment.FRAGMENT_TAG).addToBackStack(null).
//                            commit();
//                } else if (tab.getTag() == MapFragment.FRAGMENT_TAG) {
//                    getFragmentManager().beginTransaction().
//                            replace(R.id.fragment_container, createFragment(MapFragment.FRAGMENT_TAG), MapFragment.FRAGMENT_TAG).addToBackStack(null).
//                            commit();
//                } else if (tab.getTag() == MyToolsFragment.FRAGMENT_TAG){
//                    getFragmentManager().beginTransaction().
//                            replace(R.id.fragment_container, MyToolsFragment.newInstance(user), MyToolsFragment.FRAGMENT_TAG).addToBackStack(null).
//                                    commit();
//                } else {
//                    Log.d(FRAGMENT_TAG, "Invalid tab selected");
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//    }

    private void createDownloadMapLayerDialog() {
        final Dialog dialog = dialogInterface.getDialogWithTitleIcon(this, R.layout.dialog_download_map_layer_from_list, R.string.download_map_layer_dialog_title, R.drawable.ikon_kart_til_din_kartplotter);

        Button downloadMapLayerButton = (Button) dialog.findViewById(R.id.download_map_layer_download_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.download_map_layer_cancel_button);
        final ExpandableListView expListView = (ExpandableListView) dialog.findViewById(R.id.download_map_layer_dialog_expandable_list_layer_container);

        final List<String> listDataHeader = new ArrayList<>();
        final HashMap<String, List<String>> listDataChild = new HashMap<>();
        final AtomicReference<String> selectedHeader = new AtomicReference<>();
        final AtomicReference<String> selectedFormat = new AtomicReference<>();
        final BarentswatchApi barentswatchApi = new BarentswatchApi();
        final Map<String, String> nameToApi = new HashMap<>();
        final Map<Integer, Boolean> authMap = new HashMap<>();
        barentswatchApi.setAccesToken(user.getToken());
        final IBarentswatchApi api = barentswatchApi.getApi();

        @SuppressWarnings("unused")
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //TODO: REMOVE AT PRODUCTION THIS ALLOWS DEBUGGING ASYNC HTTP-REQUESTS
        List<PropertyDescription> availableSubscriptions = null;
        List<Authorization> authorizations = null;
        try {
            availableSubscriptions = api.getSubscribable();
            authorizations = api.getAuthorization();
        } catch(Exception e) {
            Log.d(TAG, "Could not download available subscriptions.\n Exception occured : " + e.toString());
        }
        if (availableSubscriptions == null || authorizations == null) {
            return;
        } else {
            for(Authorization authorization : authorizations) {
                authMap.put(authorization.Id, authorization.HasAccess);
            }
        }

        for(PropertyDescription subscription : availableSubscriptions) {
            if(!authMap.get(subscription.Id)) {
                continue;
            }
            listDataHeader.add(subscription.Name);
            nameToApi.put(subscription.Name, subscription.ApiName);
            List<String> availableFormats = new ArrayList<>();

            availableFormats.addAll(Arrays.asList(subscription.Formats));
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), availableFormats);
        }

        LegacyExpandableListAdapter legacyExpandableListAdapter = new LegacyExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(legacyExpandableListAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                selectedHeader.set(listDataHeader.get(groupPosition));
                selectedFormat.set(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));

                LinearLayout currentlySelected = (LinearLayout) parent.findViewWithTag("currentlySelectedRow");
                if (currentlySelected != null) {
                    currentlySelected.getChildAt(0).setBackgroundColor(Color.WHITE);
                    currentlySelected.setTag(null);
                }

                ((LinearLayout) v).getChildAt(0).setBackgroundColor(Color.rgb(214, 214, 214));
                v.setTag("currentlySelectedRow");
                return true;
            }
        });

        downloadMapLayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String apiName = nameToApi.get(selectedHeader.get());
                String format = selectedFormat.get();
                Response response;

                if (apiName == null || format == null) {
                    Toast.makeText(v.getContext(), R.string.error_no_format_selected, Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    response = api.geoDataDownload(apiName, format);
                    if (response == null) {
                        Log.d(TAG, "RESPONSE == NULL");
                        throw new NullPointerException();
                    }
                    byte[] fileData = FiskInfoUtility.toByteArray(response.getBody().in());
                    if (fiskInfoUtility.isExternalStorageWritable()) {
                        fiskInfoUtility.writeMapLayerToExternalStorage(MainActivity.this, fileData, selectedHeader.get(), format, user.getFilePathForExternalStorage(), true);
                    } else {
                        Toast.makeText(v.getContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }

                } catch (Exception e) {
                    Log.d(TAG, "Could not download with ApiName: " + apiName + "  and format: " + format);
                }
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(utilityOnClickListeners.getDismissDialogListener(dialog));

        dialog.show();
    }

    private void stopOfflineModeBackgroundThread() {
        if (offlineModeBackGroundThread != null) {
            offlineModeBackGroundThread.cancel(true);
            offlineModeBackGroundThread = null;
            offlineModeLooperPrepared = false;
        }

        toolbarOfflineModeView.setVisibility(View.GONE);
    }

    private void initAndStartOfflineModeBackgroundThread() {
        toolbarOfflineModeView.setOnClickListener(getOfflineModeInfoOnClickListener());

        offlineModeBackGroundThread = new FiskinfoScheduledTaskExecutor(2).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(getMainLooper());

                if(!fiskInfoUtility.isNetworkAvailable(getBaseContext())) {
                    Log.i(TAG, "Offline mode update skipped");
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            toggleNetworkErrorTextView(false);
                        }
                    }, 100);

                    return;
                } else {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            toggleNetworkErrorTextView(true);
                        }
                    }, 100);
                }



                BarentswatchApi barentswatchApi = new BarentswatchApi();
                IBarentswatchApi api = barentswatchApi.getApi();
                List<PropertyDescription> subscribables;
                Response response;
                String downloadPath;
                String format = "JSON";
                Date lastUpdatedDateTime;
                Date lastUpdatedCacheDateTime;
                byte[] data = null;
                boolean success;

                subscribables = api.getSubscribable();
                downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/FiskInfo/Offline/";

                if(!offlineModeLooperPrepared) {
                    Looper.prepare();
                    offlineModeLooperPrepared = true;
                }

                for(PropertyDescription subscribable : subscribables) {
                    SubscriptionEntry cacheEntry = user.getSubscriptionCacheEntry(subscribable.ApiName);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                    if(cacheEntry == null || !cacheEntry.mOfflineActive) {
                        continue;
                    }

                    try {
                        lastUpdatedDateTime = simpleDateFormat.parse(subscribable.LastUpdated);
                        lastUpdatedCacheDateTime = simpleDateFormat.parse(cacheEntry.mLastUpdated.equals(getBaseContext().getString(R.string.abbreviation_na)) ? "2000-00-00T00:00:00" : cacheEntry.mLastUpdated);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Invalid datetime provided");
                        continue;
                    }

                    if(lastUpdatedDateTime.getTime() <= lastUpdatedCacheDateTime.getTime()){
                        continue;
                    }

                    response = api.geoDataDownload(subscribable.ApiName, format);

                    if(response.getStatus() != 200) {
                        Log.i(TAG, "Download failed");
                        continue;
                    }

                    try {
                        data = FiskInfoUtility.toByteArray(response.getBody().in());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    success = new FiskInfoUtility().writeMapLayerToExternalStorage(MainActivity.this,
                            data,
                            subscribable.Name
                                    .replace(",", "")
                                    .replace(" ", "_"),
                            format, downloadPath, false);

                    if(success) {
                        cacheEntry.mLastUpdated = subscribable.LastUpdated;
                        cacheEntry.mSubscribable = subscribable;
                        user.setSubscriptionCacheEntry(subscribable.ApiName, cacheEntry);
                    }
                }

                user.writeToSharedPref(getBaseContext());
                Log.i(TAG, "Offline mode update");
                System.out.println("BEEP");
            }
        }, getResources().getInteger(R.integer.zero), getResources().getInteger(R.integer.offline_mode_interval_time_seconds), TimeUnit.SECONDS);

        toolbarOfflineModeView.setVisibility(View.VISIBLE);
    }


    private View.OnClickListener getOfflineModeInfoOnClickListener() {
        return                 new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new UtilityDialogs().getDialog(v.getContext(), R.layout.dialog_offline_mode_info, R.string.offline_mode);
                TextView textView = (TextView) dialog.findViewById(R.id.offline_mode_info_dialog_text_view);
                LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.offline_mode_info_dialog_linear_layout);
                Button okButton = (Button) dialog.findViewById(R.id.offline_mode_info_dialog_dismiss_button);
                final Switch offlineModeSwitch = (Switch) dialog.findViewById(R.id.offline_mode_info_dialog_switch);

                offlineModeSwitch.setChecked(user.getOfflineMode());

                if(user.getOfflineMode()) {
                    offlineModeSwitch.setText(v.getResources().getString(R.string.offline_mode_active));
                } else {
                    offlineModeSwitch.setText(v.getResources().getString(R.string.offline_mode_deactivated));
                }

                textView.setText(R.string.offline_mode_info);

                for (final SubscriptionEntry entry : user.getSubscriptionCacheEntries()) {
                    final InfoSwitchRow row = new InfoSwitchRow(v.getContext(), entry.mName, entry.mLastUpdated.replace("T", "\n"));

                    row.setChecked(entry.mOfflineActive);
                    row.setOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SubscriptionEntry updateEntry = user.getSubscriptionCacheEntry(entry.mSubscribable.ApiName);
                            updateEntry.mOfflineActive = isChecked;
                            user.setSubscriptionCacheEntry(entry.mSubscribable.ApiName, updateEntry);
                            user.writeToSharedPref(buttonView.getContext());
                        }
                    });

                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            row.setChecked(!row.isChecked());
                        }
                    });

                    linearLayout.addView(row.getView());
                }

                offlineModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        user.setOfflineMode(isChecked);
                        user.writeToSharedPref(buttonView.getContext());

                        if (isChecked) {
                            offlineModeSwitch.setText(R.string.offline_mode_active);
                            initAndStartOfflineModeBackgroundThread();
                            Toast.makeText(buttonView.getContext(), R.string.offline_mode_activated, Toast.LENGTH_LONG).show();
                        } else {
                            offlineModeSwitch.setText(R.string.offline_mode_deactivated);
                            stopOfflineModeBackgroundThread();
                            Toast.makeText(buttonView.getContext(), R.string.offline_mode_deactivated, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                okButton.setOnClickListener(new UtilityOnClickListeners().getDismissDialogListener(dialog));

                dialog.show();
            }
        };
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

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);

        // Most requests are made from fragments. Since
        switch(permsRequestCode){
            case 200:
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
            default:
//                Toast.makeText(this, R.string.permission_denied_app_limited, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(FileDialog.RESULT_PATH) + "/";
            user.setFilePathForExternalStorage(filePath);
            Toast mToast = Toast.makeText(this, "SELECTED FILEPATH AT: " + filePath, Toast.LENGTH_LONG);
            user.writeToSharedPref(this);
            mToast.show();
        }
    }

    public void toggleNetworkErrorTextView(boolean networkAvailable) {
        if(!networkAvailable) {
            mNetworkErrorTextView.setText(R.string.no_internet_access);
            mNetworkErrorTextView.setTextColor(ContextCompat.getColor(this, R.color.error_red));
            mNetworkErrorTextView.setVisibility(View.VISIBLE);
        } else {
            mNetworkErrorTextView.setVisibility(View.GONE);
        }
    }

    public boolean getNetworkStateChanged() {
        return networkStateChanged;
    }

    public void setNetworkStateChanged(boolean state) {
        networkStateChanged = state;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        android.support.v4.app.Fragment fragment;
        String tag;
        switch (item.getItemId()) {
            case R.id.navigation_view_subscriptions:
                fragment = MyPageFragment.newInstance();
                tag = MyPageFragment.FRAGMENT_TAG;
                break;
            case R.id.navigation_view_map:
                fragment = MapFragment.newInstance();
                tag = MapFragment.FRAGMENT_TAG;
                break;
            case R.id.navigation_view_tools:
                fragment = MyToolsFragment.newInstance();
                tag = MyToolsFragment.FRAGMENT_TAG;
                break;
            case R.id.navigation_view_settings:
                fragment = SettingsFragment.newInstance();
                tag = SettingsFragment.FRAGMENT_TAG;
                break;
            default:
                return false;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(tag);

        if (currentFragment != null && currentFragment.isVisible()) {
            mDrawerLayout.closeDrawers();
            return true;
        }

        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user", user);
        fragment.setArguments(userBundle);

        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .replace(R.id.main_activity_fragment_container, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        currentMenuItemID = item.getItemId();
        navigationView.setCheckedItem(item.getItemId());
        mDrawerLayout.closeDrawers();
        return true;

    }

    @Override
    public void deleteToolLogEntry(ToolEntry tool) {
        user.getToolLog().removeTool(tool.getSetupDate(), tool.getToolLogId());
        user.writeToSharedPref(this);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fragmentManager.findFragmentByTag(MyPageFragment.FRAGMENT_TAG);

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if ((getSupportFragmentManager().getBackStackEntryCount() > 0) ||
                (fragment != null && fragment.isVisible())) {
            super.onBackPressed();
        }
        else {
            navigate(R.id.navigation_view_subscriptions);
        }
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void updateUserSettings(UserSettings userSettings) {
        user.setSettings(userSettings);
        user.writeToSharedPref(this);
//        Toast.makeText(this, R.string.user_settings_updated, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateTool(ToolEntry tool) {
        if(tool != null && !tool.getSetupDate().isEmpty()) {
            user.getToolLog().addTool(tool, tool.getSetupDate());
            user.writeToSharedPref(this);
            Toast.makeText(this, R.string.tool_updated, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void toggleOfflineMode(boolean offline) {
        if(offline) {
            initAndStartOfflineModeBackgroundThread();
        } else {
            stopOfflineModeBackgroundThread();
        }
    }
}
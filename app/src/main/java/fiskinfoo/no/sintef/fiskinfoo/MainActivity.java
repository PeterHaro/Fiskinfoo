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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.FileDialog;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.SelectionMode;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SettingsButtonRow;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.this.getClass().getSimpleName();
    private UtilityRowsInterface utilityRowsInterface;
    private UtilityOnClickListeners onClickListenerInterface;
    private User user;

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
                            replace(R.id.fragment_placeholder, createFragment(MyPageFragment.TAG), MyPageFragment.TAG).
                            commit();
                } else if (tab.getTag() == MapFragment.TAG) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.fragment_placeholder, createFragment(MapFragment.TAG), MapFragment.TAG).
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

    private void createSettingsDialog() {
        final Dialog dialog = new UtilityDialogs().getDialog(this, R.layout.dialog_settings, R.string.settings);

        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.settings_dialog_fields_container);
        Button closeDialogButton = (Button) dialog.findViewById(R.id.settings_dialog_close_button);

//        RecyclerView mRecyclerView;
//        RecyclerView.Adapter mAdapter;
//        RecyclerView.LayoutManager mLayoutManager;
//
//        mRecyclerView = (RecyclerView) dialog.findViewById(R.id.settings_dialog_fields_container);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
////        mRecyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // specify an adapter (see also next example)

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

//        mAdapter = new recycleViewSettingsButtonRowAdapter(null, buttons);
//        mRecyclerView.setAdapter(mAdapter);

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            createSettingsDialog();
        }

        return super.onOptionsItemSelected(item);
    }
}

package fiskinfoo.no.sintef.fiskinfoo;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.this.getClass().getSimpleName();
    private String selectedFragment = null;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = savedInstanceState.getParcelable("user");
        if (user == null) {
            Log.d(TAG, "did not receive user");
        }

        setupToolbar();


    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tl = (TabLayout) findViewById(R.id.tabs);
        tl.addTab(tl.newTab().setText("Min side").setTag(MyPageFragment.TAG));
        tl.addTab(tl.newTab().setText("Kart").setTag(MapFragment.TAG));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

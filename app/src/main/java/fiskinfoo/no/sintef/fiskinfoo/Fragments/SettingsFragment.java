package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FileDialog;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.SelectionMode;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.LoginActivity;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SettingsRow;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public static final String FRAGMENT_TAG = "SettingsFragment";

    private OnFragmentInteractionListener mListener;
    private UserInterface userInterface;
    private LinearLayout linearLayout;
    private Tracker tracker;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        linearLayout = (LinearLayout) rootView.findViewById(R.id.settings_fragment_fields_container);

        initUserDetailsRow();
        initSetDownLoadPathRow();
        initOfflineModeRow();
        //initAboutRow();
        //initLogoutRow();

        return rootView;
    }

    private void initOfflineModeRow() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                OfflineModeFragment fragment = OfflineModeFragment.newInstance(userInterface.getUser());

                fragmentManager.beginTransaction()
                        .replace(R.id.main_activity_fragment_container, fragment, getString(R.string.offline_mode))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(getString(R.string.offline_mode))
                        .commit();
            }
        };

        SettingsRow row = new SettingsRow(getContext(), getString(R.string.offline_mode), R.drawable.ic_portable_wifi_off_black_24dp, onClickListener);
        linearLayout.addView(row.getView());
    }

    private void initSetDownLoadPathRow() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FileDialog.class);
                intent.putExtra(FileDialog.START_PATH, Environment.getExternalStorageDirectory().getPath());

                intent.putExtra(FileDialog.CAN_SELECT_DIR, true);
                intent.putExtra(FileDialog.SELECTION_MODE, SelectionMode.MODE_CREATE);

                startActivityForResult(intent, 1);
            }
        };

        SettingsRow row = new SettingsRow(getContext(), getString(R.string.set_download_path), R.drawable.ic_description_black_24dp, onClickListener);
        linearLayout.addView(row.getView());
    }

    private void initUserDetailsRow() {
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                UserSettingsFragment fragment = UserSettingsFragment.newInstance(userInterface.getUser().getSettings());

                fragmentManager.beginTransaction()
                        .replace(R.id.main_activity_fragment_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(getString(R.string.user_settings_fragment_title))
                        .commit();
            }
        };

        SettingsRow row = new SettingsRow(getContext(), getString(R.string.user_information), R.drawable.ic_account_circle_black_24dp, onClickListener);
        linearLayout.addView(row.getView());
    }

    private void initLogoutRow() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setIcon(R.drawable.ic_warning_black_24dp)
                        .setTitle(getString(R.string.log_out))
                        .setMessage(getString(R.string.confirm_log_out))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                logoutUser();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
            }
        };

        SettingsRow row = new SettingsRow(getContext(), getString(R.string.log_out), R.drawable.ic_power_settings_new_black_24dp, onClickListener);
        linearLayout.addView(row.getView());
    }

    private void initAboutRow() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AboutFragment fragment = AboutFragment.newInstance();

                fragmentManager.beginTransaction()
                        .replace(R.id.main_activity_fragment_container, fragment)
                        .addToBackStack(getString(R.string.about))
                        .commit();
            }
        };

        SettingsRow row = new SettingsRow(getContext(), getString(R.string.about), R.drawable.ic_info_black_24dp, onClickListener);
        linearLayout.addView(row.getView());
    }


    public void logoutUser() {
        User.forgetUser(getContext());
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            userInterface = (UserInterface) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        userInterface = null;
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
        String title = getResources().getString(R.string.settings_fragment_title);
        activity.refreshTitle(title);
    }

    public interface OnFragmentInteractionListener {
        void toggleOfflineMode(boolean offline);
    }
}

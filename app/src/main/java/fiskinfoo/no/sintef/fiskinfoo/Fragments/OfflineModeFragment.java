package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.InfoSwitchRow;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static fiskinfoo.no.sintef.fiskinfoo.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterface} interface
 * to handle interaction events.
 * Use the {@link OfflineModeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfflineModeFragment extends Fragment {

    private static final String ARG_PARAM_USER = "user";

    private User user;
    private UserInterface userInterface;
    private OnFragmentInteractionListener mListener;

    private LinearLayout mapLayerSwitchesLayout;
    private Switch toggleOfflineModeSwitch;
    private Tracker tracker;

    public OfflineModeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment OfflineModeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OfflineModeFragment newInstance(User user) {
        OfflineModeFragment fragment = new OfflineModeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_PARAM_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_offline_mode, container, false);

        toggleOfflineModeSwitch = (Switch) rootView.findViewById(R.id.offline_mode_fragment_offline_mode_switch);
        mapLayerSwitchesLayout = (LinearLayout) rootView.findViewById(R.id.offline_mode_fragment_map_layer_switches_container);

        populateLayerView();

        return rootView;
    }

    private void populateLayerView() {
        if(user.getOfflineMode()) {
            toggleOfflineModeSwitch.setChecked(true);
            toggleOfflineModeSwitch.setText(R.string.on);
        } else {
            toggleOfflineModeSwitch.setChecked(false);
            toggleOfflineModeSwitch.setText(R.string.off);
        }


        for (final SubscriptionEntry entry : user.getSubscriptionCacheEntries()) {
            final InfoSwitchRow row = new InfoSwitchRow(getContext(), entry.mName, entry.mLastUpdated.replace("T", "\n"));

            row.setChecked(entry.mOfflineActive);
            row.setOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
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

            mapLayerSwitchesLayout.addView(row.getView());
        }

        toggleOfflineModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED && isChecked) {
                    final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = this;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getContext())
                                    .setIcon(R.drawable.ic_warning_black_24dp)
                                    .setTitle(getString(R.string.request_write_access_dialog_title))
                                    .setMessage(getString(R.string.request_write_access_offline_mode_rationale))
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[] { WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                            buttonView.setOnCheckedChangeListener(null);
                                            buttonView.setChecked(false);
                                            buttonView.setOnCheckedChangeListener(onCheckedChangeListener);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            buttonView.setOnCheckedChangeListener(null);
                                            buttonView.setChecked(false);
                                            buttonView.setOnCheckedChangeListener(onCheckedChangeListener);
                                        }
                                    })
                                    .show();
                        }
                    });

                    return;
                }

                user.setOfflineMode(isChecked);
                userInterface.setOfflineMode(isChecked);

                if (isChecked) {
                    toggleOfflineModeSwitch.setText(R.string.on);
                    mListener.toggleOfflineMode(true);
                    Toast.makeText(buttonView.getContext(), R.string.offline_mode_activated, Toast.LENGTH_LONG).show();
                } else {
                    toggleOfflineModeSwitch.setText(R.string.on);
                    mListener.toggleOfflineMode(false);
                    Toast.makeText(buttonView.getContext(), R.string.offline_mode_deactivated, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserInterface) {
            userInterface = (UserInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserInterface");
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        String title = getResources().getString(R.string.offline_mode);
        activity.refreshTitle(title);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                for(int i = 0; i < permissions.length; i++) {
                    if(permissions[i].equals(WRITE_EXTERNAL_STORAGE)) {
                        if(results[i] == PackageManager.PERMISSION_GRANTED) {
                            toggleOfflineModeSwitch.setChecked(true);
                            toggleOfflineModeSwitch.setText(R.string.on);
                            user.setOfflineMode(true);

                            userInterface.setOfflineMode(true);
                            mListener.toggleOfflineMode(true);

                            Toast.makeText(getContext(), R.string.offline_mode_activated, Toast.LENGTH_LONG).show();
                        } else {
                            toggleOfflineModeSwitch.setChecked(false);
                            toggleOfflineModeSwitch.setText(R.string.off);
                        }
                    }
                }

                break;
            default:
        }
    }

    public interface OnFragmentInteractionListener {
        void toggleOfflineMode(boolean offline);
    }
}

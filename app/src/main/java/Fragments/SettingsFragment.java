package Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Tool;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FileDialog;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.SelectionMode;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.LoginActivity;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.EditTextRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.InfoSwitchRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SettingsRow;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SpinnerRow;


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
        initAboutRow();
        initLogoutRow();

        return rootView;
    }

    private void initOfflineModeRow() {
        final User user = userInterface.getUser();
        View.OnClickListener onClickListener = new View.OnClickListener() {
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
                            mListener.toggleOfflineMode(true);
                            Toast.makeText(buttonView.getContext(), R.string.offline_mode_activated, Toast.LENGTH_LONG).show();
                        } else {
                            offlineModeSwitch.setText(R.string.offline_mode_deactivated);
                            mListener.toggleOfflineMode(false);
                            Toast.makeText(buttonView.getContext(), R.string.offline_mode_deactivated, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                okButton.setOnClickListener(new UtilityOnClickListeners().getDismissDialogListener(dialog));

                dialog.show();
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

        MainActivity activity = (MainActivity) getActivity();
        String title = getResources().getString(R.string.settings_fragment_title);
        activity.refreshTitle(title);
    }

    public interface OnFragmentInteractionListener {
        void toggleOfflineMode(boolean offline);
    }
}

package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxRow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterface} interface
 * to handle interaction events.
 * Use the {@link TermsAndServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TermsAndServicesFragment extends Fragment {
    public static final String FRAGMENT_TAG = "TermsAndServicesFragment";
    private static final String ARG_PARAM_USER = "user";

    private Tracker tracker;
    private UserSettings userSettings;
    private OnFragmentInteractionListener mListener;
    private UserInterface userInterface;
    private LinearLayout fieldsContainer;
    private TextView privacyPolicyContainer;
    private CheckBoxRow termsAndServicesCheckboxRow;

    public TermsAndServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userSettings
     * @return A new instance of fragment TermsAndServicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TermsAndServicesFragment newInstance(UserSettings userSettings) {
        TermsAndServicesFragment fragment = new TermsAndServicesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_USER, userSettings != null ? userSettings : new UserSettings());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        if (getArguments() != null) {
            userSettings = getArguments().getParcelable(ARG_PARAM_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_terms_and_services, container, false);

        fieldsContainer = rootView.findViewById(R.id.terms_and_services_fragment_fields_container);
        privacyPolicyContainer = rootView.findViewById(R.id.terms_and_services_fragment_privacy_policy_container);
        mListener.toggleNavigationViewEnabled(userSettings.getPrivacyPolicyConsent());

        generateAndPopulateFields();

        return rootView;
    }

    private void generateAndPopulateFields() {
        privacyPolicyContainer.setText(R.string.privacy_policy);

        termsAndServicesCheckboxRow = new CheckBoxRow(getContext(), getString(R.string.terms_and_services_consent_checkbox_text), true);

        if (userSettings != null) {
            termsAndServicesCheckboxRow.setChecked(userSettings.getPrivacyPolicyConsent());
        }

        termsAndServicesCheckboxRow.setOnCheckedChangedListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b && userSettings.getPrivacyPolicyConsent()) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    userInterface.logOutAndDeleteUser();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    termsAndServicesCheckboxRow.setChecked(true);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(compoundButton.getContext());
                    builder.setMessage(R.string.confirm_privacy_policy_consent_withdrawal).setPositiveButton(R.string.delete_user, dialogClickListener)
                            .setNegativeButton(R.string.cancel, dialogClickListener).show();
                } else if(!userSettings.getPrivacyPolicyConsent() && b) {
                    setHasOptionsMenu(b);
                }
            }
        });

        fieldsContainer.addView(termsAndServicesCheckboxRow.getView());
    }

    private void validateAndUpdatePrivacyPolicyConsent() {
        if(!termsAndServicesCheckboxRow.isChecked()) {
            Toast.makeText(getActivity(), R.string.error_privacy_policy_consent_required, Toast.LENGTH_LONG).show();

            return;
        }

        mListener.toggleNavigationViewEnabled(true);
        userSettings.setPrivacyPolicyConsent(true);

        userInterface.updateUserSettings(userSettings);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_terms_and_services, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_privacy_policy:
                validateAndUpdatePrivacyPolicyConsent();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserInterface && context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            userInterface = (UserInterface) getActivity();

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement userInterface");
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
        String title = getResources().getString(R.string.terms_and_Services_fragment_title);
        activity.refreshTitle(title);
    }

    public interface OnFragmentInteractionListener {
        void toggleNavigationViewEnabled(boolean enabled);
    }
}

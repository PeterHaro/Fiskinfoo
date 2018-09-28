package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.CoordinateFormat;
import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UserSettings;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.SpinnerRow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterface} interface
 * to handle interaction events.
 * Use the {@link AppSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppSettingsFragment extends Fragment {
    private static final String ARG_PARAM_USER = "user";

    private UserSettings userSettings;
    private UserInterface userInterface;
    private LinearLayout fieldsContainer;
    private SpinnerRow coordinateFormatRow;
    private Tracker tracker;

    public AppSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userSettings
     * @return A new instance of fragment AppSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AppSettingsFragment newInstance(UserSettings userSettings) {
        AppSettingsFragment fragment = new AppSettingsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_app_settings, container, false);

        fieldsContainer = rootView.findViewById(R.id.app_settings_fragment_fields_container);

        generateAndPopulateFields();
        setHasOptionsMenu(true);

        return rootView;
    }

    private void generateAndPopulateFields() {
        coordinateFormatRow = new SpinnerRow(getContext(), getString(R.string.coordinate_format), CoordinateFormat.getValues());

        fieldsContainer.addView(coordinateFormatRow.getView());

        if (userSettings != null) {
            ArrayAdapter<String> coordinateFormatRowAdapter = coordinateFormatRow.getAdapter();
            coordinateFormatRow.setSelectedSpinnerItem(coordinateFormatRowAdapter.getPosition(userSettings.getCoordinateFormat() != null ? userSettings.getCoordinateFormat().toString() : CoordinateFormat.DEGREES_MINUTES_SECONDS.toString()));
        }
    }

    private void validateFieldsAndUpdateAppSettings() {
        userSettings.setCoordinateFormat(CoordinateFormat.createFromValue(coordinateFormatRow.getCurrentSpinnerItem()));

        userInterface.updateUserSettings(userSettings);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_app_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_app_settings:
                validateFieldsAndUpdateAppSettings();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserInterface) {
            userInterface = (UserInterface) getActivity();
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
        String title = getResources().getString(R.string.app_settings_fragment_title);
        activity.refreshTitle(title);
    }
}

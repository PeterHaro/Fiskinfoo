package fiskinfoo.no.sintef.fiskinfoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.Material.ButtonRectangle;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterToolsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterToolsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_PARAM = "user";
    private User user;

    private OnFragmentInteractionListener mListener;

    public static final String TAG = "REGTOOLSF";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user The user instance.
     * @return A new instance of fragment RegisterToolsFragment.
     */
    public static RegisterToolsFragment newInstance(User user) {
        RegisterToolsFragment fragment = new RegisterToolsFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_PARAM, user);
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterToolsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_register_tools, container, false);
        final DialogInterface dialogInterface = new UtilityDialogs();
        final TextView headerDate = (TextView) rootView.findViewById(R.id.catch_log_header_date_field);
        final String currentDate = getCurrentDate();
        headerDate.setText(currentDate);
        TextView dateTime = (TextView) rootView.findViewById(R.id.catch_log_date_time);
        dateTime.setText(getCurrentDateTime());

        //Arrow logic
        final Button arrowRightButton = (Button) rootView.findViewById(R.id.catch_log_header_arrow_right);
        setupRightArrowButton(headerDate, currentDate, arrowRightButton);

        Button arrowLeftButton = (Button) rootView.findViewById(R.id.catch_log_header_arrow_left);
        setupLeftArrowButton(headerDate, currentDate, arrowRightButton, arrowLeftButton);

        final Button dateButton = (Button) rootView.findViewById(R.id.catch_log_calendar_picker);
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new DatePickerFragment(headerDate);
                dateFragment.show(getFragmentManager(), "datePicker");
            }
        });

        setupTextChangedListenerOnHeaderDate(headerDate, currentDate, arrowRightButton);

        //BUTTONS
        ButtonRectangle registerNewToolButton = (ButtonRectangle) rootView.findViewById(R.id.register_tool_set_tool);
        registerNewToolButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_register_new_tool, R.string.start_new_tool_dialog_title);
                dialog.show();

            }
        });

        return rootView;
    }

    private void setupTextChangedListenerOnHeaderDate(final TextView headerDate, final String currentDate, final Button arrowRightButton) {
        headerDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //generateDiaryTableDataOnCurrentlySelectedDate(rootView, headerDate, haulLogSpecies, speciesHeader, haulLogLayout);
                if (!headerDate.getText().toString().equals(currentDate)) {
                    arrowRightButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupRightArrowButton(final TextView headerDate, final String currentDate, final Button arrowRightButton) {
        arrowRightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (headerDate.getText().toString().equals(currentDate)) {
                    arrowRightButton.setVisibility(View.INVISIBLE);
                } else {
                    String next = user.getToolLog().myLog.higherKey(headerDate.getText().toString());
                    if (next != null) {
                        headerDate.setText(next);
                        if (headerDate.getText().toString().equals(currentDate)) {
                            arrowRightButton.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        headerDate.setText(currentDate);
                        arrowRightButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        if (headerDate.getText().toString().equals(currentDate)) {
            if (arrowRightButton.getVisibility() == View.VISIBLE) {
                arrowRightButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupLeftArrowButton(final TextView headerDate, final String currentDate, final Button arrowRightButton, Button arrowLeftButton) {
        arrowLeftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (headerDate.toString().equals(currentDate)) {
                    if (user.getToolLog().myLog.isEmpty()) {
                        Log.d(TAG, "Decrementing date by one day, because the haul log is empty");
                        decrementDateByOneInHeader(headerDate.getText().toString(), headerDate);
                    } else {
                        String currentKey = user.getToolLog().myLog.lastKey();
                        if (currentKey.equals(currentDate)) {
                            // Skip
                            currentKey = user.getToolLog().myLog.lowerKey(currentKey);
                            if (currentKey != null) {
                                headerDate.setText(currentKey);
                            } else {
                                decrementDateByOneInHeader(headerDate.getText().toString(), headerDate);
                            }
                        }
                    }
                } else {
                    String currentKey = headerDate.getText().toString();
                    currentKey = user.getToolLog().myLog.lowerKey(currentKey);
                    if (currentKey != null) {
                        headerDate.setText(currentKey);
                    } else {
                        decrementDateByOneInHeader(headerDate.getText().toString(), headerDate);
                    }

                }

                if (arrowRightButton.getVisibility() != View.VISIBLE && !headerDate.getText().toString().equals(currentDate)) {
                    arrowRightButton.setVisibility(View.VISIBLE);
                } else {
                    if (headerDate.getText().toString().equals(currentDate)) {
                        arrowRightButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
            private void decrementDateByOneInHeader(String currentKey, final TextView headerDate) {
                final int DECREMENT_VALUE = -1;
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date selectedDate = null;
                try {
                    selectedDate = sdf.parse(currentKey);
                } catch (ParseException e) {
                    Log.d(TAG, "Could not parse the given date in decrementByOneInHeader");
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                cal.setTime(selectedDate);
                cal.add(Calendar.DATE, DECREMENT_VALUE);
                Date updatedDate = cal.getTime();
                String mDate = sdf.format(updatedDate);
                headerDate.setText(mDate);
            }
        });
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private String getCurrentDate() {
        String time = getCurrentDateTime();
        return time.split("\\s+")[0];
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    @SuppressLint("ValidFragment")
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        TextView mTextView;

        @SuppressLint("ValidFragment")
        public DatePickerFragment(TextView tx) {
            mTextView = tx;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog d = new DatePickerDialog(getActivity(), this, year, month, day);
            DatePicker dp = d.getDatePicker();
            dp.setMaxDate(new Date().getTime());  //Deprecated 5.0
            return d;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String date = sdf.format(c.getTime());
            String today = sdf.format(new Date());

            if (FiskInfoUtility.compareDates(date, today, "dd.MM.yyyy") > 0 ) {
                Toast.makeText(getActivity(), "Kan ikke sette redskaper frem i tiden", Toast.LENGTH_LONG).show();
            } else {
                mTextView.setText(date);
            }
        }
    }

}

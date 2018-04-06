package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.DefaultCardViewViewHolder;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.MediumRecyclerCardViewAdapter;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.SummaryViewItem;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

    public static final String FRAGMENT_TAG = "SummaryFragment";

    private User user;
    private Tracker tracker;
    private UserInterface userInterface;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;


    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        user = userInterface.getUser();
        //setHasOptionsMenu(true);
/*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_summary, container, false);


        mRecyclerView = (RecyclerView) v.findViewById(R.id.summary_recycler_view);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
        mListener = null;
        user = null;
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
        String title = getResources().getString(R.string.summary_fragment_title);
        activity.refreshTitle(title);

        final MediumRecyclerCardViewAdapter adapter = new MediumRecyclerCardViewAdapter(getSummaryItems(), false);
        adapter.setOnItemClickListener(new MediumRecyclerCardViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                adapter.getItem(position).getPositiveActionButtonListener().onClick(v);
            }
        });
        mRecyclerView.setAdapter(adapter);

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSummaryInteraction(int menuItemID);
    }




    private List<DefaultCardViewViewHolder> getSummaryItems() {
        List<DefaultCardViewViewHolder> list = new ArrayList<>();

        // TODO: Add cards to list
        addProfileSummary(list);
        addToolsSummary(list);
        addSubscriptionsSummary(list);
        addMapSummary(list);

        return list;
    }

    private void addProfileSummary(List<DefaultCardViewViewHolder> list) {
        SummaryViewItem item = new SummaryViewItem("Profil", "", "Husk å oppdatere", "");
        item.setPositiveActionButtonText("Edit profile");
        item.setPositiveButtonOnClickListener(getProfilePositiveButtonOnClickListener());
        list.add(item);
    }

    private void addToolsSummary(List<DefaultCardViewViewHolder> list) {
        SummaryViewItem item = new SummaryViewItem("Mine redskap", "", "Husk å oppdatere", "");
        item.setPositiveActionButtonText("Se mine redskap");
        item.setPositiveButtonOnClickListener(getToolsPositiveButtonOnClickListener());
        list.add(item);
    }

    private void addSubscriptionsSummary(List<DefaultCardViewViewHolder> list) {
        SummaryViewItem item = new SummaryViewItem("Abbonement", "", "Husk å oppdatere", "");
        item.setPositiveActionButtonText("Se abbonement");
        item.setPositiveButtonOnClickListener(getSubscriptionsPositiveButtonOnClickListener());
        list.add(item);
    }

    private void addMapSummary(List<DefaultCardViewViewHolder> list) {
        SummaryViewItem item = new SummaryViewItem("Kart", "", "Husk å oppdatere", "");
        item.setPositiveActionButtonText("Se kart");
        item.setPositiveButtonOnClickListener(getMapPositiveButtonOnClickListener());
        list.add(item);
    }

    @NonNull
    private View.OnClickListener getProfilePositiveButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_settings);
            }
        };
    }

    @NonNull
    private View.OnClickListener getToolsPositiveButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_tools);
            }
        };
    }

    @NonNull
    private View.OnClickListener getSubscriptionsPositiveButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_subscriptions);
            }
        };
    }

    @NonNull
    private View.OnClickListener getMapPositiveButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_map);
            }
        };
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;


    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragment.
     *
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

*/
/*

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
*/


}

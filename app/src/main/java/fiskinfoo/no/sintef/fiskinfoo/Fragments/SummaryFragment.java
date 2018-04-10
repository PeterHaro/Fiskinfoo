package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.DefaultCardViewViewHolder;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.MediumRecyclerIconCardViewAdapter;
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


    int unsentTools = 0;
    int activeTools = 0;
    int archivedTools = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        user = userInterface.getUser();
        unsentTools = 0;
        activeTools = 0;
        archivedTools = 0;
        final List<ArrayList<ToolEntry>> tools = new ArrayList(user.getToolLog().myLog.values());
        for (ArrayList<ToolEntry> entryList : tools) {
            for (ToolEntry tool : entryList) {
                switch (tool.getToolStatus()) {
                    case STATUS_UNSENT:
                        unsentTools++;
                        break;
                    case STATUS_RECEIVED:
                    case STATUS_UNREPORTED:
                    case STATUS_SENT_UNCONFIRMED:
                    case STATUS_TOOL_LOST_UNSENT:
                        activeTools++;
                        break;
                    case STATUS_REMOVED:
                    case STATUS_REMOVED_UNCONFIRMED:
                    case STATUS_TOOL_LOST_CONFIRMED:
                    case STATUS_TOOL_LOST_UNREPORTED:
                    case STATUS_TOOL_LOST_UNCONFIRMED:
                        archivedTools++;
                        break;
                    default:
                        break;
                }
            }
        }


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

        final MediumRecyclerIconCardViewAdapter adapter = new MediumRecyclerIconCardViewAdapter(getSummaryItems(), false);
        adapter.setOnItemClickListener(new MediumRecyclerIconCardViewAdapter
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
        void onSummaryInteraction(int menuItemID);
        void onNewTool();
    }


    private List<DefaultCardViewViewHolder> getSummaryItems() {
        List<DefaultCardViewViewHolder> list = new ArrayList<>();

        addProfileSummary(list);
        addToolsSummary(list);
        //addSubscriptionsSummary(list);
        addMapSummary(list);

        return list;
    }

    private void addProfileSummary(List<DefaultCardViewViewHolder> list) {
        SummaryViewItem item = new SummaryViewItem(getString(R.string.summary_user_profile), getString(R.string.summary_user_profile_subtitle), getString(R.string.summary_user_profile_incomplete), "", R.drawable.ic_account_circle_black_24dp);
        // TODO: Add check on whether profile needs update
        item.setPositiveActionButtonText(getString(R.string.summary_button_change_profile));
        item.setPositiveButtonOnClickListener(getChangeProfileOnClickListener());
        list.add(item);
    }

    private void addToolsSummary(List<DefaultCardViewViewHolder> list) {
        SummaryViewItem item = new SummaryViewItem(getString(R.string.summart_tools_title), getString(R.string.summary_tools_subtitle),getString(R.string.summary_tools_status_new) + unsentTools + "  " + getString(R.string.summary_tools_status_active) + activeTools + "  " + getString(R.string.summary_tools_status_archived) + archivedTools,  "", R.drawable.ic_directions_boat_black_48dp);
        item.setPositiveActionButtonText(getString(R.string.summary_button_view_tools));
        item.setPositiveButtonOnClickListener(getViewToolsOnClickListener());
        item.setNegativeActionButtonText(getString(R.string.summary_button_new_tool));
        item.setNegativeActionButtonOnClickListener(getNewToolsOnClickListener());
        list.add(item);
    }

    private void addMapSummary(List<DefaultCardViewViewHolder> list) {
        SummaryViewItem item = new SummaryViewItem(getString(R.string.summary_map_title), getString(R.string.summary_map_subtitle), "", "", R.drawable.ic_map_black_24dp);
        item.setPositiveActionButtonText(getString(R.string.summary_button_view_map));
        item.setPositiveButtonOnClickListener(getViewMapOnClickListener());
        item.setNegativeActionButtonText(getString(R.string.summary_button_download_map));
        item.setNegativeActionButtonOnClickListener(getViewSubscriptionsOnClickListener());
        list.add(item);
    }

    @NonNull
    private View.OnClickListener getChangeProfileOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_settings);
            }
        };
    }

    @NonNull
    private View.OnClickListener getViewToolsOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_tools);
            }
        };
    }

    @NonNull
    private View.OnClickListener getNewToolsOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onNewTool();
            }
        };
    }

    @NonNull
    private View.OnClickListener getViewSubscriptionsOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_subscriptions);
            }
        };
    }

    @NonNull
    private View.OnClickListener getViewMapOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSummaryInteraction(R.id.navigation_view_map);
            }
        };
    }

}

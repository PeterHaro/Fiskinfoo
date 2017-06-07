package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntry;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ToolEntryStatus;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.ToolLogRow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterface} interface
 * to handle interaction events.
 * Use the {@link ArchivedToolsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArchivedToolsFragment extends Fragment {
    private UserInterface userInterface;
    private LinearLayout toolsContainer;

    public ArchivedToolsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ArchivedToolsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArchivedToolsFragment newInstance() {
        ArchivedToolsFragment fragment = new ArchivedToolsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_archived_tools, container, false);

        toolsContainer = (LinearLayout) rootView.findViewById(R.id.archived_tools_container);
        User user = userInterface.getUser();

        final List<ArrayList<ToolEntry>> tools = new ArrayList(user.getToolLog().myLog.values());

        for(final List<ToolEntry> dateEntry : tools) {
            for(final ToolEntry toolEntry : dateEntry) {
                if(!(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_REMOVED) && !(toolEntry.getToolStatus() == ToolEntryStatus.STATUS_TOOL_LOST_CONFIRMED)) {
                    continue;
                }

                View.OnClickListener onClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        EditToolFragment fragment = EditToolFragment.newInstance(toolEntry);

                        fragmentManager.beginTransaction()
                                .replace(R.id.main_activity_fragment_container, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(getString(R.string.edit_tool_fragment_edit_title))
                                .commit();
                    }
                };

                ToolLogRow row = new ToolLogRow(getActivity(), toolEntry, onClickListener);
                row.getView().setTag(toolEntry.getToolId());
                toolsContainer.addView(row.getView());
            }
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof UserInterface) {
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
}

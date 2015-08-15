package fiskinfoo.no.sintef.fiskinfoo;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ExpandableListChildObject;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ExpandableListParentObject;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.MyPageExpandableListAdapter;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ExpandCollapseListener;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ParentObject;

/**
 * TODO: Retain instance on orientation (Alot of work)
 */
public class MyPageFragment extends Fragment implements ExpandCollapseListener {
    FragmentActivity listener;
    public static final String TAG = "MyPageFragment";
    private static final String CHILD_TEXT = "Child ";
    private static final String SECOND_CHILD_TEXT = "_2";
    private static final String PARENT_TEXT = "Parent ";
    private User user;
    private MyPageExpandableListAdapter myPageExpandableListAdapter;
    private HorribleHackOnClickListener childOnClickListener;
    private RecyclerView mCRecyclerView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (FragmentActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
        childOnClickListener = new HorribleHackOnClickListener();
    }


    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View v =  inf.inflate(R.layout.fragment_my_page, parent, false);
        ArrayList<ParentObject> data = fetchMyPage();
        myPageExpandableListAdapter = new MyPageExpandableListAdapter(this.getActivity(), data, childOnClickListener);
        myPageExpandableListAdapter.addExpandCollapseListener(this);
        myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
        mCRecyclerView = (RecyclerView)v.findViewById(R.id.recycle_test_view);
        mCRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mCRecyclerView.setAdapter(myPageExpandableListAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            Log.d(TAG, savedInstanceState.toString());
        }

        myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null) {
            Log.d(TAG, outState.toString());
        }
        ((MyPageExpandableListAdapter) mCRecyclerView.getAdapter()).onSaveInstanceState(outState);
    }

    public ArrayList<ParentObject> fetchMyPage() {
        BarentswatchApi barentswatchApi = new BarentswatchApi();
        barentswatchApi.setAccesToken(user.getToken());
        IBarentswatchApi api = barentswatchApi.getApi();
        ArrayList<ParentObject> parentObjectList = new ArrayList<>();
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            List<PropertyDescription> availableSubscriptions = api.getSubscribable();
            List<String> myWarnings = new ArrayList<>(); //TODO: Tie in polar low warning
            List<Subscription> personalMaps = api.getSubscriptions();

            ArrayList<Object> propertyDescriptionChildObjectList = new ArrayList<>();
            for(PropertyDescription propertyDescription : availableSubscriptions) {
                ExpandableListChildObject currentPropertyDescriptionChildObject = new ExpandableListChildObject();
                currentPropertyDescriptionChildObject.setChildText(propertyDescription.Name);
                propertyDescriptionChildObjectList.add(currentPropertyDescriptionChildObject);
            }

            ArrayList<Object> warningServiceChildObjectList = new ArrayList<>();
            for(String s : myWarnings) {
                ExpandableListChildObject currentWarningObject = new ExpandableListChildObject();
                currentWarningObject.setChildText(s);
                warningServiceChildObjectList.add(currentWarningObject);
            }

            ArrayList<Object> mySubscriptions = new ArrayList<>();
            for(Subscription s : personalMaps) {
                ExpandableListChildObject currentSubscription = new ExpandableListChildObject();
                currentSubscription.setChildText(s.GeoDataServiceName);
                mySubscriptions.add(currentSubscription);
            }


            ExpandableListParentObject propertyDescriptionParent = new ExpandableListParentObject();
            ExpandableListParentObject warningParent = new ExpandableListParentObject();
            ExpandableListParentObject subscriptionParent = new ExpandableListParentObject();


            propertyDescriptionParent.setChildObjectList(propertyDescriptionChildObjectList);
            propertyDescriptionParent.setParentNumber(1);
            propertyDescriptionParent.setParentText(getString(R.string.my_page_all_available_subscriptions));
            propertyDescriptionParent.setResourcePathToImageResource(R.drawable.ikon_kart_til_din_kartplotter);

            warningParent.setChildObjectList(warningServiceChildObjectList);
            warningParent.setParentNumber(2);
            warningParent.setParentText(getString(R.string.my_page_all_warnings));
            warningParent.setResourcePathToImageResource(R.drawable.ikon_varsling_av_polare_lavtrykk);

            subscriptionParent.setChildObjectList(mySubscriptions);
            subscriptionParent.setParentNumber(3);
            subscriptionParent.setParentText(getString(R.string.my_page_my_subscriptions));
            subscriptionParent.setResourcePathToImageResource(R.drawable.ikon_kart_til_din_kartplotter);

            parentObjectList.add(propertyDescriptionParent);
            parentObjectList.add(warningParent);
            parentObjectList.add(subscriptionParent);

            childOnClickListener.setSubscriptions(personalMaps);
            childOnClickListener.setWarnings(myWarnings);
            childOnClickListener.setPropertyDescriptions(availableSubscriptions);

        } catch(Exception e) {
            Log.d(TAG, "Exception occured: " + e.toString());
        }

        return parentObjectList;
    }


    /**
     * Method to set up test data used in the RecyclerView.
     * <p/>
     * Each child object contains a string.
     * Each parent object contains a number corresponding to the number of the parent and a string
     * that contains a message.
     * Each parent also contains a list of children which is generated in this. Every odd numbered
     * parent gets one child and every even numbered parent gets two children.
     *
     * @param numItems
     * @return an ArrayList of Objects that contains all parent items. Expansion of children are handled in the adapter
     */
    private ArrayList<ParentObject> setUpTestData(int numItems) {
        ArrayList<ParentObject> parentObjectList = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            ArrayList<Object> childObjectList = new ArrayList<>();

            // Evens get 2 children, odds get 1
            if (i % 2 == 0) {
                ExpandableListChildObject customChildObject = new ExpandableListChildObject();
                ExpandableListChildObject customChildObject2 = new ExpandableListChildObject();
                customChildObject.setChildText(CHILD_TEXT + i);
                customChildObject2.setChildText(CHILD_TEXT + i + SECOND_CHILD_TEXT);
                childObjectList.add(customChildObject);
                childObjectList.add(customChildObject2);
            } else {
                ExpandableListChildObject customChildObject = new ExpandableListChildObject();
                customChildObject.setChildText(CHILD_TEXT + i);
                childObjectList.add(customChildObject);
            }

            ExpandableListParentObject customParentObject = new ExpandableListParentObject();
            customParentObject.setChildObjectList(childObjectList);
            customParentObject.setParentNumber(i);
            customParentObject.setParentText(PARENT_TEXT + i);
            parentObjectList.add(customParentObject);
        }
        return parentObjectList;
    }

    @Override
    public void onRecyclerViewItemExpanded(int position) {
        Toast.makeText(this.getActivity(), "Item Expanded " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecyclerViewItemCollapsed(int position) {
        Toast.makeText(this.getActivity(), "Item Collapsed " + position, Toast.LENGTH_SHORT).show();
    }



    private class HorribleHackOnClickListener implements View.OnClickListener {
        List<PropertyDescription> propertyDescriptions;
        List<String> warnings;
        List<Subscription> subscriptions;

        public HorribleHackOnClickListener() {

        }

        public void setPropertyDescriptions(List<PropertyDescription> propertyDescriptions) {
            this.propertyDescriptions = propertyDescriptions;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }

        public void setSubscriptions(List<Subscription> subscriptions) {
            this.subscriptions = subscriptions;
        }

        @Override
        public void onClick(View v) {
            String identifier = ((TextView)v).getText().toString();
            boolean found = false;
            JsonObject object = null;
            Gson gson = new Gson();
            String type = "";
            for(PropertyDescription pd : propertyDescriptions) {
                if (pd.Name.equals(identifier)) {
                    found = true;
                    object = (new JsonParser()).parse(gson.toJson(pd)).getAsJsonObject();
                    type += "pd";
                    break;
                }
            }

            if(!found) {
                for(String warning : warnings) {
                    if(warning.equals(identifier)) {
                        found = true;
                        //TODO: Generate object proper
                        object = new JsonObject();
                        type += "warning";
                        break;
                    }
                }
            }
            if(!found) {
                for(Subscription subscription : subscriptions) {
                    if(subscription.GeoDataServiceName.equals(identifier)) {
                        found = true;
                        object =(new JsonParser()).parse(gson.toJson(subscription)).getAsJsonObject();
                        type += "sub";
                        break;
                    }
                }
            }

            if(object == null) {
                Log.d(TAG, "We fucked up retrieving the object: ");
            }

            getFragmentManager().beginTransaction().
                    replace(R.id.fragment_placeholder, createFragment(object, type), CardViewFragment.TAG).
                    commit();
        }
    }

    private Fragment createFragment(JsonObject object, String type) {
        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user", user);
        userBundle.putString("type", type);
        userBundle.putString("args", object.toString());
        CardViewFragment cardViewFragment = CardViewFragment.newInstance();
        cardViewFragment.setArguments(userBundle);
        return cardViewFragment;
    }



}

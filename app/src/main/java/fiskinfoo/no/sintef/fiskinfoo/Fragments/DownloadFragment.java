/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fiskinfoo.no.sintef.fiskinfoo.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.AvailableSubscriptionItem;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionEntry;
import fiskinfoo.no.sintef.fiskinfoo.FiskInfo;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.BarentswatchApiService;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.BarentswatchResultReceiver;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.DownloadDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.DownloadListAdapter;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskinfoConnectivityManager;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ParentObject;

public class DownloadFragment extends Fragment implements DownloadListAdapter.DownloadSelectionListener, BarentswatchResultReceiver.Receiver {
    public static final String FRAGMENT_TAG = "DownloadFragment";
    private static final String SCREEN_NAME = "MyPage";

    private DownloadListAdapter myPageExpandableListAdapter;
    private RecyclerView mCRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FiskInfoUtility fiskInfoUtility;
    private UserInterface userInterface;
    private FragmentActivity listener;
    private User user;
    private Tracker tracker;

    public BarentswatchResultReceiver mReceiver;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (FragmentActivity) context;

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
        this.listener = null;
        this.user = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mReceiver != null)
            mReceiver.setReceiver(this);

        if(getView() != null) {
            getView().refreshDrawableState();
        }

        if(tracker != null){

            tracker.setScreenName(getClass().getSimpleName());
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

        MainActivity activity = (MainActivity) getActivity();
        String title = getResources().getString(R.string.my_page_fragment_title);
        activity.refreshTitle(title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FiskInfo application = (FiskInfo) getActivity().getApplication();
        tracker = application.getDefaultTracker();

        fiskInfoUtility = new FiskInfoUtility();
        user = userInterface.getUser();
        setHasOptionsMenu(true);
    }

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View v = inf.inflate(R.layout.fragment_my_page, parent, false);
        ArrayList<AvailableSubscriptionItem> data;
   //     expandCollapseListener = this;
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.my_page_swipe_refresh_layout);


        mReceiver = new BarentswatchResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        data = new ArrayList<>();

        //data = fetchCachedDownloads();
        myPageExpandableListAdapter = new DownloadListAdapter(data, this); //(this.getActivity(), data, childOnClickListener);


        /*
        if(fiskInfoUtility.isNetworkAvailable(getActivity())) {
            data = fetchMyPage();
            myPageExpandableListAdapter = new DownloadListAdapter(data, this); //(this.getActivity(), data, childOnClickListener);
            //ES myPageExpandableListAdapter.addExpandCollapseListener(expandCollapseListener);
            //ES myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
        } else {
            data = fetchCachedDownloads();
            myPageExpandableListAdapter = new DownloadListAdapter(data, this); //(this.getActivity(), data, childOnClickListener);
            //ES myPageExpandableListAdapter.addExpandCollapseListener(expandCollapseListener);
            //ES myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
        }*/

        mCRecyclerView = (RecyclerView) v.findViewById(R.id.recycle_view);
        mCRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mCRecyclerView.setAdapter(myPageExpandableListAdapter);
        new FetchAndRefreshDownloadListTask().execute("");

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(FRAGMENT_TAG, savedInstanceState.toString());

            //ES myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
            Parcelable manager = savedInstanceState.getParcelable("recycleLayout");
            LinearLayoutManager manager1 = new LinearLayoutManager(this.getActivity());
            if(manager != null) {
                mCRecyclerView.setLayoutManager(manager1);
                manager1.onRestoreInstanceState(manager);
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchAndRefreshDownloadListTask().execute("");
            }
        });

        ((MainActivity)getActivity()).toggleNetworkErrorTextView(fiskInfoUtility.isNetworkAvailable(getActivity()));
    }


    private class FetchAndRefreshDownloadListTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            return FiskinfoConnectivityManager.hasValidNetworkConnection(getContext());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            fetchAndRefreshDownloadList(result);
        }
    }



    protected void fetchAndRefreshDownloadList(boolean hasNetwork) {
        if (hasNetwork) {
            BarentswatchApiService.startActionFetchDownloads(getContext(), mReceiver, user);
        } else {
            ArrayList<AvailableSubscriptionItem> data = fetchCachedDownloads();
            myPageExpandableListAdapter = new DownloadListAdapter(data, DownloadFragment.this);
            mCRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCRecyclerView.setAdapter(myPageExpandableListAdapter);
            ((MainActivity) getActivity()).toggleNetworkErrorTextView(false);
        }
        mSwipeRefreshLayout.setRefreshing(false);

    }


    @Override
    public void onPause() {
        super.onPause();
        mReceiver.setReceiver(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            Log.d(FRAGMENT_TAG, outState.toString());
        }

        if(mCRecyclerView != null && mCRecyclerView.getAdapter() != null) {
            //TODO: Fix on save instance ((MyPageExpandableListAdapter) mCRecyclerView.getAdapter()).onSaveInstanceState(outState);
            Parcelable layoutState = mCRecyclerView.getLayoutManager().onSaveInstanceState();
            if(outState != null) {
                outState.putParcelable("recycleLayout", layoutState);
            }
        }

    }


    protected void refreshDownloadList( List<PropertyDescription> availableSubscriptions, List<Subscription> currentSubscriptions, List<Authorization> authorizations) {
        ArrayList<AvailableSubscriptionItem> availableSubscriptionItemList = new ArrayList<>();
        ArrayList<ParentObject> parentObjectList = new ArrayList<>();
        try {
            SparseBooleanArray authMap = new SparseBooleanArray();
            Map<String, PropertyDescription> availableSubscriptionsMap = new HashMap<>();
            Map<String, Subscription> activeSubscriptionsMap = new HashMap<>();

            for(Authorization auth : authorizations) {
                authMap.put(auth.Id, auth.HasAccess);
            }

            for(Subscription subscription : currentSubscriptions) {
                activeSubscriptionsMap.put(subscription.GeoDataServiceName, subscription);
            }

            for(PropertyDescription subscribable : availableSubscriptions) {
                availableSubscriptionsMap.put(subscribable.ApiName, subscribable);
                SubscriptionEntry currentEntry = user.getSubscriptionCacheEntry(subscribable.ApiName);

                if(currentEntry == null) {
                    SubscriptionEntry entry = activeSubscriptionsMap.get(subscribable.ApiName) == null ? new SubscriptionEntry(subscribable, authMap.get(subscribable.Id)) :
                            new SubscriptionEntry(subscribable, activeSubscriptionsMap.get(subscribable.ApiName), authMap.get(subscribable.Id));
                    entry.mIsAuthorized = authMap.get(subscribable.Id);
                    user.setSubscriptionCacheEntry(subscribable.ApiName, entry);
                } else {
                    currentEntry.mSubscribable = subscribable;
                    currentEntry.mSubscription = activeSubscriptionsMap.get(subscribable.ApiName);
                    currentEntry.mIsAuthorized = authMap.get(subscribable.Id);

                    user.setSubscriptionCacheEntry(subscribable.ApiName, currentEntry);
                }
            }

            // Check and set access to fishingfacility data so we know this when loading the map later.
            user.setIsFishingFacilityAuthenticated(authMap.get(availableSubscriptionsMap.containsKey(getString(R.string.fishing_facility_api_name)) ? availableSubscriptionsMap.get(getString(R.string.fishing_facility_api_name)).Id : -1));
            user.writeToSharedPref(getActivity());


            for (final PropertyDescription propertyDescription : availableSubscriptions) {
                boolean isAuthed = authMap.get(propertyDescription.Id);
                AvailableSubscriptionItem currentItem = setupAvailableSubscriptionItem(propertyDescription, activeSubscriptionsMap.get(propertyDescription.ApiName), isAuthed);
                availableSubscriptionItemList.add(currentItem);
            }

            myPageExpandableListAdapter = new DownloadListAdapter(availableSubscriptionItemList, DownloadFragment.this);
            mCRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mCRecyclerView.setAdapter(myPageExpandableListAdapter);
            ((MainActivity) getActivity()).toggleNetworkErrorTextView(true);

        } catch (Exception e) {
            Log.d(FRAGMENT_TAG, "Exception occured: " + e.toString());
            ((MainActivity) getActivity()).toggleNetworkErrorTextView(false);
        }

    }


    public ArrayList<AvailableSubscriptionItem> fetchCachedDownloads() {
        ArrayList<AvailableSubscriptionItem> availableSubscriptionItemList = new ArrayList<>();
        try {
            Collection<SubscriptionEntry> subscriptionEntries = user.getSubscriptionCacheEntries();

            Map<String, Subscription> activeSubscriptionsMap = new HashMap<>();
            List<PropertyDescription> availableSubscriptions = new ArrayList<>();
            SparseBooleanArray authMap = new SparseBooleanArray();

            for(SubscriptionEntry subscriptionEntry : subscriptionEntries) {
                availableSubscriptions.add(subscriptionEntry.mSubscribable);
                if(subscriptionEntry.mSubscription != null) {
                    activeSubscriptionsMap.put(subscriptionEntry.mName, subscriptionEntry.mSubscription);
                }
                authMap.put(subscriptionEntry.mSubscribable.Id, subscriptionEntry.mIsAuthorized);
            }

            for (final PropertyDescription propertyDescription : availableSubscriptions) {
                boolean isAuthed = authMap.get(propertyDescription.Id);
                AvailableSubscriptionItem currentItem = setupAvailableSubscriptionItem(propertyDescription, activeSubscriptionsMap.get(propertyDescription.ApiName), isAuthed);
                availableSubscriptionItemList.add(currentItem);
            }

        } catch (Exception e) {
            Log.d(FRAGMENT_TAG, "Exception occured: " + e.toString());
        }

        //return parentObjectList;
        return availableSubscriptionItemList;
    }

    private AvailableSubscriptionItem setupAvailableSubscriptionItem(final PropertyDescription subscription, final Subscription activeSubscription, boolean canSubscribe) {

        // Need this check because not being authorized for the tools layer does not prevent subscribing or downloading, only the level of details available.
        if(!canSubscribe && subscription.ApiName.equals(getString(R.string.fishing_facility_api_name))) {
            subscription.ErrorType = ApiErrorType.WARNING.toString();
            subscription.ErrorText = getString(R.string.fishing_facility_limited_details);
        }

        String tmpUpdatedTime = subscription.LastUpdated == null ? (subscription.Created == null ? getString(R.string.abbreviation_na) : subscription.Created) : subscription.LastUpdated;

        final AvailableSubscriptionItem item = new AvailableSubscriptionItem();
        item.setTitle(subscription.Name);
        item.setLastUpdated(tmpUpdatedTime.replace("T", "\n"));
        item.setSubscribed(activeSubscription != null);
        item.setAuthorized(canSubscribe);
        item.setPropertyDescription(subscription);
        item.setSubscription(activeSubscription);

        item.setErrorType(ApiErrorType.getType(subscription.ErrorType));

        return item;
    }

    @Override
    public void onTitleClicked(AvailableSubscriptionItem item) {
        JsonObject object = null;
        Gson gson = new Gson();
        String type = "pd";
        if ((item != null) && (item.getPropertyDescription() != null)) {
            object = (new JsonParser()).parse(gson.toJson(item.getPropertyDescription())).getAsJsonObject();
            getFragmentManager().beginTransaction().
                    replace(R.id.main_activity_fragment_container, createFragment(object, type), SubscriptionDetailsFragment.TAG).addToBackStack(null).
                    commit();
        }
    }

    @Override
    public void onDownloadButton(AvailableSubscriptionItem item) {
        if (item.isAuthorized() || item.getPropertyDescription().ApiName.equals(getString(R.string.fishing_facility_api_name))) {
            DownloadDialogs.showSubscriptionDownloadDialog(getContext(), getActivity(), item.getPropertyDescription(), user, FRAGMENT_TAG, tracker, SCREEN_NAME);
        } else {
            DownloadDialogs.showInformationDialog(getContext(), item.getPropertyDescription().Name, getString(R.string.unauthorized_user));
        }
    }

    @Override
    public void onSubscribeClicked(CheckBox checkBox, AvailableSubscriptionItem item) {
        if (item.isAuthorized() || item.getPropertyDescription().ApiName.equals(getString(R.string.fishing_facility_api_name))) {
            DownloadDialogs.doSubscriptionCheckBoxAction(checkBox, mReceiver, getContext(), item.getPropertyDescription(), item.getSubscription(), user);
        }
    }

    @Override
    public void onErrorNotificationClicked(AvailableSubscriptionItem item) {
        PropertyDescription prop = item.getPropertyDescription();

        if (prop != null) {
            if (item.getErrorType().equals(ApiErrorType.WARNING.toString())) {
                DownloadDialogs.showInformationDialog(getContext(), prop.Name, prop.ErrorText);
            } else {
                DownloadDialogs.showSubscriptionErrorNotification(getContext(), prop);
            }
        }
    }

    private Fragment createFragment(JsonObject object, String type) {
        Bundle userBundle = new Bundle();
        userBundle.putParcelable("user", user);
        userBundle.putString("type", type);
        userBundle.putString("args", object.toString());
        SubscriptionDetailsFragment fragment = SubscriptionDetailsFragment.newInstance();
        fragment.setArguments(userBundle);
        return fragment;
    }

    @Override
    public void onBarentswatchResultReceived(int resultCode, Bundle resultData) {
        if (resultCode == BarentswatchApiService.RESULT_CODE_FETCH_DOWNLOADS_SUCCESS) {
            List<PropertyDescription> availableSubscriptions = resultData.getParcelableArrayList(BarentswatchApiService.RESULT_PARAM_SUBSCRIPTION);
            List<Subscription> currentSubscriptions = resultData.getParcelableArrayList(BarentswatchApiService.RESULT_PARAM_CURRENTSUBSCRIPTION);
            List<Authorization> authorizations = resultData.getParcelableArrayList(BarentswatchApiService.RESULT_PARAM_AUTHORIZATION);

            refreshDownloadList(availableSubscriptions, currentSubscriptions, authorizations);
        } else if ((resultCode == BarentswatchApiService.RESULT_CODE_SUBSCRIPTION_UPDATE_SUCCESS) ||
                (resultCode == BarentswatchApiService.RESULT_CODE_SUBSCRIPTION_DELETE_SUCCESS)) {
            Toast.makeText(getContext(), R.string.subscription_update_successful, Toast.LENGTH_LONG).show();
            new FetchAndRefreshDownloadListTask().execute("");

        } else if ((resultCode == BarentswatchApiService.RESULT_CODE_SUBSCRIPTION_UPDATE_FAILED) ||
                (resultCode == BarentswatchApiService.RESULT_CODE_SUBSCRIPTION_DELETE_FAILED)) {
            Toast.makeText(getContext(), R.string.subscription_update_failed, Toast.LENGTH_LONG).show();
            new FetchAndRefreshDownloadListTask().execute("");
        }

    }

}

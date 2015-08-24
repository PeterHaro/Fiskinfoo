package fiskinfoo.no.sintef.fiskinfoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ExpandableListParentObject;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionExpandableListChildObject;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.MyPageExpandableListAdapter;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.FormatRow;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ExpandCollapseListener;
import fiskinfoo.no.sintef.fiskinfoo.View.MaterialExpandableList.ParentObject;
import retrofit.client.Response;

/**
 * TODO: Retain instance on orientation (Alot of work)
 */
public class MyPageFragment extends Fragment implements ExpandCollapseListener {
    FragmentActivity listener;
    public static final String TAG = "MyPageFragment";
    private User user;
    private MyPageExpandableListAdapter myPageExpandableListAdapter;
    private ExpandableListAdapterChildOnClickListener childOnClickListener;
    private RecyclerView mCRecyclerView;
    private DialogInterface dialogInterface;
    private UtilityRowsInterface utilityRowsInterface;
    private UtilityOnClickListeners onClickListenerInterface;
    private FiskInfoUtility fiskInfoUtility;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (FragmentActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
        childOnClickListener = new ExpandableListAdapterChildOnClickListener();
        dialogInterface = new UtilityDialogs();
        utilityRowsInterface = new UtilityRows();
        onClickListenerInterface = new UtilityOnClickListeners();
        fiskInfoUtility = new FiskInfoUtility();
    }


    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View v = inf.inflate(R.layout.fragment_my_page, parent, false);
        ArrayList<ParentObject> data = fetchMyPage();
        myPageExpandableListAdapter = new MyPageExpandableListAdapter(this.getActivity(), data, childOnClickListener);
        myPageExpandableListAdapter.addExpandCollapseListener(this);
        myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
        mCRecyclerView = (RecyclerView) v.findViewById(R.id.recycle_test_view);
        mCRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mCRecyclerView.setAdapter(myPageExpandableListAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(TAG, savedInstanceState.toString());
        }

        myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            Log.d(TAG, outState.toString());
        }
        ((MyPageExpandableListAdapter) mCRecyclerView.getAdapter()).onSaveInstanceState(outState);
    }

    public ArrayList<ParentObject> fetchMyPage() {
        BarentswatchApi barentswatchApi = new BarentswatchApi();
        barentswatchApi.setAccesToken(user.getToken());
        final IBarentswatchApi api = barentswatchApi.getApi();
        ArrayList<ParentObject> parentObjectList = new ArrayList<>();
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            List<PropertyDescription> availableSubscriptions = api.getSubscribable();
            List<String> myWarnings = new ArrayList<>(); //TODO: Tie in polar low warning
            List<Subscription> personalMaps = api.getSubscriptions();

            ArrayList<Object> availableSubscriptionObjectsList = new ArrayList<>();
            for (final PropertyDescription propertyDescription : availableSubscriptions) {
                final SubscriptionExpandableListChildObject currentPropertyDescriptionChildObject = new SubscriptionExpandableListChildObject();
                currentPropertyDescriptionChildObject.setTitleText(propertyDescription.Name);
                currentPropertyDescriptionChildObject.setLastUpdatedText(propertyDescription.LastUpdated.replace("T", "\n"));

                boolean isSubscribed = false;

                for(Subscription subscription : personalMaps) {
                    if(subscription.GeoDataServiceName.equals(propertyDescription.ApiName)) {
                        isSubscribed = true;
                        break;
                    }
                }

                currentPropertyDescriptionChildObject.setIsSubscribed(isSubscribed);

                View. OnClickListener downloadButtonOnClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_format, R.string.download_map_layer_dialog_title);

                        final Button downloadButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                        Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                        final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.select_download_format_formats_container);

                        downloadButton.setOnClickListener(onClickListenerInterface.getShowToastListener(getActivity(), getString(R.string.choose_a_download_format)));

                        for (String format : propertyDescription.Formats) {
                            final FormatRow formatRow = utilityRowsInterface.getFormatRow(getActivity(), format);

                            formatRow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int i = 0; i < rowsContainer.getChildCount(); i++) {

                                        rowsContainer.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.background_white));
                                    }
                                    formatRow.setBackgroundColor(getResources().getColor(R.color.helpful_grey));

                                    downloadButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Response response;
                                            String downloadFormat = formatRow.getText();

                                            try {
                                                response = api.geoDataDownload(propertyDescription.ApiName, downloadFormat);
                                                if (response == null) {
                                                    Log.d(TAG, "RESPONSE == NULL");
                                                }
                                                byte[] fileData = FiskInfoUtility.toByteArray(response.getBody().in());
                                                if (fiskInfoUtility.isExternalStorageWritable()) {
                                                    fiskInfoUtility.writeMapLayerToExternalStorage(getActivity(), fileData, propertyDescription.Name, downloadFormat, user.getFilePathForExternalStorage());
                                                } else {
                                                    Toast.makeText(v.getContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                                                }
                                            } catch (Exception e) {
                                                Log.d(TAG, "Could not download with ApiName: " + propertyDescription.ApiName + "  and format: " + downloadFormat);
                                            }

                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                            rowsContainer.addView(formatRow.getView());
                        }

                        cancelButton.setOnClickListener(onClickListenerInterface.getDismissDialogListener(dialog));

                        dialog.show();
                    }
                };

                View.OnClickListener subscriptionSwitchClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if(((Switch)v).isChecked()) {
                            final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_format, R.string.subscribe_to_map_layer_dialog_title);

                            final Button subscribeButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                            Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                            final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.select_download_format_formats_container);

                            subscribeButton.setText(R.string.subscribe);
                            subscribeButton.setOnClickListener(onClickListenerInterface.getShowToastListener(getActivity(), getString(R.string.choose_a_download_format)));

                            for (String format : propertyDescription.Formats) {
                                final FormatRow formatRow = utilityRowsInterface.getFormatRow(getActivity(), format);

                                formatRow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View formatRowView) {
                                        for (int i = 0; i < rowsContainer.getChildCount(); i++) {

                                            rowsContainer.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.background_white));
                                        }
                                        formatRow.setBackgroundColor(getResources().getColor(R.color.helpful_grey));

                                        subscribeButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View subscribeButtonView) {

                                                Response response;
                                                String downloadFormat = formatRow.getText();

//                                                try {
//                                                      TODO: add API call to set up subscription
//                                                    }
//                                                } catch (Exception e) {
//                                                    Log.d(TAG, "Could set up subscription: " + propertyDescription.ApiName + "  and format: " + downloadFormat);
//                                                }
//
//                                                dialog.dismiss();
//                                                ((Switch)v).setChecked(true);
                                            }
                                        });
                                    }
                                });
                                rowsContainer.addView(formatRow.getView());
                            }

                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View cancelButton) {
                                    dialog.dismiss();
                                    ((Switch) v).setChecked(true);
                                }
                            });

                            dialog.show();
                        } else {
                            new AlertDialog.Builder(v.getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(propertyDescription.Name)
                                .setMessage(getString(R.string.confirm_subscription_cancelation))
                                .setPositiveButton(getString(R.string.yes), new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        Response response;

//                                        try {
//                                            TODO: API call to remove subscription
//                                            }
//                                        } catch (Exception e) {
//                                            Log.d(TAG, "Could not cancel subscription: " + propertyDescription.ApiName);
//                                        }
                                    }
                                })
                                .setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        ((Switch) v).setChecked(false);
                                    }
                                })
                                .show();
                        }
                    }
                };

                currentPropertyDescriptionChildObject.setDownloadButtonOnClickListener(downloadButtonOnClickListener);
                currentPropertyDescriptionChildObject.setSubscribeSwitchOnClickListener(subscriptionSwitchClickListener);

                availableSubscriptionObjectsList.add(currentPropertyDescriptionChildObject);
            }

            ArrayList<Object> warningServiceChildObjectList = new ArrayList<>();
            for (String s : myWarnings) {
                SubscriptionExpandableListChildObject currentWarningObject = new SubscriptionExpandableListChildObject();
                currentWarningObject.setTitleText(s);
                currentWarningObject.setLastUpdatedText("");
                currentWarningObject.setIsSubscribed(true);

                warningServiceChildObjectList.add(currentWarningObject);
            }

            ArrayList<Object> mySubscriptions = new ArrayList<>();
            for (Subscription s : personalMaps) {
                SubscriptionExpandableListChildObject currentSubscription = new SubscriptionExpandableListChildObject();
                currentSubscription.setTitleText(s.GeoDataServiceName);
                for(PropertyDescription subscription : availableSubscriptions) {
                    if(subscription.ApiName.equals(s.GeoDataServiceName)) {
                        currentSubscription.setTitleText(subscription.Name);
                        break;
                    }

                    currentSubscription.setLastUpdatedText(s.LastModified.replace("T", "\n"));
                    currentSubscription.setIsSubscribed(true);
                }

                mySubscriptions.add(currentSubscription);
            }

            ExpandableListParentObject propertyDescriptionParent = new ExpandableListParentObject();
            ExpandableListParentObject warningParent = new ExpandableListParentObject();
            ExpandableListParentObject subscriptionParent = new ExpandableListParentObject();


            propertyDescriptionParent.setChildObjectList(availableSubscriptionObjectsList);
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

        } catch (Exception e) {
            Log.d(TAG, "Exception occured: " + e.toString());
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


    private class ExpandableListAdapterChildOnClickListener implements View.OnClickListener {
        List<PropertyDescription> propertyDescriptions;
        List<String> warnings;
        List<Subscription> subscriptions;

        public ExpandableListAdapterChildOnClickListener() {

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
            String identifier = ((ViewGroup)v.getParent()).getTag().toString();
            boolean found = false;
            JsonObject object = null;
            Gson gson = new Gson();
            String type = "";
            for (PropertyDescription pd : propertyDescriptions) {
                if (pd.Name.equals(identifier)) {
                    found = true;
                    object = (new JsonParser()).parse(gson.toJson(pd)).getAsJsonObject();
                    type += "pd";
                    break;
                }
            }

            if (!found) {
                for (String warning : warnings) {
                    if (warning.equals(identifier)) {
                        found = true;
                        //TODO: Generate object proper
                        object = new JsonObject();
                        type += "warning";
                        break;
                    }
                }
            }
            if (!found) {
                for (Subscription subscription : subscriptions) {
                    if (subscription.GeoDataServiceName.equals(identifier)) {
                        found = true;
                        object = (new JsonParser()).parse(gson.toJson(subscription)).getAsJsonObject();
                        type += "sub";
                        break;
                    }
                }
            }

            if (object == null) {
                Log.d(TAG, "We failed at retrieving the object: ");
            }

            getFragmentManager().beginTransaction().
                    replace(R.id.fragment_placeholder, createFragment(object, type), CardViewFragment.TAG).addToBackStack(null).
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

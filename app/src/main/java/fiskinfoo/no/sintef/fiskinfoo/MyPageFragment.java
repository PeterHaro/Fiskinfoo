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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.ExpandableListParentObject;
import fiskinfoo.no.sintef.fiskinfoo.Baseclasses.SubscriptionExpandableListChildObject;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.FiskInfoUtility;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.MyPageExpandableListAdapter;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityDialogs;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.Interface.DialogInterface;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UtilityRowsInterface;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CheckBoxFormatRow;
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

        //myPageExpandableListAdapter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            Log.d(TAG, outState.toString());
        }
        //((MyPageExpandableListAdapter) mCRecyclerView.getAdapter()).onSaveInstanceState(outState);
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
            List<Authorization> authorizations = api.getAuthorization();

            Map<Integer, Boolean> authMap = new HashMap<Integer, Boolean>();

            for(Authorization auth : authorizations) {
                authMap.put(auth.Id, auth.hasAccess);
                System.out.println("Id: " + auth.Id + ", access: " + auth.hasAccess);
            }

            ArrayList<Object> availableSubscriptionObjectsList = new ArrayList<>();
            for (final PropertyDescription propertyDescription : availableSubscriptions) {
                //TODO: uncomment after fixing getAuthorization to read correct values.
//                if(!authMap.get(propertyDescription.Id)) {
//                    continue;
//                }

                SubscriptionExpandableListChildObject currentPropertyDescriptionChildObject = setupAvailableSubscriptionChildView(propertyDescription, personalMaps);

                availableSubscriptionObjectsList.add(currentPropertyDescriptionChildObject);
            }

            ArrayList<Object> warningServiceChildObjectList = new ArrayList<>();
            for (String warning : myWarnings) {
                SubscriptionExpandableListChildObject currentWarning = setupWarningChildView(warning);

                warningServiceChildObjectList.add(currentWarning);
            }

            ArrayList<Object> mySubscriptions = new ArrayList<>();
            for (Subscription s : personalMaps) {
                SubscriptionExpandableListChildObject currentSubscription = setupActiveSubscriptionChildView(s, availableSubscriptions);

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

    private SubscriptionExpandableListChildObject setupAvailableSubscriptionChildView(final PropertyDescription subscription, final List<Subscription> activeSubscriptions) {
        final SubscriptionExpandableListChildObject currentPropertyDescriptionChildObject = new SubscriptionExpandableListChildObject();
        currentPropertyDescriptionChildObject.setTitleText(subscription.Name);
        currentPropertyDescriptionChildObject.setLastUpdatedText(subscription.LastUpdated.replace("T", "\n"));

        boolean isSubscribed = false;

        for (Subscription activeSubscription : activeSubscriptions) {
            if (activeSubscription.GeoDataServiceName.equals(subscription.ApiName)) {
                isSubscribed = true;
                break;
            }
        }

        currentPropertyDescriptionChildObject.setIsSubscribed(isSubscribed);

        View.OnClickListener downloadButtonOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_format, R.string.download_map_layer_dialog_title);

                final Button downloadButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.select_download_format_formats_container);

                downloadButton.setOnClickListener(onClickListenerInterface.getShowToastListener(getActivity(), getString(R.string.choose_a_download_format)));

                for (String format : subscription.Formats) {
                    final FormatRow formatRow = utilityRowsInterface.getFormatRow(getActivity(), format);

                    formatRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < rowsContainer.getChildCount(); i++) {

                                rowsContainer.getChildAt(i).findViewById(R.id.format_row_text_view).setBackgroundColor(getResources().getColor(R.color.text_white));
                            }

                            formatRow.setTextViewBackgroundColor(getResources().getColor(R.color.helpful_grey));

                            downloadButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    BarentswatchApi barentswatchApi = new BarentswatchApi();
                                    barentswatchApi.setAccesToken(user.getToken());
                                    IBarentswatchApi api = barentswatchApi.getApi();

                                    Response response;
                                    String downloadFormat = formatRow.getText();

                                    try {
                                        response = api.geoDataDownload(subscription.ApiName, downloadFormat);
                                        if (response == null) {
                                            Log.d(TAG, "RESPONSE == NULL");
                                        }
                                        byte[] fileData = FiskInfoUtility.toByteArray(response.getBody().in());
                                        if (fiskInfoUtility.isExternalStorageWritable()) {
                                            fiskInfoUtility.writeMapLayerToExternalStorage(getActivity(), fileData, subscription.Name, downloadFormat, user.getFilePathForExternalStorage());
                                        } else {
                                            Toast.makeText(v.getContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, "Could not download with ApiName: " + subscription.ApiName + "  and format: " + downloadFormat);
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
                final Dialog dialog = dialogInterface.getDialog(getActivity(), R.layout.dialog_select_format, R.string.update_subscriptions);

                final Button subscribeButton = (Button) dialog.findViewById(R.id.select_download_format_download_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.select_download_format_cancel_button);
                final LinearLayout rowsContainer = (LinearLayout) dialog.findViewById(R.id.select_download_format_formats_container);

                final List<String> activeSubscriptionFormats = new ArrayList<>();

                for(Subscription activeSubscription : activeSubscriptions) {
                    if(activeSubscription.GeoDataServiceName.equals(subscription.ApiName)) {
                        activeSubscriptionFormats.add(activeSubscription.FileFormatType);
                    }
                }

                subscribeButton.setText(R.string.update);
                subscribeButton.setOnClickListener(onClickListenerInterface.getShowToastListener(getActivity(), getString(R.string.choose_a_download_format)));

                for (String format : subscription.Formats) {
                    final CheckBoxFormatRow formatRow = utilityRowsInterface.getCheckBoxFormatRow(getActivity(), format);
                    if(activeSubscriptionFormats.contains(format)) {
                        formatRow.setChecked(true);
                    }

                    rowsContainer.addView(formatRow.getView());
                }

                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View subscribeButton) {
                        boolean isSubscribed = false;

                        BarentswatchApi barentswatchApi = new BarentswatchApi();
                        barentswatchApi.setAccesToken(user.getToken());
                        final IBarentswatchApi api = barentswatchApi.getApi();

                        for(int i = 0; i < rowsContainer.getChildCount(); i++) {
                            String currentFormat = ((TextView) rowsContainer.getChildAt(i).findViewById(R.id.format_row_text_view)).getText().toString();


                            if(((CheckBox) rowsContainer.getChildAt(i).findViewById(R.id.format_row_check_box)).isChecked()) {
                                isSubscribed = true;

                                if(activeSubscriptionFormats.contains(currentFormat)) {
                                    continue;
                                } else {
                                    // TODO: API call to add subscription
                                    Response response;
                                }
                            } else {
                                if(activeSubscriptions.contains(currentFormat)) {
                                    // TODO: API call to remove subscription
                                    Response response;
                                }
                            }
                        }

                        ((Switch) v).setChecked(isSubscribed);
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View cancelButton) {
                        ((Switch) v).setChecked(activeSubscriptionFormats.size() > 0);

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        };

        View.OnClickListener errorNotificationOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(subscription.ErrorType)
                        .setMessage(subscription.ErrorText)
                        .setPositiveButton(getString(R.string.ok), null)
                        .show();
            }
        };

        currentPropertyDescriptionChildObject.setDownloadButtonOnClickListener(downloadButtonOnClickListener);
        currentPropertyDescriptionChildObject.setSubscribeSwitchOnClickListener(subscriptionSwitchClickListener);

        currentPropertyDescriptionChildObject.setErrorType(ApiErrorType.getType(subscription.ErrorType));
        currentPropertyDescriptionChildObject.setErrorNotificationOnClickListener(errorNotificationOnClickListener);

        return currentPropertyDescriptionChildObject;
    }


    private SubscriptionExpandableListChildObject setupWarningChildView(final String subscription) {
        SubscriptionExpandableListChildObject currentWarningObject = new SubscriptionExpandableListChildObject();
        currentWarningObject.setTitleText(subscription);
        currentWarningObject.setLastUpdatedText("");
        currentWarningObject.setIsSubscribed(true);

        return currentWarningObject;
    }

    private SubscriptionExpandableListChildObject setupActiveSubscriptionChildView(final Subscription subscription, List<PropertyDescription> availableSubscriptions) {
        SubscriptionExpandableListChildObject currentSubscription = new SubscriptionExpandableListChildObject();

        for (PropertyDescription availableSubscription : availableSubscriptions) {
            if (availableSubscription.ApiName.equals(subscription.GeoDataServiceName)) {
                currentSubscription.setTitleText(availableSubscription.Name);
                break;
            }
        }
        currentSubscription.setLastUpdatedText(subscription.LastModified.replace("T", "\n"));
        currentSubscription.setIsSubscribed(true);


        return currentSubscription;
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
            String identifier = ((ViewGroup) v.getParent()).getTag().toString();
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

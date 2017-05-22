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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.SubscriptionInterval;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityOnClickListeners;
import fiskinfoo.no.sintef.fiskinfoo.Interface.UserInterface;
import fiskinfoo.no.sintef.fiskinfoo.MainActivity;
import fiskinfoo.no.sintef.fiskinfoo.R;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CardViewInformationRow;


public class SubscriptionDetailsFragment extends Fragment {
    public static final String TAG = SubscriptionDetailsFragment.class.getSimpleName();

    private UserInterface userInterface;
    // Must be a better way (HINT: IT IS)
    private Subscription subscription = null;
    private String warning = null;
    private PropertyDescription propertyDescription = null;
    private UtilityOnClickListeners utilityOnClickListeners;
    List<Integer> takenIds;
    //END HINT

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationFragment.
     */
    public static SubscriptionDetailsFragment newInstance() {
        SubscriptionDetailsFragment fragment = new SubscriptionDetailsFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public SubscriptionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        String type;
        super.onCreate(savedInstanceState);
        type = getArguments().getString("type");
        Log.d(TAG, type);
        Gson gson = new Gson();

        if(type == null) {
            return;
        }

        switch(type) {
            case "sub":
                subscription = gson.fromJson(getArguments().getString("args"), Subscription.class);
                break;
            case "pd":
                propertyDescription = gson.fromJson(getArguments().getString("args"), PropertyDescription.class);
                break;
            case "warning":
                warning = getArguments().getString("args");
                break;
            default:
                Log.d(TAG, "INVALID type of object sent to cardview");
        }

        utilityOnClickListeners = new UtilityOnClickListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_subscription_details, container, false);

        LinearLayout textAreaPlaceHolder = (LinearLayout) rootView.findViewById(R.id.card_view_container);
        TextView title = (TextView) rootView.findViewById(R.id.card_view_title_text_view);

        if (subscription != null) {
            title.setText(subscription.GeoDataServiceName);
            TextView idView = generateTextViewWithText("id: " + subscription.Id, title);
            TextView geoDataServiceNameView = generateTextViewWithText("Navn på kartlaget: " + subscription.GeoDataServiceName, idView);
            TextView fileFormatTypeView = generateTextViewWithText("Filformat: " + subscription.FileFormatType, geoDataServiceNameView);
            TextView userEmailView = generateTextViewWithText("Bruker e-post: " + subscription.UserEmail, fileFormatTypeView);
            TextView subscriptionEmailView = generateTextViewWithText("Abbonents e-post: " + subscription.SubscriptionEmail, userEmailView);
            TextView subscriptionIntervalNameView = generateTextViewWithText("Kartet sendes: " + subscription.SubscriptionIntervalName, subscriptionEmailView);
            TextView createdView = generateTextViewWithText("Opprettet: " + subscription.Created, subscriptionIntervalNameView);
            TextView lastModifiedView = generateTextViewWithText("Sist oppdatert: " + subscription.LastModified, createdView);

            textAreaPlaceHolder.addView(idView);
            textAreaPlaceHolder.addView(geoDataServiceNameView);
            textAreaPlaceHolder.addView(fileFormatTypeView);
            textAreaPlaceHolder.addView(userEmailView);
            textAreaPlaceHolder.addView(subscriptionEmailView);
            textAreaPlaceHolder.addView(subscriptionIntervalNameView);
            textAreaPlaceHolder.addView(createdView);
            textAreaPlaceHolder.addView(lastModifiedView);
        }
        if (propertyDescription != null) {
            title.setText(propertyDescription.Name);
            final User user = userInterface.getUser();

            final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.card_view_scroll_view);
            final LinearLayout informationContainer = (LinearLayout) rootView.findViewById(R.id.card_view_information_container);
            ImageView notificationImageView = (ImageView) rootView.findViewById(R.id.card_notification_image_view);
            ImageView downloadMapImageView = (ImageView) rootView.findViewById(R.id.card_view_download_map_image_view);
            CardViewInformationRow row;
            LinearLayout bottomButtonContainer = (LinearLayout) rootView.findViewById(R.id.bottom_button_container);
            Button showOnMapButton = new Button(getActivity());

            row = new CardViewInformationRow(getActivity(), getString(R.string.last_updated), propertyDescription.LastUpdated.replace('T', ' '), true);
            informationContainer.addView(row.getView());

            String description = (propertyDescription.LongDescription == null || propertyDescription.LongDescription.trim().equals("")) ? propertyDescription.Description : propertyDescription.LongDescription;
            String hyperlink = null;
            description = description.replace("<p>", "");
            description = description.replace("</p>", "");

            // TODO: should rewrite in order to handle multiple links.
            if(description.contains("<a href=\"")) {
                hyperlink = "<a href='" + description.substring(description.indexOf("href=") + 6, description.indexOf(">", description.indexOf("href=")) - 1) + "'>" + "\t\t\t* " + getString(R.string.see_more_info) + "</a>";
                description = description.substring(0, description.indexOf("<a href")) + description.substring(description.indexOf(">", description.indexOf("<a href")) + 1, description.indexOf("</a")) +
                        "*" +  (description.indexOf("a>") > description.length() - 3 ? "" : description.substring(description.indexOf("a>") + 2, description.length()));
            }

            row = new CardViewInformationRow(getActivity(), getString(R.string.information), description, true);
            informationContainer.addView(row.getView());

            if(hyperlink != null) {
                TextView textView = new TextView(getActivity());
                textView.setClickable(true);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(Html.fromHtml(hyperlink));
                textView.setTextSize(getResources().getInteger(R.integer.hyperlinkTextSize));

                informationContainer.addView(textView);
            }

            String updateFrequency = (propertyDescription.UpdateFrequencyText == null || propertyDescription.UpdateFrequencyText.trim().equals("")) ? getString(R.string.update_frequency_not_available) : propertyDescription.UpdateFrequencyText;

            row = new CardViewInformationRow(getActivity(), getString(R.string.update_frequency), updateFrequency, true);
            informationContainer.addView(row.getView());

            if(ApiErrorType.getType(propertyDescription.ErrorType) == ApiErrorType.WARNING) {
                row = new CardViewInformationRow(getActivity(), getString(R.string.error_text), propertyDescription.ErrorText, true);
                final View dataField = row.getView();
                final Animation animation = getBlinkAnimation();

                notificationImageView.setVisibility(View.VISIBLE);
                notificationImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_warning_black_36dp));
                row.setTextColor(getResources().getColor(R.color.warning_orange));

                notificationImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusOnView(scrollView, dataField);
                        dataField.startAnimation(animation);
                    }
                });

                informationContainer.addView(row.getView());

            } else if(ApiErrorType.getType(propertyDescription.ErrorType) == ApiErrorType.WARNING) {
                row = new CardViewInformationRow(getActivity(), getString(R.string.error_text), propertyDescription.ErrorText, true);
                final TextView dataField = row.getFieldDataTextView();
                final Animation animation = getBlinkAnimation();

                notificationImageView.setVisibility(View.VISIBLE);
                notificationImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_outline_black_36dp));
                row.setTextColor(getResources().getColor(R.color.error_red));

                notificationImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusOnView(scrollView, dataField);
                        dataField.startAnimation(animation);
                    }
                });

                informationContainer.addView(row.getView());
            }

            row = new CardViewInformationRow(getActivity(), getString(R.string.data_owner), propertyDescription.DataOwner, true);
            informationContainer.addView(row.getView());

            if(propertyDescription.DataOwnerLink != null && !propertyDescription.DataOwnerLink.trim().equals("")) {
                String partnerLink = (propertyDescription.DataOwnerLink.contains("http") || propertyDescription.DataOwnerLink.contains("www")) ?
                        "<a href='" + propertyDescription.DataOwnerLink + "'>" + propertyDescription.DataOwnerLink + "</a>":
                        "<a href='" + getString(R.string.about_partners_base_address) + propertyDescription.DataOwnerLink + "'>" + getString(R.string.about_partners_base_address) + propertyDescription.DataOwnerLink + "</a>";

                row = new CardViewInformationRow(getActivity(), getString(R.string.data_owner_link), partnerLink, true);
                row.setHyperlink(partnerLink);
                row.getFieldDataTextView().setMovementMethod(LinkMovementMethod.getInstance());
                informationContainer.addView(row.getView());
            }

            StringBuilder stringBuilder = new StringBuilder();

            for(String format : propertyDescription.Formats) {
                stringBuilder.append(format);
                stringBuilder.append("\n");
            }

            row = new CardViewInformationRow(getActivity(), getString(R.string.formats), stringBuilder.toString().trim(), false);
            informationContainer.addView(row.getView());

            stringBuilder.setLength(0);
            for(String interval : propertyDescription.SubscriptionInterval) {
                stringBuilder.append(SubscriptionInterval.getType(interval).toString());
                stringBuilder.append("\n");
            }

            row = new CardViewInformationRow(getActivity(), getString(R.string.subscription_frequencies), stringBuilder.toString().trim(), false);
            informationContainer.addView(row.getView());


            row = new CardViewInformationRow(getActivity(), getString(R.string.map_creation_date), propertyDescription.Created.substring(0, propertyDescription.Created.indexOf('T')), true);
            informationContainer.addView(row.getView());

            LinearLayout.LayoutParams bottomButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bottomButtonLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            bottomButtonLayoutParams.weight = (float) 0.5;

            showOnMapButton.setLayoutParams(bottomButtonLayoutParams);
            showOnMapButton.setText(getString(R.string.show_on_map));
            showOnMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Need to update the toolbar as well.
                    List<String> layersList = user.getActiveLayers();
                    String layerName = propertyDescription.Name;

                    if(layersList != null && !layersList.contains(propertyDescription.Name)) {
                        layersList.add(layerName);
                    }

                    user.setActiveLayers(layersList);
                    user.writeToSharedPref(getActivity());

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    MapFragment fragment = MapFragment.newInstance();

                    fragmentManager.beginTransaction()
                            .replace(R.id.main_activity_fragment_container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(getString(R.string.map_fragment_title))
                            .commit();
                }
            });

            bottomButtonContainer.addView(showOnMapButton);

            downloadMapImageView.setOnClickListener(utilityOnClickListeners.getSubscriptionDownloadButtonOnClickListener(getActivity(), propertyDescription, user, TAG));

        }
        if(warning != null) {
            TextView content = generateTextViewWithText(warning, title);
            textAreaPlaceHolder.addView(content);
        }
        clearIds();


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UserInterface) {
            userInterface = (UserInterface) getActivity();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.userInterface = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() != null) {
            getView().refreshDrawableState();
        }

        MainActivity activity = (MainActivity) getActivity();
        String title = getResources().getString(R.string.subscription_details_fragment_title);
        activity.refreshTitle(title);
    }

    private TextView generateTextViewWithText(String text, TextView parent) {
        TextView textView = new TextView(getActivity());
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        p.addRule(RelativeLayout.BELOW, parent.getId());
        textView.setLayoutParams(p);
        textView.setId(generateRandomId());
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setTextColor(Color.parseColor("#666666"));
        textView.setTextSize(14);
        return textView;
    }

    private int generateRandomId() {
        if(takenIds == null) {
            takenIds = new ArrayList<>();
        }
        int num = randInt(3000, Integer.MAX_VALUE);
        while(takenIds.contains(num)) {
            num = randInt(3000, Integer.MAX_VALUE);
        }
        takenIds.add(num);
        return num;
    }

    private void clearIds() {
        takenIds = new ArrayList<>();
    }

    private static int randInt(int min, int max) {
        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return rand.nextInt((max - min) + 1) + min;
    }

    public Animation getBlinkAnimation(){
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(600);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);

        return animation;
    }

    private void focusOnView(final View scrollView, final View focusView){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, focusView.getTop());
            }
        });
    }
}
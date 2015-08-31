package fiskinfoo.no.sintef.fiskinfoo;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.SubscriptionInterval;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.UtilityRows;
import fiskinfoo.no.sintef.fiskinfoo.UtilityRows.CardViewInformationRow;


public class CardViewFragment extends Fragment {

    public static final String TAG = CardViewFragment.class.getSimpleName();
    private User user;

    // Must be a better way (HINT: IT IS)
    private Subscription subscription = null;
    private String warning = null;
    private PropertyDescription propertyDescription = null;
    private String type = null;
    private UtilityRows utilityRows;
    List<Integer> takenIds;
    //END HINT

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationFragment.
     */
    public static CardViewFragment newInstance() {
        CardViewFragment fragment = new CardViewFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    public CardViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
        type = getArguments().getString("type");
        Log.d(TAG, type);
        Gson gson = new Gson();
        switch(type) {
            case"sub":
                subscription = gson.fromJson(getArguments().getString("args"), Subscription.class);
                break;
            case"pd":
                propertyDescription = gson.fromJson(getArguments().getString("args"), PropertyDescription.class);
                break;
            case"warning":
                warning = getArguments().getString("args");
                break;
            default:
                Log.d(TAG, "INVALID type of object sent to cardview");
        }
        utilityRows = new UtilityRows();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_card_view, container, false);

        RelativeLayout textAreaPlaceHolder = (RelativeLayout) rootView.findViewById(R.id.card_view_container);
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

            ImageView notificationIconImageView = (ImageView) rootView.findViewById(R.id.card_notification_image_view);
            CardViewInformationRow row;

            final LinearLayout informationContainer = (LinearLayout) rootView.findViewById(R.id.card_view_information_container);

            row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.last_updated), propertyDescription.LastUpdated.replace('T', ' '), true);
            informationContainer.addView(row.getView());

            String description = (propertyDescription.LongDescription == null || propertyDescription.LongDescription.trim().equals("")) ? propertyDescription.Description : propertyDescription.LongDescription;
            String hyperlink = null;
            // TODO: should rewrite in order to handle multiple links.
            if(description.contains("<a href=\"")) {
                hyperlink = "<a href='" + description.substring(description.indexOf("\"") + 1, description.indexOf(">") - 1) + "'>" + "\t\t\t* " + getString(R.string.see_more_info) + "</a>";
                description = description.substring(0, description.indexOf('<')) + description.substring(description.indexOf('>') + 1, description.indexOf("</a")) +
                        "* " +  (description.indexOf("a>") > description.length() - 3 ? "" : description.substring(description.indexOf("a>") + 2, description.length()));
            }

            row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.information), description, true);
            informationContainer.addView(row.getView());

            if(hyperlink != null) {
                TextView textView = new TextView(getActivity());
                textView.setClickable(true);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(Html.fromHtml(hyperlink));

                informationContainer.addView(textView);
            }

            String updateFrequency = (propertyDescription.UpdateFrequencyText == null || propertyDescription.UpdateFrequencyText.trim().equals("")) ? getString(R.string.update_frequency_not_available) : propertyDescription.UpdateFrequencyText;

            row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.update_frequency), updateFrequency, true);
            informationContainer.addView(row.getView());

            if(ApiErrorType.getType(propertyDescription.ErrorType) == ApiErrorType.WARNING) {
                row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.error_text), propertyDescription.ErrorText, true);

                notificationIconImageView.setVisibility(View.VISIBLE);
                notificationIconImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dialog_alert_holo_light));
                row.setTextColor(getResources().getColor(R.color.warning_orange));
                informationContainer.addView(row.getView());

            } else if(ApiErrorType.getType(propertyDescription.ErrorType) == ApiErrorType.WARNING) {
                row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.error_text), propertyDescription.ErrorText, true);

                notificationIconImageView.setVisibility(View.VISIBLE);
                notificationIconImageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.indicator_input_error));
                row.setTextColor(getResources().getColor(R.color.error_red));
                informationContainer.addView(row.getView());
            }

            row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.data_owner), propertyDescription.DataOwner, true);
            informationContainer.addView(row.getView());

            if(propertyDescription.DataOwnerLink != null && !propertyDescription.DataOwnerLink.trim().equals("")) {
                String partnerLink = propertyDescription.DataOwnerLink.contains("http") ?
                        "<a href='" + propertyDescription.DataOwnerLink + "'>" + propertyDescription.DataOwnerLink + "</a>":
                        "<a href='" + getString(R.string.about_partners_base_address) + propertyDescription.DataOwnerLink + "'>" + getString(R.string.about_partners_base_address) + propertyDescription.DataOwnerLink + "</a>";

                row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.data_owner_link), partnerLink, true);
                row.setHyperlink(partnerLink);
                informationContainer.addView(row.getView());
            }

            StringBuilder stringBuilder = new StringBuilder();

            for(String format : propertyDescription.Formats) {
                stringBuilder.append(format + "\n");
            }

            row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.formats), stringBuilder.toString().trim(), false);
            informationContainer.addView(row.getView());

            stringBuilder.setLength(0);
            for(String interval : propertyDescription.SubscriptionInterval) {
                stringBuilder.append(SubscriptionInterval.getType(interval).toString() + "\n");
            }

            row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.subscription_frequencies), stringBuilder.toString().trim(), false);
            informationContainer.addView(row.getView());


            row = utilityRows.getCardViewInformationRow(getActivity(), getString(R.string.map_creation_date), propertyDescription.Created.substring(0, propertyDescription.Created.indexOf('T')), true);
            informationContainer.addView(row.getView());
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
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
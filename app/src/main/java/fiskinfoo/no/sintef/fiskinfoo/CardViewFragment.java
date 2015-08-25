package fiskinfoo.no.sintef.fiskinfoo;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import java.util.List;
import java.util.Random;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.ApiErrorType;
import fiskinfoo.no.sintef.fiskinfoo.Implementation.User;


public class CardViewFragment extends Fragment {

    public static final String TAG = CardViewFragment.class.getSimpleName();
    private User user;

    // Must be a better way (HINT: IT IS)
    private Subscription subscription = null;
    private String warning = null;
    private PropertyDescription propertyDescription = null;
    private String type = null;
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
                Log.d(TAG, "INVALUD type of object sent to cardview");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_card_view, container, false);

        RelativeLayout textAreaPlaceHolder = (RelativeLayout) rootView.findViewById(R.id.card_view_container);
        TextView title = (TextView) rootView.findViewById(R.id.card_view_title);
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
            TextView idView = generateTextViewWithText("id: " + propertyDescription.Id, title);
            TextView serviceTypeIdView = generateTextViewWithText("TjenesteId: " + propertyDescription.ServiceTypeId, idView);
            TextView nameView = generateTextViewWithText("Navn: " + propertyDescription.Name, serviceTypeIdView);
            TextView layerNameView = generateTextViewWithText("Kartlag: " + propertyDescription.LayerName, nameView);
            TextView apiNameView = generateTextViewWithText("Barentswatch tjenestenavn: " + propertyDescription.ApiName, layerNameView);
            TextView updateFrequencyText = generateTextViewWithText("Oppdateringsfrekvens: " + propertyDescription.UpdateFrequencyText, apiNameView);
            TextView descriptionView = generateTextViewWithText("Kart beskrivelse: " + propertyDescription.Description, updateFrequencyText);
            TextView longDescriptionView = generateTextViewWithText("All informasjon om kartlaget: " + propertyDescription.LongDescription, descriptionView);
            TextView errorMessageTextView = generateTextViewWithText("Errortekst: " + propertyDescription.ErrorText, longDescriptionView);
            TextView dataOwnerView = generateTextViewWithText("Dataeier: " + propertyDescription.DataOwner, errorMessageTextView);
            TextView dataOwnerLinkView = generateTextViewWithText("Dataeier sin addresse: " + propertyDescription.DataOwnerLink, dataOwnerView);
            TextView formatsView = generateTextViewWithText("Filformater tilgjengelige for nedlastning: " + propertyDescription.getFormatsAsString(), dataOwnerLinkView);
            TextView subscriptionIntervalView = generateTextViewWithText("Hvor ofte man kan få automatiske oppdateringer for dette kartlaget : " + propertyDescription.getSubscriptionIntervalAsString(), formatsView);
            TextView createdView = generateTextViewWithText("Kartlaget ble opprettet den: " + propertyDescription.Created, subscriptionIntervalView);
            TextView lastUpdatedView = generateTextViewWithText("Karlaget ble sist gang oppdatert den: " + propertyDescription.LastUpdated, createdView);
            TextView roleView = generateTextViewWithText("For å laste ned dette kartet trenger du å være: " + propertyDescription.Role, lastUpdatedView);

            if(ApiErrorType.getType(propertyDescription.ErrorType) == ApiErrorType.WARNING) {
                ImageView notificationIconImageView = (ImageView) rootView.findViewById(R.id.card_notification_image_view);

                notificationIconImageView.setVisibility(View.VISIBLE);
                notificationIconImageView.setBackground(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_info));
                errorMessageTextView.setTextColor(getResources().getColor(R.color.warning_orange));
            } else if(ApiErrorType.getType(propertyDescription.ErrorType) == ApiErrorType.WARNING) {
                ImageView notificationIconImageView = (ImageView) rootView.findViewById(R.id.card_notification_image_view);

                notificationIconImageView.setVisibility(View.VISIBLE);
                notificationIconImageView.setBackground(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_alert));
                errorMessageTextView.setTextColor(getResources().getColor(R.color.error_red));
            }

            textAreaPlaceHolder.addView(idView);
            textAreaPlaceHolder.addView(serviceTypeIdView);
            textAreaPlaceHolder.addView(nameView);
            textAreaPlaceHolder.addView(layerNameView);
            textAreaPlaceHolder.addView(apiNameView);
            textAreaPlaceHolder.addView(updateFrequencyText);
            textAreaPlaceHolder.addView(descriptionView);
            textAreaPlaceHolder.addView(longDescriptionView);
            textAreaPlaceHolder.addView(errorMessageTextView);
            textAreaPlaceHolder.addView(dataOwnerView);
            textAreaPlaceHolder.addView(dataOwnerLinkView);
            textAreaPlaceHolder.addView(formatsView);
            textAreaPlaceHolder.addView(subscriptionIntervalView);
            textAreaPlaceHolder.addView(createdView);
            textAreaPlaceHolder.addView(lastUpdatedView);
            textAreaPlaceHolder.addView(roleView);
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
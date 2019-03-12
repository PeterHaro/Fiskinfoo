package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.IBarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.SubscriptionSubmitObject;
import retrofit.client.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BarentswatchApiService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FETCH_DOWNLOADS = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.FETCHDOWNLOADS";
    private static final String ACTION_SET_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.SETSUBSCRIPTION";
    private static final String ACTION_UPDATE_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.UPDATESUBSCRIPTION";
    private static final String ACTION_DELETE_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.DELETESUBSCRIPTION";
    private static final String ACTION_FETCH_AUTHORIZATIONS = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.FETCHAUTHORIZATIONS";

    public static final String RESULT_PARAM_ACTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.ACTION";

    public static final String RESULT_PARAM_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.SUBSCRIPTION";
    public static final String RESULT_PARAM_CURRENTSUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.CURRENTSUBSCRIPTION";
    public static final String RESULT_PARAM_AUTHORIZATION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.AUTHORIZATION";


    public static final String RESULT_PARAM_UPDATED_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.UPDATEDSUBSCRIPTION";
    public static final int RESULT_CODE_FETCH_DOWNLOADS_SUCCESS = 100;
    public static final int RESULT_CODE_SUBSCRIPTION_UPDATE_SUCCESS = 101;
    public static final int RESULT_CODE_SUBSCRIPTION_DELETE_SUCCESS = 102;
    public static final int RESULT_CODE_FETCH_AUTHORIZATIONS_SUCCESS = 103;
    public static final int RESULT_CODE_FETCH_DOWNLOADS_FAILED = -100;
    public static final int RESULT_CODE_SUBSCRIPTION_UPDATE_FAILED = -101;
    public static final int RESULT_CODE_SUBSCRIPTION_DELETE_FAILED = -102;
    public static final int RESULT_CODE_FETCH_AUTHORIZATIONS_FAILED = -103;


    public static final String ACTION_PARAM_USER = "user";
    public static final String ACTION_PARAM_RECEIVER = "receiver";
    public static final String ACTION_PARAM_SUBSCRIPTION_SUBMIT = "subscription_submit";
    public static final String ACTION_PARAM_SUBSCRIPTION_ID = "subscription_id";


    public BarentswatchApiService() {
        super("BarentswatchApiService");
    }

    /**
     * Starts this service to perform fetching and downloading of download subscription information
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchDownloads(Context context, ResultReceiver receiver, User user) {
        Intent intent = createIntent(context, receiver, user, ACTION_FETCH_DOWNLOADS);
        context.startService(intent);
    }

    /**
     * Starts this service to perform update on an existing subscription
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateSubscription(Context context, ResultReceiver receiver, User user, SubscriptionSubmitObject submitObject, String subID) {
        Intent intent = createIntent(context, receiver, user, ACTION_UPDATE_SUBSCRIPTION);
        intent.putExtra(ACTION_PARAM_SUBSCRIPTION_SUBMIT, submitObject);
        intent.putExtra(ACTION_PARAM_SUBSCRIPTION_ID, subID);
        context.startService(intent);
    }

    /**
     * Starts this service to create a new subscription
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSetSubscription(Context context, ResultReceiver receiver, User user, SubscriptionSubmitObject submitObject) {
        Intent intent = createIntent(context, receiver, user, ACTION_SET_SUBSCRIPTION);
        intent.putExtra(ACTION_PARAM_SUBSCRIPTION_SUBMIT, submitObject);
        context.startService(intent);
    }

    /**
     * Starts this service to delete an existing subscription
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startDeleteSubscription(Context context, ResultReceiver receiver, User user, String subID) {
        Intent intent = createIntent(context, receiver, user, ACTION_DELETE_SUBSCRIPTION);
        intent.putExtra(ACTION_PARAM_SUBSCRIPTION_ID, subID);
        context.startService(intent);
    }

    /**
     * Starts this service to perform fetching and downloading of download subscription information
     * If the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchAuthorizations(Context context, ResultReceiver receiver, User user) {
        Intent intent = createIntent(context, receiver, user, ACTION_FETCH_AUTHORIZATIONS);
        context.startService(intent);
    }

    private static Intent createIntent(Context context, ResultReceiver receiver, User user, String action) {
        Intent intent = new Intent(context, BarentswatchApiService.class);
        intent.setAction(action);
        intent.putExtra(ACTION_PARAM_RECEIVER, receiver);
        intent.putExtra(ACTION_PARAM_USER, user);
        return intent;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final User user = intent.getParcelableExtra(ACTION_PARAM_USER);

            if (user == null) {
                Log.d("BarentswatchApiService", "Service invoked without user information");
                return;
            }
            ResultReceiver resultReceiver = intent.getParcelableExtra(ACTION_PARAM_RECEIVER);

            BarentswatchApi barentswatchApi = new BarentswatchApi();
            barentswatchApi.setAccesToken(user.getToken());
            IBarentswatchApi iAPI = barentswatchApi.getApi();

            Bundle resultBundle = new Bundle();
            resultBundle.putString(RESULT_PARAM_ACTION, action);
            if (ACTION_FETCH_DOWNLOADS.equals(action)) {
               handleFetchDownloads(resultReceiver, resultBundle, iAPI);
            } else if (ACTION_SET_SUBSCRIPTION.equals(action)) {
                SubscriptionSubmitObject submitObject = intent.getParcelableExtra(ACTION_PARAM_SUBSCRIPTION_SUBMIT);
                handleSetSubscription(resultReceiver, resultBundle, iAPI, submitObject);
            } else if (ACTION_UPDATE_SUBSCRIPTION.equals(action)) {
                SubscriptionSubmitObject submitObject = intent.getParcelableExtra(ACTION_PARAM_SUBSCRIPTION_SUBMIT);
                String subID = intent.getStringExtra(ACTION_PARAM_SUBSCRIPTION_ID);
                handleUpdateSubscription(resultReceiver, resultBundle, iAPI, subID, submitObject);
            } else if (ACTION_DELETE_SUBSCRIPTION.equals(action)) {
                String subID = intent.getStringExtra(ACTION_PARAM_SUBSCRIPTION_ID);
                handleDeleteSubscription(resultReceiver, resultBundle, iAPI, subID);
            } else if (ACTION_FETCH_AUTHORIZATIONS.equals(action)) {
                handleFetchAuthorizations(resultReceiver, resultBundle, iAPI);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleFetchDownloads(ResultReceiver receiver, Bundle b, IBarentswatchApi api) {

        try {
            List<PropertyDescription> availableSubscriptions = api.getSubscribable();
            List<Subscription> currentSubscriptions = api.getSubscriptions();
            List<Authorization> authorizations = api.getAuthorization();

            b.putParcelableArrayList(RESULT_PARAM_SUBSCRIPTION, new ArrayList<Parcelable>(availableSubscriptions));
            b.putParcelableArrayList(RESULT_PARAM_CURRENTSUBSCRIPTION, new ArrayList<Parcelable>(currentSubscriptions));
            b.putParcelableArrayList(RESULT_PARAM_AUTHORIZATION, new ArrayList<Parcelable>(authorizations));

            receiver.send(RESULT_CODE_FETCH_DOWNLOADS_SUCCESS, b);
        } catch (Exception e) {
            e.printStackTrace();
            receiver.send(RESULT_CODE_FETCH_DOWNLOADS_FAILED, b);
        }

//        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleSetSubscription(ResultReceiver receiver, Bundle b, IBarentswatchApi api, SubscriptionSubmitObject subscriptionSubmitObject) {
        try {
            Subscription subscription = api.setSubscription(subscriptionSubmitObject);
            b.putParcelable(RESULT_PARAM_UPDATED_SUBSCRIPTION, subscription);
            receiver.send(RESULT_CODE_SUBSCRIPTION_UPDATE_SUCCESS, b);
        } catch (Exception e) {
            e.printStackTrace();
            receiver.send(RESULT_CODE_SUBSCRIPTION_UPDATE_FAILED, b);
        }
    }

    private void handleUpdateSubscription(ResultReceiver receiver, Bundle b, IBarentswatchApi api, String subscriptionID, SubscriptionSubmitObject subscriptionSubmitObject) {
        try {
            Subscription subscription = api.updateSubscription(subscriptionID, subscriptionSubmitObject);
            b.putParcelable(RESULT_PARAM_UPDATED_SUBSCRIPTION, subscription);
            receiver.send(RESULT_CODE_SUBSCRIPTION_UPDATE_SUCCESS, b);
        } catch (Exception e) {
            e.printStackTrace();
            receiver.send(RESULT_CODE_SUBSCRIPTION_UPDATE_FAILED, b);
        }
    }

    private void handleDeleteSubscription(ResultReceiver receiver, Bundle b, IBarentswatchApi api, String subscriptionID) {
        try {
            Response response = api.deleteSubscription(subscriptionID);
            int responseCode = (response.getStatus() == 204) ? RESULT_CODE_SUBSCRIPTION_UPDATE_SUCCESS : RESULT_CODE_SUBSCRIPTION_DELETE_FAILED;
            receiver.send(responseCode, b);
        } catch (Exception e) {
            e.printStackTrace();
            receiver.send(RESULT_CODE_SUBSCRIPTION_DELETE_FAILED, b);
        }
    }

    private void handleFetchAuthorizations(ResultReceiver receiver, Bundle b, IBarentswatchApi api) {
        try {
            List<Authorization> authorizations = api.getAuthorization();

            b.putParcelableArrayList(RESULT_PARAM_AUTHORIZATION, new ArrayList<Parcelable>(authorizations));

            receiver.send(RESULT_CODE_FETCH_AUTHORIZATIONS_SUCCESS, b);
        } catch (Exception e) {
            e.printStackTrace();
            receiver.send(RESULT_CODE_FETCH_AUTHORIZATIONS_FAILED, b);
        }
    }



}

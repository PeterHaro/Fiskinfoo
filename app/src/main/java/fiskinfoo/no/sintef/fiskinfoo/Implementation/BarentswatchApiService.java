package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;

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
    private static final String ACTION_BAZ = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.BAZ";

    public static final String RESULT_PARAM_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.SUBSCRIPTION";
    public static final String RESULT_PARAM_CURRENTSUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.CURRENTSUBSCRIPTION";
    public static final String RESULT_PARAM_AUTHORIZATION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.AUTHORIZATION";


    public BarentswatchApiService() {
        super("BarentswatchApiService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFetchDownloads(Context context, ResultReceiver receiver, User user) {
        Intent intent = new Intent(context, BarentswatchApiService.class);
        intent.setAction(ACTION_FETCH_DOWNLOADS);
        intent.putExtra("receiver", receiver);
        intent.putExtra("user", user);

        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, ResultReceiver receiver, User user) {
        Intent intent = new Intent(context, BarentswatchApiService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra("receiver", receiver);
        intent.putExtra("user", user);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final User user = intent.getParcelableExtra("user");
            ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");
            if (ACTION_FETCH_DOWNLOADS.equals(action)) {

                handleFetchDownloads(resultReceiver, user);
            } else if (ACTION_BAZ.equals(action)) {
                handleActionBaz(resultReceiver, user);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleFetchDownloads(ResultReceiver receiver, User user) {

        BarentswatchApi barentswatchApi = new BarentswatchApi();
        barentswatchApi.setAccesToken(user.getToken());

        Bundle b = new Bundle();

        try {
            List<PropertyDescription> availableSubscriptions = barentswatchApi.getApi().getSubscribable();
            List<Subscription> currentSubscriptions = barentswatchApi.getApi().getSubscriptions();
            List<Authorization> authorizations = barentswatchApi.getApi().getAuthorization();

            b.putParcelableArrayList(RESULT_PARAM_SUBSCRIPTION, new ArrayList<Parcelable>(availableSubscriptions));
            b.putParcelableArrayList(RESULT_PARAM_CURRENTSUBSCRIPTION, new ArrayList<Parcelable>(currentSubscriptions));
            b.putParcelableArrayList(RESULT_PARAM_AUTHORIZATION, new ArrayList<Parcelable>(authorizations));

            receiver.send(200, b);
        } catch (Exception e) {
            e.printStackTrace();
            receiver.send(400, b);
        }

//        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(ResultReceiver receiver, User user) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

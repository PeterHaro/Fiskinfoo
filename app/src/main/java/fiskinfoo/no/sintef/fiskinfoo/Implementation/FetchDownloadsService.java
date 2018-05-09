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
public class FetchDownloadsService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FETCH_DOWNLOADS = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.FETCHDOWNLOADS";
    private static final String ACTION_BAZ = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "fiskinfoo.no.sintef.fiskinfoo.Implementation.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "fiskinfoo.no.sintef.fiskinfoo.Implementation.extra.PARAM2";


    public static final String RESULT_PARAM_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.SUBSCRIPTION";
    public static final String RESULT_PARAM_CURRENTSUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.CURRENTSUBSCRIPTION";
    public static final String RESULT_PARAM_AUTHORIZATION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.AUTHORIZATION";


    public FetchDownloadsService() {
        super("FetchDownloadsService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFetchDownloads(Context context, ResultReceiver receiver, String param2, User user) {
        Intent intent = new Intent(context, FetchDownloadsService.class);
        intent.setAction(ACTION_FETCH_DOWNLOADS);
        intent.putExtra("receiver", receiver);
        intent.putExtra(EXTRA_PARAM2, param2);
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
    public static void startActionBaz(Context context, ResultReceiver receiver, String param2, User user) {
        Intent intent = new Intent(context, FetchDownloadsService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra("receiver", receiver);
        intent.putExtra(EXTRA_PARAM2, param2);
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
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);

                handleFetchDownloads(resultReceiver, param1, user);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionBaz(resultReceiver, param1, user);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleFetchDownloads(ResultReceiver receiver, String param2, User user) {

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
    private void handleActionBaz(ResultReceiver receiver, String param2, User user) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

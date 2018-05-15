package fiskinfoo.no.sintef.fiskinfoo.Implementation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.BarentswatchApi;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Authorization;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.PropertyDescription;
import fiskinfoo.no.sintef.fiskinfoo.Http.BarentswatchApiRetrofit.models.Subscription;
import fiskinfoo.no.sintef.fiskinfoo.R;
import retrofit.client.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BarentswatchMapDownloadService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_MAP_DOWNLOAD = "fiskinfoo.no.sintef.fiskinfoo.Implementation.action.MAPDOWNLOAD";

    public static final String RESULT_PARAM_SUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.SUBSCRIPTION";
    public static final String RESULT_PARAM_CURRENTSUBSCRIPTION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.CURRENTSUBSCRIPTION";
    public static final String RESULT_PARAM_AUTHORIZATION = "fiskinfoo.no.sintef.fiskinfoo.Implementation.result.AUTHORIZATION";


    public static final String SERVICE_TAG = "MapDownloadService";

    public static final String MAP_DOWNLOAD_RESULT = "fiskinfoo.no.sintef.fiskinfoo.Implementation.MapDownloadResult";
    public static final String RESULT_CODE = "result_code";
    public static final String RESULT_TEXT = "result_text";

    public static final String API_NAME = "API_name";
    public static final String SUBSCRIPTION_NAME = "subscription_name";
    public static final String DOWNLOAD_FORMAT = "download_format";

    public static final int DOWNLOAD_OK = 0;
    public static final int DOWNLOAD_FAILED = 1;

    public BarentswatchMapDownloadService() {
        super("BarentswatchApiService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startMapDownload(Context context, String apiName, String subscriptionName, User user, String downloadFormat) {
        Intent intent = new Intent(context, BarentswatchMapDownloadService.class);
        intent.setAction(ACTION_MAP_DOWNLOAD);
        intent.putExtra("user", user);
        intent.putExtra(API_NAME, apiName);
        intent.putExtra(DOWNLOAD_FORMAT, downloadFormat);
        intent.putExtra(SUBSCRIPTION_NAME, subscriptionName);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final User user = intent.getParcelableExtra("user");
            if (ACTION_MAP_DOWNLOAD.equals(action)) {
                final String apiName = intent.getStringExtra(API_NAME);
                final String downloadFormat = intent.getStringExtra(DOWNLOAD_FORMAT);
                final String subscriptionName = intent.getStringExtra(SUBSCRIPTION_NAME);

                handleMapDownload(user, apiName, subscriptionName, downloadFormat );
            }
        }
    }

    /**
     * Handle action for fetching
     * parameters.
     */
    private void handleMapDownload(User user, String apiName, String subscriptionName, String downloadFormat) {

        // TODO: Add parameters to callers
        // TODO: Handle issue with permissions in writeMap()
        BarentswatchApi barentswatchApi = new BarentswatchApi();
        barentswatchApi.setAccesToken(user.getToken());

        Intent resultIntent = new Intent(MAP_DOWNLOAD_RESULT);

        Bundle b = new Bundle();

        try {
            Response response;
            response = barentswatchApi.getApi().geoDataDownload(apiName, downloadFormat);
            if (response == null) {
                resultIntent.putExtra(RESULT_CODE, DOWNLOAD_FAILED);
                resultIntent.putExtra(RESULT_TEXT, getResources().getString(R.string.download_failed));
            }

            byte[] fileData = FiskInfoUtility.toByteArray(response.getBody().in());
            final FiskInfoUtility fiskInfoUtility = new FiskInfoUtility();
            if (fiskInfoUtility.isExternalStorageWritable()) {
                this.writeMapLayerToExternalStorage(fileData, subscriptionName, downloadFormat, user.getFilePathForExternalStorage(), resultIntent);
            } else {
                resultIntent.putExtra(RESULT_CODE, DOWNLOAD_FAILED);
                resultIntent.putExtra(RESULT_TEXT, getResources().getString(R.string.download_failed));

                //                   Toast.makeText(v.getContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d(SERVICE_TAG, "Could not download with ApiName: " + apiName + "  and format: " + downloadFormat);
            e.printStackTrace();

            resultIntent.putExtra(RESULT_CODE, DOWNLOAD_FAILED);
            resultIntent.putExtra(RESULT_TEXT, getResources().getString(R.string.disk_write_failed));
        }
        this.sendBroadcast(resultIntent);
    }

    public void writeMapLayerToExternalStorage(byte[] data, String writableName, String format, String downloadSavePath, Intent resultIntent) {
        String filePath;
        OutputStream outputStream = null;
        filePath = downloadSavePath;
        String fileEnding = format;

        File directory = filePath == null ? null : new File(filePath);

        if(directory != null && !directory.isDirectory() && !directory.mkdirs()) {
            directory = null;
        }

        if(directory == null) {
            String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
            String directoryName = "FiskInfo";
            filePath = directoryPath + "/" + directoryName + "/";
            new File(filePath).mkdirs();
        }

        if(fileEnding != null && fileEnding.equals(getResources().getString(R.string.olex))) {
            fileEnding = "olx.gz";
        }

        try {
            File dir = new File(filePath);
            File file = new File(filePath + writableName + "." + fileEnding);

            if(!dir.canWrite()) {
                resultIntent.putExtra(RESULT_CODE, DOWNLOAD_FAILED);
                resultIntent.putExtra(RESULT_TEXT, getResources().getString(R.string.error_cannot_write_to_directory));
//TODO                throw new IOException(getResources().getString(R.string.error_cannot_write_to_directory));
            } else {

                outputStream = new FileOutputStream(file);
                outputStream.write(data);

                resultIntent.putExtra(RESULT_CODE, DOWNLOAD_OK);
                resultIntent.putExtra(RESULT_TEXT, "Fil lagret til " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            resultIntent.putExtra(RESULT_CODE, DOWNLOAD_FAILED);
            resultIntent.putExtra(RESULT_TEXT, getResources().getString(R.string.disk_write_failed));
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
/*

    private void broadcastResult(String resultText, int resultCode) {
        Intent intent = new Intent(MAP_DOWNLOAD_RESULT);
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(RESULT_TEXT, resultText)
        this.sendBroadcast(intent);
    }
*/

}

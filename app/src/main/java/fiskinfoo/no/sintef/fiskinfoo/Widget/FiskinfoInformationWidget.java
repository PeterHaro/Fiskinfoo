package fiskinfoo.no.sintef.fiskinfoo.Widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import fiskinfoo.no.sintef.fiskinfoo.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link FiskinfoInformationWidgetConfigureActivity FiskinfoInformationWidgetConfigureActivity}
 */
public class FiskinfoInformationWidget extends AppWidgetProvider {
    public static final String TOAST_ACTION = "fiskinfoo.no.sintef.appwidget.TOAST_ACTION";
    public static final String EXTRA_ITEM = "fiskinfoo.no.sintef.appwidget.EXTRA_ITEM";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fiskinfo_information_widget);

        //TODO: ADD INTENT FOR NAVIGATING TO TOOL PAGE

        Intent intent = new Intent(context, ToolsWodgetDataProviderService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        setRemoteAdapter(context, views, intent);

        //ON CLICK
        Intent toastIntent = new Intent(context, FiskinfoInformationWidget.class);
        toastIntent.setAction(FiskinfoInformationWidget.TOAST_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setPendingIntentTemplate(R.id.widget_listView, toastPendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views, Intent intent) {
        views.setRemoteAdapter(R.id.widget_listView, intent);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            FiskinfoInformationWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


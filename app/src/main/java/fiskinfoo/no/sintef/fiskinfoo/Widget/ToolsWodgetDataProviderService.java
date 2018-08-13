package fiskinfoo.no.sintef.fiskinfoo.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import fiskinfoo.no.sintef.fiskinfoo.R;

/**
 * Created by crono on 8/13/2018.
 */

public class ToolsWodgetDataProviderService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ToolsWidgetDataProviderFactory(this.getApplicationContext(), intent);
    }
}

class ToolsWidgetDataProviderFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final int maxItems = 10;
    private List<ToolWidgetItem> widgetItems = new ArrayList<>();
    private Context context;
    private int appWidgetId;

    public ToolsWidgetDataProviderFactory(Context context, Intent intent) {
        this.context = context;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    @Override
    public void onCreate() {
        for(int i = 0; i < maxItems; i++) {
            widgetItems.add(new ToolWidgetItem("redskap nr: " + String.valueOf(i)));
        }
    }

    @Override
    public void onDataSetChanged() {
        int i = 0;
    }

    @Override
    public void onDestroy() {
        widgetItems.clear();
    }

    @Override
    public int getCount() {
        return widgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget_list_item);
        rv.setTextViewText(R.id.stock_symbol, widgetItems.get(i).text);

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(FiskinfoInformationWidget.EXTRA_ITEM, i);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.stock_symbol, fillInIntent);

        // Do all calculation here, everything that needs to be done synchronously MUST be done here

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

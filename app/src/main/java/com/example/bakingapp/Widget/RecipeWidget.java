package com.example.bakingapp.Widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.bakingapp.R;
import com.example.bakingapp.Ui.MainActivity;
import com.example.bakingapp.Utilities.Utility;


public class RecipeWidget extends AppWidgetProvider {
    private static final String REFRESH_ACTION = "com.example.bakingapp.RecipeWidget.action.REFRESH";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.app_name);
        String recipeName = Utility.getSavedIngredientName(context);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        if (recipeName.equals("")) {
            views.setTextViewText(R.id.appwidget_text, widgetText);
        } else {
            views.setTextViewText(R.id.appwidget_text, recipeName);

        }

        Intent openMainActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openMainActivity, 0);
        views.setOnClickPendingIntent(R.id.widget_appBar, pendingIntent);

        // Set up the collection
        setRemoteAdapter(context, views);
        views.setEmptyView(R.id.widget_list, R.id.widget_emptyView);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.d("RecipeWidget","OnUpdate class call");

        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, MyWidgetRemoteViewsService.class));

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        Log.d("RecipeWidget","OnReceive class call");
        if (REFRESH_ACTION.equals(intent.getAction())) {
            Log.d("RecipeWidget","OnReceive class call action match");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appwidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass())
            );
            appWidgetManager.notifyAppWidgetViewDataChanged(appwidgetIds, R.id.widget_list);
            onUpdate(context, appWidgetManager, appwidgetIds);
        }
        super.onReceive(context,intent);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, MyWidgetRemoteViewsService.class));
    }

    public static void setWidgetText(Context context, String name) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        views.setTextViewText(R.id.appwidget_text, name);

    }
    public static void sendRefreshBroadcast(Context context) {
        Log.d("RecipeWidget","Refresh call");
        Intent intent = new Intent(REFRESH_ACTION);
        intent.setComponent(new ComponentName(context, RecipeWidget.class));
        context.sendBroadcast(intent);
    }
}


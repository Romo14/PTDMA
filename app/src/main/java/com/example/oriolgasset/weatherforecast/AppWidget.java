package com.example.oriolgasset.weatherforecast;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.example.oriolgasset.weatherservices.ApixuClient;
import com.weatherlibrary.datamodel.WeatherModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        SharedPreferences sp = context.getSharedPreferences("weatherForecastPreferences", Context.MODE_PRIVATE);
        List<String> cities = new ArrayList<>(sp.getStringSet("citiesList", new HashSet<String>()));
        String actualCitydisplayed = sp.getString("widgetCity", "Barcelona");
        String cityToDisplay = "";
        String cityName = "";
        for (int i = 0; i < cities.size(); ++i) {
            if (cities.get(i).equals(actualCitydisplayed)) {
                cityName = cities.get((i + 1) % cities.size());
                cityToDisplay = sp.getString(cities.get(((i + 1) % cities.size())), "");
                sp.edit().putString("widgetCity", cityName).commit();
            }
        }
        WeatherModel weather = new ApixuClient().parseJSON(cityToDisplay);

        views.setTextViewText(R.id.widgetTemperature, String.format("%sº", String.valueOf(weather.getCurrent().temp_c)));
        views.setTextViewText(R.id.widgetDescription, weather.getCurrent().getCondition().getText());
        views.setTextViewText(R.id.widgetCityName,cityName.split (",")[0]);
        views.setImageViewResource(R.id.widgetIcon, ApixuClient.getImageData(weather.getCurrent().getCondition()));

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("notificationCity", cityName);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        views.setOnClickPendingIntent(R.id.widgetLayout, resultPendingIntent);

        Intent intentSync = new Intent(context, AppWidget.class);
        intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context, 0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetNext, pendingSync);


        ComponentName thisWidget = new ComponentName(context, AppWidget.class);
        appWidgetManager.updateAppWidget(thisWidget, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateAppWidget(context, appWidgetManager);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE") || intent.getAction() == null) {
            updateAppWidget(context, AppWidgetManager.getInstance(context));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


}


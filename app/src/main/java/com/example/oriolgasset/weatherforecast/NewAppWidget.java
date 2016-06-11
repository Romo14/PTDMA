package com.example.oriolgasset.weatherforecast;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.example.oriolgasset.utils.CountryCodes;
import com.example.oriolgasset.utils.WeatherForecastUtils;
import com.example.oriolgasset.weatherservices.ApixuClient;
import com.google.android.gms.maps.model.LatLng;
import com.weatherlibrary.datamodel.WeatherModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private int index;
    private List<String> citiesList;
    private String city;
    private Context ctx;
    private AppWidgetManager appWidgetManager;
    private int appWidgetId;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId)  {
        city = citiesList.get(index);
        String[] valueAux = city.split("=")[1].split(",");
        LatLng value = new LatLng(Double.valueOf(valueAux[0]), Double.valueOf(valueAux[1]));
        ctx = context;
        new getWeatherList().execute(value);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            index = ++index % citiesList.size();
        }
    }

    @Override
    public void onEnabled(Context context) {
        SharedPreferences sp = context.getSharedPreferences("weatherForecastPreferences",Context.MODE_PRIVATE);
        citiesList = new ArrayList<>(sp.getStringSet("citiesList", new LinkedHashSet<String>()));
        index = 0;
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public class getWeatherList extends AsyncTask<LatLng, Void, WeatherModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected WeatherModel doInBackground(LatLng... params) {
            WeatherModel result;
            String data;
            ApixuClient weatherClient = new ApixuClient();
            data = weatherClient.getWeatherData("forecast", params[0], null, 1);
            result = weatherClient.parseJSON(data);
            return result;
        }

        @Override
        protected void onPostExecute(WeatherModel weather) {
            if (weather != null) {
                if (city.split("=")[0].equals("")) {
                    String[] ll = city.split("=")[1].split(",");
                    LatLng aux = new LatLng(Double.valueOf(ll[0]), Double.valueOf(ll[1]));
                    city = WeatherForecastUtils.getCityByLatLang(ctx, aux) + city;
                } else {
                    String country = new CountryCodes().getCode(weather.getLocation().getCountry());
                    city = city.split("=")[0] + ", " + country + "=" + city.split("=")[1];
                }
                RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.new_app_widget);
                views.setTextViewText(R.id.temperatureText, String.format("%sº", String.valueOf(weather.getCurrent().temp_c)));
                views.setTextViewText(R.id.descriptionText, weather.getCurrent().getCondition().getText());
                views.setTextViewText(R.id.realFeelValue, String.format("%sº", String.valueOf(weather.getCurrent().feelslike_c)));
                views.setTextViewText(R.id.maxTempValue, String.format("%sº", String.valueOf(weather.getForecast().getForecastday().get(0).getDay().maxtemp_c)));
                views.setTextViewText(R.id.minTempValue, String.format("%sº", String.valueOf(weather.getForecast().getForecastday().get(0).getDay().mintemp_c)));
                views.setImageViewResource(R.id.weatherIconMain, ApixuClient.getImageData(weather.getCurrent().getCondition()));

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);

            }
        }
    }
}


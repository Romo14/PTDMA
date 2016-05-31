package com.example.oriolgasset.weatherservices;

import android.text.TextUtils;

import com.example.oriolgasset.weatherforecast.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weatherlibrary.datamodel.Condition;
import com.weatherlibrary.datamodel.Current;
import com.weatherlibrary.datamodel.Forecast;
import com.weatherlibrary.datamodel.Forecastday;
import com.weatherlibrary.datamodel.Location;
import com.weatherlibrary.datamodel.WeatherModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApixuClient {

    String key = ".json?key=eb2a0633229b456ba6093557151106&q=";
    String url = "";
    private String APIURL = "http://api.apixu.com/v1/";
    private WeatherModel weatherModel;
    private Gson gson = new GsonBuilder().create();

    public WeatherModel parseJSON(String data) {
        WeatherModel result = new WeatherModel();
        if (!TextUtils.isEmpty(data)) {
            JSONObject jObj;
            try {
                jObj = new JSONObject(data);
                weatherModel = gson.fromJson(jObj.toString(), WeatherModel.class);
                if (jObj.has("location")) {
                    JSONObject locObj = jObj.getJSONObject("location");
                    result.setLocation(gson.fromJson(locObj.toString(), Location.class));
                }
                if (jObj.has("current")) {
                    JSONObject curObj = jObj.getJSONObject("current");
                    JSONObject condObj = curObj.getJSONObject("condition");
                    Current current = gson.fromJson(curObj.toString(), Current.class);
                    Condition condition = gson.fromJson(condObj.toString(), Condition.class);
                    current.setCondition(condition);
                    result.setCurrent(current);
                }
                if (jObj.has("forecast")) {
                    JSONObject forecastObj = jObj.getJSONObject("forecast");
                    JSONArray forecastDayObj = forecastObj.getJSONArray("forecastday");
                    ArrayList<Forecastday> forecastDayArray = new ArrayList<>();
                    for (int i = 0; i < forecastDayObj.length(); ++i) {
                        JSONObject dayObj = forecastDayObj.getJSONObject(i);
                        JSONObject dayWeatherObj = dayObj.getJSONObject("day");
                        JSONObject dayConditionObj = dayWeatherObj.getJSONObject("condition");
                        Forecastday day = gson.fromJson(dayObj.toString(), Forecastday.class);
                        Condition dayCondition = gson.fromJson(dayConditionObj.toString(), Condition.class);
                        day.getDay().setCondition(dayCondition);
                        forecastDayArray.add(day);
                    }
                    Forecast forecast = gson.fromJson(forecastObj.toString(), Forecast.class);
                    forecast.setForecastday(forecastDayArray);
                    result.setForecast(forecast);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getWeatherData(String method, LatLng cityLatLng, String cityName, int numDdays) {
        try {
            String name = cityName;
            if (name == null) {
                name = cityLatLng.latitude + "," + cityLatLng.longitude;
            }
            if (method.equals("forecast")) {
                name += "&days=" + numDdays;
            }
            url = APIURL + method + key + name;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getImageData(Condition condition) {
        switch (condition.code) {
            case 1000:
                if (condition.icon.contains("day")) return R.mipmap.sun;
                return R.mipmap.moon;
            case 1003:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_1cloud;
                return R.mipmap.moon_with_clouds;
            case 1006:
                return R.mipmap.clouds;
            case 1009:
                return R.mipmap.clouds;
            case 1030:
                if (condition.icon.contains("day")) return R.mipmap.sun_haze_01;
                return R.mipmap.moon_haze_01;
            case 1063:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_2cloud_littlerain;
                return R.mipmap.moon_drizzle_01;
            case 1066:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_littlesnow;
                return R.mipmap.moon_with_clouds_littlesnow;
            case 1069:
                if (condition.icon.contains("day")) return R.mipmap.sun_rain_snow_01;
                return R.mipmap.moon_rain_snow_01;
            case 1072:
                return R.mipmap.clouds_with_littlerain;
            case 1087:
                if (condition.icon.contains("day")) return R.mipmap.clouds_with_lighting;
                return R.mipmap.moon_clouds_thunder_01;
            case 1114:
                return R.mipmap.clouds_with_littlesnow;
            case 1117:
                return R.mipmap.clouds_with_snow;
            case 1135:
                if (condition.icon.contains("day")) return R.mipmap.sun_haze_01;
                return R.mipmap.moon_haze_01;
            case 1147:
                if (condition.icon.contains("day")) return R.mipmap.sun_haze_01;
                return R.mipmap.moon_haze_01;
            case 1150:
                return R.mipmap.clouds_with_littlerain;
            case 1153:
                return R.mipmap.clouds_with_littlerain;
            case 1168:
                return R.mipmap.clouds_with_littlerain;
            case 1171:
                return R.mipmap.clouds_with_littlerain;
            case 1180:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_2cloud_littlerain;
                return R.mipmap.moon_drizzle_01;
            case 1183:
                return R.mipmap.clouds_with_littlerain;
            case 1186:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_2cloud_littlerain;
                return R.mipmap.moon_drizzle_01;
            case 1189:
                return R.mipmap.clouds_with_littlerain;
            case 1192:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_2cloud_rain;
                return R.mipmap.moon_drizzle_01;
            case 1195:
                return R.mipmap.clouds_with_rain;
            case 1198:
                return R.mipmap.clouds_with_rain;
            case 1201:
                return R.mipmap.clouds_with_rain;
            case 1204:
                if (condition.icon.contains("day")) return R.mipmap.sun_rain_snow_01;
                return R.mipmap.moon_rain_snow_01;
            case 1207:
                if (condition.icon.contains("day")) return R.mipmap.sun_rain_snow_01;
                return R.mipmap.moon_rain_snow_01;
            case 1210:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_snow;
                return R.mipmap.moon_with_clouds_snow;
            case 1213:
                return R.mipmap.clouds_with_littlesnow;
            case 1216:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_snow;
                return R.mipmap.moon_with_clouds_snow;
            case 1219:
                return R.mipmap.clouds_with_littlesnow;
            case 1222:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_snow;
                return R.mipmap.moon_with_clouds_snow;
            case 1225:
                return R.mipmap.clouds_with_snow;
            case 1237:
                return R.mipmap.clouds_with_snow;
            case 1240:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_2cloud_littlerain;
                return R.mipmap.moon_drizzle_01;
            case 1243:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_2cloud_rain;
                return R.mipmap.moon_drizzle_01;
            case 1246:
                return R.mipmap.clouds_with_rain;
            case 1249:
                if (condition.icon.contains("day")) return R.mipmap.sun_rain_snow_01;
                return R.mipmap.moon_rain_snow_01;
            case 1252:
                if (condition.icon.contains("day")) return R.mipmap.sun_rain_snow_01;
                return R.mipmap.moon_rain_snow_01;
            case 1255:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_snow;
                return R.mipmap.moon_with_clouds_snow;
            case 1258:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_snow;
                return R.mipmap.moon_with_clouds_snow;
            case 1261:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_snow;
                return R.mipmap.moon_with_clouds_snow;
            case 1264:
                if (condition.icon.contains("day")) return R.mipmap.sun_with_clouds_snow;
                return R.mipmap.moon_with_clouds_snow;
            case 1273:
                if (condition.icon.contains("day"))
                    return R.mipmap.clouds_with_lighting_littlerain;
                return R.mipmap.moon_clouds_thunder_01;
            case 1276:
                return R.mipmap.clouds_with_lighting_rain;
            case 1279:
                return R.mipmap.clouds_with_lighting_rain;
            case 1282:
                return R.mipmap.clouds_with_lighting_rain;
        }

        return 0;

    }

    public int getBackgroundImage(int condition) {
        switch (condition) {

            case R.mipmap.clouds_with_2lighting:
            case R.mipmap.clouds_with_lighting:
            case R.mipmap.clouds_with_lighting_littlerain:
            case R.mipmap.clouds_with_lighting_rain:
            case R.mipmap.moon_clouds_thunder_01:
                return R.mipmap.thunderstorms_wallpaper;
            case R.mipmap.clouds:
                return R.mipmap.cloudy_wallpaper;
            case R.mipmap.clouds_with_littlerain:
            case R.mipmap.clouds_with_rain:
            case R.mipmap.moon_drizzle_01:
            case R.mipmap.moon_rain_snow_01:
            case R.mipmap.sun_rain_snow_01:
            case R.mipmap.sun_with_2cloud_littlerain:
            case R.mipmap.sun_with_2cloud_rain:
                return R.mipmap.rain_wallpaper;
            case R.mipmap.clouds_with_littlesnow:
            case R.mipmap.clouds_with_snow:
            case R.mipmap.moon_with_clouds_littlesnow:
            case R.mipmap.moon_with_clouds_snow:
            case R.mipmap.sun_with_clouds_littlesnow:
            case R.mipmap.sun_with_clouds_snow:
                return R.mipmap.snow_wallpaper;
            case R.mipmap.moon:
            case R.mipmap.moon_windy_01:
                return R.mipmap.night_clear_wallpaper;
            case R.mipmap.moon_haze_01:
                return R.mipmap.night_fog_wallpaper;
            case R.mipmap.moon_with_clouds:
                return R.mipmap.night_partly_cloudy_wallpaper;
            case R.mipmap.sun:
                return R.mipmap.fair_wallpaper;
            case R.mipmap.sun_haze_01:
                return R.mipmap.fog_wallpaper;
            case R.mipmap.sun_windy_01:
                return R.mipmap.windy_wallpaper;
            case R.mipmap.sun_with_1cloud:
                return R.mipmap.partly_cloudy_wallpaper;
            case R.mipmap.sun_with_3clouds:
                return R.mipmap.mostly_cloudy_wallpaper;
        }
        return 0;
    }

}

package com.example.oriolgasset.weatherservices;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weatherlibrary.IRepository;
import com.weatherlibrary.RequestBlocks;
import com.weatherlibrary.RequestBlocks.Days;
import com.weatherlibrary.RequestBlocks.MethodType;
import com.weatherlibrary.RequestBuilder;
import com.weatherlibrary.datamodel.Condition;
import com.weatherlibrary.datamodel.Current;
import com.weatherlibrary.datamodel.Forecast;
import com.weatherlibrary.datamodel.Forecastday;
import com.weatherlibrary.datamodel.Location;
import com.weatherlibrary.datamodel.WeatherModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ApixuClient implements IRepository {

    String key = "eb2a0633229b456ba6093557151106";
    String url = "";
    private String APIURL = "http://api.apixu.com/v1";
    private WeatherModel weatherModel;
    private Gson gson = new GsonBuilder().create();


    //TODO check internet connection
    public WeatherModel getWeather(String cityName) {
        try {
            GetWeatherData(key, RequestBlocks.GetBy.CityName,cityName, Days.Five);
            return weatherModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public WeatherModel getWeather(LatLng latLng) {
        try {
            GetWeatherDataByLatLong(key, String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), Days.Two);
            return weatherModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void GetWeatherData(String key, RequestBlocks.GetBy getBy, String value,
                               RequestBlocks.Days ForecastOfDays) throws Exception {
        String cityName = URLEncoder.encode(value, "UTF-8");
        url = APIURL + RequestBuilder.PrepareRequest(RequestBlocks.MethodType.Forecast, key, getBy, cityName);
        url += "&days=5";
        this.weatherModel = new getWeatherList().execute(url).get();
    }

    @Override
    public void GetWeatherDataByLatLong(String key, String latitude,
                                        String longitude, RequestBlocks.Days ForecastOfDays) throws Exception {
        url = APIURL + RequestBuilder.PrepareRequestByLatLong(MethodType.Forecast, key, latitude, longitude);
        url += "&days=2";
        this.weatherModel = new getWeatherList().execute(url).get();
    }

    @Override
    public void GetWeatherDataByAutoIP(String key, RequestBlocks.Days ForecastOfDays) throws Exception {
        url = APIURL + RequestBuilder.PrepareRequestByAutoIP(MethodType.Forecast, key, ForecastOfDays);
        new getWeatherList().execute(url);
    }

    @Override
    public void GetWeatherData(String key, RequestBlocks.GetBy getBy, String value) throws Exception {
        url = APIURL + RequestBuilder.PrepareRequest(MethodType.Current, key, getBy, value);
        new getWeatherList().execute(url);
    }

    @Override
    public void GetWeatherDataByLatLong(String key, String latitude,
                                        String longitude) throws Exception {
        url = APIURL + RequestBuilder.PrepareRequestByLatLong(MethodType.Current, key, latitude, longitude);
        this.weatherModel = new getWeatherList().execute(url).get();
    }

    @Override
    public void GetWeatherDataByAutoIP(String key) throws Exception {
        url = APIURL + RequestBuilder.PrepareRequestByAutoIP(MethodType.Current, key);
        new getWeatherList().execute(url);
    }

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
                    for (int i=0; i < forecastDayObj.length(); ++i){
                        JSONObject dayObj = forecastDayObj.getJSONObject(i);
                        JSONObject dayWeatherObj = dayObj.getJSONObject("day");
                        JSONObject dayConditionObj = dayWeatherObj.getJSONObject("condition");
                        Forecastday day = gson.fromJson(dayObj.toString(), Forecastday.class);
                        Condition dayCondition = gson.fromJson(dayConditionObj.toString(),Condition.class);
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

    public String getWeatherData() {
        HttpURLConnection connection = null;
        InputStream is = null;

        try {
            URL urlApixuAPI = new URL(url);
            connection = (HttpURLConnection) urlApixuAPI.openConnection();

            // Let's read the response
            StringBuilder buffer = new StringBuilder();
            is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
                buffer.append(line).append("\r\n");

            is.close();
            connection.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Throwable ignored) {
            }
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    public Bitmap getImageData(String icon) {
        try {
            return new LoadImage().execute("http:"+icon).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public class getWeatherList extends AsyncTask<String, Void, WeatherModel> {

        @Override
        protected WeatherModel doInBackground(String... arg0) {

            WeatherModel result;
            String data = getWeatherData();
            result = parseJSON(data);
            return result;
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        protected Bitmap doInBackground(String... args) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}

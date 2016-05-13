package com.example.oriolgasset.weatherservices;

import android.os.AsyncTask;

import com.example.oriolgasset.model.CityWeather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Oriol Gasset on 8/5/2016.
 */
public class ApixuClient {

    private static final String BASE_URL =
            "http://api.apixu.com/v1/%s%s&q=%s";

    private static final String CURRENT_URL = "current.json?";
    private static final String FORECAST_URL = "forecast.json?";

    private static final String KEY = "key=3ab34f80858546daa8f92536161305";


    public CityWeather getWeather(String cityName) {
        JSONWeatherTask task = new JSONWeatherTask();
        CityWeather result = null;
        try {
            result = task.execute(new String[]{cityName}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace ();
        }
        return result;
    }

    public String getWeatherData(String cityName) {
        HttpURLConnection connection = null;
        InputStream is = null;

        try {
            URL urlApixuAPI = new URL (String.format(BASE_URL,CURRENT_URL,KEY,cityName));

            connection = (HttpURLConnection) urlApixuAPI.openConnection();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            connection.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                connection.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }



    private class JSONWeatherTask extends AsyncTask<String, Void, CityWeather> {

        @Override
        protected CityWeather doInBackground(String... params) {
            publishProgress();
            CityWeather weather = new CityWeather();
            String data = (getWeatherData(params[0]));
            try {
                weather = JSONWeatherParser.getCurrentWeather (data);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onPostExecute(CityWeather weather) {
            super.onPostExecute(weather);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }


}

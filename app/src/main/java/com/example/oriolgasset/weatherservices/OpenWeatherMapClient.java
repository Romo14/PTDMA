package com.example.oriolgasset.weatherservices;

import android.os.AsyncTask;

import com.example.oriolgasset.model.OpenWeatherMapVO;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Oriol Gasset on 8/5/2016.
 */
public class OpenWeatherMapClient {

    private static final String BASE_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";

    private static final String OPEN_WEATHER_MAP_API = "9ac226bfa2820374259ab6f62b83a3bd";

    private static String IMG_URL = "http://openweathermap.org/img/w/";

    public OpenWeatherMapVO getWeather(LatLng latLng) {
        JSONWeatherTask task = new JSONWeatherTask();
        OpenWeatherMapVO result = null;
        try {
            result = task.execute(new String[]{String.valueOf(latLng.latitude),String.valueOf(latLng.longitude)}).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getWeatherData(String lat, String lon) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {
            con = (HttpURLConnection) (new URL(String.format(BASE_URL, lat, lon))).openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

    public byte[] getImage(String code) {
        HttpURLConnection con = null;
        InputStream is = null;
        try {
            con = (HttpURLConnection) (new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

    private class JSONWeatherTask extends AsyncTask<String, Void, OpenWeatherMapVO> {

        @Override
        protected OpenWeatherMapVO doInBackground(String... params) {
            publishProgress();
            OpenWeatherMapVO weather = new OpenWeatherMapVO();
            String data = (getWeatherData(params[0], params[1]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = (getImage(weather.currentCondition.getIcon()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onPostExecute(OpenWeatherMapVO weather) {
            super.onPostExecute(weather);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }


}

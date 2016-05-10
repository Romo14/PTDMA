package com.example.oriolgasset.weatherservices;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.oriolgasset.weatherforecast.R;
import com.google.android.gms.maps.model.LatLng;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.exception.WeatherProviderInstantiationException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;
import com.survivingwithandroid.weather.lib.provider.forecastio.ForecastIOProviderType;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.provider.yahooweather.YahooProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.List;

/**
 * Created by Oriol on 9/5/2016.
 */
public class WeatherForecastClient implements WeatherClient.ForecastWeatherEventListener, WeatherClient.HourForecastWeatherEventListener, WeatherClient.WeatherEventListener {

    private static String IMG_URL = "http://openweathermap.org/img/w/";
    private com.survivingwithandroid.weather.lib.WeatherClient weatherClient;
    private String provider;
    private String API_KEY = "dj0yJmk9WXM1MlptaWZTTWZqJmQ9WVdrOWFFeGhlVVJqTkhVbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD00Mw--";
    private CurrentWeather currentCondition;
    private WeatherHourForecast weatherHourForecast;
    private WeatherForecast weatherDailyForecast;
    private RelativeLayout layoutToFill = null;


    public WeatherForecastClient(Context context) {
        com.survivingwithandroid.weather.lib.WeatherClient.ClientBuilder builder = new com.survivingwithandroid.weather.lib.WeatherClient.ClientBuilder();
        WeatherConfig config = new WeatherConfig();
        config.ApiKey = API_KEY;
        try {
            weatherClient = builder.attach(context)
                    .provider(new YahooProviderType())
                    .httpClient(com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient.class)
                    .config(config)
                    .build();
        } catch (WeatherProviderInstantiationException e) {
            e.printStackTrace();
        }
    }

    public void getHourlyForecast(LatLng latLng) {
        weatherClient.getHourForecastWeather(new WeatherRequest(latLng.longitude, latLng.latitude), this);
    }

    public void getDailyForecast(LatLng latLng, RelativeLayout layout) {
        weatherClient.getForecastWeather(new WeatherRequest(latLng.longitude, latLng.latitude), this);
    }

    public void getCurrentWeather(String cityName, RelativeLayout layout) {
        this.layoutToFill = layout;
        weatherClient.searchCity(cityName, new WeatherClient.CityEventListener() {
            @Override
            public void onCityListRetrieved(List<City> cityList) {
                weatherClient.getCurrentCondition(new WeatherRequest(cityList.get(0).getId()), WeatherForecastClient.this);
                weatherClient.getForecastWeather(new WeatherRequest(cityList.get(0).getId()), WeatherForecastClient.this);
            }

            @Override
            public void onWeatherError(WeatherLibException wle) {

            }

            @Override
            public void onConnectionError(Throwable t) {

            }
        });

    }

    @Override
    public void onWeatherRetrieved(CurrentWeather weather) {
        currentCondition = weather;
        int temp = Math.round((int) weather.weather.temperature.getTemp());
        ((TextView) layoutToFill.findViewById(R.id.temperatureText)).setText(String.valueOf(temp) + "º");
        ((TextView) layoutToFill.findViewById(R.id.descriptionText)).setText(String.valueOf(currentCondition.weather.currentCondition.getCondition()));
        ((TextView) layoutToFill.findViewById(R.id.realFeelValue)).setText(String.valueOf(currentCondition.weather.currentCondition.getFeelsLike()));
        ((ImageView) layoutToFill.findViewById(R.id.weatherIconMain)).setImageResource(getImage(weather.weather.currentCondition.getIcon()));
    }

    @Override
    public void onWeatherRetrieved(WeatherForecast forecast) {
        weatherDailyForecast = forecast;
        int max = (int) Math.ceil(forecast.getForecast(0).forecastTemp.max);
        int min = (int) Math.floor( forecast.getForecast(0).forecastTemp.min);
        ((TextView) layoutToFill.findViewById(R.id.maxTempValue)).setText(String.valueOf(max) + "º");
        ((TextView) layoutToFill.findViewById(R.id.minTempValue)).setText(String.valueOf(min) + "º");
        this.layoutToFill = null;

    }

    @Override
    public void onWeatherRetrieved(WeatherHourForecast forecast) {
        this.layoutToFill = null;

    }

    @Override
    public void onWeatherError(WeatherLibException wle) {

    }

    @Override
    public void onConnectionError(Throwable t) {

    }

    private int getImage(String icon) {
        switch (icon) {
            case "01d":
                return R.mipmap.fair;
            case "01n":
                return R.mipmap.night_clear;
            case "02d":
                return R.mipmap.partly_cloudy;
            case "02n":
                return R.mipmap.night_partly_cloudy;
            case "03d":
                return R.mipmap.mostly_cloudy;
            case "03n":
                return R.mipmap.night_partly_cloudy_wallpaper;
            case "04d":
                return R.mipmap.cloudy;
            case "04n":
                return R.mipmap.cloudy;
            case "09d":
                return R.mipmap.rain;
            case "09n":
                return R.mipmap.rain;
            case "10d":
                return R.mipmap.rain;
            case "10n":
                return R.mipmap.rain;
            case "11d":
                return R.mipmap.thunderstorms;
            case "11n":
                return R.mipmap.thunderstorms;
            case "13d":
                return R.mipmap.snow;
            case "13n":
                return R.mipmap.snow;
            case "50d":
                return R.mipmap.fog;
            case "50n":
                return R.mipmap.night_fog;
        }
        return 0;
    }

}

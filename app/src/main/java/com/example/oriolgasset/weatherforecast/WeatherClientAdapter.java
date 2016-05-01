package com.example.oriolgasset.weatherforecast;

import android.content.Context;

import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.exception.WeatherProviderInstantiationException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.Weather;
import com.survivingwithandroid.weather.lib.provider.forecastio.ForecastIOProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.List;

/**
 * Created by Oriol on 30/4/2016.
 */
public class WeatherClientAdapter {
    public WeatherClient client;

    public WeatherClientAdapter(Context ctx)  {
        WeatherClient.ClientBuilder builder = new WeatherClient.ClientBuilder();
        WeatherConfig config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        config.lang = "en";
        config.maxResult = 5;
        config.numDays = 5;

        try {
            client = builder.attach(ctx)
                    .provider(new ForecastIOProviderType())
                    .httpClient(com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient.class)
                    .config(config)
                    .build();
        } catch (WeatherProviderInstantiationException e) {
            e.printStackTrace();
        }

    }

    public void loadForecastByCity(String city) {
        client.searchCity(city, new WeatherClient.CityEventListener() {
            @Override
            public void onCityListRetrieved(List<City> cityList) {
               loadForecast(cityList.get(0));
            }

            @Override
            public void onWeatherError(WeatherLibException t) {
                // Error
            }

            @Override
            public void onConnectionError(Throwable t) {
                // Connection error
            }
        });
    }

    private void loadForecast(City city) {
        client.getCurrentCondition(new WeatherRequest(city.getId()), new WeatherClient.WeatherEventListener() {
            @Override
            public void onWeatherRetrieved(CurrentWeather cWeather) {
                Weather weather = cWeather.weather;
            }

            @Override
            public void onWeatherError(WeatherLibException t) {

            }

            @Override
            public void onConnectionError(Throwable t) {

            }
        });
    }

}

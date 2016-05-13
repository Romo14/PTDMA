package com.example.oriolgasset.model;

/**
 * Created by Oriol Gasset on 13/5/2016.
 */
public class CityWeather {
    private String locationName;
    private String locationRegion;
    private String locationCountry;
    private CurrentWeather currentWeather;
    private DailyForecast dailyForecast;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationRegion() {
        return locationRegion;
    }

    public void setLocationRegion(String locationRegion) {
        this.locationRegion = locationRegion;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }

    public DailyForecast getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(DailyForecast dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

}

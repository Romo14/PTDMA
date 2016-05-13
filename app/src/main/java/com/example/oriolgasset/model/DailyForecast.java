package com.example.oriolgasset.model;

import java.util.List;

/**
 * Created by Oriol Gasset on 13/5/2016.
 */
public class DailyForecast {
    private String date;
    private String maxTemp;
    private String minTemp;
    private String conditionText;
    private String icon;
    private String precipitation;
    private String sunrise;
    private String sunset;
    private String moonrise;
    private String moonset;
    private List<HourlyForecast> hourlyForecast;

    public List<HourlyForecast> getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(List<HourlyForecast> hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(String precipitation) {
        this.precipitation = precipitation;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getMoonrise() {
        return moonrise;
    }

    public void setMoonrise(String moonrise) {
        this.moonrise = moonrise;
    }

    public String getMoonset() {
        return moonset;
    }

    public void setMoonset(String moonset) {
        this.moonset = moonset;
    }
}

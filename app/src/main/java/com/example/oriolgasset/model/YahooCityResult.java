package com.example.oriolgasset.model;

/**
 * Created by Oriol on 11/5/2016.
 */
public class YahooCityResult {

    private String woeid;
    private String cityName;
    private String country;

    public YahooCityResult() {
    }

    public YahooCityResult(String woeid, String cityName, String country) {
        this.woeid = woeid;
        this.cityName = cityName;
        this.country = country;
    }

// get and set methods

    @Override
    public String toString() {
        return cityName + "," + country;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public String getWoeid() {
        return woeid;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }
}

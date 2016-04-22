package com.example.oriolgasset.weatherforecast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Oriol-Sony Vaio on 22/4/2016.
 */
public class Forecast {
    public  static HashMap<String,List<String>> getHourlyForecast(){
        HashMap<String,List<String>> result = new HashMap<String, List<String>>();
        List<String> hourlyForecast = new ArrayList<String>();
        List<String> dailyForecast = new ArrayList<String>();
        hourlyForecast.add("hora 1");
        hourlyForecast.add("hora 2");
        hourlyForecast.add("hora 3");
        hourlyForecast.add("hora 4");

        dailyForecast.add("dia 1");
        dailyForecast.add("dia 2");
        dailyForecast.add("dia 3");
        dailyForecast.add("dia 4");

        result.put("Hourly Forecast", hourlyForecast);
        result.put("Daily Forecast", dailyForecast);


        return result;
    }

}

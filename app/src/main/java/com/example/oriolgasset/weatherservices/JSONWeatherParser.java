/**
 * This is a tutorial source code 
 * provided "as is" and without warranties.
 *
 * For any question please visit the web site
 * http://www.survivingwithandroid.com
 *
 * or write an email to
 * survivingwithandroid@gmail.com
 *
 */
package com.example.oriolgasset.weatherservices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.oriolgasset.model.*;

/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class JSONWeatherParser {

	public static CityWeather getCurrentWeather(String data) throws JSONException  {
		CityWeather weather = new CityWeather();

		JSONObject jObj = new JSONObject(data);

		JSONObject locationObj = getObject("location", jObj);
		weather.setLocationName (getString ("name", locationObj));
		weather.setLocationRegion (getString ("region", locationObj));
		weather.setLocationCountry (getString ("country", locationObj));

		JSONObject currentObj = getObject("current", jObj);
        CurrentWeather current = new CurrentWeather ();
		current.setCurrentUpdated (getString("last_updated", currentObj));
        current.setCurrentTemp (getString("temp_c", currentObj));
        current.setCurrentIsDay (getString("is_day", currentObj));

        JSONObject currentConditionObj = getObject("condition", currentObj);
        current.setConditionIconCode (getString("icon", currentConditionObj));
        current.setConditionText (getString("text", currentConditionObj));

        current.setConditionWindSpeed (getString("wind_kph", currentObj));
        current.setConditionWindDegree (getString("wind_degree", currentObj));
        current.setConditionWindDir (getString("wind_dir", currentObj));
        current.setConditionPressure (getString("pressure_mb", currentObj));
        current.setConditionPrecip (getString("precip_mm", currentObj));
        current.setConditionHumidity (getString("humidity", currentObj));
        current.setConditionCloud (getString("cloud", currentObj));
        current.setConditionFeelsLike (getString("feelslike_c", currentObj));

        weather.setCurrentWeather (current);

		// We get weather info (This is an array)
		JSONArray jArr = jObj.getJSONArray("forecastday");
		
		// We use only the first value
        for (int i = 0; i<jArr.length (); ++i){
            DailyForecast dailyForecast = new DailyForecast ();
            JSONObject dailyJSON = jArr.getJSONObject(i);
            dailyForecast.setDate (getString ("date",dailyJSON));
            JSONObject dayJSON = dailyJSON.getJSONObject ("day");
            dailyForecast.setMaxTemp (getString ("maxtemp_c",dayJSON));
            dailyForecast.setMinTemp (getString ("mintemp_c",dayJSON));
            dailyForecast.setPrecipitation (getString ("totalprecip_mm",dayJSON));

            JSONObject dayCondition = dayJSON.getJSONObject ("condition");
            dailyForecast.setConditionText (getString ("text",dayCondition));
            dailyForecast.setIcon (getString ("icon",dayCondition));

            JSONObject dayAstro = dayJSON.getJSONObject ("astro");
            dailyForecast.setSunrise (getString ("sunrise",dayAstro));
            dailyForecast.setSunset (getString ("sunset",dayAstro));
            dailyForecast.setMoonrise (getString ("moonrise",dayAstro));
            dailyForecast.setMoonset (getString ("moonset",dayAstro));

            JSONArray hourlyForecastJSON = dailyJSON.getJSONArray ("hour");

            for (int j = 0; j < hourlyForecastJSON.length (); ++j){
                HourlyForecast hourlyForecast = new HourlyForecast ();
                JSONObject hourlyJSON = hourlyForecastJSON.getJSONObject (j);

                hourlyForecast.setTime (getString ("time",hourlyJSON));
                hourlyForecast.setTemp (getString ("temp_c",hourlyJSON));
                hourlyForecast.setIsDay (getString ("is_day",hourlyJSON));
                hourlyForecast.setTime (getString ("time",hourlyJSON));
                hourlyForecast.setTime (getString ("time",hourlyJSON));
                hourlyForecast.setTime (getString ("time",hourlyJSON));
                hourlyForecast.setTime (getString ("time",hourlyJSON));
                hourlyForecast.setTime (getString ("time",hourlyJSON));
                hourlyForecast.setTime (getString ("time",hourlyJSON));

            }

        }
		JSONObject JSONWeather = jArr.getJSONObject(0);
		weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
		weather.currentCondition.setDescr(getString("description", JSONWeather));
		weather.currentCondition.setCondition(getString("main", JSONWeather));
		weather.currentCondition.setIcon(getString("icon", JSONWeather));
		
		JSONObject mainObj = getObject("main", jObj);
		weather.currentCondition.setHumidity(getInt("humidity", mainObj));
		weather.currentCondition.setPressure(getInt("pressure", mainObj));
		weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
		weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
		weather.temperature.setTemp(getFloat("temp", mainObj));
		
		// Wind
		JSONObject wObj = getObject("wind", jObj);
		weather.wind.setSpeed(getFloat("speed", wObj));
		weather.wind.setDeg(getFloat("deg", wObj));
		
		// Clouds
		JSONObject cObj = getObject("clouds", jObj);
		weather.clouds.setPerc(getInt("all", cObj));
		
		// We download the icon to show
		
		
		return weather;
	}

    public static CityWeather getDailyWeather(String data) throws JSONException  {

    }

    public static CityWeather getHourlyWeather(String data) throws JSONException  {


    }
	
	
	private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
		JSONObject subObj = jObj.getJSONObject(tagName);
		return subObj;
	}
	
	private static String getString(String tagName, JSONObject jObj) throws JSONException {
		return jObj.getString(tagName);
	}

	private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
		return (float) jObj.getDouble(tagName);
	}
	
	private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
		return jObj.getInt(tagName);
	}
	
}

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
package com.example.oriolgasset.model;
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
public class CurrentWeather {

	private String currentUpdated;
	private String currentTemp;
	private String currentIsDay;
	private String conditionText;
	private String conditionIconCode;
	private String conditionWindSpeed;
	private String conditionWindDegree;
	private String conditionWindDir;
	private String conditionPressure;
	private String conditionPrecip;
	private String conditionHumidity;
	private String conditionCloud;
	private String conditionFeelsLike;




    public String getCurrentUpdated() {
        return currentUpdated;
    }

    public void setCurrentUpdated(String currentUpdated) {
        this.currentUpdated = currentUpdated;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getCurrentIsDay() {
        return currentIsDay;
    }

    public void setCurrentIsDay(String currentIsDay) {
        this.currentIsDay = currentIsDay;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public String getConditionIconCode() {
        return conditionIconCode;
    }

    public void setConditionIconCode(String conditionIconCode) {
        this.conditionIconCode = conditionIconCode;
    }

    public String getConditionWindSpeed() {
        return conditionWindSpeed;
    }

    public void setConditionWindSpeed(String conditionWindSpeed) {
        this.conditionWindSpeed = conditionWindSpeed;
    }

    public String getConditionWindDegree() {
        return conditionWindDegree;
    }

    public void setConditionWindDegree(String conditionWindDegree) {
        this.conditionWindDegree = conditionWindDegree;
    }

    public String getConditionWindDir() {
        return conditionWindDir;
    }

    public void setConditionWindDir(String conditionWindDir) {
        this.conditionWindDir = conditionWindDir;
    }

    public String getConditionPressure() {
        return conditionPressure;
    }

    public void setConditionPressure(String conditionPressure) {
        this.conditionPressure = conditionPressure;
    }

    public String getConditionPrecip() {
        return conditionPrecip;
    }

    public void setConditionPrecip(String conditionPrecip) {
        this.conditionPrecip = conditionPrecip;
    }

    public String getConditionHumidity() {
        return conditionHumidity;
    }

    public void setConditionHumidity(String conditionHumidity) {
        this.conditionHumidity = conditionHumidity;
    }

    public String getConditionCloud() {
        return conditionCloud;
    }

    public void setConditionCloud(String conditionCloud) {
        this.conditionCloud = conditionCloud;
    }

    public String getConditionFeelsLike() {
        return conditionFeelsLike;
    }

    public void setConditionFeelsLike(String conditionFeelsLike) {
        this.conditionFeelsLike = conditionFeelsLike;
    }
}

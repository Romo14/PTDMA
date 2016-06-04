package com.example.oriolgasset.weatherforecast;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.oriolgasset.weatherservices.ApixuClient;
import com.weatherlibrary.datamodel.Forecastday;
import com.weatherlibrary.datamodel.Hour;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailedDailyForecast extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_daily_forecast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.primary_dark));
        this.setTaskDescription(taskDesc);

        LinearLayout temperatureLayout = (LinearLayout) findViewById(R.id.temperatureDailyDetail);
        LinearLayout precipitationLayout = (LinearLayout) findViewById(R.id.precipDailyDetail);
        LinearLayout humidityLayout = (LinearLayout) findViewById(R.id.humidityDailyDetail);
        LinearLayout windLayout = (LinearLayout) findViewById(R.id.windDailyDetail);
        LinearLayout pressureLayout = (LinearLayout) findViewById(R.id.pressureDailyDetail);
        LinearLayout cloudsLayout = (LinearLayout) findViewById(R.id.cloudsDailyDetail);
        LinearLayout dewLayout = (LinearLayout) findViewById(R.id.dewDailyDetail);
        LinearLayout sunLayout = (LinearLayout) findViewById(R.id.sunDailyDetail);
        LinearLayout moonLayout = (LinearLayout) findViewById(R.id.moonDailyDetail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Forecastday forecastday = (Forecastday) extras.get("dailyForecast");
            assert forecastday != null;
            SimpleDateFormat dformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date d = null;
            try {
                d = dformatter.parse(forecastday.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE dd/MM", Locale.US);
            String dayOfWeek = formatter.format(d);
            if (toolbar != null) {
                getSupportActionBar().setSubtitle(forecastday.getDay().getCondition().text);
                getSupportActionBar().setTitle(dayOfWeek);
                int image = ApixuClient.getImageData(forecastday.getDay().getCondition());
                toolbar.setBackgroundResource(ApixuClient.getBackgroundImage(image));
            }

            // Temperature
            TextView text = (TextView) temperatureLayout.findViewById(R.id.detailText);
            TextView value = (TextView) temperatureLayout.findViewById(R.id.detailValue);
            ImageView image = (ImageView) temperatureLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.temperatureDetail));
            value.setText(forecastday.getDay().maxtemp_c + "º / " + forecastday.getDay().mintemp_c + "º");
            image.setImageResource(R.mipmap.temperature_icon);

            // precipitation
            text = (TextView) precipitationLayout.findViewById(R.id.detailText);
            value = (TextView) precipitationLayout.findViewById(R.id.detailValue);
            image = (ImageView) precipitationLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.precipitationDetail));
            value.setText(String.format("%s mm", forecastday.getDay().getTotalprecipMm()));
            image.setImageResource(R.mipmap.precipitation_icon);

            // humidity
            text = (TextView) humidityLayout.findViewById(R.id.detailText);
            value = (TextView) humidityLayout.findViewById(R.id.detailValue);
            image = (ImageView) humidityLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.humidityText));
            value.setText(getHumidityText(forecastday));
            image.setImageResource(R.mipmap.humidity_icon);

            // wind
            text = (TextView) windLayout.findViewById(R.id.detailText);
            value = (TextView) windLayout.findViewById(R.id.detailValue);
            image = (ImageView) windLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.wind_text));
            value.setText(forecastday.getDay().maxwind_kph + " km/h");
            image.setImageResource(R.mipmap.wind_icon);

            // pressure
            text = (TextView) pressureLayout.findViewById(R.id.detailText);
            value = (TextView) pressureLayout.findViewById(R.id.detailValue);
            image = (ImageView) pressureLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.pressureText));
            value.setText(getPressureText(forecastday));
            image.setImageResource(R.mipmap.pressure_icon);

            // clouds
            text = (TextView) cloudsLayout.findViewById(R.id.detailText);
            value = (TextView) cloudsLayout.findViewById(R.id.detailValue);
            image = (ImageView) cloudsLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.cloudtext));
            value.setText(getCloudsText(forecastday));
            image.setImageResource(R.mipmap.cloud_icon);

            // dew
            text = (TextView) dewLayout.findViewById(R.id.detailText);
            value = (TextView) dewLayout.findViewById(R.id.detailValue);
            image = (ImageView) dewLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.dew_point));
            value.setText(getDewPointText(forecastday));
            image.setImageResource(R.mipmap.dew_point_icon);

            // sun
            text = (TextView) sunLayout.findViewById(R.id.detailText);
            value = (TextView) sunLayout.findViewById(R.id.detailValue);
            image = (ImageView) sunLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.sunriseset));
            value.setText(forecastday.getAstro().sunrise + " - " + forecastday.getAstro().sunset);
            image.setImageResource(R.mipmap.sunrise_icon);

            // moon
            text = (TextView) moonLayout.findViewById(R.id.detailText);
            value = (TextView) moonLayout.findViewById(R.id.detailValue);
            image = (ImageView) moonLayout.findViewById(R.id.imageView);
            text.setText(getString(R.string.moonriseset));
            value.setText(forecastday.getAstro().moonrise + " - " + forecastday.getAstro().moonset);
            image.setImageResource(R.mipmap.moon_icon);

        }

    }

    private String getDewPointText(Forecastday forecastday) {
        int dew = 0;
        for (Hour aux : forecastday.getHour()) {
            dew += aux.dewpoint_c;
        }
        return dew / forecastday.getHour().size() + "º";
    }

    private String getCloudsText(Forecastday forecastday) {
        int clouds = 0;
        for (Hour aux : forecastday.getHour()) {
            clouds += aux.cloud;
        }
        return clouds / forecastday.getHour().size() + "%";
    }

    private String getPressureText(Forecastday forecastday) {
        int pressure = 0;
        for (Hour aux : forecastday.getHour()) {
            pressure += aux.pressure_mb;
        }
        return pressure / forecastday.getHour().size() + " mb";
    }

    private String getHumidityText(Forecastday forecastday) {
        int humidity = 0;
        for (Hour aux : forecastday.getHour()) {
            humidity += aux.humidity;
        }
        return humidity / forecastday.getHour().size() + "%";
    }

}

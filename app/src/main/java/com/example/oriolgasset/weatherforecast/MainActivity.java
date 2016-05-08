package com.example.oriolgasset.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<String> cities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.findViewById(R.id.citiesMenu);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu m = navigationView.getMenu();


        cities = loadCities();
        addCitiesToMenu(m, cities);
        loadCurrentWeather(cities.get(0));
    }

    private void addCitiesToMenu(Menu menu, List<String> cities) {
        menu.add(R.id.citiesMenu, Menu.FIRST, Menu.NONE, cities.get(0)).setIcon(R.mipmap.fair);
        menu.add(R.id.citiesMenu, Menu.FIRST + 1, Menu.NONE, cities.get(1)).setIcon(R.mipmap.cloudy);
        menu.add(R.id.citiesMenu, Menu.FIRST + 2, Menu.NONE, cities.get(2)).setIcon(R.mipmap.partly_cloudy);
        menu.add(R.id.citiesMenu, Menu.FIRST + 3, Menu.NONE, cities.get(3)).setIcon(R.mipmap.rain);
        menu.add(R.id.citiesMenu, Menu.FIRST + 4, Menu.NONE, cities.get(4)).setIcon(R.mipmap.rain);
        menu.add(R.id.group_settings, Menu.FIRST, Menu.NONE, R.string.action_settings).setIcon(R.mipmap.ic_settings_black_48dp);
    }

    private void loadCurrentWeather(String city) {
        //weatherClient.loadForecastByCity(city);
        TextView temperatureText = (TextView) findViewById(R.id.temperatureText);
        TextView maxTemperatureText = (TextView) findViewById(R.id.maxTempValue);
        TextView minTemperatureText = (TextView) findViewById(R.id.minTempValue);
        TextView descriptionText = (TextView) findViewById(R.id.descriptionText);
        TextView humidityText = (TextView) findViewById(R.id.humidityValue);
        TextView windText = (TextView) findViewById(R.id.windValue);
        ImageView weatherIcon = (ImageView) findViewById(R.id.weatherIconMain);
        TextView cityName = (TextView) findViewById(R.id.locationName);
        TextView realFeelText = (TextView) findViewById(R.id.realFeelValue);
        cityName.setText(city);
        switch (city) {
            case "Barcelona":
                temperatureText.setText("18º");
                maxTemperatureText.setText("21º");
                minTemperatureText.setText("12º");
                descriptionText.setText("Sunny");
                humidityText.setText("54%");
                windText.setText("17 km/h");
                weatherIcon.setImageResource(R.mipmap.fair);
                realFeelText.setText("20º");
                break;
            case "New York":
                temperatureText.setText("9º");
                maxTemperatureText.setText("14º");
                minTemperatureText.setText("5º");
                descriptionText.setText("Cloudy");
                humidityText.setText("76%");
                windText.setText("3 km/h");
                weatherIcon.setImageResource(R.mipmap.cloudy);
                realFeelText.setText("10º");
                break;
            case "London":
                temperatureText.setText("13º");
                maxTemperatureText.setText("16º");
                minTemperatureText.setText("8º");
                descriptionText.setText("Rain");
                humidityText.setText("80%");
                windText.setText("10 km/h");
                weatherIcon.setImageResource(R.mipmap.rain);
                realFeelText.setText("11º");
                break;
            case "Paris":
                temperatureText.setText("18º");
                maxTemperatureText.setText("19º");
                minTemperatureText.setText("10º");
                descriptionText.setText("Partly cloudy");
                humidityText.setText("54%");
                windText.setText("25 km/h");
                weatherIcon.setImageResource(R.mipmap.partly_cloudy);
                realFeelText.setText("16º");
                break;
            case "Tokyo":
                temperatureText.setText("18º");
                maxTemperatureText.setText("25º");
                minTemperatureText.setText("15º");
                descriptionText.setText("Rain");
                humidityText.setText("90%");
                windText.setText("2 km/h");
                weatherIcon.setImageResource(R.mipmap.rain);
                realFeelText.setText("17º");
                break;
        }

        loadHourlyForecast(city);
        loadDailyForecast(city);
    }

    private void loadHourlyForecast(String city) {
        LinearLayout hourlyLinearLayoutParent = (LinearLayout) findViewById(R.id.hourlyParentLayout);
        hourlyLinearLayoutParent.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(3, 3, 3, 3);


        for (int i = 0; i < 24; ++i) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            TextView hour = new TextView(this);
            String hourText = String.valueOf(i) + ":00";
            hour.setText(hourText);
            hour.setGravity(Gravity.CENTER);
            ll.addView(hour);

            TextView temp = new TextView(this);
            String tempText = String.valueOf(i + 3) + "º";
            temp.setText(tempText);
            temp.setGravity(Gravity.CENTER);
            ll.addView(temp);

            ImageView icon = new ImageView(this);

            if (i % 2 == 0) {
                icon.setImageResource(R.mipmap.fog);
            } else {
                icon.setImageResource(R.mipmap.thunderstorms);
            }
            ll.addView(icon);

            hourlyLinearLayoutParent.addView(ll, layoutParams);
        }
    }

    private void loadDailyForecast(String city) {
        LinearLayout dailyLinearLayoutParent = (LinearLayout) findViewById(R.id.dailyParentLayout);
        dailyLinearLayoutParent.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(25, 10, 25, 10);

        for (int i = 0; i < 5; ++i) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            TextView day = new TextView(this);
            String dayText = String.valueOf(i + 1) + "/04";
            day.setText(dayText);
            day.setGravity(Gravity.CENTER);
            ll.addView(day);

            TextView temp = new TextView(this);
            String tempText = String.valueOf(i + 3) + "º " + String.valueOf(i + 10) + "º";
            temp.setText(tempText);
            temp.setGravity(Gravity.CENTER);
            ll.addView(temp);

            ImageView icon = new ImageView(this);


            TextView description = new TextView(this);
            if (i % 2 == 0) {
                description.setText("Mostly cloudy");
                icon.setImageResource(R.mipmap.mostly_cloudy);
            } else {
                description.setText("Rain");
                icon.setImageResource(R.mipmap.rain);
            }
            description.setGravity(Gravity.CENTER);
            ll.addView(description);

            ll.addView(icon);

            dailyLinearLayoutParent.addView(ll, layoutParams);
        }
    }


    private List<String> loadCities() {
        List<String> result = new ArrayList<>();
        result.add("Barcelona");
        result.add("New York");
        result.add("Paris");
        result.add("London");
        result.add("Tokyo");
        return result;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_add_task) {
            Intent intent = new Intent(this, AddCityActivity.class);
            startActivity(intent);
        } else {

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String city = (String) item.getTitle();
        LinearLayout layout = (LinearLayout) findViewById(R.id.menuHeaderLayout);
        TextView text = (TextView) findViewById(R.id.menuHeaderText);
        text.setText(city);
        if (city.equals(cities.get(0))) {
            loadCurrentWeather(city);
            layout.setBackgroundResource(R.mipmap.fair_wallpaper);
        } else if (city.equals(cities.get(1))) {
            loadCurrentWeather(city);
            layout.setBackgroundResource(R.mipmap.cloudy_wallpaper);
        } else if (city.equals(cities.get(2))) {
            loadCurrentWeather(city);
            layout.setBackgroundResource(R.mipmap.partly_cloudy_wallpaper);
        } else if (city.equals(cities.get(3))) {
            loadCurrentWeather(city);
            layout.setBackgroundResource(R.mipmap.rain_wallpaper);
        } else if (city.equals(cities.get(4))) {
            loadCurrentWeather(city);
            layout.setBackgroundResource(R.mipmap.rain_wallpaper);
        } else if (city.equals(getResources().getString(R.string.action_settings))) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.example.oriolgasset.weatherforecast;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oriolgasset.utils.WeatherForecastUtils;
import com.example.oriolgasset.weatherservices.ApixuClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.weatherlibrary.datamodel.Current;
import com.weatherlibrary.datamodel.Forecastday;
import com.weatherlibrary.datamodel.Hour;
import com.weatherlibrary.datamodel.WeatherModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<String> cities = new ArrayList<>();
    private ApixuClient weatherClient;
    private TextView temperatureText;
    private TextView maxTemperatureText;
    private TextView minTemperatureText;
    private TextView descriptionText;
    private TextView humidityText;
    private TextView windText;
    private ImageView weatherIcon;
    private TextView realFeelText;
    private TextView pressureText;
    private TextView cloudsText;
    private TextView precipitationsText;
    private TextView lastUpdatedText;
    private SharedPreferences sharedPreferences;
    private String defaultCity;
    private boolean weatherLoaded = false;
    private Menu m;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private Typeface typeface;
    private TextView toolbarTitle;
    private String cityName;
    private LinearLayout menuHeaderLayout;
    private Boolean showWeatherInfo = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.primary_dark));
        this.setTaskDescription(taskDesc);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        if (drawer != null) {
            drawer.findViewById(R.id.citiesMenu);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        m = null;

        if (navigationView != null) {
            m = navigationView.getMenu();
        }

        temperatureText = (TextView) findViewById(R.id.temperatureText);
        maxTemperatureText = (TextView) findViewById(R.id.maxTempValue);
        minTemperatureText = (TextView) findViewById(R.id.minTempValue);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        humidityText = (TextView) findViewById(R.id.humidityValue);
        windText = (TextView) findViewById(R.id.windValue);
        weatherIcon = (ImageView) findViewById(R.id.weatherIconMain);
        realFeelText = (TextView) findViewById(R.id.realFeelValue);
        pressureText = (TextView) findViewById(R.id.pressureValue);
        precipitationsText = (TextView) findViewById(R.id.precipitationsValue);
        cloudsText = (TextView) findViewById(R.id.cloudValue);
        lastUpdatedText = (TextView) findViewById(R.id.lastUpdatedValue);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        menuHeaderLayout = (LinearLayout) findViewById(R.id.menuHeaderLayout);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        if (mySwipeRefreshLayout != null) {
            mySwipeRefreshLayout.setColorSchemeResources(R.color.primary);
        }

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        loadCurrentWeather(cityName);
                                    }
                                });
                            }
                        }).start();
                    }
                }
        );

        sharedPreferences = getSharedPreferences("weatherForecastPreferences", MODE_PRIVATE);

        weatherClient = new ApixuClient();

        if (!weatherLoaded) {
            cities = loadCities();
            addCitiesToMenu(m, cities);
            cityName = defaultCity;
            loadCurrentWeather(defaultCity);
            loadAndCachePlaces();
        }
        weatherLoaded = true;
    }

    private void addCitiesToMenu(Menu menu, List<String> cities) {
        TextView text = (TextView) findViewById(R.id.menuHeaderText);
        if (text != null) {
            text.setText(defaultCity);
        }
        menu.add(R.id.citiesMenu, Menu.FIRST, Menu.NONE, defaultCity);
        for (String cityName : cities) {
            if (!cityName.equals(defaultCity))
                menu.add(R.id.citiesMenu, Menu.FIRST, Menu.NONE, cityName);
        }
        menu.add(R.id.group_settings, Menu.FIRST, Menu.NONE, getString(R.string.edit_places)).setIcon(R.mipmap.ic_mode_edit_black_48dp);
        menu.add(R.id.group_settings, Menu.FIRST, Menu.NONE, R.string.action_settings).setIcon(R.mipmap.ic_settings_black_48dp);
    }


    private void loadCurrentWeather(String city) {

        clearInfo();
        cityName = city;
        new getWeatherList().execute(city);
        mySwipeRefreshLayout.setRefreshing(false);
    }

    private void loadHourlyForecast(WeatherModel weather) {
        LinearLayout hourlyLinearLayoutParent = (LinearLayout) findViewById(R.id.hourlyParentLayout);
        if (hourlyLinearLayoutParent != null) {
            hourlyLinearLayoutParent.removeAllViews();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(22, 10, 22, 10);

        if (hourlyLinearLayoutParent != null) {
            int i = 0;
            int j = 0;
            while (i < 24) {
                for (Hour hour : weather.getForecast().getForecastday().get(j).getHour()) {
                    if (hour.time_epoch > weather.getLocation().localtime_epoch && i < 24) {
                        LinearLayout ll = new LinearLayout(this);
                        ll.setOrientation(LinearLayout.VERTICAL);

                        TextView hourView = new TextView(this);
                        String hourText = String.format("%s", hour.getTime().substring(10));
                        hourView.setText(hourText);
                        hourView.setGravity(Gravity.CENTER);
                        hourView.setTypeface(typeface);
                        ll.addView(hourView);

                        ImageView icon = new ImageView(this);
                        icon.setImageResource(weatherClient.getImageData(hour.getCondition()));
                        ll.addView(icon);

                        TextView temp = new TextView(this);
                        String tempText = String.format("%sº", String.valueOf(hour.getTempC()));
                        temp.setText(tempText);
                        temp.setGravity(Gravity.CENTER);
                        temp.setTypeface(typeface);
                        ll.addView(temp);

                        hourlyLinearLayoutParent.addView(ll, layoutParams);
                        ++i;
                    }
                }
                ++j;
            }
        }
    }

    private void loadDailyForecast(WeatherModel weather) {
        LinearLayout dailyLinearLayoutParent = (LinearLayout) findViewById(R.id.dailyParentLayout);
        if (dailyLinearLayoutParent != null) {
            dailyLinearLayoutParent.removeAllViews();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(35, 15, 35, 15);
            for (Forecastday day : weather.getForecast().getForecastday()) {
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);

                TextView dayView = new TextView(this);
                String[] dayText = day.getDate().substring(6).split("-");
                dayView.setText(dayText[1] + "/" + dayText[0]);
                dayView.setGravity(Gravity.CENTER);
                dayView.setTypeface(typeface);
                ll.addView(dayView);

                LinearLayout llaux = new LinearLayout(this);
                llaux.setOrientation(LinearLayout.HORIZONTAL);

                TextView maxTemp = new TextView(this);
                String tempText = String.format("%sº ", String.valueOf(day.getDay().maxtemp_c));
                maxTemp.setText(tempText);
                maxTemp.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
                maxTemp.setTextSize(12);
                dayView.setTypeface(typeface);
                llaux.addView(maxTemp);

                TextView minTemp = new TextView(this);
                String minTempText = String.format("%sº", String.valueOf(day.getDay().mintemp_c));
                minTemp.setText(minTempText);
                minTemp.setTextColor(ContextCompat.getColor(this, R.color.secondary_text));
                minTemp.setTextSize(12);
                dayView.setTypeface(typeface);
                llaux.addView(minTemp);

                llaux.setGravity(Gravity.CENTER);
                ll.addView(llaux);

                ImageView icon = new ImageView(this);
                icon.setImageResource(weatherClient.getImageData(day.getDay().getCondition()));
                ll.addView(icon);

                dailyLinearLayoutParent.addView(ll, layoutParams);
            }
        }
    }

    private List<String> loadCities() {
        List<String> result = new ArrayList<>();
        defaultCity = sharedPreferences.getString("defaultCity", "Barcelona, ES");
        Set<String> citiesList = sharedPreferences.getStringSet("citiesList", new LinkedHashSet<String>());
        LinkedHashSet<String> citiesAux = new LinkedHashSet<>(citiesList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!citiesAux.contains(defaultCity)) {
            citiesAux.add(defaultCity);
            editor.putStringSet("citiesList", citiesAux);
        }
        for (String cityName : citiesAux) {
            result.add(cityName);
        }
        editor.commit();
        return result;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
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
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String city = (String) item.getTitle();
        TextView text = (TextView) findViewById(R.id.menuHeaderText);
        if (text != null) {
            text.setText(city);
        }
        cityName = city;
        // TODO afegir imatge background header + icona del temps

        if (city.equals(getResources().getString(R.string.action_settings))) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (city.equals(getResources().getString(R.string.edit_places))) {
            Intent intent = new Intent(this, EditPlacesActivity.class);
            startActivityForResult(intent, 2);
        } else {
            showWeatherInfo = true;
            loadCurrentWeather(city);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        showWeatherInfo = true;
        if (resultCode == 1) {
            cities = loadCities();
            m.clear();
            clearInfo();
            addCitiesToMenu(m, cities);
            cityName = sharedPreferences.getString("newCity", defaultCity);
            loadCurrentWeather(cityName);
        } else if (resultCode == 2) {
            cities = loadCities();
            m.clear();
            clearInfo();
            addCitiesToMenu(m, cities);
            cityName = sharedPreferences.getString("defaultCity", defaultCity);
            loadCurrentWeather(cityName);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void clearInfo() {
        temperatureText.setText("");
        maxTemperatureText.setText("");
        minTemperatureText.setText("");
        descriptionText.setText("");
        humidityText.setText("");
        windText.setText("");
        weatherIcon.setImageResource(R.mipmap.unknown);
        // menuHeaderLayout.setBackgroundResource(weatherClient.getBackgroundImage(image));
        realFeelText.setText("");
        pressureText.setText("");
        precipitationsText.setText("");
        cloudsText.setText("");
        lastUpdatedText.setText("");
        toolbarTitle.setText("");
    }

    public void loadAndCachePlaces() {
        for (String city : cities) {
            new getWeatherList().execute(city);
        }
    }

    public class getWeatherList extends AsyncTask<String, Void, WeatherModel> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            if (showWeatherInfo)
                dialog = ProgressDialog.show(MainActivity.this, "Loading", "Please Wait", false, false);
        }

        @Override
        protected WeatherModel doInBackground(String... params) {
            WeatherModel result = null;
            String city = params[0].replace(", ", ",");
            city = city.substring(0, 1).toUpperCase() + city.substring(1, city.length() - 2).toLowerCase() + city.substring(city.length() - 2);
            String data = sharedPreferences.getString(params[0], "");
            if (data.equals("")) {
                if (WeatherForecastUtils.isConnected(getBaseContext())) {
                    data = weatherClient.getWeatherData("forecast", null, city, 7);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(params[0], data);
                    editor.commit();
                } else {
                    Toast.makeText(MainActivity.this, R.string.interntet_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Gson gson = new GsonBuilder().create();
                JSONObject jObj;
                String dateString;
                try {
                    jObj = new JSONObject(data);
                    if (jObj.has("current")) {
                        JSONObject curObj = jObj.getJSONObject("current");
                        Current current = gson.fromJson(curObj.toString(), Current.class);
                        dateString = current.getLastUpdated();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        Date startDate = null;
                        try {
                            startDate = df.parse(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        cal.add(Calendar.HOUR, -5);
                        Date fiveHourBack = cal.getTime();
                        if (fiveHourBack.compareTo(startDate) >= 0) {
                            data = weatherClient.getWeatherData("forecast", null, city, 7);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (params[0].equalsIgnoreCase(cityName)) {
                result = weatherClient.parseJSON(data);
            }
            return result;
        }

        @Override
        protected void onPostExecute(WeatherModel weather) {
            String[] locationId = cityName.split(",");
            if (weather != null && weather.getLocation() != null && locationId[0].equalsIgnoreCase(weather.getLocation().name)) {
                if (showWeatherInfo) {
                    temperatureText.setText(String.format("%sº", String.valueOf(weather.getCurrent().temp_c)));
                    maxTemperatureText.setText(String.format("%sº", String.valueOf(weather.getForecast().getForecastday().get(0).getDay().maxtemp_c)));
                    minTemperatureText.setText(String.format("%sº", String.valueOf(weather.getForecast().getForecastday().get(0).getDay().mintemp_c)));
                    descriptionText.setText(weather.getCurrent().getCondition().getText());
                    humidityText.setText(String.format("%s%%", String.valueOf(weather.getCurrent().humidity)));
                    windText.setText(String.format("%skm/h %dº %s", weather.getCurrent().wind_kph, weather.getCurrent().wind_degree, weather.getCurrent().wind_dir));
                    int image = weatherClient.getImageData(weather.getCurrent().getCondition());
                    weatherIcon.setImageResource(image);
                    // menuHeaderLayout.setBackgroundResource(weatherClient.getBackgroundImage(image));
                    realFeelText.setText(String.format("%sº", String.valueOf(weather.getCurrent().feelslike_c)));
                    pressureText.setText(String.format("%smb", weather.getCurrent().pressure_mb));
                    precipitationsText.setText(String.format("%smm", weather.getCurrent().precip_mm));
                    cloudsText.setText(String.valueOf(weather.getCurrent().cloud));
                    lastUpdatedText.setText((weather.getCurrent().last_updated));
                    toolbarTitle.setText(cityName);
                    loadHourlyForecast(weather);
                    loadDailyForecast(weather);
                    showWeatherInfo = false;
                }
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.apixu_errpr), Toast.LENGTH_LONG).show();
            }
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
        }
    }
}



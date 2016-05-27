package com.example.oriolgasset.weatherforecast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oriolgasset.utils.SwipeDetector;
import com.example.oriolgasset.utils.WeatherForecastUtils;
import com.example.oriolgasset.weatherservices.ApixuClient;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.weatherlibrary.datamodel.Forecastday;
import com.weatherlibrary.datamodel.Hour;
import com.weatherlibrary.datamodel.WeatherModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<String> cities = new ArrayList<> ();
    private ApixuClient weatherClient;
    private TextView temperatureText;
    private TextView maxTemperatureText;
    private TextView minTemperatureText;
    private TextView descriptionText;
    private TextView humidityText;
    private TextView windText;
    private ImageView weatherIcon;
    private TextView cityName;
    private TextView realFeelText;
    private TextView pressureText;
    private TextView cloudsText;
    private TextView precipitationsText;
    private TextView lastUpdatedText;
    private SharedPreferences sharedPreferences;
    private String defaultCity;
    private WeatherModel weather;
    private boolean weatherLoaded = false;
    private Menu m;
    private SwipeDetector swipeDetector;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar ().setDisplayShowHomeEnabled (true);

        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState ();
        if (drawer != null) {
            drawer.findViewById (R.id.citiesMenu);
        }

        NavigationView navigationView = (NavigationView) findViewById (R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener (this);
        }

        swipeDetector = new SwipeDetector ();
        m = null;

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if (swipeDetector.swipeDetected ()) {
                    if (swipeDetector.getAction () == SwipeDetector.Action.RL) {

                    } else {

                    }
                }
            }
        };


        if (navigationView != null) {
            m = navigationView.getMenu ();
            navigationView.setOnTouchListener (swipeDetector);
            navigationView.setOnClickListener ((View.OnClickListener) listener);
        }
        temperatureText = (TextView) findViewById (R.id.temperatureText);
        maxTemperatureText = (TextView) findViewById (R.id.maxTempValue);
        minTemperatureText = (TextView) findViewById (R.id.minTempValue);
        descriptionText = (TextView) findViewById (R.id.descriptionText);
        humidityText = (TextView) findViewById (R.id.humidityValue);
        windText = (TextView) findViewById (R.id.windValue);
        weatherIcon = (ImageView) findViewById (R.id.weatherIconMain);
        cityName = (TextView) findViewById (R.id.locationName);
        realFeelText = (TextView) findViewById (R.id.realFeelValue);
        pressureText = (TextView) findViewById (R.id.pressureValue);
        precipitationsText = (TextView) findViewById (R.id.precipitationsValue);
        cloudsText = (TextView) findViewById (R.id.cloudValue);
        lastUpdatedText = (TextView) findViewById (R.id.lastUpdatedValue);

        sharedPreferences = getSharedPreferences ("weatherForecastPreferences", MODE_PRIVATE);

        weatherClient = new ApixuClient ();

        if (!weatherLoaded) {
            cities = loadCities ();
            addCitiesToMenu (m, cities);
            loadCurrentWeather (defaultCity);
        }
        weatherLoaded = true;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder (this).addApi (AppIndex.API).build ();
    }

    private void addCitiesToMenu(Menu menu, List<String> cities) {
        TextView text = (TextView) findViewById (R.id.menuHeaderText);
        if (text != null) {
            text.setText (defaultCity);
        }
        menu.add (R.id.citiesMenu, Menu.FIRST, Menu.NONE, defaultCity);
        for (String cityName : cities) {
            if (!cityName.equals (defaultCity))
                menu.add (R.id.citiesMenu, Menu.FIRST, Menu.NONE, cityName);
        }
        menu.add (R.id.group_settings, Menu.FIRST, Menu.NONE, R.string.action_settings).setIcon (R.mipmap.ic_settings_black_48dp);
    }

    private void loadCurrentWeather(String city) {
        cityName.setText (city);
        if (WeatherForecastUtils.isConnected (this)) {
            weather = weatherClient.getWeather (city);
            if (weather.getLocation () != null) {
                temperatureText.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().temp_c)));
                maxTemperatureText.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().maxtemp_c)));
                minTemperatureText.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().mintemp_c)));
                descriptionText.setText (weather.getCurrent ().getCondition ().getText ());
                humidityText.setText (String.format ("%s%%", String.valueOf (weather.getCurrent ().humidity)));
                windText.setText (String.format ("%skm/h %dº %s", weather.getCurrent ().wind_kph, weather.getCurrent ().wind_degree, weather.getCurrent ().wind_dir));
                weatherIcon.setImageResource (weatherClient.getImageData (weather.getCurrent ().getCondition ()));
                realFeelText.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().feelslike_c)));
                pressureText.setText (String.format ("%smb", weather.getCurrent ().pressure_mb));
                precipitationsText.setText (String.format ("%smm", weather.getCurrent ().precip_mm));
                cloudsText.setText (String.valueOf (weather.getCurrent ().cloud));
                lastUpdatedText.setText ((weather.getCurrent ().last_updated));
                loadHourlyForecast (weather);
                loadDailyForecast (weather);
            }
        } else {
            Toast.makeText (MainActivity.this, "Weather information could not be retrieved", Toast.LENGTH_SHORT).show ();
        }
    }

    private void loadHourlyForecast(WeatherModel weather) {
        LinearLayout hourlyLinearLayoutParent = (LinearLayout) findViewById (R.id.hourlyParentLayout);
        if (hourlyLinearLayoutParent != null) {
            hourlyLinearLayoutParent.removeAllViews ();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins (35, 10, 35, 10);

        if (hourlyLinearLayoutParent != null) {
            int i = 0;
            int j = 0;
            while (i < 24) {
                for (Hour hour : weather.getForecast ().getForecastday ().get (j).getHour ()) {
                    if (hour.time_epoch > weather.getLocation ().localtime_epoch && i < 24) {
                        LinearLayout ll = new LinearLayout (this);
                        ll.setOrientation (LinearLayout.VERTICAL);

                        TextView hourView = new TextView (this);
                        String hourText = String.format ("%s", hour.getTime ().substring (10));
                        hourView.setText (hourText);
                        hourView.setGravity (Gravity.CENTER);
                        ll.addView (hourView);

                        ImageView icon = new ImageView (this);
                        icon.setImageResource (weatherClient.getImageData (hour.getCondition ()));
                        ll.addView (icon);

                        TextView temp = new TextView (this);
                        String tempText = String.format ("%sº", String.valueOf (hour.getTempC ()));
                        temp.setText (tempText);
                        temp.setGravity (Gravity.CENTER);
                        ll.addView (temp);

                        hourlyLinearLayoutParent.addView (ll, layoutParams);
                        ++i;
                    }
                }
                ++j;
            }
        }
    }

    private void loadDailyForecast(WeatherModel weather) {
        LinearLayout dailyLinearLayoutParent = (LinearLayout) findViewById (R.id.dailyParentLayout);
        if (dailyLinearLayoutParent != null) {
            dailyLinearLayoutParent.removeAllViews ();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins (35, 15, 35, 15);
            for (Forecastday day : weather.getForecast ().getForecastday ()) {
                LinearLayout ll = new LinearLayout (this);
                ll.setOrientation (LinearLayout.VERTICAL);

                TextView dayView = new TextView (this);
                String dayText = day.getDate ().substring (6).replace ('-', '/');
                dayView.setText (dayText);
                dayView.setGravity (Gravity.CENTER);
                ll.addView (dayView);

                LinearLayout llaux = new LinearLayout (this);
                llaux.setOrientation (LinearLayout.HORIZONTAL);

                TextView maxTemp = new TextView (this);
                String tempText = String.format ("%sº ", String.valueOf (day.getDay ().maxtemp_c));
                maxTemp.setText (tempText);
                maxTemp.setTextColor (ContextCompat.getColor (this, R.color.primary_text));
                llaux.addView (maxTemp);

                TextView minTemp = new TextView (this);
                String minTempText = String.format ("%sº", String.valueOf (day.getDay ().mintemp_c));
                minTemp.setText (minTempText);
                minTemp.setTextColor (ContextCompat.getColor (this, R.color.secondary_text));
                llaux.addView (minTemp);

                llaux.setGravity (Gravity.CENTER);
                ll.addView (llaux);

                ImageView icon = new ImageView (this);
                icon.setImageResource (weatherClient.getImageData (day.getDay ().getCondition ()));
                ll.addView (icon);

                dailyLinearLayoutParent.addView (ll, layoutParams);
            }
        }
    }


    private List<String> loadCities() {
        List<String> result = new ArrayList<> ();
        defaultCity = sharedPreferences.getString ("defaultCity", "Barcelona");
        Set<String> citiesList = sharedPreferences.getStringSet ("citiesList", new LinkedHashSet<String> ());
        LinkedHashSet<String> citiesAux = new LinkedHashSet<> (citiesList);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        if (citiesAux != null) {
            if (!citiesAux.contains (defaultCity)) {
                citiesAux.add (defaultCity);
                editor.putStringSet ("citiesList", citiesAux);
            }
            for (String cityName : citiesAux) {
                result.add (cityName);
            }

        } else {
            result.add ("Barcelona");
            result.add ("New York");
            result.add ("Paris");
            result.add ("London");
            result.add ("Tokyo");
            citiesList = new LinkedHashSet<> (citiesAux);
            editor.putStringSet ("citiesList", citiesList);
            editor.putString ("defaultCity", "Barcelona");
        }
        editor.commit ();
        return result;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen (GravityCompat.START)) {
            drawer.closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId () == R.id.action_add_task) {
            Intent intent = new Intent (this, AddCityActivity.class);
            startActivityForResult (intent, 1);
        }
        return super.onOptionsItemSelected (item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String city = (String) item.getTitle ();
        TextView text = (TextView) findViewById (R.id.menuHeaderText);
        if (text != null) {
            text.setText (city);
        }

        if (swipeDetector.swipeDetected ()) {
            if (swipeDetector.getAction () == SwipeDetector.Action.RL) {

            } else {

            }
        }
        // TODO afegir imatge background header + icona del temps

        if (city.equals (getResources ().getString (R.string.action_settings))) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity (intent);
        } else {
            loadCurrentWeather (city);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById (R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer (GravityCompat.START);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            cities = loadCities ();
            m.clear ();
            addCitiesToMenu (m, cities);
            loadCurrentWeather (cities.get (cities.size () - 1));
        }
    }

    @Override
    protected void onResume() {
        super.onResume ();
    }

    @Override
    public void onStart() {
        super.onStart ();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect ();
        Action viewAction = Action.newAction (
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse ("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse ("android-app://com.example.oriolgasset.weatherforecast/http/host/path")
        );
        AppIndex.AppIndexApi.start (client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop ();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction (
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse ("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse ("android-app://com.example.oriolgasset.weatherforecast/http/host/path")
        );
        AppIndex.AppIndexApi.end (client, viewAction);
        client.disconnect ();
    }
}

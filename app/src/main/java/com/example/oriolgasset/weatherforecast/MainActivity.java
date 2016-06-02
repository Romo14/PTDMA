package com.example.oriolgasset.weatherforecast;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oriolgasset.utils.WeatherForecastUtils;
import com.example.oriolgasset.weatherservices.ApixuClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private List<String> cities = new ArrayList<> ();
    private ApixuClient weatherClient;
    private TextView temperatureText;
    private TextView maxTemperatureText;
    private TextView minTemperatureText;
    private TextView descriptionText;
    private ImageView weatherIcon;
    private TextView realFeelText;
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
    private Boolean reloadWeatherInfo = false;
    private Map<String, LatLng> citiesCoordinates;
    private TextView menuHeaderText;
    private LinearLayout humidityDetail;
    private LinearLayout pressureDetail;
    private LinearLayout windDetail;
    private LinearLayout precipitationsDetail;
    private LinearLayout cloudsDetail;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);
        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
        getSupportActionBar ().setDisplayShowHomeEnabled (true);

        Bitmap bm = BitmapFactory.decodeResource (getResources (), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription (getString (R.string.app_name), bm, getResources ().getColor (R.color.primary_dark));
        this.setTaskDescription (taskDesc);

        typeface = Typeface.createFromAsset (getAssets (), "fonts/Roboto-Light.ttf");

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

        m = null;

        if (navigationView != null) {
            m = navigationView.getMenu ();
        }

        temperatureText = (TextView) findViewById (R.id.temperatureText);
        maxTemperatureText = (TextView) findViewById (R.id.maxTempValue);
        minTemperatureText = (TextView) findViewById (R.id.minTempValue);
        descriptionText = (TextView) findViewById (R.id.descriptionText);
        weatherIcon = (ImageView) findViewById (R.id.weatherIconMain);
        realFeelText = (TextView) findViewById (R.id.realFeelValue);
        lastUpdatedText = (TextView) findViewById (R.id.lastUpdatedValue);
        toolbarTitle = (TextView) findViewById (R.id.toolbar_title);
        menuHeaderLayout = (LinearLayout) findViewById (R.id.menuHeaderLayout);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById (R.id.swiperefresh);
        if (mySwipeRefreshLayout != null) {
            mySwipeRefreshLayout.setColorSchemeResources (R.color.primary);
        }

        mySwipeRefreshLayout.setOnRefreshListener (
                new SwipeRefreshLayout.OnRefreshListener () {
                    @Override
                    public void onRefresh() {
                        mySwipeRefreshLayout.setRefreshing (true);
                        new Thread (new Runnable () {
                            @Override
                            public void run() {
                                runOnUiThread (new Runnable () {
                                    @Override
                                    public void run() {
                                        showWeatherInfo = true;
                                        reloadWeatherInfo = true;
                                        loadCurrentWeather (cityName);
                                    }
                                });
                            }
                        }).start ();
                    }
                }
        );

        humidityDetail = (LinearLayout) findViewById (R.id.humidityDetail);
        pressureDetail = (LinearLayout) findViewById (R.id.pressureDetail);
        windDetail = (LinearLayout) findViewById (R.id.windDetail);
        precipitationsDetail = (LinearLayout) findViewById (R.id.precipitationsDetail);
        cloudsDetail = (LinearLayout) findViewById (R.id.cloudsDetail);

        sharedPreferences = getSharedPreferences ("weatherForecastPreferences", MODE_PRIVATE);

        buildGoogleApiClient ();

        weatherClient = new ApixuClient ();

        if (!weatherLoaded) {
            cities = loadCities ();
            addCitiesToMenu (m, cities);
            cityName = defaultCity;
            loadCurrentWeather (defaultCity);
            loadAndCachePlaces ();
        }
        weatherLoaded = true;
    }

    private void addCitiesToMenu(Menu menu, List<String> cities) {
        menuHeaderText = (TextView) findViewById (R.id.menuHeaderText);
        if (menuHeaderText != null) {
            menuHeaderText.setText (defaultCity.split ("=")[0]);
        }
        menu.add (R.id.citiesMenu, Menu.FIRST, Menu.NONE, defaultCity.split ("=")[0]);
        for (String cityName : cities) {
            if (!cityName.equals (defaultCity))
                menu.add (R.id.citiesMenu, Menu.FIRST, Menu.NONE, cityName.split ("=")[0]);
        }
        menu.add (R.id.group_settings, Menu.FIRST, Menu.NONE, getString (R.string.edit_places)).setIcon (R.mipmap.ic_mode_edit_black_48dp);
        menu.add (R.id.group_settings, Menu.FIRST, Menu.NONE, R.string.action_settings).setIcon (R.mipmap.ic_settings_black_48dp);
    }


    private void loadCurrentWeather(String city) {

        //clearInfo();
        cityName = city;
        if (!toolbarTitle.getText ().equals (city)) {
            new getWeatherList ().execute (city);

        }
    }

    private void loadHourlyForecast(WeatherModel weather) {
        LinearLayout hourlyLinearLayoutParent = (LinearLayout) findViewById (R.id.hourlyParentLayout);
        if (hourlyLinearLayoutParent != null) {
            hourlyLinearLayoutParent.removeAllViews ();
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins (22, 10, 22, 10);

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
                        hourView.setTypeface (typeface);
                        ll.addView (hourView);

                        ImageView icon = new ImageView (this);
                        icon.setImageResource (weatherClient.getImageData (hour.getCondition ()));
                        ll.addView (icon);

                        TextView temp = new TextView (this);
                        String tempText = String.format ("%sº", String.valueOf (hour.getTempC ()));
                        temp.setText (tempText);
                        temp.setGravity (Gravity.CENTER);
                        temp.setTypeface (typeface);
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
                String[] dayText = day.getDate ().substring (6).split ("-");
                dayView.setText (dayText[1] + "/" + dayText[0]);
                dayView.setGravity (Gravity.CENTER);
                dayView.setTypeface (typeface);
                ll.addView (dayView);

                LinearLayout llaux = new LinearLayout (this);
                llaux.setOrientation (LinearLayout.HORIZONTAL);

                TextView maxTemp = new TextView (this);
                String tempText = String.format ("%sº ", String.valueOf (day.getDay ().maxtemp_c));
                maxTemp.setText (tempText);
                maxTemp.setTextColor (ContextCompat.getColor (this, R.color.primary_text));
                maxTemp.setTextSize (12);
                dayView.setTypeface (typeface);
                llaux.addView (maxTemp);

                TextView minTemp = new TextView (this);
                String minTempText = String.format ("%sº", String.valueOf (day.getDay ().mintemp_c));
                minTemp.setText (minTempText);
                minTemp.setTextColor (ContextCompat.getColor (this, R.color.secondary_text));
                minTemp.setTextSize (12);
                dayView.setTypeface (typeface);
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
        citiesCoordinates = new ArrayMap<> ();
        defaultCity = sharedPreferences.getString ("defaultCity", "userLocation");
        Set<String> citiesList = sharedPreferences.getStringSet ("citiesList", new LinkedHashSet<String> ());
        LinkedHashSet<String> citiesAux = new LinkedHashSet<> (citiesList);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        if (defaultCity.equals ("userLocation")) {
            loadUserLocationWeather ();
            defaultCity = cityName;
            editor.putString ("defaultCity",defaultCity);
        } else if (!citiesAux.contains (defaultCity)) {
            citiesAux.add (defaultCity);
            editor.putStringSet ("citiesList", citiesAux);

        }
        for (String cityName : citiesAux) {
            String key = cityName.split ("=")[0];
            String[] valueAux = cityName.split ("=")[1].split (",");
            result.add (cityName);
            LatLng value = new LatLng (Double.valueOf (valueAux[0]), Double.valueOf (valueAux[1]));
            citiesCoordinates.put (key, value);
        }
        editor.putString ("defaultCity", defaultCity);
        editor.commit ();
        return result;
    }

    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder (this)
                    .addConnectionCallbacks (this)
                    .addOnConnectionFailedListener (this)
                    .addApi (LocationServices.API)
                    .build ();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect ();
        super.onStart ();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect ();
        super.onStop ();
    }

    @Override
    protected void onPause() {
        super.onPause ();
    }

    private void loadUserLocationWeather() {
        WeatherForecastUtils.checkLocationPermission (this);
        Location location = LocationServices.FusedLocationApi.getLastLocation (
                mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng (location.getLatitude (), location.getLongitude ());
            cityName = "=" + location.getLatitude () + "," + location.getLongitude ();
            loadCurrentWeather (cityName);
        } else {
            Toast.makeText (this, R.string.no_location_detected, Toast.LENGTH_LONG).show ();
        }
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
        menu.clear ();
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
            text.setText (city.split ("=")[0]);
        }
        cityName = city;
        // TODO afegir imatge background header + icona del temps

        if (city.equals (getResources ().getString (R.string.action_settings))) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity (intent);
        } else if (city.equals (getResources ().getString (R.string.edit_places))) {
            Intent intent = new Intent (this, EditPlacesActivity.class);
            startActivityForResult (intent, 2);
        } else {
            showWeatherInfo = true;
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
        showWeatherInfo = true;
        if (resultCode == 1) {
            cities = loadCities ();
            m.clear ();
            clearInfo ();
            addCitiesToMenu (m, cities);
            cityName = sharedPreferences.getString ("newCity", defaultCity);
            loadCurrentWeather (cityName);
        } else if (resultCode == 2) {
            cities = loadCities ();
            m.clear ();
            clearInfo ();
            addCitiesToMenu (m, cities);
            cityName = sharedPreferences.getString ("defaultCity", defaultCity);
            loadCurrentWeather (cityName);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext (CalligraphyContextWrapper.wrap (newBase));
    }

    private void clearInfo() {
        temperatureText.setText ("");
        maxTemperatureText.setText ("");
        minTemperatureText.setText ("");
        descriptionText.setText ("");
        weatherIcon.setImageResource (R.mipmap.unknown);
        // menuHeaderLayout.setBackgroundResource(weatherClient.getBackgroundImage(image));
        realFeelText.setText ("");
        lastUpdatedText.setText ("");
        toolbarTitle.setText ("");
    }

    public void loadAndCachePlaces() {
        for (String city : cities) {
            new getWeatherList ().execute (city);
        }
    }

    private void loadDetailsForecast(WeatherModel weather) {
        /*  pressureText.setText (String.format ("%smb", weather.getCurrent ().pressure_mb));
                    precipitationsText.setText (String.format ("%smm", weather.getCurrent ().precip_mm));
                    cloudsText.setText (String.valueOf (weather.getCurrent ().cloud));
                    humidityText.setText (String.format ("%s%%", String.valueOf (weather.getCurrent ().humidity)));
                    windText.setText (String.format ("%skm/h %dº %s", weather.getCurrent ().wind_kph, weather.getCurrent ().wind_degree, weather.getCurrent ().wind_dir));*/
        TextView text;
        TextView value;
        ImageView icon;

        // Humidity
        text = (TextView) humidityDetail.findViewById (R.id.detailText);
        value = (TextView) humidityDetail.findViewById (R.id.detailValue);
        icon = (ImageView) humidityDetail.findViewById (R.id.imageView);
        text.setText (getString (R.string.humidityText));
        value.setText (String.format ("%s%%", String.valueOf (weather.getCurrent ().humidity)));
        icon.setImageResource (R.mipmap.humidity_icon);

        // Wind
        text = (TextView) windDetail.findViewById (R.id.detailText);
        value = (TextView) windDetail.findViewById (R.id.detailValue);
        icon = (ImageView) windDetail.findViewById (R.id.imageView);
        text.setText (getString (R.string.wind_text));
        value.setText (String.format ("%skm/h %dº %s", weather.getCurrent ().wind_kph, weather.getCurrent ().wind_degree, weather.getCurrent ().wind_dir));
        icon.setImageResource (R.mipmap.wind_icon);

        // Pressure
        text = (TextView) pressureDetail.findViewById (R.id.detailText);
        value = (TextView) pressureDetail.findViewById (R.id.detailValue);
        icon = (ImageView) pressureDetail.findViewById (R.id.imageView);
        text.setText (getString (R.string.pressureText));
        value.setText (String.format ("%smb", weather.getCurrent ().pressure_mb));
        icon.setImageResource (R.mipmap.pressure_icon);

        // Precipitations
        text = (TextView) precipitationsDetail.findViewById (R.id.detailText);
        value = (TextView) precipitationsDetail.findViewById (R.id.detailValue);
        icon = (ImageView) precipitationsDetail.findViewById (R.id.imageView);
        text.setText (getString (R.string.precipText));
        value.setText (String.format ("%smm", weather.getCurrent ().precip_mm));
        icon.setImageResource (R.mipmap.precipitation_icon);

        // Cloud
        text = (TextView) cloudsDetail.findViewById (R.id.detailText);
        value = (TextView) cloudsDetail.findViewById (R.id.detailValue);
        icon = (ImageView) cloudsDetail.findViewById (R.id.imageView);
        text.setText (getString (R.string.cloudtext));
        value.setText (String.valueOf (weather.getCurrent ().cloud) + "%");
        icon.setImageResource (R.mipmap.cloud_icon);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class getWeatherList extends AsyncTask<String, Void, WeatherModel> {


        private ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog (MainActivity.this);
            progDailog.setMessage ("Loading...");
            progDailog.setIndeterminate (false);
            progDailog.setProgressStyle (ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable (true);
            progDailog.show ();
        }

        @Override
        protected WeatherModel doInBackground(String... params) {
            WeatherModel result = null;
            String data = sharedPreferences.getString (params[0], "");
            if (data.equals ("") || reloadWeatherInfo) {
                if (WeatherForecastUtils.isConnected (getBaseContext ())) {
                    data = weatherClient.getWeatherData ("forecast", citiesCoordinates.get (params[0].split ("=")[0]), null, 7);
                    Log.i ("Weather loaded", "no info");
                    SharedPreferences.Editor editor = sharedPreferences.edit ();
                    editor.putString (params[0], data);
                    editor.commit ();
                } else {
                    Toast.makeText (MainActivity.this, R.string.interntet_error, Toast.LENGTH_SHORT).show ();
                }
                reloadWeatherInfo = false;
            } else {
                Gson gson = new GsonBuilder ().create ();
                JSONObject jObj;
                String dateString;
                try {
                    jObj = new JSONObject (data);
                    if (jObj.has ("current")) {
                        JSONObject curObj = jObj.getJSONObject ("current");
                        Current current = gson.fromJson (curObj.toString (), Current.class);
                        dateString = current.getLastUpdated ();
                        DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
                        Date startDate = null;
                        try {
                            startDate = df.parse (dateString);
                        } catch (ParseException e) {
                            e.printStackTrace ();
                        }
                        Calendar cal = Calendar.getInstance ();
                        cal.setTime (new Date ());
                        cal.add (Calendar.HOUR, -3);
                        Date threrHourBack = cal.getTime ();
                        if (threrHourBack.compareTo (startDate) >= 0) {
                            data = weatherClient.getWeatherData ("forecast", citiesCoordinates.get (params[0].split ("=")[0]), null, 7);
                            Log.i ("Weather loaded", "date");
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            }
            result = weatherClient.parseJSON (data);
            return result;
        }

        @Override
        protected void onPostExecute(WeatherModel weather) {
            if (weather != null && weather.getLocation () != null) {
                if (showWeatherInfo) {
                    if (cityName.split ("=")[0].equals ("")) {
                        String[] ll = cityName.split ("=")[1].split (",");
                        LatLng aux = new LatLng (Double.valueOf (ll[0]), Double.valueOf (ll[1]));
                        cityName = WeatherForecastUtils.getCityByLatLang (getBaseContext (), aux) + cityName;
                    }
                    // Main info
                    temperatureText.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().temp_c)));
                    maxTemperatureText.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().maxtemp_c)));
                    minTemperatureText.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().mintemp_c)));
                    descriptionText.setText (weather.getCurrent ().getCondition ().getText ());
                    int image = weatherClient.getImageData (weather.getCurrent ().getCondition ());
                    weatherIcon.setImageResource (image);
                    menuHeaderText = (TextView) findViewById (R.id.menuHeaderText);
                    menuHeaderText.setText (cityName.split ("=")[0]);
                    menuHeaderLayout = (LinearLayout) findViewById (R.id.menuHeaderLayout);
                    menuHeaderLayout.setBackgroundResource (weatherClient.getBackgroundImage (image));
                    realFeelText.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().feelslike_c)));
                    DateFormat dfaux = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
                    Date startDate = null;
                    try {
                        startDate = dfaux.parse (weather.getCurrent ().last_updated);
                    } catch (ParseException e) {
                        e.printStackTrace ();
                    }
                    dfaux = new SimpleDateFormat ("dd/MM HH:mm");
                    String updated = dfaux.format (startDate);
                    lastUpdatedText.setText (updated);
                    toolbarTitle.setText (cityName.split ("=")[0]);

                    // Details
                    loadDetailsForecast (weather);
                    loadHourlyForecast (weather);
                    loadDailyForecast (weather);
                    showWeatherInfo = false;
                }
            } else {
                Toast.makeText (getBaseContext (), getString (R.string.apixu_errpr), Toast.LENGTH_SHORT).show ();
            }
            progDailog.dismiss ();
            mySwipeRefreshLayout.setRefreshing (false);
        }
    }
}



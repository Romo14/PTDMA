package com.example.oriolgasset.weatherforecast;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oriolgasset.utils.CountryCodes;
import com.example.oriolgasset.utils.WeatherForecastUtils;
import com.example.oriolgasset.weatherservices.ApixuClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.weatherlibrary.datamodel.WeatherModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AddCityActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private TextView realFeelTextView;
    private TextView maxTemperatureTextView;
    private TextView minTemperatureTextView;
    private ImageView weatherIconImageView;
    private RelativeLayout mainInfoLayout;
    private PlaceAutocompleteFragment autocompleteFragment;
    private RelativeLayout loadingPanel;
    private ApixuClient weatherClient;
    private SharedPreferences sharedPreferences;
    private String cityName;
    private FloatingActionButton addButton;
    private TextView lastUpdatedText;
    private CountryCodes countryCodes;
    private Boolean searchByLatLng = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_city);

        countryCodes = new CountryCodes ();

        temperatureTextView = (TextView) findViewById (R.id.temperatureText);
        descriptionTextView = (TextView) findViewById (R.id.descriptionText);
        realFeelTextView = (TextView) findViewById (R.id.realFeelValue);
        maxTemperatureTextView = (TextView) findViewById (R.id.maxTempValue);
        minTemperatureTextView = (TextView) findViewById (R.id.minTempValue);
        weatherIconImageView = (ImageView) findViewById (R.id.weatherIconMain);
        lastUpdatedText = (TextView) findViewById ((R.id.lastUpdatedValue));
        mainInfoLayout = (RelativeLayout) findViewById (R.id.mainInfoLayout);
        loadingPanel = (RelativeLayout) findViewById (R.id.loadingPanel);
        addButton = (FloatingActionButton) findViewById (R.id.addCityButton);
        if (addButton != null) {
            addButton.setVisibility (View.INVISIBLE);
        }

        buildGoogleApiClient ();

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager ().findFragmentById (R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener (new PlaceSelectionListener () {
            @Override
            public void onPlaceSelected(Place place) {
                mainInfoLayout.setVisibility (View.GONE);
                cityName = (String) place.getName () + "=" + place.getLatLng ().latitude + "," + place.getLatLng ().longitude;
                getWeatherForecast (place.getLatLng ());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder ()
                .setTypeFilter (AutocompleteFilter.TYPE_FILTER_CITIES)
                .build ();
        autocompleteFragment.setFilter (typeFilter);
        autocompleteFragment.setHint (getString (R.string.city_search_hint));
        ((EditText) autocompleteFragment.getView ().findViewById (R.id.place_autocomplete_search_input)).setTextSize (16.0f);
        ((EditText) autocompleteFragment.getView ().findViewById (R.id.place_autocomplete_search_input)).addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.equals ("")) {
                    addButton.setVisibility (View.GONE);
                } else {
                    addButton.setVisibility (View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sharedPreferences = getSharedPreferences ("weatherForecastPreferences", MODE_PRIVATE);
        loadingPanel.setVisibility (View.GONE);
        mainInfoLayout.setVisibility (View.GONE);
        weatherClient = new ApixuClient ();
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
        LocationServices.FusedLocationApi.removeLocationUpdates (mGoogleApiClient, this);
    }

    private void getWeatherForecast(LatLng latLng) {
        if (WeatherForecastUtils.isConnected (this)) {
            mainInfoLayout.setVisibility (View.GONE);
            new getWeatherList ().execute (latLng);
        }

    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission (this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission (this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale (this,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale (this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale (this,
                    Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            } else {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            }
        }
    }

    public void addCity(View view) {
        if (cityName != null && !cityName.equals ("")) {
            SharedPreferences.Editor editor = sharedPreferences.edit ();
            Set<String> cities = sharedPreferences.getStringSet ("citiesList", new LinkedHashSet<String> ());
            Set<String> citiesAux = new LinkedHashSet<> (cities);
            if (cities.contains (cityName)) {
                Toast.makeText (this, R.string.city_duplicate, Toast.LENGTH_LONG).show ();
                return;
            } else {
                citiesAux.add (cityName);
            }
            cities = new LinkedHashSet<> (citiesAux);
            editor.putStringSet ("citiesList", cities);
            editor.putString ("newCity", cityName);
            editor.commit ();
        }
        setResult (1);
        finish ();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.add_city_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask (this);
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    public void searchCurrentLocation(MenuItem item) {
        mainInfoLayout.setVisibility (View.GONE);
        checkLocationPermission ();
        location = LocationServices.FusedLocationApi.getLastLocation (
                mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng (location.getLatitude (), location.getLongitude ());
            cityName = "=" + location.getLatitude () + "," + location.getLongitude ();
            getWeatherForecast (latLng);
        } else {
            Toast.makeText (this, R.string.no_location_detected, Toast.LENGTH_LONG).show ();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationPermission ();
        location = LocationServices.FusedLocationApi.getLastLocation (
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location locationChanges) {
        if (location.getAccuracy () > this.location.getAccuracy ()) {
            this.location = locationChanges;
        }

    }

    public class getWeatherList extends AsyncTask<LatLng, Void, WeatherModel> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
            loadingPanel.setVisibility (View.VISIBLE);
            dialog = ProgressDialog.show (AddCityActivity.this, "Loading", "Please Wait", false, false);
        }

        @Override
        protected WeatherModel doInBackground(LatLng... params) {
            WeatherModel result;
            String data;
            data = weatherClient.getWeatherData ("forecast", params[0], null, 1);
            result = weatherClient.parseJSON (data);
            return result;
        }

        @Override
        protected void onPostExecute(WeatherModel weather) {
            if (weather != null) {
                if (cityName.split ("=")[0].equals ("")) {
                    String[] ll = cityName.split ("=")[1].split (",");
                    LatLng aux = new LatLng (Double.valueOf (ll[0]), Double.valueOf (ll[1]));
                    cityName = WeatherForecastUtils.getCityByLatLang (getBaseContext (), aux) + cityName;
                } else {
                    String country = countryCodes.getCode (weather.getLocation ().getCountry ());
                    cityName = cityName.split ("=")[0] + ", " + country + "=" + cityName.split ("=")[1];
                }
                temperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().temp_c)));
                descriptionTextView.setText (weather.getCurrent ().getCondition ().getText ());
                realFeelTextView.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().feelslike_c)));
                weatherIconImageView.setImageResource (weatherClient.getImageData (weather.getCurrent ().getCondition ()));
                ((EditText) autocompleteFragment.getView ().findViewById (R.id.place_autocomplete_search_input)).setText (cityName.split ("=")[0]);
                autocompleteFragment.setText (cityName.split ("=")[0]);
                maxTemperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().maxtemp_c)));
                minTemperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().mintemp_c)));
                DateFormat df = new SimpleDateFormat ("yyyy-MM-dd hh:mm");
                Date startDate = null;
                try {
                    startDate = df.parse (weather.getCurrent ().last_updated);
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
                df = new SimpleDateFormat ("dd/MM HH:mm");
                String updated = df.format (startDate);
                lastUpdatedText.setText (updated);
                mainInfoLayout.setVisibility (View.VISIBLE);
                loadingPanel.setVisibility (View.GONE);
            }
            if (dialog != null && dialog.isShowing ())
                dialog.dismiss ();
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext (CalligraphyContextWrapper.wrap (newBase));
    }
}

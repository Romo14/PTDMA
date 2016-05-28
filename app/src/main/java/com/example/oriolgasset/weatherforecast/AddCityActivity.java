package com.example.oriolgasset.weatherforecast;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_add_city);

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
                mainInfoLayout.setVisibility(View.GONE);
                loadingPanel.setVisibility (View.VISIBLE);
                getCityName (place.getLatLng ());
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
        loadingPanel.setVisibility(View.GONE);

        mainInfoLayout.setVisibility (View.INVISIBLE);
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
            WeatherModel weather = weatherClient.getWeather (latLng);
            if (weather != null) {
                temperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().temp_c)));
                descriptionTextView.setText (weather.getCurrent ().getCondition ().getText ());
                realFeelTextView.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().feelslike_c)));
                weatherIconImageView.setImageResource (weatherClient.getImageData (weather.getCurrent ().getCondition ()));
                ((EditText) autocompleteFragment.getView ().findViewById (R.id.place_autocomplete_search_input)).setText (cityName);
                autocompleteFragment.setText(cityName);
                maxTemperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().maxtemp_c)));
                minTemperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().mintemp_c)));
                lastUpdatedText.setText ((weather.getCurrent ().last_updated));
                mainInfoLayout.setVisibility (View.VISIBLE);
            }
        }
        loadingPanel.setVisibility (View.GONE);
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
            editor.putString("newCity",cityName);
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
        loadingPanel.setVisibility (View.VISIBLE);
        mainInfoLayout.setVisibility (View.GONE);
        checkLocationPermission ();
        location = LocationServices.FusedLocationApi.getLastLocation (
                mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng (location.getLatitude (), location.getLongitude ());
            getCityName (latLng);
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

    public void getCityName(LatLng latLng) {
        Geocoder gcd = new Geocoder (this, Locale.US);
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation (latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        if (addresses.size () > 0) {
            String locality = addresses.get (0).getLocality ();
            if (locality == null) {
                locality = addresses.get (0).getSubAdminArea ();
            }
                cityName = locality + ", " + addresses.get (0).getCountryCode ();
        }
    }

}

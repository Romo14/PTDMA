package com.example.oriolgasset.weatherforecast;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.weatherlibrary.datamodel.WeatherModel;

import java.util.HashSet;
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
        mainInfoLayout = (RelativeLayout) findViewById (R.id.mainInfoLayout);
        loadingPanel = (RelativeLayout) findViewById (R.id.loadingPanel);
        addButton = (FloatingActionButton) findViewById (R.id.addCityButton);
        if (addButton != null) {
            addButton.setVisibility (View.GONE);
        }

        buildGoogleApiClient ();

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager ().findFragmentById (R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener (new PlaceSelectionListener () {
            @Override
            public void onPlaceSelected(Place place) {
                loadingPanel.setVisibility (View.VISIBLE);
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
                cityName = weather.getLocation ().getName ();
                temperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().temp_c)));
                descriptionTextView.setText (weather.getCurrent ().getCondition ().getText ());
                realFeelTextView.setText (String.format ("%sº", String.valueOf (weather.getCurrent ().feelslike_c)));
                weatherIconImageView.setImageResource (weatherClient.getImageData (weather.getCurrent ().getCondition ()));
                autocompleteFragment.setText (weather.getLocation ().getName ());
                maxTemperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().maxtemp_c)));
                minTemperatureTextView.setText (String.format ("%sº", String.valueOf (weather.getForecast ().getForecastday ().get (0).getDay ().mintemp_c)));
                mainInfoLayout.setVisibility (View.VISIBLE);
            }
            cityName = ((EditText) autocompleteFragment.getView ().findViewById (R.id.place_autocomplete_search_input)).getText ().toString ();
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
            Set<String> cities = sharedPreferences.getStringSet ("citiesList", new HashSet<String> ());
            cities.add (cityName);
            editor.putStringSet ("citiesList", cities);
            editor.apply ();
        }
        backToMainScreen ();
    }

    private void backToMainScreen() {
        // TODO carregar dades ciutat a pantalla principal
        Intent intent = new Intent (this, MainActivity.class);
        startActivity (intent);
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

}

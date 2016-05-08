package com.example.oriolgasset.weatherforecast;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oriolgasset.model.OpenWeatherMapVO;
import com.example.oriolgasset.weatherservices.OpenWeatherMapService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;


public class AddCityActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_PERMISSION_LOCATION = 1;
    private LocationManager locationManager;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private TextView realFeelTextView;
    private TextView maxTemperatureTextView;
    private TextView minTemperatureTextView;
    private ImageView weatherIconImageView;
    private LinearLayout mainInfoLayout;
    private OpenWeatherMapService openWeatherMapService;
    private OpenWeatherMapVO weatherInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        temperatureTextView = (TextView) findViewById(R.id.temperatureText);
        descriptionTextView = (TextView) findViewById(R.id.descriptionText);
        realFeelTextView = (TextView) findViewById(R.id.realFeelValue);
        maxTemperatureTextView = (TextView) findViewById(R.id.maxTempValue);
        minTemperatureTextView = (TextView) findViewById(R.id.minTempValue);
        weatherIconImageView = (ImageView) findViewById(R.id.weahterIconMain);
        mainInfoLayout = (LinearLayout) findViewById(R.id.mainInfoLayout);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                getWeatherForecast(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setHint(getString(R.string.city_search_hint));

        initiateLocationManager();

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    private void getWeatherForecast(LatLng latLng) {
        openWeatherMapService = new OpenWeatherMapService();
        weatherInfo = openWeatherMapService.getWeather(latLng);
        temperatureTextView.setText(String.valueOf(weatherInfo.temperature.getTemp()));
        descriptionTextView.setText(String.valueOf(weatherInfo.currentCondition.getCondition()));
        realFeelTextView.setText(String.valueOf(weatherInfo.currentCondition.getHumidity()));
    }

    private void initiateLocationManager() {

        checkLocationPermission();

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            }
        }
    }

    public void addCity(View view) {
        backToMainScreen();
    }

    private void backToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_city_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void searchCurrentLocation(MenuItem item) {
        checkLocationPermission();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Toast.makeText(getBaseContext(), "latitud: " + location.getLatitude() + " longitud: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationPermission();
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location locationChanges) {
        if (location.getAccuracy() > this.location.getAccuracy()) {
            this.location = locationChanges;
            Toast.makeText(getBaseContext(), "latitud: " + location.getLatitude() + " longitud: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        getWeatherForecast(latLng);
    }

}

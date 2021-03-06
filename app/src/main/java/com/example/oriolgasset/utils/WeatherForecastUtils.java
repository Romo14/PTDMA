package com.example.oriolgasset.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.oriolgasset.weatherforecast.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oriol on 16/5/2016.
 */
public class WeatherForecastUtils {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String getCityByLatLang(Context ctx, LatLng latLng) {
        Geocoder gcd = new Geocoder(ctx, Locale.US);
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            String locality = addresses.get(0).getLocality();
            if (locality == null) {
                locality = addresses.get(0).getSubAdminArea();
            }
            return locality + ", " + addresses.get(0).getCountryCode();
        }
        return null;
    }

    public static LatLng getCityByName(Context ctx, String name) {
        Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        String city = name.split(",")[0];
        try {
            addresses = gcd.getFromLocationName(city, 1);
        } catch (Exception e) {
            Toast.makeText (ctx, R.string.interntet_error, Toast.LENGTH_SHORT).show ();
            return null;
        }
        if (addresses.size() > 0) {
            return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        }
        return null;
    }

    public static void checkLocationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(activity.getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity.getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.INTERNET) || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
            }
        }
    }


}

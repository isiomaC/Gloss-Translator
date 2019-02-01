package com.chuck.artranslate.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.ArrayList;

public class FusedLocationClient {

    private FusedLocationProviderClient mFusedLocationClient;
    private  Activity activity;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;

    private LocationCallback mLocationCallback;

    private Location mCurrentLocation;
    private ArrayList<Location> mLocations;

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public ArrayList<Location> getmLocations() {
        return mLocations;
    }

    private static final long UPDATE_INTERVAL = 100000; //1min

    private static final long FASTEST_UPDATE_INTERVAL = 5000; //5sec

    public FusedLocationClient(Activity activity){
        this.activity = activity;
    }

    protected LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    public void stopLocationUpdates() {
        if(mLocationCallback != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

//    public Location getTranslateLocation() {
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( activity);
//
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            mFusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//
//                            if (location != null) {
//                                mCurrentLocation = location;
//                            }else{
//                                Log.e("LOCATION IS NULL", "location is null");
//                            }
//                        }
//                    });
//        }
//
//        return mCurrentLocation;
//    }


    public void register() {

        mLocations = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mSettingsClient = LocationServices.getSettingsClient(activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

             mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        mCurrentLocation = locationResult.getLastLocation();
                        mLocations.addAll(locationResult.getLocations());
                    }
                }
             };

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(createLocationRequest());
            mLocationSettingsRequest = builder.build();

            mSettingsClient.checkLocationSettings(mLocationSettingsRequest);

        }

    }

}

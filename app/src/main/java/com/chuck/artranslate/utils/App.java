package com.chuck.artranslate.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import com.chuck.artranslate.R;
import com.chuck.artranslate.authentication.authSActivity;
import com.chuck.artranslate.fragments.CameraFragment;
import com.chuck.artranslate.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

public class App extends Application {

    private static App instance;
    private static LocationManager mLocationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        FlowManager.init(new FlowConfig.Builder(this).build());

        FlowLog.setMinimumLoggingLevel(FlowLog.Level.E);
    }

    public static void SignOut(){
        FirebaseAuth.getInstance().signOut();
        authSActivity.start(instance);
    }

    public boolean isInternetConnected(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo net = null;
        if (manager != null) {
            net = manager.getActiveNetworkInfo();
        }
        return net != null && net.isConnectedOrConnecting();
    }

    public static App getInstance() {
        return instance;
    }

    public static boolean checkLocationPermission(final Activity activity) {

        final String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION};

        mLocationManager = (LocationManager) instance.getSystemService(Activity.LOCATION_SERVICE);

        String[] mProviders = new String[]{LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER};

        if (!mLocationManager.isProviderEnabled(mProviders[0])
                && !mLocationManager.isProviderEnabled(mProviders[1])) {

            MyBroadcastReceiver.sendBroadcast(CameraFragment.LOCATION_ID);

        }

            if (ContextCompat.checkSelfPermission(activity, permissions[0])
                    != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(activity, permissions[1])
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        permissions[0]) || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        permissions[1])) {


                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.title_location_permission)
                            .setMessage(R.string.text_location_permission)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    ActivityCompat.requestPermissions(activity, permissions, MainActivity.MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            })
                            .create()
                            .show();

                } else {
                    ActivityCompat.requestPermissions(activity, permissions, MainActivity.MY_PERMISSIONS_REQUEST_LOCATION);
                }

                return false;
        } else {
                return true;
        }
    }
}

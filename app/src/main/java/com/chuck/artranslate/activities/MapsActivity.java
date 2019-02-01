package com.chuck.artranslate.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chuck.artranslate.R;
import com.chuck.artranslate.dbresources.DBresources;
import com.chuck.artranslate.dbresources.MyDatabase;
import com.chuck.artranslate.utils.FusedLocationClient;
import com.chuck.artranslate.utils.ViewsUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener{

    private GoogleMap mMap;
    private FusedLocationClient location;
    private String fulladdress;

    private TextView btn;

    private static int PATTERN_GAP_LENGTH_PX = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn = findViewById(R.id.startBt);

        location = new FusedLocationClient(this);
        location.register();
    }

    @Override
    protected void onResume() {
        super.onResume();
        location.startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        location.stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<DBresources> res = MyDatabase.getAll();

        LatLng places;

        List<Address> adds = null;

        Geocoder geo = new Geocoder(getApplicationContext());

        for (DBresources r : res) {

            places = new LatLng(r.getLatitude(), r.getLongitude());

            try {
                adds = geo.getFromLocation(r.getLatitude(), r.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            fulladdress = adds.get(0).getAddressLine(0);

            MarkerOptions myMarkerOptions = new MarkerOptions()
                    .position(places)
                    .title(fulladdress)
                    .snippet(r.getDetection() + " > " + r.getTranslation());

            Marker myMarker = mMap.addMarker(myMarkerOptions);
            myMarker.setTag(r);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            btn.setOnClickListener(this);
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places, 18.0f));
            mMap.setOnInfoWindowClickListener(this);
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

        DBresources title = (DBresources) marker.getTag();

        assert title != null;
        ViewsUtil.customDialog(this)
                .setTitle(fulladdress)
                .setMessage(title.getDetection() +"\n" +title.getTranslation())
                .setPositiveButton("Cancel", null)
                .show();

    }

    @Override
    public void onClick(View view) {

        List<Location> allLocations = location.getmLocations();
        List<LatLng> latLngs = new ArrayList<>();

        Log.e("test", allLocations.toString());

        for(int i = 0; i< allLocations.size(); i++){
             latLngs.add(new LatLng(allLocations.get(i).getLatitude(), allLocations.get(i).getLongitude()));
        }

        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(latLngs));

        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());

        polyline.setColor(getResources().getColor(R.color.colorAccent));
        polyline.setJointType(JointType.ROUND);
        polyline.setTag("A");


    }

}

package com.example.locationdemo.mainPackage;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.locationdemo.R;
import com.example.locationdemo.backgroundThreads.LocationThread;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.locationdemo.backgroundThreads.LocationThread.FAILED;
import static com.example.locationdemo.backgroundThreads.LocationThread.SUCCESSFUL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    public static Handler threadHandler;

    private GoogleMap mMap;
    private LatLng location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        initMap();

        setLocation();

    }

    private void setLocation() {
        Intent intent = getIntent();
        location = new LatLng(intent.getDoubleExtra(LATITUDE, 0),
                intent.getDoubleExtra(LONGITUDE, 0));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        mMap.addMarker(new MarkerOptions().position(location).title("you are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    public static void startScreen(Context callingContext, double longitude, double latitude) {
        Intent intent = new Intent(callingContext, MapsActivity.class);
        intent.putExtra(LATITUDE,latitude);
        intent.putExtra(LONGITUDE,longitude);
        callingContext.startActivity(intent);
    }

}
package com.example.locationdemo.backgroundThreads;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.locationdemo.mainPackage.GoogleServicesActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class GoogleAPIThread extends Thread implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "GoogleAPIThread";

    private Activity googleServicesActivity;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private  GoogleApiClient mGoogleApiClient;

    public GoogleAPIThread(Activity googleServicesActivity) {

        this.googleServicesActivity = googleServicesActivity;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(googleServicesActivity);

        initLocationCallback();

        initGoogleAPIClient();
    }

    private void initLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocationResult: got location");
                Log.d(TAG, "onLocationResult: " + Thread.currentThread());
                Location location = locationResult.getLastLocation();
                Message msg = Message.obtain();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                msg.obj = latLng;
                GoogleServicesActivity.threadHandler.sendMessage(msg);
            }
        };
    }

    //google api client is connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        sendRequestLoc();
    }

    //connection is lost
    @Override
    public void onConnectionSuspended(int i) {
        //attempt to re-establish the connection
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());

    }

    @Override
    public void run() {
        mGoogleApiClient.connect();
        Looper.prepare();
        sendRequestLoc();
        Looper.loop();
    }

    public void sendRequestLoc() {

        if (ActivityCompat.checkSelfPermission(googleServicesActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(googleServicesActivity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        initLocRequest();
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }

    public void initLocRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
    }


    public void removeLocUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(googleServicesActivity.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void stopThread() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}

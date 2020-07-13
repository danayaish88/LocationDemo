package com.example.locationdemo.backgroundThreads;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.locationdemo.mainPackage.LocationByGoogleAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;


public class GoogleAPIThread extends Thread implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    FusedLocationProviderClient fusedLocationClient;
    private LocationByGoogleAPI locationByGoogleAPI;

    public GoogleAPIThread(FusedLocationProviderClient fusedLocationClient, LocationByGoogleAPI locationByGoogleAPI) {
        this.fusedLocationClient = fusedLocationClient;
        this.locationByGoogleAPI = locationByGoogleAPI;
    }

    //google api client is connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
      locationByGoogleAPI.sendRequestLoc();
    }

    //connection is lost
    @Override
    public void onConnectionSuspended(int i) {
        //attempt to re-establish the connection
        LocationByGoogleAPI.mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("TAG", "Connection failed. Error: " + connectionResult.getErrorCode());

    }

    @Override
    public void run() {
        super.run();
    }


}

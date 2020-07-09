package com.example.locationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import backgroundThreads.GoogleAPIThread;
import backgroundThreads.GoogleLocationCallback;
import butterknife.BindView;
import butterknife.ButterKnife;


public class LocationByGoogleAPI extends AppCompatActivity {

    public static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleAPIThread backgroundThread;
    private GoogleLocationCallback googleLocationCallback;
    public static Handler threadHandler;

    @BindView(R.id.locationtv)
    TextView locationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_by_google_a_p_i);
        ButterKnife.bind(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        backgroundThread = new GoogleAPIThread(fusedLocationClient, this);
        backgroundThread.start();

        googleLocationCallback = new GoogleLocationCallback();

        instatiateHnadler();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(backgroundThread)
                .addOnConnectionFailedListener(backgroundThread)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

         if(requestCode == 30) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendRequestLoc();
            }
        }
    }


    public void sendRequestLoc() {
        initLocRequest();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermission();
            return;
        }

        fusedLocationClient.requestLocationUpdates(mLocationRequest, googleLocationCallback.mLocationCallback , Looper.getMainLooper());
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET},
                30);
    }

    private void initLocRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
    }

    private void instatiateHnadler() {
        threadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                locationTV.append((String) msg.obj);
            }
        };
    }

}
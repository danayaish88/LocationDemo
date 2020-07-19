package com.example.locationdemo.mainPackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.example.locationdemo.R;

import com.example.locationdemo.backgroundThreads.GoogleAPIThread;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GoogleServicesActivity extends AppCompatActivity {

    public static Handler threadHandler;

    private GoogleAPIThread googleAPIThread;


    @BindView(R.id.locationtv)
    TextView locationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_by_google_a_p_i);
        ButterKnife.bind(this);

        startGoogleAPIThread();

        instatiateHnadler();

    }

    private void startGoogleAPIThread() {
        googleAPIThread = new GoogleAPIThread(this);
        googleAPIThread.start();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        googleAPIThread.removeLocUpdate();

        googleAPIThread.stopThread();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

         if(requestCode == 30) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendRequestLoc();
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET},
                30);
    }

    private void instatiateHnadler() {
        threadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                LatLng latLng = (LatLng)msg.obj;
                locationTV.append(latLng.latitude + " , " + latLng.longitude + "\n");
            }
        };
    }

    public void sendRequestLoc() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermission();
            return;
        }
        googleAPIThread.sendRequestLoc();
    }

    public static void startScreen(Context context) {
        Intent intent = new Intent(context, GoogleServicesActivity.class);
        context.startActivity(intent);
    }

}
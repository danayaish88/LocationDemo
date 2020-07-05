package com.example.locationdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.locationdemo.getLocationTask.FAILED;
import static com.example.locationdemo.getLocationTask.SUCCESSFUL;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {
    public static Handler threadHandler;
    private static final int REQUEST_CODE = 10;

    private LocationManager locationManager;
    private getLocationTask BGthread;


    @BindView(R.id.coordinates)
    TextView coordinatesTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        instatiateHnadler();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        BGthread=new getLocationTask();
        BGthread.start();
    }

    private void instatiateHnadler() {
        threadHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.arg1){
                case SUCCESSFUL:
                    coordinatesTv.append((String) msg.obj);
                    break;
                case FAILED:
                    enableLocationSettings();
                    break;
                default:
                    break;
            }
        }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendRequestLoc();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                sendRequestLoc();
            }
        }

    }

    public void sendRequestLoc() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET}, REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000,          // 5-second interval.
                0,             // 0 meters.
                BGthread.listener);
    }

    private void enableLocationSettings() {
          Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(settingsIntent);
    }

}
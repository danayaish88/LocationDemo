package com.example.locationdemo;


import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


public class getLocationTask extends Thread {

    public static final int SUCCESSFUL = 1;
    public static final int FAILED = 2;

    LocationListener listener;


    public void run() {
        listener = new LocationListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLocationChanged(Location location) {
                // A new location update is received.
                Message msg = Message.obtain();
                msg.obj = "Latitude" + location.getLatitude() + " Longitude " + location.getLongitude() + "\n";
                msg.arg1 = SUCCESSFUL;
                MainActivity.threadHandler.sendMessage(msg);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Message msg = Message.obtain();
                msg.arg1 = FAILED;
                MainActivity.threadHandler.sendMessage(msg);
            }
        };
    }





}

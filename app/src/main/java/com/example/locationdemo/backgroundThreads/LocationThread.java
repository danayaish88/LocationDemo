package com.example.locationdemo.backgroundThreads;


import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.locationdemo.mainPackage.MainActivity;
import com.google.android.gms.maps.model.LatLng;


public class LocationThread extends Thread {

    public static final int SUCCESSFUL = 1;
    public static final int FAILED = 2;

    public LocationListener listener;


    public void run() {
        listener = new LocationListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onLocationChanged(Location location) {
                // A new location update is received.
                Message msg = Message.obtain();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                msg.obj = latLng;
                msg.arg1 = SUCCESSFUL;
                MainActivity.threadHandler.sendMessage(msg);
                Log.d("onLocationChanged:", "Accuracy: " + location.getAccuracy());
                Log.d("onLocationChanged:", "provider: " + location.getProvider());
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

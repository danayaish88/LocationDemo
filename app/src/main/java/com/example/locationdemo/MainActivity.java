package com.example.locationdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.locationdemo.LocationThread.FAILED;
import static com.example.locationdemo.LocationThread.SUCCESSFUL;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {
    public static Handler threadHandler;
    private static final int GPS_REQUEST_CODE = 10;
    private static final int NETWORK_REQUEST_CODE = 20;

    private LocationManager locationManager;
    private LocationThread BGthread;

    @BindView(R.id.coordinates)
    TextView coordinatesTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        instatiateHnadler();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        BGthread = new LocationThread();
        BGthread.start();

        chechPermissions();
    }

    private void instatiateHnadler() {
        threadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.arg1) {
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
        sendRequestLoc(LocationManager.GPS_PROVIDER);
        sendRequestLoc(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendRequestLoc(LocationManager.GPS_PROVIDER);
        sendRequestLoc(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(BGthread.listener);
            Log.d("TAG", "onPause: ");
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == GPS_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {

                sendRequestLoc(LocationManager.GPS_PROVIDER);

            } else {
                Toast.makeText(this,"Permission GPS not granted", Toast.LENGTH_SHORT). show();
                //TODO: ask the user to enable location permission manually
            }

        } else if (requestCode == NETWORK_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {

                sendRequestLoc(LocationManager.NETWORK_PROVIDER);

            } else {
                Toast.makeText(this,"Permission NETWORK not granted", Toast.LENGTH_SHORT). show();
                //TODO: ask the user to enable location permission manually
            }
        }

    }

    public void chechPermissions() {
        //permissions NOT granted
        if (ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //show a dialog to explain to the user why this permission is requested
                showPermissionDialog();

            } else {
                // No explanation needed, we can request the permission.
                requestPermission();
            }

        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET},
                GPS_REQUEST_CODE);
    }

    private void showPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_msg)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        requestPermission();
                    }
                })
                .create()
                .show();
    }

    public void sendRequestLoc(String PROVIDER) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            locationManager.requestLocationUpdates(PROVIDER,
                    5000,  //5 seconds
                    0,   //0 meters
                    BGthread.listener);
        }
    }

    private void enableLocationSettings() {
          Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(settingsIntent);
    }


}
package com.example.locationdemo.mainPackage;

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.locationdemo.R;

import com.example.locationdemo.backgroundThreads.LocationThread;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.locationdemo.backgroundThreads.LocationThread.FAILED;
import static com.example.locationdemo.backgroundThreads.LocationThread.SUCCESSFUL;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    public static Handler threadHandler;
    private static final int GPS_REQUEST_CODE = 10;
    private static final int NETWORK_REQUEST_CODE = 20;
    private static final String TAG = "Main Activity";

    private LocationThread BGthread;
    private LatLng latLng;
    private SharedPreferences sharedPreferences;
    private LocationManager locationManager;

    @BindView(R.id.coordinates)
    TextView coordinatesTv;
    @BindView(R.id.button)
    Button btn;
    @BindView(R.id.googleMaps)
    ImageView googleMaps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        instatiateHnadler();

        startLocationThread();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        checkPermissions();

    }

    private void startLocationThread() {
        BGthread = new LocationThread();
        BGthread.start();
    }

    private void instatiateHnadler() {
        threadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.arg1) {
                    case SUCCESSFUL:
                        latLng = (LatLng)msg.obj;
                        Log.d(TAG, "handleMessage: long" + latLng.longitude + " lat: " + latLng.latitude + "\n");
                        coordinatesTv.append(latLng.latitude + " , " + latLng.longitude + "\n");
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
        getToken();
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
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if (requestCode == GPS_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {

                sendRequestLoc(LocationManager.GPS_PROVIDER);

            } else {
                Toast.makeText(this,"Permission GPS not granted", Toast.LENGTH_SHORT). show();
                enableAppSettings();
            }

        } else if (requestCode == NETWORK_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {

                sendRequestLoc(LocationManager.NETWORK_PROVIDER);

            } else {
                Toast.makeText(this,"Permission NETWORK not granted", Toast.LENGTH_SHORT). show();
            }
        }

    }

    public void checkPermissions() {
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

    private void enableAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.button)
    public void click() {
        GoogleServicesActivity.startScreen(this);
    }

    @OnClick(R.id.googleMaps)
    public void goToGoogleMaps() {
        //checkPermissions();
        if(latLng != null){
            MapsActivity.startScreen(MainActivity.this, latLng.longitude, latLng.latitude);
        }
    }

    public void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        setToken(token);
                    }
                });
    }

    private void setToken(String token) {
        if(token != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(token, null);
            editor.apply();
        }
    }

}
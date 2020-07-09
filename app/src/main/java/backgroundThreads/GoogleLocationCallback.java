package backgroundThreads;

import android.location.Location;
import android.os.Message;

import com.example.locationdemo.LocationByGoogleAPI;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class GoogleLocationCallback extends Thread {

    public LocationCallback mLocationCallback;

    public GoogleLocationCallback() {
        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Message msg = Message.obtain();
                msg.obj = "Latitude" + location.getLatitude() + " Longitude " + location.getLongitude() + "\n";
                LocationByGoogleAPI.threadHandler.sendMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        super.run();
    }
}

package com.example.locationdemo.fcm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseMessageReceiver extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("TAG", "onNewToken: " + token);
        sendTokenToServer();
    }

    private void sendTokenToServer() {
    }
}

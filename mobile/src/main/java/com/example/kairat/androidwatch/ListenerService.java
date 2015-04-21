package com.example.kairat.androidwatch;


import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    private static final String TAG = "MOBILE MESSAGE: ";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received");
        Log.d(TAG, messageEvent.getPath());
        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        Log.d(TAG, "showToast is called");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.d(TAG, "toast was shown");
    }
}

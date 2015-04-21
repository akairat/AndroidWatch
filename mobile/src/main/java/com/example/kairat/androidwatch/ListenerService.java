package com.example.kairat.androidwatch;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    private static final String TAG = "MOBILE MESSAGE: ";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received");
        queryMessage(messageEvent.getPath());
    }

    private void queryMessage(String message) {
        Intent i = new Intent(this, JSONProcess.class);
        i.putExtra("event", message);
        startActivity(i);
    }
}

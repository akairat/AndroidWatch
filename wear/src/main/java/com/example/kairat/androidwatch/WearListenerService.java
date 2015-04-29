package com.example.kairat.androidwatch;


import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearListenerService extends WearableListenerService{


    private static final String TAG = "MOBILE MESSAGE:";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received");
        Log.d(TAG, messageEvent.getPath());
        queryMessage(messageEvent.getPath());
    }

    private void queryMessage(String message) {
        Intent i = new Intent(this, SuggestionActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("suggestions", message);
        startActivity(i);
    }
}

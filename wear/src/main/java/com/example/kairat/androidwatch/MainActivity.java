package com.example.kairat.androidwatch;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity{

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final String TAG = "Wearable messages: ";
    private GoogleApiClient client;
    public static String MESSAGE = "#";
    int CONNECTION_TIME_OUT_MS = 100;
    private String nodeId;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initApi();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    public void launchSpeechActivity(View v){
        //Intent i = new Intent(this, SpeechActivity.class);
        //startActivity(i);

        displaySpeechRecognizer();
    }

    public void launchManualActivity(View v){
        Intent i = new Intent(this, ManualActivity.class);
        startActivity(i);
    }

    /**
     * Initialize API and connected device nodeId
     */
    private void initApi(){
        client = getGoogleApiClient();
        getDeviceNode();
    }

    /**
     * create new GoogleApiClient to be able to send messages to the mobile
     *
     * @return new Wearable GoogleApiClient
     */
    private GoogleApiClient getGoogleApiClient(){
        return new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Get node and assign nodeId of the mobile connected to the watch
     * (Since watch can connect to only one phone at a time, size of nodes should be 1)
     */
    private void getDeviceNode(){
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result = Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0){
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        });
        th.start();
    }



    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        MESSAGE = "#";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            parseSpeech(spokenText);
            //String[] inputStrings = spokenText.split("\\s+");
            //mTextView.setText(spokenText);
            parseSpeech(spokenText.toLowerCase());
            Log.d(TAG, MESSAGE);
            sendMessage();

            Context context = getApplicationContext();
            CharSequence text = "Please wait...";
            int duration = Toast.LENGTH_LONG;
            toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * parse user spoken text and assign appropriate message
     *
     * @param spokenText
     */
    private void parseSpeech(String spokenText){
        List<String> splitText = Arrays.asList(spokenText.toLowerCase().split("\\s+"));
        if (splitText.contains("museum") || splitText.contains("museums")){
            if (MESSAGE.equals("#")){
                MESSAGE = "museum";
            } else {
                MESSAGE = MESSAGE + "|museum|art_gallery|zoo|aquarium";
            }
        }

        if (splitText.contains("food") || splitText.contains("restaurant")
                || splitText.contains("cafe") || splitText.contains("restaurants")
                || splitText.contains("cafeteria")){
            if (MESSAGE.equals("#")){
                MESSAGE = "food";
            } else {
                MESSAGE = MESSAGE + "|food";
            }
        }

        if (splitText.contains("park") || splitText.contains("wildlife")
                || splitText.contains("nature") || splitText.contains("sightseeing")
                || splitText.contains("landmarks") || splitText.contains("landmark")){
            if (MESSAGE.equals("#")){
                MESSAGE = "park|amusement_park";
            } else {
                MESSAGE += "|park|amusement_park";
            }
        }

        if(splitText.contains("store") || splitText.contains("shopping")
                || splitText.contains("mall")){
            if (MESSAGE.equals("#")){
                MESSAGE = "shopping_mall|department_store|shoe_store|clothing_store|book_store|furniture_store";
            } else {
                MESSAGE += "|shopping_mall|department_store|shoe_store|clothing_store|book_store|furniture_store";
            }
        }
    }

    /**
     * Method for sending message to the mobile
     */
    private void sendMessage(){
        Log.d(TAG, "MESSAGE BEING SENT");
        if (nodeId != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE, null).setResultCallback(
                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    if (!sendMessageResult.getStatus().isSuccess()){
                                        Log.e(TAG, "Failed to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                    } else {
                                        Log.d(TAG, "message sent successfully: "+sendMessageResult.getStatus().getStatusCode());
                                    }
                                }
                            }
                    );
                    client.disconnect();
                }
            }).start();
        }
    }


}

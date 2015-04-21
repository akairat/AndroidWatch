package com.example.kairat.androidwatch;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpeechActivity extends Activity {
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final String TAG = "Wearable messages: ";
    private TextView mTextView;
    GoogleApiClient mGoogleApiClient;
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";
    TextView tv;
    int CONNECTION_TIME_OUT_MS = 1000;

    private boolean isConnectedToWearable;

    private List<String> museumKeyWords;
    private List<String> landmarksKeyWords;
    private List<String> nightlifeKeyWords;
    private List<String> restaurantKeyWords;
    private List<String> natureKeyWords;
    private List<String> suprisemeKeyWords;

    String ss = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        if (servicesAvailable()) {
                            new CheckWearableConnected().execute();
                        }
                        // Now you can use the Data Layer API
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.kaira);
                if (isConnectedToWearable == true){
                    mTextView.setText("CONNECTED");
                } else if (isConnectedToWearable == false) {
                    mTextView.setText("NOT CONNECTED");
                }
            }
        });

        //tv = (TextView) findViewById(R.id.kaira);

        new Thread(new Runnable() {
            public void run() {
                Collection<String> nodes = getNodes();
                if (nodes.size() == 0) {
                    ss = "no nodes to send message";
                    Log.d(TAG, ss);
                }
                else {
                    ss = nodes.toString();
                    Log.d(TAG, ss);
                }
            }
        }).start();


        //displaySpeechRecognizer();

        museumKeyWords = Arrays.asList("museum", "museums");
        landmarksKeyWords = Arrays.asList("landmarks", "landmark", "sightseeing");
        nightlifeKeyWords = Arrays.asList("nightlife", "clubs", "club", "bar", "bars");
        restaurantKeyWords = Arrays.asList("restaurant", "restaurants", "cafe", "cafeteria");
        natureKeyWords = Arrays.asList("nature", "wildlife");
        suprisemeKeyWords = Arrays.asList("surprise");

    }


    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
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
            //String[] inputStrings = spokenText.split("\\s+");
            mTextView.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendStartActivityMessage(String nodeId) {
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, nodeId, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    private List<String> getNodes() {
        List<String> results = new ArrayList<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private boolean servicesAvailable() {
        // Check that Google Play Services are available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);

        return (ConnectionResult.SUCCESS == resultCode);
    }


    private class CheckWearableConnected extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            NodeApi.GetConnectedNodesResult nodes =
                    Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

            if (nodes != null && nodes.getNodes().size() > 0) {
                isConnectedToWearable = true;
            }

            return null;
        }
    }

}

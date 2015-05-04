package com.example.kairat.androidwatch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SuggestionActivity extends Activity {

    private TextView nameOfPlace;
    private TextView distanceAndTimeToPlace;
    private TextView addressOfPlace;
    private ImageButton chooseButton;
    private ImageButton nextButton;

    private List<String> suggested_place_name;
    private List<String> suggested_place_address;
    private List<String> suggested_place_geo;
    private List<String> suggested_place_distance;
    private List<String> suggested_place_duration;

    private boolean refreshOn = false;

    private int place_index = 1;

    private int max_index;

    private GoogleApiClient client;
    private String nodeId;
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static String MESSAGE = "";
    private static String TAG = "SUGGESTION";
    private String userLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        initApi();

        suggested_place_name = new ArrayList<String>();
        suggested_place_address = new ArrayList<String>();
        suggested_place_geo = new ArrayList<String>();
        suggested_place_distance = new ArrayList<String>();
        suggested_place_duration = new ArrayList<String>();

        Bundle extras = getIntent().getExtras();

        String places = "";
        if (extras != null) {
            places = extras.getString("suggestions");
        }
        Log.d("PLACES RECEIVED", places);
        List<String> list_places = Arrays.asList(places.split("\\*"));
        max_index = list_places.size();

        for (String place : list_places){
            List<String> place_parts = Arrays.asList(place.split("#"));
            Log.d("PLACES RECEIVED", place);
            suggested_place_name.add(place_parts.get(0));
            suggested_place_distance.add(place_parts.get(1));
            suggested_place_duration.add(place_parts.get(2));
            suggested_place_address.add(place_parts.get(3));
            suggested_place_geo.add(place_parts.get(4));
            userLocation = place_parts.get(5);
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                distanceAndTimeToPlace = (TextView) stub.findViewById(R.id.distanceToPlace);
                nameOfPlace = (TextView) stub.findViewById(R.id.nameOfThePlace);
                addressOfPlace = (TextView) stub.findViewById(R.id.addressOfPlace);

                chooseButton = (ImageButton) stub.findViewById(R.id.chooseButton);
                nextButton = (ImageButton) stub.findViewById(R.id.nextButton);

                chooseButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        choiceMade(v);
                    }
                });

                nextButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        nextChoice(v);
                    }
                });

                if (suggested_place_name.get(0).equals("-")){
                    nameOfPlace.setText("No Suggestions.");
                    distanceAndTimeToPlace.setText("Please, go back and pick something else.");
                    addressOfPlace.setText("");
                    nextButton.setEnabled(false);
                    chooseButton.setEnabled(false);
                } else {
                    nameOfPlace.setText(suggested_place_name.get(0));
                    distanceAndTimeToPlace.setText(suggested_place_distance.get(0) + ", " + suggested_place_duration.get(0));
                    addressOfPlace.setText(suggested_place_address.get(0));
                }

            }
        });
    }


    // Must implement these onClick methods
    private void choiceMade(View v){
        if(place_index < max_index){
            // Send the correct choice to the mobile to open the map
            MESSAGE = "+" + ":" +suggested_place_geo.get(place_index)+":"+userLocation+":"+suggested_place_name.get(place_index);
            sendChoice();
            Context context = getApplicationContext();
            CharSequence text = "Please, check your phone for directions.";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            finish();
        }
    }

    private void nextChoice(View v){
        if (place_index < max_index){
            String name = suggested_place_name.get(place_index);
            if(name.length() < 20){
                nameOfPlace.setText(name);
            } else{
                nameOfPlace.setText(name.substring(0, 18)+".");
            }

            distanceAndTimeToPlace.setText(suggested_place_distance.get(place_index) + ", " + suggested_place_duration.get(place_index));
            addressOfPlace.setText(suggested_place_address.get(place_index));
            place_index++;
        } else if (place_index >= max_index && !refreshOn){
            nameOfPlace.setText("No Suggestions Left");
            distanceAndTimeToPlace.setText("Click refresh or go back and pick something else.");
            addressOfPlace.setText("");
            nextButton.setImageResource(R.drawable.refresh);
            nextButton.setBackgroundColor(Color.parseColor("#283593"));
            refreshOn = true;
        } else if (refreshOn){
            nextButton.setImageResource(R.drawable.cross);
            nextButton.setBackgroundColor(Color.parseColor("#EE0000"));
            place_index = 0;
            String name = suggested_place_name.get(place_index);
            if(name.length() < 20){
                nameOfPlace.setText(name);
            } else{
                nameOfPlace.setText(name.substring(0, 18)+".");
            }
            distanceAndTimeToPlace.setText(suggested_place_distance.get(place_index) + ", " + suggested_place_duration.get(place_index));
            addressOfPlace.setText(suggested_place_address.get(place_index));
            refreshOn = false;
            place_index++;
        }
    }

    @Override
    public void onStop() {
        Log.e("MOBILE MESSAGE:", "Suggestions activity has been stopped");
        super.onStop();  // Always call the superclass method first
        suggested_place_duration = new ArrayList<String>();
        suggested_place_geo = new ArrayList<String>();
        suggested_place_address = new ArrayList<String>();
        suggested_place_distance = new ArrayList<String>();
        suggested_place_name = new ArrayList<String>();
    }

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Method for sending message to the mobile
     */
    public void sendChoice() {
        Log.d(TAG, nodeId);
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Message being sent");
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE, null).setResultCallback(
                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    if (!sendMessageResult.getStatus().isSuccess()) {
                                        Log.e(TAG, "Failed to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                    } else {
                                        Log.e(TAG, "message was sent: "+sendMessageResult.getStatus().getStatusCode());
                                    }
                                }
                            }
                    );
                    client.disconnect();
                    Log.d(TAG, "Client disconnected");
                }
            }).start();
        }
    }

}

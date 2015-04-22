package com.example.kairat.androidwatch;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ManualActivity extends Activity /*implements ChoicesFragment.ChoicesFragmentListener*/ {

    private TextView mTextView;
    private int startHour;
    private int startMinute;

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static String MESSAGE = "#";

    private static final String TAG = "KAIRATS MESSAGE";
    private GoogleApiClient client;
    private String nodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        initApi();


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                showChoiceButtons();
            }
        });

        //showTimePickerDialog();
        //showChoiceFragment();
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
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
    /*
    @Override
    public void sendChoice(String choice) {
        MESSAGE = choice;
        Log.d(TAG, "SENDING CHOICE");
        if (nodeId != null) {
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
                                        Log.i(TAG, "message was sent: "+sendMessageResult.getStatus().getStatusCode());
                                    }
                                }
                            }
                    );
                }
            });
        }
    }


    public void showChoiceFragment() {
        ChoicesFragment cf = new ChoicesFragment();
    }
    */


/*
TimePicker Dialog
    @Override
    public void setStartTime(int hourOfDay, int minute) {
        startHour = hourOfDay;
        startMinute = minute;
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    */

    /**
     * Assign onClick listeners to the buttons on the screen
     */
    public void showChoiceButtons(){
        findViewById(R.id.parkButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //putImage();
                MESSAGE = "park|amusement_park";
                sendChoice();
            }
        });

        findViewById(R.id.museumButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //putImage();
                MESSAGE = "museum";
                sendChoice();
            }
        });

        findViewById(R.id.shoppingButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //putImage();
                MESSAGE = "store|shopping_mall|department_store";
                sendChoice();
            }
        });

        findViewById(R.id.foodButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //putImage();
                MESSAGE = "food";
                sendChoice();
            }
        });

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


    /**
     * Method for putting images to the new activity (Hardcoded for presentation)
     */
    public void putImage(){
        Intent i = new Intent(this, ImageActivity.class);
        startActivity(i);
    }
}

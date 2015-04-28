package com.example.kairat.androidwatch;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestionActivity extends Activity {

    private TextView nameOfPlace;
    private TextView distanceAndTimeToPlace;
    private TextView addressOfPlace;
    private ImageButton chooseButton;
    private ImageButton nextButton;

    private List<String> suggested_place_name = new ArrayList<String>();
    private List<String> suggested_place_address = new ArrayList<String>();
    private List<String> suggested_place_geo = new ArrayList<String>();
    private List<String> suggested_place_distance = new ArrayList<String>();
    private List<String> suggested_place_duration = new ArrayList<String>();

    private int place_index = 1;

    private int max_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        Bundle extras = getIntent().getExtras();

        String places = "";
        if (extras != null) {
            places = extras.getString("suggestions");
        }

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
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                distanceAndTimeToPlace = (TextView) stub.findViewById(R.id.distanceToPlace);
                nameOfPlace = (TextView) stub.findViewById(R.id.nameOfThePlace);
                addressOfPlace = (TextView) stub.findViewById(R.id.addressOfPlace);

                nameOfPlace.setText(suggested_place_name.get(0));
                distanceAndTimeToPlace.setText(suggested_place_distance.get(0) + ", " + suggested_place_duration.get(0));
                addressOfPlace.setText(suggested_place_address.get(0));

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
            }
        });
    }


    // Must implement these onClick methods
    private void choiceMade(View v){
        if(place_index < max_index){
            // Send the correct choice to the mobile to open the map
        } else {
            // do nothing
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
        } else {
            nameOfPlace.setText("No Suggestions.");
            distanceAndTimeToPlace.setText("");
            addressOfPlace.setText("");
        }
    }
}

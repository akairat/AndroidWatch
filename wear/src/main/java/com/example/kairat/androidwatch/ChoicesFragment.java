package com.example.kairat.androidwatch;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ChoicesFragment extends Fragment {

    ChoicesFragmentListener activityCommander;

    public interface ChoicesFragmentListener {
        public void sendChoice(String choice);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_choices, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCommander = (ChoicesFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }


    public void chooseFood(View v){
        activityCommander.sendChoice("food");
    }

    public void chooseMuseum(View v){
        activityCommander.sendChoice("museum");
    }

    public void choosePark(View v) {
        activityCommander.sendChoice("park");
    }

    public void chooseShopping(View v){
        activityCommander.sendChoice("shopping");
    }

}

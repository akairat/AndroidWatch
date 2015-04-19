package com.example.kairat.androidwatch;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class ManualActivity extends Activity implements TimePickerFragment.TimePickerFragmentListener {

    private TextView mTextView;
    private int startHour;
    private int startMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        showTimePickerDialog();
    }

    @Override
    public void setStartTime(int hourOfDay, int minute) {
        startHour = hourOfDay;
        startMinute = minute;
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
}

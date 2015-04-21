package com.example.kairat.androidwatch;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ImageActivity extends Activity {

    private TextView mTextView;
    String img;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //mTextView = (TextView) stub.findViewById(R.id.text);
                iv = (ImageView) stub.findViewById(R.id.randomImg);
                Random rand = new Random();
                int randomNum = rand.nextInt();
                if (randomNum < 0.5){
                    img = "@drawable/choice1";
                } else {
                    img = "@drawable/choice2";
                }
                int imageResource = getResources().getIdentifier(img, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource, null);
                iv.setImageDrawable(res);
            }
        });
    }
}

package com.example.androidcodechallenge;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.androidcodechallenge.view.Timer;

public class MainActivity extends AppCompatActivity {


    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        timer = (Timer) findViewById(R.id.timer);

        //TEST CONFIGURATIONS
        runAutomatedTests();
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        timer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timer.stop();
    }

    private void runAutomatedTests() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int width = displayMetrics.widthPixels;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.setRingThickness(20);
            }
        }, 5000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.setThemeColor(Color.RED);
            }
        }, 8000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.setSize(width / 2);
                timer.setRingThickness(60);
            }
        }, 12000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.setSize(width * 2 / 3);
                timer.setRingThickness(80);
                timer.setThemeColor(Color.YELLOW);
            }
        }, 18000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.setSize(width);
                timer.setRingThickness(40);
                timer.setThemeColor(Timer.DEFAULT_COLOR);
            }
        }, 22000);

    }

}

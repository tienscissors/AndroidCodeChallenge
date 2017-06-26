package com.example.androidcodechallenge;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.androidcodechallenge.view.Timer;

public class MainActivity extends AppCompatActivity {


    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        timer = (Timer) findViewById(R.id.timer);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int width = displayMetrics.widthPixels;

        ViewTreeObserver observer = timer.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ViewTreeObserver observer = timer.getViewTreeObserver();
                if(observer != null) {
                    observer.removeOnPreDrawListener(this);
                }

                timer.setSize(width / 2);

                timer.setRingThickness(40);

                return false;
            }
        });


        //TESTS
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
}

package com.example.oriolgasset.weatherforecast;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

/**
 * Created by Oriol on 15/5/2016.
 */
public class SplashScreen extends Activity {

    private static final long SPLASH_TIME_OUT = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_splash);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.primary_dark));
        this.setTaskDescription(taskDesc);

        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run() {
                Intent i = new Intent (SplashScreen.this, MainActivity.class);
                startActivity (i);
                finish ();
            }
        }, SPLASH_TIME_OUT);
    }
}

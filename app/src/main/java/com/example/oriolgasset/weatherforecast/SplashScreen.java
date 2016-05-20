package com.example.oriolgasset.weatherforecast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

/**
 * Created by Oriol on 15/5/2016.
 */
public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_splash);
        VideoView video = (VideoView) findViewById (R.id.videoView);
        Uri uri = Uri.parse ("android.resource://" + getPackageName () + "/" + R.raw.intro);
        if (video != null) {
            video.setVideoURI (uri);
        }

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

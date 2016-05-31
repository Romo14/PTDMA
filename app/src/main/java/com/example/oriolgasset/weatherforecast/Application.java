package com.example.oriolgasset.weatherforecast;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Oriol Gasset on 31/5/2016.
 */


public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate ();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}

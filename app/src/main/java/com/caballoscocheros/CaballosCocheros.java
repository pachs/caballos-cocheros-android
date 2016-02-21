package com.caballoscocheros;

import android.app.Application;

import com.caballoscocheros.util.DatabaseHelper;

/**
 * Created by Alvin on 21/02/2016.
 */
public class CaballosCocheros extends Application {

    //Other
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        //dbHelper = new DatabaseHelper(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}

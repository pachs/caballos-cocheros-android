package com.caballoscocheros.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import com.caballoscocheros.CaballosCocheros;
import com.caballoscocheros.R;
import com.caballoscocheros.util.SingleClickActionListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * Created by Alvin on 14/02/2016.
 */
public class MainActivity extends BaseDrawerActivity {

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("Error", "OpenCV no se pudo inicializar.");
        }
    }

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityContent(R.layout.content_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new SingleClickActionListener() {
            @Override
            public void onSingleClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });

        //Tratar de cargar el reconocedor antes de que la cámara mande frames
        CaballosCocheros mApp = (CaballosCocheros) getApplication();
        mApp.getObjectRecon();
    }
}

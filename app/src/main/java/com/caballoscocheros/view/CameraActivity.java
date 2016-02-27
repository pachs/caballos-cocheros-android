package com.caballoscocheros.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.caballoscocheros.CaballosCocheros;
import com.caballoscocheros.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    /**
     * Bloque estatico que inicia la libreria de OpenCV
     */
    static {
        if (!OpenCVLoader.initDebug()) {
            //Open CV init errors
        }
    }

    /**
     * Vista de camara de OpenCV.
     */
    private JavaCameraView mOpenCvCameraView;
    /**
     * Instancia del singleton de esta aplicacion.
     */
    private CaballosCocheros mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.content_camera);

        //OpenCV
        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.camera_capture);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mOpenCvCameraView.enableView();

        //Other
        mApp = (CaballosCocheros) getApplication();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        ObjectRecognition r = mApp.getObjectRecon();

        Mat frame = inputFrame.rgba();
//        r.processFrame(frame);

        return frame;
    }
}

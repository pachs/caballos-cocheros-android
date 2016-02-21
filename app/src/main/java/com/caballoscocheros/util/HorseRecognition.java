package com.caballoscocheros.util;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * Created by Alvin on 21/02/2016.
 */
public class HorseRecognition {

    private final static String TAG = HorseRecognition.class.getSimpleName();

    private static HorseRecognition ourInstance = new HorseRecognition();

    private CascadeClassifier classifier;

    private HorseRecognition() {
        if (classifier.load("haar_caballos.xml")) {
            Log.e(TAG, "Error cargando modelo de reconocimiento");
        }
    }

    public static HorseRecognition getInstance() {
        return ourInstance;
    }

    public void processFrame(Mat frame) {
        if (!frame.empty()) {
            MatOfRect detected = new MatOfRect();

            //TODO Ajustar minSize y maxSize para que no detecte si es muy grande o muy pequeno
            classifier.detectMultiScale(frame, detected, 1.1, 2, 2, new Size(), new Size());

            for (Rect rect : detected.toList()) {
                double x = rect.x;
                double y = rect.y;

                //Scalar B,G,R
                Imgproc.rectangle(frame, new Point(new double[]{x, y}), new Point(new double[]{x + rect.width, y + rect.height}), new Scalar(255, 0, 0));
            }
        } else {
            Log.d(TAG, "No se capturo frame");
        }
    }
}

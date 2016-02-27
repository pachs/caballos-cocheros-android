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
 * Detecta los objetos que se quieren detectar.
 * Created by Alvin on 21/02/2016.
 */
public class ObjectRecognition {

    private final static String TAG = ObjectRecognition.class.getSimpleName();

    /**
     * Clasificador de caballos.
     */
    private CascadeClassifier horseClassifier;
    /**
     * Clasificador de placas
     */
    private CascadeClassifier plateClassifier;

    public ObjectRecognition() {
        if (horseClassifier.load("haar_caballos.xml")) {
            Log.e(TAG, "Error cargando modelo de reconocimiento");
        }
    }

    /**
     * Procesa un frame capturado por la camara, cuando detecta un caballo pinta en el frame un cuadrado al rededor del objeto detectado.
     *
     * @param frame la imagen capturada.
     */
    public void processFrame(Mat frame) {
        if (!frame.empty()) {
            MatOfRect detected = new MatOfRect();

            //TODO Ajustar minSize y maxSize para que no haga deteccion si el objeto es muy grande o muy pequeno
            horseClassifier.detectMultiScale(frame, detected, 1.1, 2, 2, new Size(), new Size());

            for (Rect rect : detected.toList()) {
                //Start
                double x = rect.x;
                double y = rect.y;
                //End
                double xx = x + rect.width;
                double yy = y + rect.height;

                //Scalar B,G,R
                Imgproc.rectangle(frame, new Point(new double[]{x, y}), new Point(new double[]{xx, yy}), new Scalar(255, 0, 0));
            }

        } else {
            Log.d(TAG, "No se capturo frame");
        }
    }
}

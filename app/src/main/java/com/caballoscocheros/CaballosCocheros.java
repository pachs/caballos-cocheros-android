package com.caballoscocheros;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.media.ExifInterface;
import android.util.Log;

import com.caballoscocheros.util.DatabaseHelper;
//import com.caballoscocheros.util.ObjectRecognition;
import com.caballoscocheros.view.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alvin on 21/02/2016.
 */
public class CaballosCocheros extends Application {

    /**
     * Instancia de la base de datos que se usa en esta ejecucion de la aplicacion.
     */
    private DatabaseHelper dbHelper;
    /**
     * Instancia del objeto que reconoce imagenes.
     */
    //private ObjectRecognition recon;

    @Override
    public void onCreate() {
        super.onCreate();

        //dbHelper = new DatabaseHelper(this);
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

//    No se usa por el momento

//    public ObjectRecognition getObjectRecon() {
//        if (recon == null) {
//            try{
//                InputStream is = getAssets().open("haar_caballos.xml");
//                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//                File mCascadeFile = new File(cascadeDir, "haar_caballos.xml");
//                FileOutputStream os = new FileOutputStream(mCascadeFile);
//
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = is.read(buffer)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                is.close();
//                os.close();
//                recon = new ObjectRecognition(mCascadeFile.getAbsolutePath());
//                Log.e("Cargue", "Objeto de reconocimiento creado exitosamente");
//            } catch(Exception e){
//                Log.e("Cargue", e.getMessage());
//            }
//        }
//        return recon;
//    }

    public float[] getLatLng(String ruta)throws Exception{
        try {
            ExifInterface exif = new ExifInterface(ruta);
            float[] latlng = new float[2];
            if(exif.getLatLong(latlng)){
                return latlng;
            }
            else{
                //throw new Exception("No fue posible extraer la latitud y longitud.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getImageDate(String ruta){
        try{
            ExifInterface exif = new ExifInterface(ruta);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            String fecha = exif.getAttribute(ExifInterface.TAG_DATETIME);
            return sdf.parse(fecha);

        } catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}

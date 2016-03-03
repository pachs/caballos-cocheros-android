package com.caballoscocheros;

import android.app.Application;
import android.util.Log;

import com.caballoscocheros.util.DatabaseHelper;
import com.caballoscocheros.util.ObjectRecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
    private ObjectRecognition recon;

    @Override
    public void onCreate() {
        super.onCreate();

        //dbHelper = new DatabaseHelper(this);
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public ObjectRecognition getObjectRecon() {
        if (recon == null) {
            File f = new File(getCacheDir()+"/haar_caballos.xml");
            if (!f.exists()) try {
                InputStream is = getAssets().open("haar_caballos.xml");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) { throw new RuntimeException(e); }
            Log.d("MainClass", "\n El path es " + f.getPath()+"\n");

            recon = new ObjectRecognition(f.getAbsolutePath());
        }

        return recon;
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}

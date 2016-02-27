package com.caballoscocheros;

import android.app.Application;

import com.caballoscocheros.util.DatabaseHelper;
import com.caballoscocheros.util.ObjectRecognition;

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
            recon = new ObjectRecognition();
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

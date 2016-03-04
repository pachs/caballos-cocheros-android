package com.caballoscocheros;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.caballoscocheros.util.DatabaseHelper;
import com.caballoscocheros.util.ObjectRecognition;
import com.caballoscocheros.view.MainActivity;

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
            try{
                InputStream is = getAssets().open("haar_caballos.xml");
                File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                File mCascadeFile = new File(cascadeDir, "haar_caballos.xml");
                FileOutputStream os = new FileOutputStream(mCascadeFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();
                recon = new ObjectRecognition(mCascadeFile.getAbsolutePath());
                Log.e("Cargue", "Objeto de reconocimiento creado exitosamente");
            } catch(Exception e){
                Log.e("Cargue", e.getMessage());
            }
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

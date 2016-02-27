package com.caballoscocheros.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.caballoscocheros.db.DaoMaster;
import com.caballoscocheros.db.DaoSession;

/**
 * Helper para la base de datos, usa GreenDAO como ORM
 * Created by Alvin on 21/02/2016.
 */
public class DatabaseHelper extends DaoMaster.OpenHelper {

    private final String TAG = DatabaseHelper.class.getSimpleName();

    private SQLiteDatabase db;
    private DaoSession daoSession;

    /**
     * Abre una nueva sesion en la base de datos. Se debe usar esta misma sesion durante la ejecucion de la aplicacion.
     *
     * @param context el contexto de la aplicacion.
     */
    public DatabaseHelper(Context context) {
        super(context, "caballoscocheros.db", null);

        db = getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    /**
     * Cierra la base de datos.
     */
    public void close() {
        if (db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

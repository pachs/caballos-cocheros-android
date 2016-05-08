package com.caballoscocheros.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caballoscocheros.CaballosCocheros;
import com.caballoscocheros.R;
import com.caballoscocheros.util.SingleClickActionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;

import org.openalpr.OpenALPR;
import org.openalpr.model.Results;
import org.openalpr.model.ResultsError;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Alvin on 14/02/2016.
 * Ejemplo de Openalpr tomado de https://github.com/SandroMachado/openalpr-android
 */
public class MainActivity extends BaseDrawerActivity implements OnMapReadyCallback{

    //static {
    //    if (!OpenCVLoader.initDebug()) {
    //        Log.e("Error", "OpenCV no se pudo inicializar.");
    //    }
    //}

    private CaballosCocheros mApp;

    private final static String TAG = "MainActivity";
    private static final int REQUEST_IMAGE = 100;

    private String ANDROID_DATA_DIR;
    final int STORAGE=1;
    private static File destination;
    //private TextView resultTextView;
    //private ImageView imageView;
    private GoogleMap mapa;

    private Uri imageUri;

    public static class ReconocimientoPlaca {
        double lat;
        double lng;
        String placa;
        Date fecha;

        public ReconocimientoPlaca(double lat, double lng, String placa, Date fecha) {
            this.lat = lat;
            this.lng = lng;
            this.placa = placa;
            this.fecha = fecha;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityContent(R.layout.content_main);

        //Extraer el directorio de la aplicación
        ANDROID_DATA_DIR = this.getApplicationInfo().dataDir;
        //Log.i(TAG, "Data dir es: "+ANDROID_DATA_DIR);
        //Log.i(TAG,"getFilesDir es: "+getFilesDir());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new SingleClickActionListener() {
            @Override
            public void onSingleClick(View v) {
                checkPermission();
                //startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });

        FloatingActionButton fab_update = (FloatingActionButton) findViewById(R.id.fab_update);
        fab_update.setOnClickListener(new SingleClickActionListener() {
            @Override
            public void onSingleClick(View v) {
                new HttpAsyncGet().execute();
            }
        });

        //Referencia a la clase CaballosCocheros
        mApp = (CaballosCocheros) getApplication();

        //No hay reconocimiento de objetos por el momento
        //mApp.getObjectRecon();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void checkPermission() {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissions.isEmpty()) {
            Toast.makeText(this, "Se necesita permisos de escritura.", Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, params, STORAGE);
        } else { // We already have permissions, so handle as normal
            takePicture();
        }
    }

    public void takePicture(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        File mediaStorage = new File(getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "ReconocimientoPlacas");
        if (! mediaStorage.exists()){
            if (! mediaStorage.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
            }
        }
        imageUri = Uri.fromFile(new File(mediaStorage.getPath() + File.separator +
                "IMG_"+ name+ ".jpg"));


        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            destination = new File(imageUri.getPath());
            Log.i(TAG,"Se guardó la imagen en "+data.getData()+". La imagen tiene un tamaño de "+destination.length());
            final ProgressDialog progress = ProgressDialog.show(this, "Loading", "Parsing result...", true);
            final String openAlprConfFile = ANDROID_DATA_DIR + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    String result = OpenALPR.Factory.create(MainActivity.this, ANDROID_DATA_DIR+"/files").recognizeWithCountryRegionNConfig("us", "", imageUri.getPath(), openAlprConfFile, 10);

                    Log.i("OPEN ALPR", result);

                    try {
                        final Results results = new Gson().fromJson(result, Results.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (results == null || results.getResults() == null || results.getResults().size() == 0) {
                                    Log.e(TAG,"No se detectó una placa.");
                                    //Toast.makeText(MainActivity.this, R.string.no_deteccion_placa, Toast.LENGTH_LONG).show();
                                    //resultTextView.setText(R.string.no_deteccion_placa);
                                } else {
                                    Log.i(TAG,"Se detectó la placa "+results.getResults().get(0).getPlate());
                                    //resultTextView.setText("Plate: " + results.getResults().get(0).getPlate()
                                    // Trim confidence to two decimal places
                                    //+ " Confidence: " + String.format("%.2f", results.getResults().get(0).getConfidence()) + "%"
                                    // Convert processing time to seconds and trim to two decimal places
                                    //+ " Processing time: " + String.format("%.2f", ((results.getProcessing_time_ms() / 1000.0) % 60)) + " seconds");
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    Date fecha = ((CaballosCocheros) getApplication()).getImageDate(destination.getAbsolutePath());
                                                    double[] latlong = {0, 0};
                                                    try {
                                                        ((CaballosCocheros) getApplication()).getLatLng(destination.getAbsolutePath());
                                                    } catch (Exception e) {
                                                        Log.e(TAG, e.getMessage());
                                                    }
                                                    ReconocimientoPlaca rp = new ReconocimientoPlaca(latlong[0], latlong[1], results.getResults().get(0).getPlate(), fecha);
                                                    new HttpAsyncPost().execute(rp);

                                                    dialog.dismiss();

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    //No button clicked
                                                    dialog.dismiss();
                                            }
                                        }
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                    builder.setMessage("Confirmar envío de placa: "+ results.getResults().get(0).getPlate()).setPositiveButton("Sí", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                                }
                            }
                        });

                    } catch (JsonSyntaxException exception) {
                        final ResultsError resultsError = new Gson().fromJson(result, ResultsError.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //resultTextView.setText(resultsError.getMsg());
                            }
                        });
                    }

                    progress.dismiss();
                }
            });
        }

    }


    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());

        return df.format(date);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.0424121, -75.550958), 10));
        mapa.getUiSettings().setZoomControlsEnabled(true);
    }

    public void ponerMarcador(double lat, double lng, String titulo){
        mapa.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(titulo));
    }

    //Actualiza el mapa, debe llamarse luego de haber hecho sincronización
    private void actualizarMapa(){
        //Hacemos clear del mapa
        mapa.clear();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);

        //Agregamos un marcador por cada evento de reconocimiento
        ArrayList<ReconocimientoPlaca> reconocimientos = mApp.darCapturas();
        for (ReconocimientoPlaca r : reconocimientos){
            ponerMarcador(r.lat,r.lng,r.placa+"\r\n"+df.format(r.fecha));
        }
    }

    private class HttpAsyncPost extends AsyncTask<ReconocimientoPlaca, Void, String> {
        @Override
        protected String doInBackground(ReconocimientoPlaca... rp) {

            return CaballosCocheros.enviarCaptura(rp[0].lat,rp[0].lng, rp[0].placa, rp[0].fecha);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Se ha enviado la información al servidor", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Se ha enviado la información al servidor.");
        }
    }

    private class HttpAsyncGet extends AsyncTask<Context, Void, String> {
        @Override
        protected String doInBackground(Context... contexts) {

            String resultado = CaballosCocheros.actualizarCaballos();
            MainActivity ma = (MainActivity) contexts[0];
            ma.actualizarMapa();
            return resultado;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Se ha enviado la información al servidor", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Se ha actualizado la información de los caballos.");
        }
    }




}

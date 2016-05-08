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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Alvin on 21/02/2016.
 */
public class CaballosCocheros extends Application {

    private final static String URL_DESCARGA = "http://157.253.236.146:8060/cocheros/rest/download";
    private final static String URL_CARGA = "http://157.253.236.146:8060/cocheros/rest/upload";

    /**
     * Instancia de la base de datos que se usa en esta ejecucion de la aplicacion.
     */
    //private DatabaseHelper dbHelper;

    private static ArrayList<MainActivity.ReconocimientoPlaca> reconocimientos;

    private static Date ultimaActualizacion;
    /**
     * Instancia del objeto que reconoce imagenes.
     */
    //private ObjectRecognition recon;

    @Override
    public void onCreate() {
        super.onCreate();

        //dbHelper = new DatabaseHelper(this);
    }

//    public DatabaseHelper getDbHelper() {
//        return dbHelper;
//    }

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

    //Consigue la latitud y longitud de una imagen tomada
    public float[] getLatLng(String ruta)throws Exception{
        try {
            ExifInterface exif = new ExifInterface(ruta);
            float[] latlng = new float[2];
            if(exif.getLatLong(latlng)){
                return latlng;
            }
            else{
                throw new Exception("No fue posible extraer la latitud y longitud.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Recoge la fecha de captura de una imagen
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

    //Retorna las capturas que se tienen en un momento dado
    public ArrayList<MainActivity.ReconocimientoPlaca> darCapturas(){
        return reconocimientos;
    }

    //Envia la captura de una captura de caballo en un lugar y hora específicos al servidor
    public static String enviarCaptura(double lat, double lng, String placa, Date fechaCaptura){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL_CARGA);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            jsonObject.accumulate("uuid", uuid);
            jsonObject.accumulate("latitude", lat);
            jsonObject.accumulate("longitude", lng);
            jsonObject.accumulate("plate", placa);

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String capturaAsISO = df.format(fechaCaptura);
            jsonObject.accumulate("time",capturaAsISO);
            String nowAsISO = df.format(new Date());
            jsonObject.accumulate("syncTime",nowAsISO);

            //Ponemos un array de 1 Json en el POST
            JSONArray array = new JSONArray();
            array.put(jsonObject);

            json = array.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null){
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line = "";
                result = "";
                while((line = bufferedReader.readLine()) != null)
                    result += line;

                inputStream.close();
                return result;
            }
            else
                result = "Ocurrió un error enviando el Post.";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public static String actualizarCaballos(){
        InputStream inputStream = null;
        String result;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            String descarga = URL_DESCARGA;
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);

            if(ultimaActualizacion!=null){
                String fecha = df.format(ultimaActualizacion);
                descarga+="?time="+ fecha;
            }
            HttpResponse httpResponse = httpclient.execute(new HttpGet(descarga));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null){
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line;
                result = "";
                while((line = bufferedReader.readLine()) != null)
                    result += line;

                inputStream.close();
                JSONArray array = new JSONArray(result);
                if(!array.isNull(0) && array.length()>0){
                    JSONObject ultimo = (JSONObject) array.get(array.length()-1);
                    //Se actualiza respecto al tiempo de captura (debería ser syncTime?)
                    ultimaActualizacion = df.parse(ultimo.getString("time"));

                    for(int i=0; i<array.length();i++){
                        JSONObject json = (JSONObject) array.get(i);
                        double lat = json.getDouble("latitude");
                        double lng = json.getDouble("longitude");
                        String placa = json.getString("plate");
                        String time = json.getString("time");
                        Date fecha = df.parse(time);
                        MainActivity.ReconocimientoPlaca rp = new MainActivity.ReconocimientoPlaca(lat,lng,placa,fecha);
                        reconocimientos.add(rp);
                    }
                    result = "Se actualizaron correctamente las capturas.";
                }
            }
            else
                result = "Hubo un problema actualizando los caballos.";

        } catch (IOException e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "Problema con el InputStream al actualizar caballos";
        } catch (Exception e) {
            Log.d("Error actualización", e.getLocalizedMessage());
            result = "Problema con la comunicación con el servidor al actualizar caballos";
        }
        return result;
    }

    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


}

package es.tfandroid.roombarlauncher;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLocationListener implements LocationListener {
    Context ctx=null;
    double longitud;
    double latitud;

    public MyLocationListener(Context context){
        ctx=context;
        LocationManager locationManager = (LocationManager)
                ctx.getSystemService(ctx.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
        } catch (NullPointerException e) {
            latitud = -1.0;
            longitud = -1.0;
        }

    }
    public String getLongitud(){
        return String.valueOf(longitud);

    }
    public String getLatitud(){
        return String.valueOf(latitud);
    }
    @Override
    public void onLocationChanged(Location loc) {
        longitud=loc.getLongitude();
        latitud= loc.getLatitude();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream os = null;
                InputStream is = null;
                HttpURLConnection conn = null;
                try {
                    //constants
                    URL url = new URL("http://tfandroid.es/roombar/php_inputGPS.php");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("imei",InicioActivity.terminalBean.imei);
                    jsonObject.put("diahora",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    jsonObject.put("longitud",longitud);
                    jsonObject.put("latitud", latitud);
                    String message = jsonObject.toString();

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout( 10000 /*milliseconds*/ );
                    conn.setConnectTimeout( 15000 /* milliseconds */ );
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(message.getBytes().length);

                    //make some HTTP header nicety
                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                    //open
                    conn.connect();

                    //setup send
                    os = new BufferedOutputStream(conn.getOutputStream());
                    os.write(message.getBytes());
                    //clean up
                    os.flush();

                    //do somehting with response
                    is = conn.getInputStream();
                    //String contentAsString = readIt(is,len);
                } catch(Exception e){

                    try{
                        File n=new File(Constants.EXTERNAL_STORAGE+"/gps.log");
                        FileOutputStream fos=new FileOutputStream(n,true);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("imei",InicioActivity.terminalBean.imei);
                        jsonObject.put("diahora",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        jsonObject.put("longitud",longitud);
                        jsonObject.put("latitud", latitud);
                        String message = jsonObject.toString();
                        fos.write((message+"\r\n").getBytes());
                        fos.flush();
                        fos.close();
                    }catch(Exception e1){
                    }
                } finally {
                    //clean up
                    try {
                        if(os!=null) {
                            os.close();
                        }
                        if(is!=null){
                            is.close();
                        }
                    } catch (Exception e) {

                    }
                    if(conn!=null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
        }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
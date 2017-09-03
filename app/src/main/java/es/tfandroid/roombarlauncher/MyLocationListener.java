package es.tfandroid.roombarlauncher;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

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
        try{
            File n=new File(Environment.getExternalStorageDirectory()+"/gps.log");
            FileOutputStream fos=new FileOutputStream(n,true);
            fos.write((longitud+"---"+latitud+"\n").getBytes());
            fos.flush();
            fos.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
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
            try{
                File n=new File(Environment.getExternalStorageDirectory()+"/gps.log");
                FileOutputStream fos=new FileOutputStream(n,true);
                fos.write((longitud+"---"+latitud+"\n").getBytes());
                fos.flush();
                fos.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
package es.tfandroid.roombarlauncher;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
    Context ctx=null;
    double longitud;
    double latitud;
    public MyLocationListener(Context context){
        ctx=context;
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
        }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
package es.tfandroid.roombarlauncher;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by root on 17/07/17.
 */

public class Utilidades {
    public static SharedPreferences preferences;
    public final static String CHANGE_PERMISSION = "/system/bin/chmod -R 777 ";
    public static long downloadREF=-1;
    public static long downloadREF2=-1;
    public static long downloadREF3=-1;
    //FRAGGEL app interna
    /*public static void createMysqlUpdateRepo(Context context) {
        double versionActual;
        double versionServidor;
        BufferedReader in = null;
        BufferedReader br = null;
        String fileToDownload="";
        fileToDownload=obtenerFicheroFecha();

        File ff=new File(Environment.getExternalStorageDirectory() + "/roombar.txt");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            if (!ff.exists()) {
                if (comprobarConexion("http://roombar.es/app_clientes.zip",context)) {
                    downloadWeb(context);
                }
            } else {
                if (comprobarConexion("http://gruporrompruebas.ovh/a_sincronizar/"+fileToDownload,context)) {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(ff)));
                    versionActual = Double.parseDouble(br.readLine());
                    URL jsonUrl = new URL("http://fraggel/roombar.txt");
                    in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
                    versionServidor = Double.parseDouble(in.readLine());

                    if (versionActual < versionServidor) {
                        downloadWebActualizacion(context);
                    }
                }
            }
        }catch(Exception e){}
    }*/

    public static void downloadWeb(Context context) {
        BufferedWriter brw = null;
        DataOutputStream fos = null;
        DataInputStream stream = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://roombar.es/app_clientes.zip"));
            request.setDescription("app_clientes.zip");
            request.setTitle("app_clientes.zip");
            if (Build.VERSION.SDK_INT >= 11) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                new File(Environment.getExternalStorageDirectory() + "/droidphp/app_clientes.zip").delete();

            }
            request.setDestinationInExternalPublicDir("/droidphp/", "app_clientes.zip");

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Toast.makeText(context, "Actualizando servicio" + " " + "app_clientes.zip", Toast.LENGTH_SHORT).show();
            downloadREF = manager.enqueue(request);

            request = new DownloadManager.Request(Uri.parse("http://fraggel/roombarlauncher.apk"));
            request.setDescription("roombarlauncher.apk");
            request.setTitle("roombarlauncher.apk");
            if (Build.VERSION.SDK_INT >= 11) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                new File(Environment.getExternalStorageDirectory() + "/droidphp/roombarlauncher.apk").delete();
            }
            request.setDestinationInExternalPublicDir("/droidphp/", "roombarlauncher.apk");

            manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Toast.makeText(context, "Actualizando servicio" + " " + "roombarlauncher.apk", Toast.LENGTH_SHORT).show();
            downloadREF3 = manager.enqueue(request);

        } catch (Exception e) {
        } finally {
            try {
                brw.flush();
            } catch (Exception e) {
            }
            try {
                brw.close();
            } catch (Exception e) {
            }
            try {
                fos.flush();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
            try {
                stream.close();
            } catch (Exception e) {
            }
        }
    }
    public static TerminalBean crearTerminalBean(String[] listaDatos){
        TerminalBean bean=new TerminalBean(listaDatos[0],listaDatos[1],listaDatos[2],listaDatos[3],listaDatos[4],listaDatos[5],listaDatos[6],listaDatos[7],listaDatos[8],listaDatos[9],listaDatos[10],listaDatos[11],listaDatos[12],listaDatos[13],listaDatos[14],listaDatos[15],"");
        return bean;

    }
    public static boolean comprobarConexion(String urlString,Context context) {
        boolean retorno=false;
        try {
            /*ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (!activeNetwork.isConnectedOrConnecting()) {
                retorno = false;
            } else {*/
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);
                URL url = new URL(urlString);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                int statusCode = http.getResponseCode();
                if (statusCode == 200) {
                    retorno = true;
                } else if (statusCode > 500) {
                    /*try{
                        try {
                            setPermissionRecursive(new File(Constants.INTERNAL_LOCATION));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        preferences = PreferenceManager.
                                getDefaultSharedPreferences(context);
                        boolean enableSU = preferences.getBoolean("run_as_root", false);
                        final String execName = preferences.getString("use_server_httpd", "lighttpd");
                        final String bindPort = preferences.getString("server_port", "8080");
                        String shell = "su";

                        List<String> command2 = Collections.unmodifiableList(new ArrayList<String>() {
                            {
                                add(CHANGE_PERMISSION.concat(Constants.INTERNAL_LOCATION + "/scripts/server-sh.sh"));
                                add(String.format("%s/scripts/server-sh.sh %s %s", Constants.INTERNAL_LOCATION, execName, bindPort));
                            }
                        });
                        String command[] = command2.toArray(new String[command2.size()]);
                        List<String> res = Shell.run(shell, command, null, true);
                        for (String queryRes : res){
                            System.out.println(queryRes);
                        }
                        return false;
                    }catch(Exception e2){
                        e2.printStackTrace();
                        return false;
                    }*/
                }
            } catch (Exception e) {
                retorno = false;
            }

            //}
        }catch(Exception e1){}
        return retorno;
    }
    public static void setPermissionRecursive(File permissionRecursive) {
        permissionRecursive.setExecutable(true,false);
        permissionRecursive.setReadable(true,false);
        permissionRecursive.setWritable(true,false);
        if(permissionRecursive.isDirectory()){
            File[] files = permissionRecursive.listFiles();
            for(int x=0;x<files.length;x++){
                if(files[x].isDirectory()){
                    setPermissionRecursive(files[x]);
                }else{
                    files[x].setExecutable(true,false);
                    files[x].setReadable(true,false);
                    files[x].setWritable(true,false);
                }
            }
        }
    }
    public static void setMobileDataState(boolean mobileDataEnabled,Context context)
    {
        try
        {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod)
            {
                setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void cambiarBarraEstado(Context context,TerminalBean terminalBean) {
        try {
                String hot=Settings.System.getString(context.getContentResolver(), "status_bar_hotel");
                String hab=Settings.System.getString(context.getContentResolver(), "status_bar_habitacion");

                if((hot==null || !hot.equals(terminalBean.getHotel()))&&(hab==null||!hab.equals(terminalBean.getHabitacion()))){
                    Settings.System.putString(context.getContentResolver(), "status_bar_hotel", terminalBean.getHotel());
                    Settings.System.putString(context.getContentResolver(), "status_bar_habitacion", terminalBean.getHabitacion());
                    //meter bootanimation y logo.bin
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    URL website = new URL("http://fraggel/bootanimation"+terminalBean.getHotel()+".zip");
                    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/bootanimation.zip");
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.flush();
                    fos.close();
                    website = new URL("http://fraggel/logo"+terminalBean.getHotel()+".bin");
                    rbc = Channels.newChannel(website.openStream());
                    fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/logo.bin");
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.flush();
                    fos.close();
                    Process su = Runtime.getRuntime().exec("su");
                    OutputStream outputStream = su.getOutputStream();
                    outputStream.write("mount -o,remount rw /system\n".getBytes());
                    outputStream.write(("dd if="+Environment.getExternalStorageDirectory() + "/logo.bin of=/dev/block/platform/mtk-msdc.0/11230000.msdc0/by-name/logo \n").getBytes());
                    outputStream.write("rm -rf /system/media/bootanimation\n".getBytes());
                    outputStream.write(("cp -rf "+Environment.getExternalStorageDirectory() + "/bootanimation.zip\n").getBytes());
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String obtenerFicheroFecha(){
        String fecha="";
        Calendar instance = Calendar.getInstance();
        fecha+=String.valueOf(instance.get(Calendar.YEAR));
        String mes=String.valueOf(instance.get(Calendar.MONTH)+1);
        String dia=String.valueOf(instance.get(Calendar.DAY_OF_MONTH));
        if(mes.length()<2){
            mes="0"+mes;
        }
        if(dia.length()<2){
            dia="0"+dia;
        }
        fecha+=mes+dia+".zip";
        return fecha;
    }
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
    private String asignaFecha() {
        String fecha_mod = null;
        Calendar cal = Calendar.getInstance();
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf((cal.get(Calendar.MONTH) + 1));
        String year = String.valueOf(cal.get(Calendar.YEAR));
        if (day.length() < 2) {
            day = "0" + day;
        }
        if (month.length() < 2) {
            month = "0" + month;
        }
        fecha_mod = (day + "/" + month + "/" + year);
        return fecha_mod;
    }
    public static String asignaFechaCompleta(){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        return df.format(Calendar.getInstance().getTime());
    }
    public static String asignaHoras() {
        String fecha_mod = null;
        Calendar cal = Calendar.getInstance();
        String hour = String.valueOf(cal.get(Calendar.HOUR));
        String minute = String.valueOf((cal.get(Calendar.MINUTE)));
        if (hour.length() < 2) {
            hour = "0" + hour;
        }
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        fecha_mod = (hour + ":" + minute);
        return fecha_mod;
    }
    public static void setWifiTetheringEnabled(Context ctx,boolean enable,String nombreSSID,String pass) throws Exception {

        WifiManager mWifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
        WifiConfiguration wifiConfiguration = (WifiConfiguration) method.invoke(mWifiManager);
        //WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = nombreSSID;
        wifiConfiguration.preSharedKey = pass;
        wifiConfiguration.hiddenSSID = false;
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfiguration.allowedKeyManagement.set(4);
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

        Method method2 = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
        method2.invoke(mWifiManager, wifiConfiguration);
        mWifiManager.setWifiEnabled(false);
        //mWifiManager.setWifiApEnabled(wifiConfiguration,enable);
        Method method3 = mWifiManager.getClass().getMethod("setWifiApEnabled",  WifiConfiguration.class, boolean.class);
        method3.invoke(mWifiManager,  wifiConfiguration, enable);

    }

    public static void actualizarDatos(Context ctx,TerminalBean terminalBean) {
        cambiarBarraEstado(ctx,terminalBean);
    }

    public static void activarDatos(Context ctx) {
       try {
           Utilidades.setMobileDataState(true, ctx);
           WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
           wifiManager.setWifiEnabled(true);
           LocationManager locationManager = (LocationManager)
                   ctx.getSystemService(Context.LOCATION_SERVICE);
           boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
           if (!gpsStatus) {
               Settings.Secure.putString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "network,gps");
           }
           LocationListener locationListener = new MyLocationListener(ctx);
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
       }catch(Exception e){
           e.printStackTrace();
       }
    }
}

package es.tfandroid.roombarlauncher;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public static void createMysqlUpdateRepo(Context context) {
        double versionActual;
        double versionServidor;
        BufferedReader in = null;
        BufferedReader br=null;
        BufferedWriter brw=null;
        File ff=new File("/sdcard/roombar.txt");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            if (!ff.exists()) {
                if (comprobarConexion("http://fraggel",context)) {
                    downloadWeb(context);
                }
            } else {
                if (comprobarConexion("http://fraggel",context)) {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(ff)));
                    versionActual = Double.parseDouble(br.readLine());
                    URL jsonUrl = new URL("http://fraggel/roombar.txt");
                    in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
                    versionServidor = Double.parseDouble(in.readLine());

                    if (versionActual < versionServidor) {
                        downloadWeb(context);
                    }
                }
            }
        }catch(Exception e){}
    }

    public static void downloadWeb(Context context) {
        BufferedWriter brw = null;
        DataOutputStream fos = null;
        DataInputStream stream = null;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://fraggel/localhost.sql.zip"));
            request.setDescription("localhost.sql.zip");
            request.setTitle("localhost.sql.zip");
            if (Build.VERSION.SDK_INT >= 11) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                new File(Environment.getExternalStorageDirectory() + "/droidphp/localhost.sql.zip").delete();

            }
            request.setDestinationInExternalPublicDir("/droidphp/", "localhost.sql.zip");

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Toast.makeText(context, "Actualizando servicio" + " " + "localhost.sql.zip", Toast.LENGTH_SHORT).show();
            downloadREF = manager.enqueue(request);

            request = new DownloadManager.Request(Uri.parse("http://fraggel/tfandroidweb.zip"));
            request.setDescription("sqlwebupdate.zip");
            request.setTitle("sqlwebupdate.zip");
            if (Build.VERSION.SDK_INT >= 11) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                new File(Environment.getExternalStorageDirectory() + "/droidphp/sqlwebupdate.zip").delete();
            }
            request.setDestinationInExternalPublicDir("/droidphp/", "sqlwebupdate.zip");

            manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Toast.makeText(context, "Actualizando servicio" + " " + "sqlwebupdate.zip", Toast.LENGTH_SHORT).show();
            downloadREF2 = manager.enqueue(request);

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
                    try{
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
                    }
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

    public static void cambiarBarraEstado(Context context,String imei,String imei2) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=root&password=");
            CallableStatement callableStatement = connection.prepareCall("SELECT hotel,habitacion from where IMEI=? and IMEI2=?");
            callableStatement.setString(1,imei);
            callableStatement.setString(2,imei2);
            ResultSet resultSet = callableStatement.executeQuery();
            while(resultSet.next()){
                String hotel=resultSet.getString("hotel");
                String habitacion=resultSet.getString("habitacion");
                String hot=Settings.System.getString(context.getContentResolver(), "status_bar_hotel");
                String hab=Settings.System.getString(context.getContentResolver(), "status_bar_habitacion");
                if((!hot.equals(hotel))&&(!hab.equals(habitacion))){
                    Settings.System.putString(context.getContentResolver(), "status_bar_hotel", hotel);
                    Settings.System.putString(context.getContentResolver(), "status_bar_habitacion", habitacion);
                    //meter bootanimation y logo.bin
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    URL website = new URL("http://fraggel/bootanimation"+hotel+".zip");
                    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                    FileOutputStream fos = new FileOutputStream("/sdcard/bootanimation.zip");
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.flush();
                    fos.close();
                    website = new URL("http://fraggel/logo"+hotel+".bin");
                    rbc = Channels.newChannel(website.openStream());
                    fos = new FileOutputStream("/sdcard/logo.bin");
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.flush();
                    fos.close();
                    Process su = Runtime.getRuntime().exec("su");
                    OutputStream outputStream = su.getOutputStream();
                    outputStream.write("mount -o,remount rw /system\n".getBytes());
                    outputStream.write("dd if=/sdcard/logo.bin of=/dev/block/platform/mtk-msdc.0/11230000.msdc0/by-name/logo \n".getBytes());
                    outputStream.write("rm -rf /system/media/bootanimation\n".getBytes());
                    outputStream.write("cp -rf /sdcard/bootanimation.zip\n".getBytes());

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

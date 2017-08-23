package es.tfandroid.roombarlauncher;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Fraggel on 10/08/13.
 */
public class NotifyService extends Service{
    static long downloadREF = -1;
    static long downloadREF2 = -1;
    static long downloadREF3 = -1;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            double versionActual=0.0;
            double versionServidor=0.0;
            double versionActualAPK=0.0;
            double versionServidorAPK=0.0;
            File ff=new File(Environment.getExternalStorageDirectory() + "/roombar.txt");
            File ff2=new File(Environment.getExternalStorageDirectory() + "/roombarlauncher.txt");
            BufferedReader in=null;
            BufferedReader br= null;
            try {
                if(ff.exists()){
                    br=new BufferedReader(new InputStreamReader(new FileInputStream(ff)));
                    versionActual=Double.parseDouble(br.readLine());
                    URL jsonUrl = new URL("http://fraggel/roombar.txt");
                    in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
                    versionServidor = Double.parseDouble(in.readLine());

                }else{
                    versionActual=0.0;
                    versionServidor=0.1;
                }
                if(ff2.exists()){
                    br=new BufferedReader(new InputStreamReader(new FileInputStream(ff2)));
                    versionActualAPK=Double.parseDouble(br.readLine());
                    URL jsonUrl = new URL("http://fraggel/roombarlauncher.txt");
                    in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
                    versionServidorAPK = Double.parseDouble(in.readLine());

                }else{
                    versionActualAPK=0.0;
                    versionServidorAPK=0.1;
                    if(versionActualAPK<versionServidorAPK){
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://fraggel/roombarlauncher.apk"));
                        request.setDescription("roombarlauncher.apk");
                        request.setTitle("roombarlauncher.apk");
                        if (Build.VERSION.SDK_INT >= 11) {
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            new File(Environment.getExternalStorageDirectory() + "/droidphp/roombarlauncher.apk").delete();

                        }
                        request.setDestinationInExternalPublicDir("/droidphp/", "roombarlauncher.apk");
                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Toast.makeText(getApplicationContext(), "Actualizando servicio" + " " + "roombarlauncher.apk", Toast.LENGTH_SHORT).show();
                        downloadREF3 = manager.enqueue(request);
                    }
                }
            } catch (Exception e) {
                versionActual=0.0;
                versionServidor=0.1;
            }finally {
                try{
                    br.close();
                } catch (Exception e) {}
                try{

                    in.close();
                } catch (Exception e) {}
            }

            if(versionActual<versionServidor) {
                checkBBDD(versionServidor);
            }else{
                checkBBDD(versionServidor);
            }

        return START_STICKY;
    }
    private boolean checkBBDD(double versionServidor) {
        try {
            ResultSet resultSet =null;
            PreparedStatement preparedStatement=null;
            Connection conn=null;

            Class.forName("com.mysql.jdbc.Driver");
            try {
                conn= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/duvfjvdv_tfweb","root","");
                preparedStatement = conn.prepareStatement("select * from marca");
                /*preparedStatement.setString(1, imei);
                preparedStatement.setString(2, imei2);*/
                resultSet = preparedStatement.executeQuery();
                resultSet.close();
                preparedStatement.close();
                conn.close();
                //Establecer el
            }catch(Exception e){
                if(e.getMessage().contains("Unknown database") || e.getMessage().contains("doesn't exist")){
                    try{
                        downloadWeb(versionServidor);
                    } catch (Exception e2) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void downloadWeb(double versionServidor) {
        BufferedWriter brw=null;
        DataOutputStream fos =null;
        DataInputStream stream =null;
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

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Toast.makeText(getApplicationContext(), "Actualizando servicio" + " " + "localhost.sql.zip", Toast.LENGTH_SHORT).show();
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

            manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Toast.makeText(getApplicationContext(), "Actualizando servicio" + " " + "sqlwebupdate.zip", Toast.LENGTH_SHORT).show();
            downloadREF2 = manager.enqueue(request);

        } catch(Exception e) {
            return; // swallow a 404
        }finally{
            try {
                brw.flush();
            }catch(Exception e){}
            try {
                brw.close();
            }catch(Exception e){}
            try {
                fos.flush();
            }catch(Exception e){}
            try {
                fos.close();
            }catch(Exception e){}
            try {
                stream.close();
            }catch(Exception e){}
        }

    }
    @Override
    public void onDestroy() {

        super.onDestroy();


    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


}

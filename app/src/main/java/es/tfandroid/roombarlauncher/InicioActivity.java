package es.tfandroid.roombarlauncher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import eu.chainfire.libsuperuser.Debug;
import eu.chainfire.libsuperuser.Shell;
import eu.chainfire.libsuperuser.StreamGobbler;

public class InicioActivity extends AppCompatActivity implements AsyncResponse{
    public static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    protected customViewGroup blockingView = null;
    private SharedPreferences preferences;
    public final static String CHANGE_PERMISSION = "/system/bin/chmod -R 777 ";
    String imei = null;
    String imei2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        ActionBar actionBar = null;
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.hide();
        new File("/sdcard/droidphp/conf/").mkdirs();
        new File("/sdcard/droidphp/hosts/").mkdirs();
        new File("/sdcard/droidphp/conf/nginx/conf/").mkdirs();
        new File("/sdcard/droidphp/hosts/nginx/").mkdirs();
        new File("/sdcard/droidphp/tmp/").mkdirs();
        new File("/sdcard/droidphp/logs/").mkdirs();
        new File("/sdcard/droidphp/sessions/").mkdirs();

        preventStatusBarExpansion(this);
        try {
        FileInputStream fis = new FileInputStream(new File("/system/build.prop"));
            String buildprop="";
        byte[] input = new byte[fis.available()];
        while (fis.read(input) != -1) {
            buildprop += new String(input);
        }
        buildprop.lastIndexOf("ro.roombar.destination=");
        //startService(new Intent(getApplicationContext(), LockService.class));
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                imei = tm.getDeviceId(0);
                imei2=tm.getDeviceId(1);

            }


            /*if (imei == null || imei.length() == 0) {
                imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }*/
        } catch (Exception e) {
        }
        createMysqlUpdateRepo();

        URL url = null;
        try {
            this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
            //this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {

        }
            preferences = PreferenceManager.
                    getDefaultSharedPreferences(getApplicationContext());
         try{
                ZipInputStream zipInputStream = null;
                try {
                    zipInputStream = new ZipInputStream(getApplicationContext().getAssets().open("data.zip"));
                    ZipEntry zipEntry;
                    try {
                        FileOutputStream fout;
                        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                            if (zipEntry.isDirectory()) {
                                File file = new File(Constants.INTERNAL_LOCATION + "/"+zipEntry.getName());
                                if (!file.isDirectory()) file.mkdirs();
                            } else {

                                fout = new FileOutputStream(Constants.INTERNAL_LOCATION + "/" + zipEntry.getName());
                                byte[] buffer = new byte[4096 * 10];
                                int length;
                                while ((length = zipInputStream.read(buffer)) != -1) {
                                    fout.write(buffer, 0, length);
                                }
                                zipInputStream.closeEntry();
                                fout.close();
                            }
                        }
                        zipInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch(Exception e){}
             try {
                 setPermissionRecursive(new File(Constants.INTERNAL_LOCATION));
             } catch (Exception e) {
                 e.printStackTrace();
             }

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
        }catch(Exception e){
            e.printStackTrace();
        }
        VersionThread asyncTask = new VersionThread();
        asyncTask.delegate = this;
        asyncTask.execute();
    }
    /*private BroadcastReceiver mNetworkStateReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                if (comprobarConexion("http://www.roombar.com/App-RoomBar/01/")) {
                    Intent i = new Intent(context, FullscreenActivity.class);
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(i);
                }
            }
        }
    };*/

    private final BroadcastReceiver mWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                    if( wifiInfo.getNetworkId() == -1 ){
                        //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi not connected");
                    }
                    VersionThread asyncTask = new VersionThread();
                    asyncTask.delegate = InicioActivity.this;
                    asyncTask.execute();
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi on");
                }
                else {
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi off");
                }
            }
        }
    };
    public boolean onKeyUp(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_MENU){
            return true;
        }else {

            return super.onKeyUp(keyCode, event);
        }
    }
    @Override
    public void onResume(){
        try{

            try {
                setPermissionRecursive(new File(Constants.INTERNAL_LOCATION));
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        }catch(Exception e){
            e.printStackTrace();
        }
        String testUrl="http://localhost:8080/";
        if(comprobarConexion(testUrl)){
            Intent i = new Intent(this, FullscreenActivity.class);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
        super.onResume();
    }

    private void createMysqlUpdateRepo() {
        //Mysql
        //ver como recoger la versión actual y cual es la versión que hay subida antes de bajar todo
        double versionActual;
        double versionServidor;
        BufferedReader in = null;
        BufferedReader br=null;
        BufferedWriter brw=null;
        File ff=new File("/sdcard/roombar.txt");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {
            if(ff.exists()){
                br=new BufferedReader(new InputStreamReader(new FileInputStream(ff)));
                versionActual=Double.parseDouble(br.readLine());
                URL jsonUrl = new URL("http://");
                in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
                versionServidor = Double.parseDouble(in.readLine());
            }else{
                versionActual=0.0;
                versionServidor=0.1;
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
            try {
                //Descargar base de datos y los php
                checkBBDD(versionServidor);
                brw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ff)));
                brw.write(String.valueOf(versionServidor));
                brw.flush();
                brw.close();

            }catch(Exception e){}
        }else{
            //Actualizado
            //Testeamos que la base de datos está correcta
            checkBBDD(versionServidor);
        }
    }

    private boolean checkBBDD(double versionServidor) {
        try {

            ResultSet resultSet =null;
            PreparedStatement preparedStatement=null;
            Connection conn=null;
            BufferedWriter brw=null;
            Class.forName("com.mysql.jdbc.Driver");
            try {
                conn= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/testdb","root","");
                preparedStatement = conn.prepareStatement("select * from testtable where IMEI=? and IMEI2=?");
                preparedStatement.setString(1, imei);
                preparedStatement.setString(2, imei2);
                resultSet = preparedStatement.executeQuery();
                resultSet.close();
                preparedStatement.close();
                conn.close();
            }catch(Exception e){
                if(e.getMessage().contains("Unknown database") || e.getMessage().contains("doesn't exist")){
                    try{
                        /*String username = "root";
                        String password = "";
                        String[] baseShell = new String[]{
                                Constants.MYSQL_MONITOR_SBIN_LOCATION, "-h",
                                "127.0.0.1", "-T", "-f", "-r", "-t", "-E", "--disable-pager",
                                "-n", "--user=" + username, "--password=" + password,
                                "--default-character-set=utf8", "-L"};

                        java.lang.Process process = new ProcessBuilder(baseShell).
                                redirectErrorStream(true).
                                start();
                        stdin = process.getOutputStream();
                        stdin.write(("create database testdb;" + "\r\n").getBytes());
                        stdin.flush();*/

                        //Descargar BBDD y php


                            /*stdin.write(("use testdb;" + "\r\n").getBytes());
                            stdin.flush();
                            stdin.write(("create table testtable(IMEI VARCHAR(15),IMEI2 VARCHAR(15),HOTEL VARCHAR(100),HABITACION VARCHAR(5),PRIMARY KEY(IMEI));" + "\r\n").getBytes());
                            stdin.flush();*/

                        //download database
                        File ff=new File("/sdcard/roombar.txt");
                        brw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ff)));
                        brw.write(String.valueOf(versionServidor));
                        brw.flush();
                        brw.close();
                    createMysqlUpdateRepo();
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

    @Override
    public void onSaveInstanceState(Bundle outState){
        /*View decorWiew =getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorWiew.setSystemUiVisibility(uiOptions);*/
        super.onSaveInstanceState(outState);
    }
    @Override
    public void processFinish(String output) {
        if(!"".equals(output)){
            Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(this.mWifi);
            //unregisterReceiver(this.mNetworkStateReceiver);

        }catch(Exception e){}
        if (blockingView!=null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
        super.onDestroy();
    }

    public boolean comprobarConexion(String urlString) {
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
    @Override
    public void onBackPressed() {
    }
    public static void preventStatusBarExpansion(Context context) {
        try {

            WindowManager manager = ((WindowManager) context.getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE));

            Activity activity = (Activity) context;
            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
            localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            localLayoutParams.gravity = Gravity.TOP;
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                    // this is to enable the notification to recieve touch events
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                    // Draws over status bar
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

            localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            int result = 0;
            if (resId > 0) {
                result = activity.getResources().getDimensionPixelSize(resId);
            }

            localLayoutParams.height = result;

            localLayoutParams.format = PixelFormat.TRANSPARENT;

            customViewGroup view = new customViewGroup(context);

            manager.addView(view, localLayoutParams);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            Log.v("customViewGroup", "**********Intercepted");
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "User can access system settings without this permission!", Toast.LENGTH_SHORT).show();
            }
            else
            { disableStatusBar();
            }
        }
    }

    protected void disableStatusBar() {

        WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to receive touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        blockingView = new customViewGroup(this);
        manager.addView(blockingView, localLayoutParams);
    }
    private void setPermissionRecursive(File permissionRecursive) {
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
}

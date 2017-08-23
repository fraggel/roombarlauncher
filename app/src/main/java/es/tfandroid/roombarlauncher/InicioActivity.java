package es.tfandroid.roombarlauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InicioActivity extends Activity implements AsyncResponse{
    public static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    protected customViewGroup blockingView = null;
    private SharedPreferences preferences;
    public static TerminalBean terminalBean=null;
    public static String imei = "";
    public static String imei2 = "";
    public static String mac = "";
    public static String mac2 = "";
    //FRAGGEL app interna
    //String testUrl="http://localhost:8080/index.php";
    String testUrl="http://www.roombar.com/App-RoomBar/01/";
    static long downloadREF = -1;
    static long downloadREF2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        /*ActionBar actionBar = null;
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.hide();*/
        new File(Constants.SERVER_LOCATION).mkdirs();
        new File(Environment.getExternalStorageDirectory() + "/droidphp/conf/").mkdirs();
        new File(Environment.getExternalStorageDirectory() + "/droidphp/hosts/").mkdirs();
        new File(Environment.getExternalStorageDirectory() + "/droidphp/conf/nginx/conf/").mkdirs();
        new File(Environment.getExternalStorageDirectory() + "/droidphp/hosts/nginx/").mkdirs();
        new File(Environment.getExternalStorageDirectory() + "/droidphp/tmp/").mkdirs();
        new File(Environment.getExternalStorageDirectory() + "/droidphp/logs/").mkdirs();
        new File(Environment.getExternalStorageDirectory() + "/droidphp/sessions/").mkdirs();
        Utilidades.activarDatos(getApplicationContext());

        //preventStatusBarExpansion(this);

        try{
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    imei = tm.getDeviceId(0);
                    imei2= tm.getDeviceId(1);
                } else{
                    imei=tm.getDeviceId();
                    imei2=tm.getDeviceId();
                }

        } catch (Exception e) {
            imei = "";
            imei2="";
        }
        mac=Utilidades.getMACAddress("wlan0");
        mac2=Utilidades.getMACAddress("eth0");
/*

        try {
            this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
            this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {

        }*/

            preferences = PreferenceManager.
                    getDefaultSharedPreferences(getApplicationContext());
        /* try{
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
                 Utilidades.setPermissionRecursive(new File(Constants.INTERNAL_LOCATION));
             } catch (Exception e) {
                 e.printStackTrace();
             }

             //FRAGGEL app interna
            boolean enableSU = preferences.getBoolean("run_as_root", false);
            final String execName = preferences.getString("use_server_httpd", "lighttpd");
            final String bindPort = preferences.getString("server_port", "8080");
            String shell = "su";

            List<String> command2 = Collections.unmodifiableList(new ArrayList<String>() {
                {
                    add(Utilidades.CHANGE_PERMISSION.concat(Constants.INTERNAL_LOCATION + "/scripts/server-sh.sh"));
                    add(String.format("%s/scripts/server-sh.sh %s %s", Constants.INTERNAL_LOCATION, execName, bindPort));
                }
            });
            String command[] = command2.toArray(new String[command2.size()]);
            List<String> res = Shell.run(shell, command, null, true);
            for (String queryRes : res){
                System.out.println(queryRes);
            }
             Utilidades.createMysqlUpdateRepo(getApplicationContext());


         }catch(Exception e){
             e.printStackTrace();
         }*/
        VersionThread asyncTask = new VersionThread();
        asyncTask.delegate = this;
        asyncTask.execute(imei,imei2,mac,mac2);
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
    /*private BroadcastReceiver mNetworkStateReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            VersionThread asyncTask = new VersionThread();
            asyncTask.delegate = InicioActivity.this;
            asyncTask.execute(imei,imei2,mac,mac2);
        }
    };*/
    /*private final BroadcastReceiver mWifi = new BroadcastReceiver() {
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
                    asyncTask.execute(imei,imei2,mac,mac2);
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi on");
                }
                else {
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi off");
                }
            }
        }
    };*/
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
            /*try {
                Settings.System.putString(getContentResolver(), "status_bar_hotel", "aaaa");
                Settings.System.putString(getContentResolver(), "status_bar_habitacion", "eeee");
            }catch(Exception e){

                e.printStackTrace();
            }*/

            //FRAGGEL app interna
            /*try {
                Utilidades.setPermissionRecursive(new File(Constants.INTERNAL_LOCATION));
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean enableSU = preferences.getBoolean("run_as_root", false);
            final String execName = preferences.getString("use_server_httpd", "lighttpd");
            final String bindPort = preferences.getString("server_port", "8080");
            String shell = "su";

            List<String> command2 = Collections.unmodifiableList(new ArrayList<String>() {
                {
                    add(Utilidades.CHANGE_PERMISSION.concat(Constants.INTERNAL_LOCATION + "/scripts/server-sh.sh"));
                    add(String.format("%s/scripts/server-sh.sh %s %s", Constants.INTERNAL_LOCATION, execName, bindPort));
                }
            });
            String command[] = command2.toArray(new String[command2.size()]);
            List<String> res = Shell.run(shell, command, null, true);
            for (String queryRes : res){
                System.out.println(queryRes);
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
        Intent intent = getIntent();
        String action = intent.getAction();

        Utilidades.activarDatos(getApplicationContext());
        VersionThread asyncTask = new VersionThread();
        asyncTask.delegate = this;
        asyncTask.execute(imei,imei2,mac,mac2);
        super.onResume();
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
        if(!"".equals(output) && !"NotFound".equals(output)){
            terminalBean=Utilidades.crearTerminalBean(output.split(";"));
            Utilidades.actualizarDatos(getApplicationContext(),InicioActivity.terminalBean);
            Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        /*try{
            unregisterReceiver(this.mWifi);
            //unregisterReceiver(this.mNetworkStateReceiver);

        }catch(Exception e){}*/
        if (blockingView!=null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
        super.onDestroy();
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
            /*if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "User can access system settings without this permission!", Toast.LENGTH_SHORT).show();
            }
            else
            { */disableStatusBar();
            //}
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

}

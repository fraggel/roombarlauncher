package es.tfandroid.roombarlauncher;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    WebView webview = null;
    ActionBar actionBar = null;
    View mContentView;
    String imei = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            actionBar = getSupportActionBar();
            actionBar.setTitle("");
            actionBar.hide();
            /*View decorWiew =getWindow().getDecorView();
            int uiOptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorWiew.setSystemUiVisibility(uiOptions);*/
            setContentView(R.layout.activity_fullscreen);
            while(!comprobarConexion("http://www.roombar.com/App-RoomBar/01/")){}
            String buildprop = "";
            FileInputStream fis = new FileInputStream(new File("/system/build.prop"));
            byte[] input = new byte[fis.available()];
            while (fis.read(input) != -1) {
                buildprop += new String(input);
            }
            buildprop.lastIndexOf("ro.roombar.destination=");
                //startService(new Intent(getApplicationContext(), LockService.class));
                TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    if (tm != null) {
                        imei = tm.getDeviceId();
                    }
                } catch (Exception e) {
                }

                if (imei == null || imei.length() == 0) {
                    imei = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                }
                //mControlsView = findViewById(R.id.fullscreen_content_controls);
                mContentView = findViewById(R.id.fullscreen_content);

                webview = (WebView) findViewById(R.id.fullscreen_content);

                webview.loadUrl("http://www.roombar.com/App-RoomBar/01/");
                webview.setWebViewClient(new JiayuWebViewClient());
                webview.getSettings().setSupportZoom(false);
                webview.setWebChromeClient(new ChromeWebViewClient());
                webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webview.getSettings().setBuiltInZoomControls(false);
                webview.getSettings().setUserAgentString("Android");
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setLoadWithOverviewMode(false);
                webview.getSettings().setLightTouchEnabled(true);
                webview.getSettings().setUseWideViewPort(true);
                webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 1024);
                webview.getSettings().setAppCachePath(this.getCacheDir().getAbsolutePath());
                webview.getSettings().setAppCacheEnabled(true);
                webview.getSettings().setBlockNetworkImage(false);
                webview.getSettings().setBlockNetworkLoads(false);
                webview.getSettings().setEnableSmoothTransition(false);
                webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webview.getSettings().setLoadsImagesAutomatically(true);
                webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


                //LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                /*View customNav = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.statusbar, null); // layout which contains your button.
                actionBar.setCustomView(customNav);
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setTitle("");
                ((TextView) customNav.findViewById(R.id.date)).setText(asignaHoras());
                TextView tx = (TextView) findViewById(R.id.date);
                tx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this);
                        builder.setMessage(asignaFechaCompleta())
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        // Create the AlertDialog object and return it
                        builder.create();
                        builder.show();
                    }
                });*/
                this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                this.registerReceiver(this.mTime, new IntentFilter(Intent.ACTION_TIME_TICK));
                this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
                this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                this.registerReceiver(this.mBtC, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
                this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
                this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                if (isConnectedViaWifi()) {
                    //((TextView) findViewById(R.id.batteryLevel)).setText("Wifi Activo");
                }
                if (isConnectedBt()) {
                    //((TextView) findViewById(R.id.date)).setText("BT Activo");
                }

        } catch (Exception e) {
            System.exit(0);
            try {
                PrintWriter bw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/log.app"), true);
                e.printStackTrace(bw);
            } catch (Exception e2) {

            }

        }
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                disableStatusBar();
            }
        }
        else {*/
            //disableStatusBar();
        //}
    }
    private boolean isConnectedViaWifi() {
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }
    private boolean isConnectedBt() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(10);
    }
    @Override
    public void onResume(){
       /* View decorWiew =getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorWiew.setSystemUiVisibility(uiOptions);*/
        if(webview!=null){
            webview.reload();
            webview.loadUrl(webview.getUrl());
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        /*View decorWiew =getWindow().getDecorView();
        int uiOptions=View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorWiew.setSystemUiVisibility(uiOptions);*/
        if(webview!=null){
            webview.reload();
            webview.loadUrl(webview.getUrl());
        }
        super.onSaveInstanceState(outState);
    }
    private BroadcastReceiver mNetworkStateReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                if (comprobarConexion("http://www.roombar.com/App-RoomBar/01/")) {
                    if(webview!=null) {
                        webview.loadUrl(webview.getUrl());
                        webview.reload();
                    }
                }
            }
        }
    };
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            try {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

                //LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                /*if (level <= 15) {
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
                } else {
                    actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLUE));
                }*/
                //((TextView) findViewById(R.id.batteryLevel)).setText(level + "%");
                //((TextView) findViewById(R.id.date)).setText(asignaHoras());
            } catch (Exception e) {
                try {
                    PrintWriter bw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/log.app"), true);
                    e.printStackTrace(bw);
                } catch (Exception e2) {

                }

            }
        }
    };
    private BroadcastReceiver mTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            try {
                //((TextView) findViewById(R.id.date)).setText(asignaHoras());
            } catch (Exception e) {
                try {
                    PrintWriter bw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/log.app"));
                    e.printStackTrace(bw);
                } catch (Exception e2) {

                }

            }
        }
    };
    private final BroadcastReceiver mBt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private final BroadcastReceiver mBtC = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        //((TextView)findViewById(R.id.date)).setText("Bluetooth off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        //((TextView)findViewById(R.id.date)).setText("Turning Bluetooth off...");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        //((TextView)findViewById(R.id.date)).setText("Bluetooth on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        //((TextView)findViewById(R.id.date)).setText("Turning Bluetooth on...");
                        break;
                }
            }
        }
    };
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
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi on");
                }
                else {
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi off");
                }
            }
        }
    };


    @Override
    public void onBackPressed() {
        /*if (webview.getUrl().equals("http://foro.tfandroid.es/")
                || webview.getUrl().equals("http://www.youtube.com/channel/UCL1i90sCYqJhehj45dM2Qhg/videos")
                || webview.getUrl().equals("http://m.youtube.com/#/channel/UCL1i90sCYqJhehj45dM2Qhg/videos")
                || webview.getUrl().equals("http://www.tfandroid.es/jiayues/apk/appabout.php")
                || webview.getUrl().equals("http://www.tfandroid.es/about.php")
                || webview.getUrl().equals("http://www.tfandroid.es/jiayues/apk/appboots.php")
                || webview.getUrl().equals("http://www.tfandroid.es/jiayues/apk/apptools.php")
                || webview.getUrl().equals("http://www.tfandroid.es/downloadsModelos.php?detalle=1")
                || webview.getUrl().equals("http://www.tfandroid.es/jiayues/apk/soft.php")
                || (webview.getUrl().lastIndexOf("http://www.tfandroid.es/jiayues/apk/appsoft.php?jiayu=") != -1)
                || (webview.getUrl().equals("http://www.tfandroid.es/3-jiayu-moviles"))) {
            super.onBackPressed();
        } else {*/
        if (webview != null) {
            if(webview.canGoBack()) {
                webview.goBack();
            }else{
                if (webview.getUrl().equals("http://www.roombar.com/App-RoomBar/01/")){

                }else{
                    System.exit(0);
                }

            }
        }

        //}
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
    private String asignaFechaCompleta(){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        return df.format(Calendar.getInstance().getTime());
    }
    private String asignaHoras() {
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

    private class JiayuWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String urlDestino = url;
            if("http://www.roombar.com/App-RoomBar/01/06/01/".equals(url)){
                Intent settings = new Intent(Settings.ACTION_SETTINGS);
                startActivity(settings);
                return true;
            }else {
                if (!comprobarConexion(urlDestino)) {
                    Toast.makeText(getApplicationContext(), "No existe la web", Toast.LENGTH_LONG).show();
                    return true;
                    //view.goBack();
                } else {
                    return false;

                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            System.exit(0);
        }
    }
    private class ChromeWebViewClient extends WebChromeClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String urlDestino = url;
            if(!comprobarConexion(urlDestino)){
                Toast.makeText(getApplicationContext(),"No existe la web",Toast.LENGTH_LONG).show();
                return true;
                //view.goBack();
            }else {
                return false;
            }
        }
    }


    public boolean comprobarConexion(String urlString) {
        boolean retorno=false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (!activeNetwork.isConnectedOrConnecting()) {
                retorno = false;
            } else {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    URL url = new URL(urlString);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    int statusCode = http.getResponseCode();
                    if (statusCode == 200) {
                        retorno = true;
                    } else {
                        retorno = false;
                    }
                } catch (Exception e) {
                    retorno = false;
                }

            }
        }catch(Exception e1){}
        return retorno;
    }

    public boolean onKeyUp(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_MENU){
            webview.loadUrl("http://www.roombar.com/App-RoomBar/01/06/02/");
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}


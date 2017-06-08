package es.tfandroid.roombarlauncher;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InicioActivity extends AppCompatActivity implements AsyncResponse{
    public static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    protected customViewGroup blockingView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        preventStatusBarExpansion(this);
        ActionBar actionBar = null;
        URL url = null;
        try {
            this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
            //this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {

        }
        actionBar = getSupportActionBar();
        actionBar.setTitle("RoomBar");
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
    @Override
    public void onResume(){
        if(!comprobarConexion("http://www.roombar.com/App-RoomBar/01/")){

        }else {
            Intent i = new Intent(this, FullscreenActivity.class);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configuracion:
                Intent i = new Intent(this, SettingsActivity.class);
                this.startActivity(i);
                return true;
            case R.id.help:
                Intent i2 = new Intent(this, SettingsActivity.class);
                this.startActivity(i2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        }catch(Exception e){}
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
}

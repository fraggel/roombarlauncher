package es.tfandroid.roombarlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity implements AsyncResponse, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    static WebView webview = null;
    //ActionBar actionBar = null;
    View mContentView;
    View mlinearBotones;
    TextView mTextHotel;
    TextView mTextHotelT;
    TextView mTimeTxt;
    TextView mTimeTxtT;
    TextView mBatTxt;
    TextView mBatTxtT;
    ImageView mBatIconT;
    ImageButton buttonMenu;
    ImageButton buttonHome;
    ImageButton buttonBack;

    File newfilePicture = null;
    public static String urlSaved = null;
    FrameLayout mProgressDialog;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    //FRAGGEL app interna
    //public static String testUrl="http://localhost:8080/index.php";
    public static String testUrl = "http://www.roombar.com/App-RoomBar/01/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_fullscreen);
            String modo = getIntent().getStringExtra("modo");
            /*if ("noconectado".equals(modo)) {
                Toast.makeText(this, "Navegación sin conexión", Toast.LENGTH_LONG).show();
            }*/
            String buildprop = "";
            webview = (WebView) findViewById(R.id.fullscreen_content);
            mContentView = findViewById(R.id.fullscreen_content);
            mlinearBotones = findViewById(R.id.linearBotones);
            mTextHotel = (TextView) findViewById(R.id.textHotel);
            mTextHotelT=(TextView) findViewById(R.id.textHotelT);
            mTimeTxt = (TextView) findViewById(R.id.date);
            mTimeTxtT = (TextView) findViewById(R.id.dateT);
            mBatTxt = (TextView) findViewById(R.id.battery);
            mBatTxtT = (TextView) findViewById(R.id.batteryT);
            mTimeTxt.setText(asignaHoras());
            mTimeTxtT.setText(asignaHoras());
            mBatIconT = (ImageView)findViewById(R.id.iconBatteryT);
            BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
            mBatTxt.setText(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)+"%");
            mBatTxtT.setText(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)+"%");
            if (!Utilidades.esTablet(getApplicationContext())) {
                mlinearBotones.setVisibility(View.GONE);
                mTextHotel.setVisibility(View.VISIBLE);
                mTextHotelT.setVisibility(View.VISIBLE);
                findViewById(R.id.layout_barra_superior).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_barra_superior_tablet).setVisibility(View.GONE);
            } else {
                findViewById(R.id.layout_barra_superior).setVisibility(View.GONE);
                findViewById(R.id.layout_barra_superior_tablet).setVisibility(View.VISIBLE);
                mContentView.setBackgroundColor(Color.parseColor("#e55427"));
                TextView text = (TextView) findViewById(R.id.textHotel);
                if (!"noconectado".equals(modo)) {
                    text.setText(InicioActivity.terminalBean.hotel + InicioActivity.terminalBean.habitacion);
                }
                //webview.setKeepScreenOn(true);
                mlinearBotones.setVisibility(View.VISIBLE);
                mTextHotel.setVisibility(View.VISIBLE);
                mTextHotelT.setVisibility(View.VISIBLE);
                mBatTxt.setVisibility(View.GONE);
                mBatTxtT.setVisibility(View.GONE);
                mBatIconT.setVisibility(View.GONE);
            }
            if (!"noconectado".equals(modo)) {
                mTextHotel.setText(InicioActivity.terminalBean.hotel + InicioActivity.terminalBean.habitacion);
                mTextHotelT.setText(InicioActivity.terminalBean.hotel + InicioActivity.terminalBean.habitacion);
            }
            try {
                ((TextView) findViewById(R.id.date)).setText(asignaHoras());
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
            buttonMenu = (ImageButton) findViewById(R.id.buttonMenu);
            buttonHome = (ImageButton) findViewById(R.id.buttonHome);
            buttonBack = (ImageButton) findViewById(R.id.buttonBack);
            buttonMenu.setOnClickListener(this);
            buttonHome.setOnClickListener(this);
            buttonBack.setOnClickListener(this);
            buttonMenu.setOnLongClickListener(this);
            buttonHome.setOnTouchListener(this);
            buttonMenu.setOnTouchListener(this);
            buttonBack.setOnTouchListener(this);
            registerReceivers();



            if (Utilidades.esTablet(getApplicationContext())) {
                webview.setOnTouchListener(new OnSwipeTouchListener() {
                    public boolean onSwipeTop() {
                        return true;
                    }

                    public boolean onSwipeRight() {
                        onBackPressed();
                        return true;
                    }

                    public boolean onSwipeLeft() {
                        if (webview.canGoForward()) {
                            inAnimation = new AlphaAnimation(0f, 1f);
                            inAnimation.setDuration(200);
                            mProgressDialog.setAnimation(inAnimation);
                            mProgressDialog.setVisibility(View.VISIBLE);
                            webview.goForward();
                        }
                        return true;
                    }

                    public boolean onSwipeBottom() {
                        return true;
                    }
                });
            }

            webview.setWebViewClient(new RoomBarWebViewClient());
            webview.getSettings().setSupportZoom(false);
            webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.getSettings().setBuiltInZoomControls(false);
            webview.getSettings().setUserAgentString("Android");
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setAppCachePath(this.getCacheDir().getAbsolutePath());
            webview.getSettings().setAppCacheEnabled(true);
            webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.getSettings().setLoadsImagesAutomatically(true);
            webview.getSettings().setDomStorageEnabled(true);
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

            webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webview.loadUrl(testUrl);
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
            onResume();
        }
    }

    private void registerReceivers() {
        this.registerReceiver(this.mTime, new IntentFilter(Intent.ACTION_TIME_TICK));
        this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
        this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        this.registerReceiver(this.mBtC, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
        this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        this.registerReceiver(this.mBatteryStatus,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        onResume();
    }

    @Override
    public void onResume() {

        super.onResume();
        try {
            registerReceivers();
            VersionThread asyncTask = new VersionThread(getApplicationContext());
            asyncTask.delegate = FullscreenActivity.this;
            asyncTask.execute(InicioActivity.imei, InicioActivity.imei2, InicioActivity.mac, InicioActivity.mac2);
            webview.reload();
            webview.onResume();
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    private  BroadcastReceiver mBatteryStatus =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                ImageView mIconBat = (ImageView) findViewById(R.id.iconBattery);
                ImageView mIconBatCharging = (ImageView) findViewById(R.id.iconBatteryCharging);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                ((TextView) findViewById(R.id.battery)).setText(String.valueOf(level) + "%");
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                if( status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL){
                    mIconBat.setVisibility(View.GONE);
                    mIconBatCharging.setVisibility(View.VISIBLE);
                }else{
                    mIconBat.setVisibility(View.VISIBLE);
                    mIconBatCharging.setVisibility(View.GONE);
                }

            }catch(Exception e){
                Utilidades.escribirLogErrores(e);
            }
        }
    };

    private BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            VersionThread asyncTask = new VersionThread(getApplicationContext());
            asyncTask.delegate = FullscreenActivity.this;
            asyncTask.execute(InicioActivity.imei, InicioActivity.imei2, InicioActivity.mac, InicioActivity.mac2);
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            // check for wifi
            android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo eth = connMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            // check for mobile data
            android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            ImageView mIconNetwork = (ImageView) findViewById(R.id.iconNetwork);
            ImageView mIconWifi = (ImageView) findViewById(R.id.iconWifi);
            ImageView mIconLan = (ImageView) findViewById(R.id.iconLan);
            //ImageView mIconTether = (ImageView) findViewById(R.id.iconTethering);
            ImageView mIconNetworkT = (ImageView) findViewById(R.id.iconNetworkT);
            ImageView mIconWifiT = (ImageView) findViewById(R.id.iconWifiT);
            ImageView mIconLanT = (ImageView) findViewById(R.id.iconLanT);
            //ImageView mIconTetherT = (ImageView) findViewById(R.id.iconTetheringT);
            if(wifi!=null && wifi.isConnectedOrConnecting() ) {
                mIconNetwork.setVisibility(View.GONE);
                mIconWifi.setVisibility(View.VISIBLE);
                //mIconTether.setVisibility(View.GONE);
                mIconLan.setVisibility(View.GONE);
                mIconNetworkT.setVisibility(View.GONE);
                mIconWifiT.setVisibility(View.VISIBLE);
                //mIconTetherT.setVisibility(View.GONE);
                mIconLanT.setVisibility(View.GONE);
            } else if(mobile != null && mobile.isConnectedOrConnecting() ) {
                mIconNetwork.setVisibility(View.VISIBLE);
                mIconWifi.setVisibility(View.GONE);
                //mIconTether.setVisibility(View.GONE);
                mIconLan.setVisibility(View.GONE);
                mIconNetworkT.setVisibility(View.VISIBLE);
                mIconWifiT.setVisibility(View.GONE);
                //mIconTetherT.setVisibility(View.GONE);
                mIconLanT.setVisibility(View.GONE);
            } else if (eth !=null && eth.isConnectedOrConnecting()){
                mIconNetwork.setVisibility(View.GONE);
                mIconWifi.setVisibility(View.GONE);
                //mIconTether.setVisibility(View.GONE);
                mIconLan.setVisibility(View.VISIBLE);
                mIconNetworkT.setVisibility(View.GONE);
                mIconWifiT.setVisibility(View.GONE);
                //mIconTetherT.setVisibility(View.GONE);
                mIconLanT.setVisibility(View.VISIBLE);
            }else{
                mIconNetwork.setVisibility(View.GONE);
                mIconWifi.setVisibility(View.GONE);
                //mIconTether.setVisibility(View.GONE);
                mIconLan.setVisibility(View.GONE);
                mIconNetworkT.setVisibility(View.GONE);
                mIconWifiT.setVisibility(View.GONE);
                //mIconTetherT.setVisibility(View.GONE);
                mIconLanT.setVisibility(View.GONE);
            }
        }
    };
    private BroadcastReceiver mTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            try {
                ((TextView) findViewById(R.id.date)).setText(asignaHoras());
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
        }
    };
    public String asignaHoras(){
        String aux="";
        Calendar rightNow = Calendar.getInstance();
        int hora = rightNow.get(Calendar.HOUR_OF_DAY);
        if(hora<10){
            aux=aux+"0"+hora;
        }else{
            aux=aux+String.valueOf(hora);
        }
        aux=aux+":";
        int minutos = rightNow.get(Calendar.MINUTE);
        if(minutos<10){
            aux=aux+"0"+minutos;
        }else{
            aux=aux+String.valueOf(minutos);
        }

        return aux;

    }
    private BroadcastReceiver mBt = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private BroadcastReceiver mBtC = new BroadcastReceiver() {
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
    private BroadcastReceiver mWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                ImageView mIconNetwork = (ImageView) findViewById(R.id.iconNetwork);
                ImageView mIconWifi = (ImageView) findViewById(R.id.iconWifi);
                //ImageView mIconTether = (ImageView) findViewById(R.id.iconTethering);
                ImageView mIconNetworkT = (ImageView) findViewById(R.id.iconNetworkT);
                ImageView mIconWifiT = (ImageView) findViewById(R.id.iconWifiT);
                //ImageView mIconTetherT = (ImageView) findViewById(R.id.iconTetheringT);
                if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                    if (wifiInfo.getNetworkId() == -1) {
                        //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi not connected");
                    }
                    Utilidades.enviarEmailsEncolados(getApplicationContext());
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi on");
                    //Utilidades.recuentoMB(false);
                    mIconNetwork.setVisibility(View.GONE);
                    mIconWifi.setVisibility(View.VISIBLE);
                    mIconNetworkT.setVisibility(View.GONE);
                    mIconWifiT.setVisibility(View.VISIBLE);

                } else {
                    //Utilidades.recuentoMB(true);
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi off");
                    mIconNetwork.setVisibility(View.GONE);
                    mIconWifi.setVisibility(View.GONE);
                    mIconNetworkT.setVisibility(View.GONE);
                    mIconWifiT.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        try {
            if (webview != null) {

                if (webview.canGoBack()) {
                    inAnimation = new AlphaAnimation(0f, 1f);
                    inAnimation.setDuration(200);
                    mProgressDialog.setAnimation(inAnimation);
                    mProgressDialog.setVisibility(View.VISIBLE);
                    webview.goBackOrForward(-1);
                    VersionThread asyncTask = new VersionThread(getApplicationContext());
                    asyncTask.delegate = FullscreenActivity.this;
                    asyncTask.execute(InicioActivity.imei, InicioActivity.imei2, InicioActivity.mac, InicioActivity.mac2);
                    //webview.goBack();
                }
            }
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
            Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (newfilePicture != null &&requestCode==0 && resultCode==-1)  {
            Utilidades.showDialogCamera(this,newfilePicture,"");
        }else{
            try {
                newfilePicture.delete();
            }catch(Exception e){}
        }
        try {
            if (mProgressDialog != null) {
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(100);
                mProgressDialog.setAnimation(outAnimation);
                mProgressDialog.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonHome) {
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME));
        }
        if (id == R.id.buttonMenu) {
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU));
        }
        if (id == R.id.buttonBack) {
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        }
    }
    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        if (id == R.id.buttonHome)
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                outAnimation = new AlphaAnimation(1f, .1f);
                outAnimation.setDuration(100);
                v.setAnimation(outAnimation);
                v.startAnimation(outAnimation);
            }
            else
            {
                v.setAlpha(1f);
            }

        }
        if (id == R.id.buttonMenu)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                outAnimation = new AlphaAnimation(1f, .1f);
                outAnimation.setDuration(100);
                v.setAnimation(outAnimation);
                v.startAnimation(outAnimation);
            } else {
                v.setAlpha(1f);
            }
        }
        if (id == R.id.buttonBack)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                outAnimation = new AlphaAnimation(1f, .1f);
                outAnimation.setDuration(100);
                v.setAnimation(outAnimation);
                v.startAnimation(outAnimation);
            } else {
                v.setAlpha(1f);
            }
        }
        return false;
    }


    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();

        if (id == R.id.buttonMenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this, R.style.MyCustomDialogTheme);

            builder.setTitle(getResources().getString(R.string.hint_password));

            // Set up the input
            final EditText input = new EditText(getApplicationContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text="12345678";
                    try{
                        m_Text=InicioActivity.terminalBean.getPassSistema();
                    }catch(Exception e){

                    }
                    if (input.getText().toString().equals(m_Text)) {
                        InicioActivity.unpreventStatusBarExpansion(getApplicationContext());
                        Intent settings = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(settings);
                    }
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            alertDialog.show();
            input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        String m_Text="12345678";
                        try{
                            m_Text=InicioActivity.terminalBean.getPassSistema();
                        }catch(Exception e){

                        }
                        if (input.getText().toString().equals(m_Text)) {
                            Intent settings = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(settings);
                        }
                    }
                    alertDialog.dismiss();
                    return false;
                }
            });
            input.requestFocus();
            try {
                if (mProgressDialog != null) {
                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(100);
                    mProgressDialog.setAnimation(outAnimation);
                    mProgressDialog.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
        }

        return false;
    }


    private boolean listaNegraNumeros(String s) {
        boolean retorno = false;
        String[] listaTelefonos = new String[]{"80"};
        for (int x = 0; x < listaTelefonos.length; x++) {
            if (s.substring(0, 2).equals(listaTelefonos[x])) {
                retorno = true;
                break;
            } else {
                retorno = false;
            }
        }
        return retorno;
    }

    private boolean listaBlancaNumeros(String s) {
        boolean retorno = false;
        String[] listaTelefonos = new String[]{"010", "091", "112"};
        for (int x = 0; x < listaTelefonos.length; x++) {
            if (s.equals(listaTelefonos[x])) {
                retorno = true;
                break;
            } else {
                retorno = false;
            }
        }
        return retorno;
    }

    @Override
    public void processFinish(String output) {
        if (!"".equals(output.trim()) && !"NotFound".equals(output)) {
            try {
                JSONObject jObject = new JSONObject(output);
                InicioActivity.terminalBean = Utilidades.crearTerminalBean(jObject);
                /*Utilidades.cambiarBarraEstado(getApplicationContext(), InicioActivity.terminalBean);*/
                Utilidades.actualizarAppRom(getApplicationContext(), InicioActivity.terminalBean);

                Utilidades.eliminarNotificacionies(getApplicationContext());
                if(Utilidades.checkWifiOnAndConnected(getApplicationContext())){
                    Utilidades.enviarEmailsEncolados(getApplicationContext());
                }
                mlinearBotones = findViewById(R.id.linearBotones);
                /*if("0".equals(InicioActivity.terminalBean.getTablet())){
                    mlinearBotones.setVisibility(View.GONE);
                }else{
                    if("".equals(InicioActivity.terminalBean.hotel)){

                    }else{
                        mContentView.setBackgroundColor(Color.parseColor("#e55427"));
                    }
                    mlinearBotones.setVisibility(View.VISIBLE);

                }*/
                if (!Utilidades.esTablet(getApplicationContext())) {
                    mlinearBotones.setVisibility(View.GONE);
                    mTextHotel.setVisibility(View.VISIBLE);
                    mTextHotelT.setVisibility(View.VISIBLE);
                } else {
                    mContentView.setBackgroundColor(Color.parseColor("#e55427"));
                    TextView text = (TextView) findViewById(R.id.textHotel);
                    text.setText(InicioActivity.terminalBean.hotel + InicioActivity.terminalBean.habitacion);
                    //webview.setKeepScreenOn(true);
                    mlinearBotones.setVisibility(View.VISIBLE);
                    mTextHotel.setVisibility(View.VISIBLE);
                    mTextHotelT.setVisibility(View.VISIBLE);
                    mBatTxt.setVisibility(View.GONE);
                    mBatTxtT.setVisibility(View.GONE);
                    mBatIconT.setVisibility(View.GONE);
                }
                mTextHotel.setText(InicioActivity.terminalBean.hotel + InicioActivity.terminalBean.habitacion);
                mTextHotelT.setText(InicioActivity.terminalBean.hotel + InicioActivity.terminalBean.habitacion);
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
        }
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        //TODO long click action
                    } else {
                        //TODO click action
                    }
                    try{
                        Toast.makeText(getApplicationContext(),"Habilitando Tethering",Toast.LENGTH_SHORT).show();
                        String ssid = InicioActivity.terminalBean.getNameTethering();
                        String password = InicioActivity.terminalBean.getPassTethering();
                        Utilidades.setWifiTethering(getApplicationContext(),true, ssid, password);
                    }catch(Exception e){
                        Utilidades.escribirLogErrores(e);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        //TODO long click action
                    } else {
                        //TODO click action
                    }
                    try{
                        Toast.makeText(getApplicationContext(),"Deshabilitando Tethering",Toast.LENGTH_SHORT).show();
                        String ssid = InicioActivity.terminalBean.getNameTethering();
                        String password = InicioActivity.terminalBean.getPassTethering();
                        Utilidades.setWifiTethering(getApplicationContext(),false, ssid, password);
                        Utilidades.activarDatos(getApplicationContext());
                    }catch(Exception e){
                        Utilidades.escribirLogErrores(e);
                    }
                    try{
                        String ssid = InicioActivity.terminalBean.getNameTethering();
                        String password = InicioActivity.terminalBean.getPassTethering();
                        Utilidades.setWifiTethering(getApplicationContext(),false, ssid, password);
                        Utilidades.activarDatos(getApplicationContext());
                    }catch(Exception e){
                        Utilidades.escribirLogErrores(e);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                try {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this, R.style.MyCustomDialogTheme);

                        builder.setTitle(getResources().getString(R.string.hint_password));

                        // Set up the input
                        final EditText input = new EditText(getApplicationContext());
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String m_Text="12345678";
                                try{
                                    m_Text=InicioActivity.terminalBean.getPassSistema();
                                }catch(Exception e){

                                }
                                if (input.getText().toString().equals(m_Text)) {
                                    InicioActivity.unpreventStatusBarExpansion(getApplicationContext());
                                    Intent settings = new Intent(Settings.ACTION_SETTINGS);
                                    startActivity(settings);
                                }
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                        alertDialog.show();
                        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                                    String m_Text="12345678";
                                    try{
                                        m_Text=InicioActivity.terminalBean.getPassSistema();
                                    }catch(Exception e){

                                    }
                                    if (input.getText().toString().equals(m_Text)) {
                                        Intent settings = new Intent(Settings.ACTION_SETTINGS);
                                        startActivity(settings);
                                    }
                                }
                                alertDialog.dismiss();
                                return false;
                            }
                        });
                        input.requestFocus();
                        try {
                            if (mProgressDialog != null) {
                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(100);
                                mProgressDialog.setAnimation(outAnimation);
                                mProgressDialog.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            Utilidades.escribirLogErrores(e);
                        }
                    } else {
                        //TODO click action
                    }
                }catch(Exception e){

                }
                if (action == KeyEvent.ACTION_DOWN) {
                    webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    //FRAGGEL app interna
                    //String urlBase="http://localhost:8080";
                    String urlBase = "http://www.roombar.com";
                    webview.loadUrl((urlBase + "/App-RoomBar/01/06/02/"));
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (action == KeyEvent.ACTION_DOWN) {
                    onBackPressed();
                }
                return true;
            case KeyEvent.KEYCODE_HOME:
                if (action == KeyEvent.ACTION_DOWN) {
                    webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    //FRAGGEL app interna
                    //String urlBase="http://localhost:8080";
                    String urlBase = "http://www.roombar.com";
                    webview.clearHistory();

                    webview.loadUrl((urlBase + "/App-RoomBar/01/"));

                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            String urlBase = "http://www.roombar.com";
            webview.clearHistory();

            webview.loadUrl((urlBase + "/App-RoomBar/01/"));

        }
        return true;
    }



    public void onPause() {
        super.onPause();
        //this.urlSaved=webview.getUrl();
        this.unregisterReceiver(this.mTime);
        this.unregisterReceiver(this.mBt);
        this.unregisterReceiver(this.mBtC);
        this.unregisterReceiver(this.mWifi);
        //this.unregisterReceiver(this.mHome);
        this.unregisterReceiver(this.mNetworkStateReceiver);
        this.unregisterReceiver(this.mBatteryStatus);
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FullscreenActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (mProgressDialog != null) {
                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(100);
                    mProgressDialog.setAnimation(outAnimation);
                    mProgressDialog.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                Utilidades.escribirLogErrores(e);
            }
            return null;
        }
    }
    private class RoomBarWebViewClient  extends WebViewClient {

        boolean errorLoading = false;

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean retorno = false;
            try {

                mProgressDialog = (FrameLayout) findViewById(R.id.progressBarHolder);
                inAnimation = new AlphaAnimation(0f, 1f);
                inAnimation.setDuration(200);
                mProgressDialog.setAnimation(inAnimation);
                mProgressDialog.setVisibility(View.VISIBLE);
                String urlDestino = url;//.getUrl().toString();
                VersionThread asyncTask = new VersionThread(getApplicationContext());
                asyncTask.delegate = FullscreenActivity.this;
                asyncTask.execute(InicioActivity.imei, InicioActivity.imei2, InicioActivity.mac, InicioActivity.mac2);

                //FRAGGEL app interna
                //String urlBase="http://localhost:8080/";
                String urlBase = "http://www.roombar.com";
                if ((urlBase + "/App-RoomBar/01/06/01/").equals(urlDestino)) {

                }else if ((urlBase + "/App-RoomBar/01/06/03/").equals(urlDestino) && !Utilidades.esTablet(getApplicationContext())) {
                    //CAMARA
                    final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
                    File newdir = new File(dir);
                    newdir.mkdirs();
                    String file = dir + Utilidades.asignaFechaCompleta() + ".jpg";
                    newfilePicture = new File(file);
                    File[] files = newdir.listFiles();
                    if(files!=null) {
                        for (int x = 0; x < files.length; x++) {
                            files[x].delete();
                        }
                    }
                    try {
                        newfilePicture.createNewFile();
                    } catch (IOException e) {
                        Utilidades.escribirLogErrores(e);
                    }

                    Uri outputFileUri = Uri.fromFile(newfilePicture);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, 0);

                    retorno = true;
                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(100);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Utilidades.escribirLogErrores(e);
                    }
                } else if ((urlBase + "/App-RoomBar/01/06/04/").equals(urlDestino)) {
                    final String m_Text = InicioActivity.terminalBean.getPassSistema();
                    AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this, R.style.MyCustomDialogTheme);

                    builder.setTitle(getResources().getString(R.string.hint_password));

                    // Set up the input
                    final EditText input = new EditText(getApplicationContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (input.getText().toString().equals(m_Text)) {
                                InicioActivity.unpreventStatusBarExpansion(getApplicationContext());
                                Intent settings = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(settings);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    alertDialog.show();
                    input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                                if (input.getText().toString().equals(m_Text)) {
                                    Intent settings = new Intent(Settings.ACTION_SETTINGS);
                                    startActivity(settings);
                                }
                            }
                            alertDialog.dismiss();
                            return false;
                        }
                    });
                    input.requestFocus();
                    retorno = true;
                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(100);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Utilidades.escribirLogErrores(e);
                    }
                }else if("http://www.roombar.com/App-RoomBar/01/06/06/".equals(urlDestino) && !Utilidades.esTablet(getApplicationContext())){
                    if(Utilidades.cam!=null){
                        Utilidades.flashLightOff(getApplicationContext());
                    }else{
                        Utilidades.flashLightOn(getApplicationContext());
                    }
                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(100);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Utilidades.escribirLogErrores(e);
                    }
                    retorno=true;
                } else if (urlDestino.contains("tel:") && !Utilidades.esTablet(getApplicationContext())) {
                    //Telefono
                    boolean invalid = false;
                    String tel = urlDestino.split("tel:")[1];
                    if (!"".equals(tel)) {
                        if (tel.indexOf("#") == -1 && tel.indexOf("*") == -1) {
                            if (tel.length() > 8 && !listaNegraNumeros(tel)) {
                                invalid = false;
                            } else {
                                if (listaBlancaNumeros(tel)) {
                                    invalid = false;
                                } else {
                                    invalid = true;
                                }
                            }

                        } else {
                            invalid = true;
                        }

                        if (invalid) {
                            Toast.makeText(getApplicationContext(), "Número no válido", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + tel));
                            startActivity(intent);
                        }
                    }
                    retorno = true;
                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(100);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Utilidades.escribirLogErrores(e);
                    }
                } else {
                    webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                }
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }

            return retorno;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            if (!errorLoading) {
                urlSaved = url;
            }
            try {
                try {
                    if (mProgressDialog != null) {
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(100);
                        mProgressDialog.setAnimation(outAnimation);
                        mProgressDialog.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Utilidades.escribirLogErrores(e);
                }
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
            errorLoading = false;
        }
        @Override
        public void onReceivedHttpError (WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            try {
                try {
                    if (mProgressDialog != null) {
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(100);
                        mProgressDialog.setAnimation(outAnimation);
                        mProgressDialog.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Utilidades.escribirLogErrores(e);
                }
                super.onReceivedHttpError(view, request, errorResponse);

                if (testUrl.equals(request.getUrl())) {
                    //Mostrar pantalla que diga que no se puede mostrar nada, que compruebe que hay conexión a internet,
                    //Toast.makeText(FullscreenActivity.this, "No hay conexión para la pantalla inicial", Toast.LENGTH_SHORT).show();
                } else {
                    if (!Utilidades.comprobarConexion(request.getUrl().toString(), getApplicationContext())) {
                        //Toast.makeText(FullscreenActivity.this, "No hay conexión", Toast.LENGTH_SHORT).show();
                        errorLoading = true;
                    } else {
                        errorLoading = false;
                    }
                    if (!urlSaved.equals(request.getUrl())) {
                        view.loadUrl(urlSaved);
                    }
                }
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
        }
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            try {
                try {
                    if (mProgressDialog != null) {
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(100);
                        mProgressDialog.setAnimation(outAnimation);
                        mProgressDialog.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Utilidades.escribirLogErrores(e);
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (testUrl.equals(failingUrl)) {
                    //Mostrar pantalla que diga que no se puede mostrar nada, que compruebe que hay conexión a internet,
                    //Toast.makeText(FullscreenActivity.this, "No hay conexión para la pantalla inicial", Toast.LENGTH_SHORT).show();
                    //if("net::ERR_INTERNET_DISCONNECTED".equals(description)){
                        ImageView mImageNoConnection = (ImageView) findViewById(R.id.imageViewFull);
                        if(!new File(Environment.getExternalStorageDirectory() + "/logo.png").exists()){
                            mImageNoConnection.setImageResource(R.drawable.inicio);
                        }else {
                            mImageNoConnection.setImageURI(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/logo.png")));
                        }
                    //}
                } else {
                    if (!Utilidades.comprobarConexion(failingUrl, getApplicationContext())) {
                        errorLoading = true;
                    } else {
                        errorLoading = false;
                    }
                    if (!urlSaved.equals(failingUrl)) {
                        view.loadUrl(urlSaved);
                    }
                }
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
        }
    }
}




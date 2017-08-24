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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.security.spec.ECField;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity implements AsyncResponse {

    static WebView webview = null;
    //ActionBar actionBar = null;
    View mContentView;
    File newfilePicture =null;
    public static String urlSaved=null;
    FrameLayout mProgressDialog;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    //FRAGGEL app interna
    //public static String testUrl="http://localhost:8080/index.php";
    public static String testUrl="http://www.roombar.com/App-RoomBar/01/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_fullscreen);
            /*actionBar = getSupportActionBar();
            actionBar.setTitle("");
            actionBar.hide();*/


            while(!Utilidades.comprobarConexion(testUrl,this)){}
            String buildprop = "";

                //mControlsView = findViewById(R.id.fullscreen_content_controls);
                mContentView = findViewById(R.id.fullscreen_content);

                webview = (WebView) findViewById(R.id.fullscreen_content);

                webview.setOnTouchListener(new OnSwipeTouchListener() {
                    public boolean onSwipeTop() {
                        return true;
                    }
                    public boolean onSwipeRight() {
                        onBackPressed();
                        return true;
                    }
                    public boolean onSwipeLeft() {
                        if(webview.canGoForward()){
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
                webview.loadUrl(testUrl);
                webview.setWebViewClient(new RoomBarWebViewClient());
                webview.getSettings().setSupportZoom(false);
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
                /*webview.getSettings().setBlockNetworkImage(true);
                webview.getSettings().setBlockNetworkLoads(true);*/
                webview.getSettings().setEnableSmoothTransition(false);
                webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webview.getSettings().setLoadsImagesAutomatically(true);

                webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

                this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                this.registerReceiver(this.mTime, new IntentFilter(Intent.ACTION_TIME_TICK));
                this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
                this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
                this.registerReceiver(this.mBtC, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
                this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
                this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                //this.registerReceiver(this.mHome, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        } catch (Exception e) {
            Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
            startActivity(intent);

        }
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
    @Override
    public void onResume(){

        super.onResume();
        try {
            this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            this.registerReceiver(this.mTime, new IntentFilter(Intent.ACTION_TIME_TICK));
            this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
            this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
            this.registerReceiver(this.mBt, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
            this.registerReceiver(this.mBtC, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
            this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            //this.registerReceiver(this.mHome, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

        super.onSaveInstanceState(outState);
    }
    private BroadcastReceiver mNetworkStateReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            try {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

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
    private final BroadcastReceiver mHome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                try {

                }catch(Exception e){
                    e.printStackTrace();
                }

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
                    //Utilidades.recuentoMB(false);
                }
                else {
                    //Utilidades.recuentoMB(true);
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi off");
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
                webview.goBack();
            } else {
                if (webview.getUrl().equals(testUrl)) {
                    if(!Utilidades.comprobarConexion(testUrl,getApplicationContext())){
                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                    startActivity(intent);
                }

            }
        }
    }catch(Exception e){
        Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
        startActivity(intent);
    }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(newfilePicture!=null){
            sendMail("fraggelillo666@gmail.com", "Prueba", "Adjuntamos imagen",newfilePicture.getAbsolutePath());
        }
        newfilePicture=null;
    }
    private class RoomBarWebViewClient extends WebViewClient {
        boolean errorLoading=false;
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean retorno=false;
            try {

                mProgressDialog = (FrameLayout) findViewById(R.id.progressBarHolder);
                inAnimation = new AlphaAnimation(0f, 1f);
                inAnimation.setDuration(200);
                mProgressDialog.setAnimation(inAnimation);
                mProgressDialog.setVisibility(View.VISIBLE);
                String urlDestino = url;//.getUrl().toString();
                VersionThread asyncTask = new VersionThread();
                asyncTask.delegate = FullscreenActivity.this;
                asyncTask.execute(InicioActivity.imei,InicioActivity.imei2,InicioActivity.mac,InicioActivity.mac2);

                //FRAGGEL app interna
                //String urlBase="http://localhost:8080/";
                String urlBase="http://www.roombar.com";
                if ((urlBase+"/App-RoomBar/01/06/01/").equals(urlDestino)) {
                    /*Intent settings = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                    startActivity(settings);
                    return true;
                    */
                } else if ((urlBase+"/App-RoomBar/01/06/02/").equals(urlDestino)) {
                    //get Data from webservice
                    String ssid = InicioActivity.terminalBean.getNameTethering();
                    String password = InicioActivity.terminalBean.getPassTethering();
                    try{
                        Utilidades.setWifiTetheringEnabled(getApplicationContext(),true, ssid, password);
                    }catch(Exception e){}

                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(200);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    }catch(Exception e){}
                    retorno= true;
                } else if ((urlBase+"/App-RoomBar/01/06/03/").equals(urlDestino)) {
                    //CAMARA
                    final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
                    File newdir = new File(dir);
                    newdir.mkdirs();
                    String file = dir + Utilidades.asignaFechaCompleta() + ".jpg";
                    newfilePicture = new File(file);
                    File[] files = newdir.listFiles();
                    for(int x=0;x<files.length;x++){
                        files[x].delete();
                    }
                    try {
                        newfilePicture.createNewFile();
                    } catch (IOException e) {
                    }

                    Uri outputFileUri = Uri.fromFile(newfilePicture);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, 0);

                    retorno= true;
                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(200);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    }catch(Exception e){}
                } else if ((urlBase+"/App-RoomBar/01/06/04/").equals(urlDestino)) {
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
                    retorno= true;
                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(200);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    }catch(Exception e){}
                } else if ((urlBase+"/App-RoomBar/01/06/05/").equals(urlDestino)) {
                    //Telefono
                    //get Data from webservice
                    AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this, R.style.MyCustomDialogTheme);
                    builder.setTitle(getResources().getString(R.string.hint_phone));

                    // Set up the input
                    final EditText input = new EditText(getApplicationContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean invalid=false;
                            String tel=input.getText().toString();
                            if (!"".equals(tel)) {
                                if(tel.indexOf("#")==-1 && tel.indexOf("*")==-1){
                                    if(tel.length()>8 && !listaNegraNumeros(tel)){
                                        invalid=false;
                                    }else{
                                        if(listaBlancaNumeros(tel)){
                                            invalid=false;
                                        }else{
                                            invalid=true;
                                        }
                                    }

                                }else{
                                    invalid=true;
                                }

                                if(invalid){
                                    Toast.makeText(getApplicationContext(), "Número no válido", Toast.LENGTH_LONG).show();
                                }else{
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" + input.getText()));
                                    startActivity(intent);
                                }
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
                                boolean invalid=false;
                                String tel=input.getText().toString();
                                if (!"".equals(tel)) {
                                    if(tel.indexOf("#")==-1 && tel.indexOf("*")==-1){
                                        if(tel.length()>8 && !listaNegraNumeros(tel)){
                                            invalid=false;
                                        }else{
                                            if(listaBlancaNumeros(tel)){
                                                invalid=false;
                                            }else{
                                                invalid=true;
                                            }
                                        }

                                    }else{
                                        invalid=true;
                                    }

                                    if(invalid){
                                        Toast.makeText(getApplicationContext(), "Número no válido", Toast.LENGTH_LONG).show();
                                    }else{
                                        /*if("112".equals(input.getText())) {
                                            Intent intent = new Intent(Intent.ACTION_CALL_EMERGENCY);
                                            intent.setData(Uri.fromParts("tel", "112", null));
                                            startActivity(intent);

                                        }else{*/
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse("tel:" + input.getText()));
                                            startActivity(intent);
                                        /*}*/
                                    }
                                }
                            }
                            alertDialog.dismiss();
                            return false;
                        }
                    });
                    input.requestFocus();
                    retorno= true;
                    try {
                        if (mProgressDialog != null) {
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(200);
                            mProgressDialog.setAnimation(outAnimation);
                            mProgressDialog.setVisibility(View.GONE);
                        }
                    }catch(Exception e){}
                }else{
                    /*if(!Utilidades.comprobarConexion(urlDestino,getApplicationContext())){
                        Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                        startActivity(intent);
                    }*/
                    if(!Utilidades.comprobarConexion(testUrl,getApplicationContext())){
                        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
                    }else{
                        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if(!Utilidades.comprobarConexion(testUrl,getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                    startActivity(intent);
                }
            }

            return retorno;
        }


        @Override
        public void onPageFinished(WebView view, String url){
            if(!errorLoading) {
                urlSaved = url;
            }
            if("404 - Not Found".equals(view.getTitle().trim())){
                Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                startActivity(intent);
            }
            try {
                try {
                    if (mProgressDialog != null) {
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        mProgressDialog.setAnimation(outAnimation);
                        mProgressDialog.setVisibility(View.GONE);
                    }
                }catch(Exception e){}
            }catch(Exception e){}
            errorLoading=false;
        }
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            try {
                try {
                    if (mProgressDialog != null) {
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        mProgressDialog.setAnimation(outAnimation);
                        mProgressDialog.setVisibility(View.GONE);
                    }
                }catch(Exception e){}
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (!Utilidades.comprobarConexion(failingUrl,getApplicationContext())) {
                    errorLoading = true;
                } else {
                    errorLoading = false;
                }
                if (!urlSaved.equals(failingUrl)) {
                    view.loadUrl(urlSaved);
                }
            }catch(Exception e){}
            }
        }
    private boolean listaNegraNumeros(String s) {
        boolean retorno=false;
        String[] listaTelefonos=new String[]{"80"};
        for(int x =0;x<listaTelefonos.length;x++){
            if(s.substring(0,2).equals(listaTelefonos[x])){
                retorno=true;
                break;
            }else{
                retorno=false;
            }
        }
        return retorno;
    }

    private boolean listaBlancaNumeros(String s) {
        boolean retorno=false;
        String[] listaTelefonos=new String[]{"010","091","112"};
        for(int x =0;x<listaTelefonos.length;x++){
            if(s.equals(listaTelefonos[x])){
                retorno=true;
                break;
            }else{
                retorno=false;
            }
        }
        return retorno;
    }
    @Override
    public void processFinish(String output) {
        if(!"".equals(output) && !"NotFound".equals(output)){
            InicioActivity.terminalBean=Utilidades.crearTerminalBean(output.split(";"));
            Utilidades.actualizarDatos(getApplicationContext(),InicioActivity.terminalBean);
        }
    }

    public boolean onKeyUp(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_MENU){
            if(!Utilidades.comprobarConexion(testUrl,getApplicationContext())){
                webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            }else{
                webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
            //FRAGGEL app interna
            //String urlBase="http://localhost:8080";
            String urlBase="http://www.roombar.com";
            webview.loadUrl((urlBase+"/App-RoomBar/01/06/02/"));
            String ssid = InicioActivity.terminalBean.getNameTethering();
            String password = InicioActivity.terminalBean.getPassTethering();
            try{
                Utilidades.setWifiTetheringEnabled(getApplicationContext(),true, ssid, password);
            }catch(Exception e){}
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    private void sendMail(String email, String subject, String messageBody,String filename)
    {
        try {
           //ENVIO DE EMAIL
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onPause() {
        super.onPause();
        //this.urlSaved=webview.getUrl();
        this.unregisterReceiver(this.mBatInfoReceiver);
        this.unregisterReceiver(this.mTime);
        this.unregisterReceiver(this.mBt);
        this.unregisterReceiver(this.mBtC);
        this.unregisterReceiver(this.mWifi);
        //this.unregisterReceiver(this.mHome);
        this.unregisterReceiver(this.mNetworkStateReceiver);
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
                    outAnimation.setDuration(200);
                    mProgressDialog.setAnimation(outAnimation);
                    mProgressDialog.setVisibility(View.GONE);
                }
            }catch(Exception e){}
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


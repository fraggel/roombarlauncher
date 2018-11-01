package es.tfandroid.roombarlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InicioActivity extends Activity implements AsyncResponse, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    public static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    protected customViewGroup blockingView = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;
    public static LocationManager locationManager=null;
    public static TerminalBean terminalBean=null;
    public static String imei = "";
    public static String imei2 = "";
    public static String mac = "";
    public static String mac2 = "";
    public static String device = "";
    public static String vendor = "";
    public static String rom = "";
    View mlinearBotonesM;
    ImageButton buttonMenuM;
    ImageButton buttonHomeM;
    ImageButton buttonBackM;
    public static float version;
    public static String pathRecovery = "";
    int screenOn=0;
    Handler handler = new Handler();
    TextView textView=null;
    ImageView imageView=null;
    //FRAGGEL app interna
    //String testUrl="http://localhost:8080/index.php";
    String testUrl="http://www.roombar.com/App-RoomBar/01/";
    static long downloadREF = -1;
    static long downloadREF2 = -1;
    static long downloadREF3 = -1;
    static long downloadREF4 = -1;
    static boolean descargaApkLanzada=false;
    static boolean descargaRomLanzada=false;
    static boolean descargaLogosLanzada=false;
    static boolean descargaLogoFinalizada=false;
    static boolean descargaLogoPersonalizado=false;
    static int verCodeApp;
    static boolean primeraEjecucion=true;
    static ViewGroup view;
    FrameLayout mProgressDialog;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inicio);

        //requiere root
        Utilidades.eliminarNotificacionies(getApplicationContext());
        Utilidades.actualizarPermisos();
        new File(Constants.SERVER_LOCATION).mkdirs();
        new File(Constants.EXTERNAL_STORAGE + "/droidphp/conf/").mkdirs();
        new File(Constants.EXTERNAL_STORAGE + "/droidphp/hosts/").mkdirs();
        new File(Constants.EXTERNAL_STORAGE + "/droidphp/conf/nginx/conf/").mkdirs();
        new File(Constants.EXTERNAL_STORAGE + "/droidphp/hosts/nginx/").mkdirs();
        new File(Constants.EXTERNAL_STORAGE + "/droidphp/tmp/").mkdirs();
        new File(Constants.EXTERNAL_STORAGE + "/droidphp/logs/").mkdirs();
        new File(Constants.EXTERNAL_STORAGE + "/droidphp/sessions/").mkdirs();
        Utilidades.borrarFicheros();

        registrarReceivers();

        Utilidades.activarDatos(getApplicationContext());

        view= (ViewGroup) findViewById(android.R.id.content);
        imageView=(ImageView)findViewById(R.id.imageView);
        if(!new File(Constants.EXTERNAL_STORAGE + "/logo.png").exists()){
            imageView.setImageResource(R.drawable.inicio);
        }else {
            imageView.setImageURI(Uri.fromFile(new File(Constants.EXTERNAL_STORAGE + "/logo.png")));
        }
        mlinearBotonesM=findViewById(R.id.linearBotonesMovil);
        if(!Utilidades.tieneBotonesFisicos(getApplicationContext())){
            mlinearBotonesM.setVisibility(View.VISIBLE);

        }
        buttonMenuM  = (ImageButton) findViewById(R.id.buttonMenuM);
        buttonHomeM = (ImageButton) findViewById(R.id.buttonHomeM);
        buttonBackM = (ImageButton) findViewById(R.id.buttonBackM);
        buttonMenuM.setOnClickListener(this);
        buttonHomeM.setOnClickListener(this);
        buttonBackM.setOnClickListener(this);
        buttonMenuM.setOnLongClickListener(this);
        buttonHomeM.setOnTouchListener(this);
        buttonMenuM.setOnTouchListener(this);
        buttonBackM.setOnTouchListener(this);
        preventStatusBarExpansion(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        Utilidades.getImei(getApplicationContext());

        try {
            BufferedReader br=null;
            br = new BufferedReader(new FileReader(new File("/system/build.prop")));
            String cadenaLeida = br.readLine();
            while (cadenaLeida != null) {
                if (cadenaLeida.trim().indexOf("ro.tfota.device") != -1) {
                    device = cadenaLeida.trim().replaceAll(" ", "").replaceAll("ro.tfota.device=", "");
                }
                if (cadenaLeida.trim().indexOf("ro.tfota.vendor") != -1) {
                    vendor = cadenaLeida.trim().replaceAll(" ", "").replaceAll("ro.tfota.vendor=", "");
                }
                if (cadenaLeida.trim().indexOf("ro.tfota.rom") != -1) {
                    rom = cadenaLeida.trim().replaceAll(" ", "").replaceAll("ro.tfota.rom=", "");
                }
                if (cadenaLeida.trim().indexOf("ro.tfota.version") != -1) {
                    version = Float.parseFloat(cadenaLeida.trim().replaceAll(" ", "").replaceAll("ro.tfota.version=", ""));
                }
                if (cadenaLeida.trim().indexOf("ro.tfota.recpath") != -1) {
                    pathRecovery = cadenaLeida.trim().replaceAll(" ", "").replaceAll("ro.tfota.recpath=", "");
                }
                cadenaLeida = br.readLine();
            }
        }catch(Exception e){
            Utilidades.escribirLogErrores(e);
        }
        textView=(TextView)findViewById(R.id.textView);
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo("es.tfandroid.roombarlauncher", 0);
            verCodeApp = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Utilidades.escribirLogErrores(e);
        }
        textView.setText(rom+" "+version+" apk "+verCodeApp);
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            try {
            ZipInputStream zipInputStream = null;
            try {
                zipInputStream = new ZipInputStream(getApplicationContext().getAssets().open("data.zip"));
                ZipEntry zipEntry;
                try {
                    FileOutputStream fout;
                    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                        if (zipEntry.isDirectory()) {
                            File file = new File(Constants.INTERNAL_LOCATION + "/" + zipEntry.getName());
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
                    Utilidades.escribirLogErrores(e);
                }
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
            try {
                Utilidades.setPermissionRecursive(new File(Constants.INTERNAL_LOCATION));
            } catch (Exception e) {
                Utilidades.escribirLogErrores(e);
            }
        }catch(Exception e){
            Utilidades.escribirLogErrores(e);
        }
        VersionThread asyncTask = new VersionThread(getApplicationContext());
        asyncTask.delegate = InicioActivity.this;
        asyncTask.execute(imei,imei2,mac,mac2);
    }
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonHomeM) {
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME));
        }
        if (id == R.id.buttonMenuM) {
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU));
        }
        if (id == R.id.buttonBackM) {
            this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        }

    }
    private BroadcastReceiver mScreenOn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            try {
                if(Utilidades.esTablet(getApplicationContext())) {
                    String action = intent.getAction();
                    if (Intent.ACTION_SCREEN_ON.equals(action)) {
                        screenOn += 1;
                        if (screenOn < 2) {
                            Intent intent2 = new Intent(getApplicationContext(), BlackActivity.class);
                            startActivity(intent2);
                        }
                        if (screenOn >= 2) {

                            java.lang.Process proc = Runtime.getRuntime().exec("su");
                            OutputStream outputStream = proc.getOutputStream();
                            outputStream.write("reboot\n".getBytes());
                            outputStream.flush();
                            outputStream.close();

                        }
                    }
                }
            }catch(Exception e){Utilidades.escribirLogErrores(e);}
        }
    };
    private void registrarReceivers() {
        this.registerReceiver(this.mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        this.registerReceiver(this.mWifi, new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION));
        if(Utilidades.esTablet(getApplicationContext())) {
            this.registerReceiver(this.mScreenOn, new IntentFilter(Intent.ACTION_SCREEN_ON));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_HOME){
            Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
            startActivity(intent);
        }
        return true;
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
            case KeyEvent.KEYCODE_BACK:
                if (action == KeyEvent.ACTION_DOWN) {
                    onResume();
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                try {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this, R.style.MyCustomDialogTheme);

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

                    } else {
                        //TODO click action
                    }
                }catch(Exception e){

                }
                if (action == KeyEvent.ACTION_DOWN) {
                    onResume();
                }
                return true;
            case KeyEvent.KEYCODE_HOME:
                if (action == KeyEvent.ACTION_DOWN) {
                    onResume();
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
    public void onResume(){
        try{
            if(!new File(Constants.EXTERNAL_STORAGE + "/logo.png").exists()){
                imageView.setImageResource(R.drawable.inicio);
            }else {
                imageView.setImageURI(Uri.fromFile(new File(Constants.EXTERNAL_STORAGE + "/logo.png")));
            }
            Utilidades.getImei(getApplicationContext());
            registrarReceivers();
            Utilidades.activarDatos(getApplicationContext());
            VersionThread asyncTask = new VersionThread(getApplicationContext());
            asyncTask.delegate=InicioActivity.this;
            asyncTask.execute(imei,imei2,mac,mac2);
        }catch(Exception e){
            Utilidades.escribirLogErrores(e);
        }

        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //onResume();
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }
    @Override
    public void processFinish(String output) {
        if(!"".equals(output.trim()) && !"NotFound".equals(output)){
            try {
                handler.removeCallbacks(runnable);
                primeraEjecucion=false;
                JSONObject jObject = new JSONObject(output);
                InicioActivity.terminalBean = Utilidades.crearTerminalBean(jObject);
                Utilidades.actualizarAppRom(getApplicationContext(), InicioActivity.terminalBean);

                if(Utilidades.checkWifiOnAndConnected(getApplicationContext())){
                    Utilidades.enviarEmailsEncolados(getApplicationContext());
                }
                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                startActivity(intent);
            }catch(Exception e){
                Utilidades.escribirLogErrores(e);
            }

        }else {
            if(primeraEjecucion) {
                primeraEjecucion=false;
                handler.postDelayed(runnable, 30000);
            }else{
                handler.postDelayed(runnable, 100);
            }
        }

    }
    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(this.mNetworkStateReceiver);
        }catch(Exception e){
            Utilidades.escribirLogErrores(e);
        }
        try{
            unregisterReceiver(this.mWifi);
        }catch(Exception e){
            Utilidades.escribirLogErrores(e);
        }
        try{
            if(Utilidades.esTablet(getApplicationContext())) {
                unregisterReceiver(this.mScreenOn);
            }
        }catch(Exception e){}
        if (blockingView!=null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
        super.onDestroy();
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
            Utilidades.escribirLogErrores(e);
        }
    }
    public static void unpreventStatusBarExpansion(Context context) {
        try {

            WindowManager manager = ((WindowManager) context.getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE));

            Activity activity = (Activity) context;

            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();

            customViewGroup view = new customViewGroup(context);

            manager.addView(view, localLayoutParams);
        }catch(Exception e){
            Utilidades.escribirLogErrores(e);
        }
    }
    public void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(this.mNetworkStateReceiver);
            this.unregisterReceiver(this.mWifi);
        }catch(Exception e){}
    }

    public boolean onTouch(View v, MotionEvent event)
    {
        int id = v.getId();
        if (id == R.id.buttonHome || id == R.id.buttonHomeM)
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
        if (id == R.id.buttonMenu || id == R.id.buttonMenuM)
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
        if (id == R.id.buttonBack || id == R.id.buttonBackM)
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
        if (id == R.id.buttonPower)
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

        if (id == R.id.buttonMenuM) {
            AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this, R.style.MyCustomDialogTheme);

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


    public static class customViewGroup extends ViewGroup {

        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            /*if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "User can access system settings without this permission!", Toast.LENGTH_SHORT).show();
            }
            else
            { */disableStatusBar();
            //}
        //}
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
    private Runnable runnable=new Runnable() {
        public void run() {
            Intent i3 = new Intent(getApplicationContext(), FullscreenActivity.class);
            i3.putExtra("modo", "noconectado");
            startActivity(i3);
        }
    };

    private BroadcastReceiver mNetworkStateReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Utilidades.getImei(context);
                textView.setText(rom+" "+version+" apk "+verCodeApp);
                VersionThread asyncTask = new VersionThread(getApplicationContext());
                asyncTask.delegate = InicioActivity.this;
                asyncTask.execute(InicioActivity.imei, InicioActivity.imei2, InicioActivity.mac, InicioActivity.mac2);
            }catch(Exception e){
                Utilidades.escribirLogErrores(e);
            }
        }
    };
    private final BroadcastReceiver mWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

                    WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

                    if (wifiInfo.getNetworkId() == -1) {
                        //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi not connected");
                    }
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi on");
                    //Utilidades.recuentoMB(false);
                } else {
                    //Utilidades.recuentoMB(true);
                    //((TextView)findViewById(R.id.batteryLevel)).setText("Wifi off");
                }
            }
        }
    };
}

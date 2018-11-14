package es.tfandroid.roombarlauncher;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by root on 17/07/17.
 */

public class Utilidades {
    public static SharedPreferences preferences;
    public final static String CHANGE_PERMISSION = "/system/bin/chmod -R 777 ";
    public static long downloadREF = -1;
    public static long downloadREF2 = -1;
    public static long downloadREF3 = -1;
    public static long downloadREF4 = -1;
    public static long downloadREF5 = -1;
    public static Camera cam = null;
    //public static long downloadREF6 = -1;
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

    /*public static void downloadWeb(Context context) {
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
    }*/

    public static TerminalBean crearTerminalBean(JSONObject listaDatos) throws JSONException {
        TerminalBean bean = new TerminalBean(listaDatos);
        return bean;

    }

    public static boolean comprobarConexion(String urlString, Context context) {
        boolean retorno = false;
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
                } else if (statusCode > 402) {
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
        } catch (Exception e1) {
        }
        return retorno;
    }

    public static void setPermissionRecursive(File permissionRecursive) {
        permissionRecursive.setExecutable(true, false);
        permissionRecursive.setReadable(true, false);
        permissionRecursive.setWritable(true, false);
        if (permissionRecursive.isDirectory()) {
            File[] files = permissionRecursive.listFiles();
            for (int x = 0; x < files.length; x++) {
                if (files[x].isDirectory()) {
                    setPermissionRecursive(files[x]);
                } else {
                    files[x].setExecutable(true, false);
                    files[x].setReadable(true, false);
                    files[x].setWritable(true, false);
                }
            }
        }
    }

    public static void setMobileDataState(boolean mobileDataEnabled, Context context) {
        habilitarRoaming(true,context);
        try {
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(telephonyService, mobileDataEnabled);
            }
        } catch (Exception ex) {
            Utilidades.escribirLogErrores(ex);
        }
    }

    private static void habilitarRoaming(boolean b,Context context) {
        try {
            Settings.Global.putInt(context.getContentResolver(), Settings.Global.DATA_ROAMING, 1);
        }catch(Exception e){Utilidades.escribirLogErrores(e);}

        try {
            java.lang.Process proc = Runtime.getRuntime().exec("su");
            OutputStream outputStream = proc.getOutputStream();
            outputStream.write("settings put global data_roaming0 1\n".getBytes());
            outputStream.write("settings put global data_roaming1 1\n".getBytes());
            outputStream.write("settings put global data_roaming2 1\n".getBytes());
            outputStream.flush();
            outputStream.close();
        }catch(Exception e){Utilidades.escribirLogErrores(e);}
    }

    public static boolean isTethering(Context context){
        boolean isTethering=false;
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Method meth = (Method) wm.getClass().getDeclaredMethod("isWifiApEnabled");
            isTethering=(Boolean)meth.invoke(wm);
        }catch(Exception e){
            Utilidades.escribirLogErrores(e);
        }
        return isTethering;
    }
    public static void cambiarBarraEstado(Context context, TerminalBean terminalBean) {
        try {
            String hot = Settings.System.getString(context.getContentResolver(), "status_bar_hotel");
            String hab = Settings.System.getString(context.getContentResolver(), "status_bar_habitacion");

            if (((hot == null || !hot.equals(terminalBean.getHotel())) && !InicioActivity.descargaApkLanzada && !InicioActivity.descargaRomLanzada && !InicioActivity.descargaLogosLanzada) || terminalBean.getActualizarLogos()) {
                //meter bootanimation y logo.bin
                new File(Constants.EXTERNAL_STORAGE + "/droidphp/logos.zip").delete();
                Uri uriParse = Uri.parse("http://tfandroid.es/roombar/logos/logos" + terminalBean.getHotel().replaceAll(" ","") + ".zip");
                DownloadManager.Request request = new DownloadManager.Request(uriParse);
                String nombreFichero = "";
                nombreFichero = "logos.zip";

                request.setDescription(nombreFichero);
                request.setTitle(nombreFichero);
                if (Build.VERSION.SDK_INT >= 11) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                }
                request.setDestinationInExternalPublicDir("/droidphp/", nombreFichero);

                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                downloadREF4 = manager.enqueue(request);

                if(terminalBean.getLogoPersonalizado()) {
                    new File(Constants.EXTERNAL_STORAGE + "/logo.png").delete();
                    uriParse = Uri.parse("http://tfandroid.es/roombar/logos/logo" + terminalBean.getHotel().replaceAll(" ","") + ".png");
                    request = new DownloadManager.Request(uriParse);
                    nombreFichero = "logo.png";

                    request.setDescription(nombreFichero);
                    request.setTitle(nombreFichero);
                    if (Build.VERSION.SDK_INT >= 11) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    request.setDestinationInExternalPublicDir("/", nombreFichero);

                    manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadREF5 = manager.enqueue(request);
                }
                Settings.System.putString(context.getContentResolver(), "status_bar_hotel", terminalBean.getHotel());
                Settings.System.putString(context.getContentResolver(), "status_bar_habitacion", terminalBean.getHabitacion());
                InicioActivity.descargaLogosLanzada = true;
            }

        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
        }
    }

    public static String obtenerFicheroFecha() {
        String fecha = "";
        Calendar instance = Calendar.getInstance();
        fecha += String.valueOf(instance.get(Calendar.YEAR));
        String mes = String.valueOf(instance.get(Calendar.MONTH) + 1);
        String dia = String.valueOf(instance.get(Calendar.DAY_OF_MONTH));
        if (mes.length() < 2) {
            mes = "0" + mes;
        }
        if (dia.length() < 2) {
            dia = "0" + dia;
        }
        fecha += mes + dia + ".zip";
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
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
            Utilidades.escribirLogErrores(ex);
        } // for now eat exceptions
        return "";
    }

    public static boolean esTablet(Context context) {
            return (Utilidades.getDpi(context) <= 240);

    }
    public static boolean tieneBotonesFisicos(Context context) {
        boolean hasMenuKey=ViewConfiguration.get(context).hasPermanentMenuKey();
        return hasMenuKey;
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

    public static String asignaFechaCompleta() {
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

    public static void setWifiTethering(Context ctx, boolean enable, String nombreSSID, String pass) throws Exception {

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
        mWifiManager.saveConfiguration();
        Method method2 = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
        method2.invoke(mWifiManager, wifiConfiguration);
        mWifiManager.setWifiEnabled(!enable);

        //mWifiManager.setWifiApEnabled(wifiConfiguration,enable);

        Method method3 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
        method3.invoke(mWifiManager, wifiConfiguration, enable);


    }

    public static void activarDatos(Context ctx) {
        try {
            Utilidades.setMobileDataState(true, ctx);
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration conf=new WifiConfiguration();
            if(InicioActivity.terminalBean!=null){
                String[] splitSSID = InicioActivity.terminalBean.getSsid().split("---");
                String[] splitSSIDKeyType = InicioActivity.terminalBean.getSsidKeyType().split("---");
                String[] splitSSIDpass = InicioActivity.terminalBean.getPassSsid().split("---");
                List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

                for(int x=0;x<splitSSID.length;x++) {
                    for(int y=0;y<configuredNetworks.size();y++){
                        if(splitSSID[x].equals(configuredNetworks.get(y).SSID)) {
                            wifiManager.removeNetwork(configuredNetworks.get(y).networkId);
                        }
                    }
                    conf.SSID = "\"" + splitSSID[x] + "\"";
                    if ("None".equals(splitSSIDKeyType[x])) {
                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    } else if ("WEP".equals(splitSSIDKeyType[x])) {
                        conf.wepKeys[0] = "\"" + splitSSIDpass[x] + "\"";
                        conf.wepTxKeyIndex = 0;
                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    } else if ("WPA".equals(splitSSIDKeyType[x])) {
                        conf.preSharedKey = "\"" + splitSSIDpass[x] + "\"";
                    }
                    wifiManager.addNetwork(conf);
                    wifiManager.saveConfiguration();
                }
            }
            wifiManager.setWifiEnabled(true);

            LocationManager locationManager = (LocationManager)
                    ctx.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Settings.Secure.putString(ctx.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "network,gps");
            Settings.Secure.putInt(ctx.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
           /*Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
           intent.putExtra("enabled", true);
           ctx.sendBroadcast(intent);*/
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            ctx.sendBroadcast(poke);

            LocationListener locationListener = new MyLocationListener(ctx);
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

       }catch(Exception e){
            Utilidades.escribirLogErrores(e);
       }
    }

    public static int obtenerUid(Context ctx) {
        final PackageManager pm = ctx.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        int UID=-1;
        //loop through the list of installed packages and see if the selected
        //app is in the list
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals("es.tfandroid.roombarlauncher")){
                //get the UID for the selected app
                UID = packageInfo.uid;
                break; //found a match, don't need to search anymore
            }

        }
        return UID;
    }

    public static void actualizarAppRom(Context applicationContext, TerminalBean terminalBean) {
        int verCodeApp=-1;
        float verCodeROM=-1;
        try {
            PackageInfo pInfo = applicationContext.getPackageManager().getPackageInfo("es.tfandroid.roombarlauncher", 0);
            verCodeApp = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Utilidades.escribirLogErrores(e);
        }
        verCodeROM=InicioActivity.version;
        if((terminalBean.getRomVersionBP()!=null && !"".equals(terminalBean.getRomVersionBP()))&& Float.parseFloat(terminalBean.getRomVersionBP())>verCodeROM && !InicioActivity.descargaRomLanzada && !InicioActivity.descargaApkLanzada & !InicioActivity.descargaLogosLanzada){
            if(InicioActivity.device.equals(terminalBean.getDeviceBP()) && InicioActivity.vendor.equals(terminalBean.getVendorBP()) && InicioActivity.rom.equals(terminalBean.getRomBP())) {
                //ActualizarROM de terminalBean.getUrlROM();
                Uri uriParse = Uri.parse(terminalBean.getUrlROM());
                DownloadManager.Request request = new DownloadManager.Request(uriParse);
                String nombreFichero = InicioActivity.terminalBean.urlROM.split("/")[InicioActivity.terminalBean.urlROM.split("/").length - 1];
                nombreFichero.trim().replaceAll("\r\n","");

                request.setDescription(nombreFichero);
                request.setTitle(nombreFichero);
                if (Build.VERSION.SDK_INT >= 11) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    new File(Constants.EXTERNAL_STORAGE + "/droidphp/"+nombreFichero).delete();
                }
                request.setDestinationInExternalPublicDir("/droidphp/", nombreFichero);

                DownloadManager manager = (DownloadManager) applicationContext.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor c = manager.query(new DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING));
                boolean yadescargando = false;
                if (c != null && c.moveToFirst()) {
                    int mID = c.getInt(0);
                    String mName = c.getString(1);
                    if (mName.contains(nombreFichero)) {
                        if (yadescargando) {
                            manager.remove(mID);
                        }
                        yadescargando = true;
                    }
                }
                while (c.moveToNext()) ;
                if (!yadescargando) {
                    Toast.makeText(applicationContext, "Updating service", Toast.LENGTH_SHORT).show();
                    downloadREF2 = manager.enqueue(request);
                }
                InicioActivity.descargaRomLanzada = true;
            }
        }
        if((terminalBean.getApkVersionBP()!=null && !"".equals(terminalBean.getApkVersionBP()))&& Integer.parseInt(terminalBean.getApkVersionBP())>verCodeApp && !InicioActivity.descargaApkLanzada && !InicioActivity.descargaRomLanzada& !InicioActivity.descargaLogosLanzada){
            //ActualizarAPK de terminalBean.getUrlApk();

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(terminalBean.getUrlApk()));
            request.setDescription("roombarlauncher.apk");
            request.setTitle("roombarlauncher.apk");
            if (Build.VERSION.SDK_INT >= 11) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                new File(Constants.EXTERNAL_STORAGE + "/droidphp/roombarlauncher.apk").delete();
            }
            Uri dst_uri = Uri.parse("file://"+Constants.EXTERNAL_STORAGE+"/droidphp/roombarlauncher.apk");
            request.setDestinationUri(dst_uri);

            DownloadManager manager = (DownloadManager) applicationContext.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor c=manager.query(new DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_PAUSED| DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING));
            boolean yadescargando=false;
            if(c!=null && c.moveToFirst()){
                int mID=c.getInt(0);
                String mName=c.getString(1);
                if(mName.contains("roombarlauncher.apk")){
                    if(yadescargando){
                        manager.remove(mID);
                    }
                    yadescargando=true;
                }
            }while(c.moveToNext());
            if(!yadescargando){
                downloadREF3 = manager.enqueue(request);
                Toast.makeText(applicationContext, "Updating serviceº" + " " + "roombarlauncher.apk", Toast.LENGTH_SHORT).show();
            }
            InicioActivity.descargaApkLanzada=true;
        }


    }
    public static void getImei(Context ctx){
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    InicioActivity.imei = tm.getDeviceId(0);
                    InicioActivity.imei2 = tm.getDeviceId(1);
                } else {
                    InicioActivity.imei = tm.getDeviceId();
                    InicioActivity.imei2 = tm.getDeviceId();
                }
            }
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
            InicioActivity.imei = "";
            InicioActivity.imei2 = "";
        }
        InicioActivity.mac = Utilidades.getMACAddress("wlan0");
        InicioActivity.mac2 = Utilidades.getMACAddress("eth0");
    }

    public static void eliminarNotificacionies(Context applicationContext) {
        NotificationManager nm=(NotificationManager)applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        try {
            java.lang.Process proc = Runtime.getRuntime().exec("su");
            OutputStream outputStream = proc.getOutputStream();
            outputStream.write("settings put global heads_up_notifications_enabled 0\n".getBytes());
            outputStream.flush();
            outputStream.close();
        }catch(Exception e){Utilidades.escribirLogErrores(e);}
        try{
            IBinder b = (IBinder) Class.forName("android.os.ServiceManager").getMethod("getService", new Class[] {
                    String.class
            }).invoke(null, new Object[] {
                    "statusbar"
            });

            Object iFace = Class.forName("com.android.internal.statusbar.IStatusBarService$Stub").getDeclaredMethod("asInterface", new Class[] {
                    IBinder.class
            }).invoke(null, new Object[] {
                    b
            });

            //iFace.getClass().getMethod("onClearAllNotifications", new Class[0]).invoke(iFace, (Object[]) null);
        }catch(Exception e){Utilidades.escribirLogErrores(e);}
        try{
            java.lang.Process proc = Runtime.getRuntime().exec("su");
            OutputStream outputStream = proc.getOutputStream();
            outputStream.write("service call notification 1\n".getBytes());
            outputStream.flush();
            outputStream.close();
        }catch(Exception e){Utilidades.escribirLogErrores(e);}
    }

    public static void borrarFicheros() {
        try{
            java.lang.Process proc = Runtime.getRuntime().exec("su");
            OutputStream outputStream = proc.getOutputStream();
            outputStream.write(("rm "+Constants.EXTERNAL_STORAGE + "/droidphp/*\n").getBytes());
            outputStream.flush();
            outputStream.close();

        File f=new File(Constants.EXTERNAL_STORAGE + "/droidphp/");
        File[] files = f.listFiles();
        for(int x =0;x<files.length;x++){
            if(files[x].isFile()){
                files[x].delete();
            }
        }
        }catch(Exception e){Utilidades.escribirLogErrores(e);}
    }

    public static void actualizarPermisos() {
        try {
            java.lang.Process proc = Runtime.getRuntime().exec("su");
            OutputStream outputStream = proc.getOutputStream();
            outputStream.write("mount -o,remount rw /system\n".getBytes());
            outputStream.write("chmod -R 777 /cache\n".getBytes());
            outputStream.write("exit".getBytes());
            outputStream.flush();
            outputStream.close();
        }catch(Exception e){Utilidades.escribirLogErrores(e);}
    }
    public static int getDpi(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    public static void escribirLogErrores(Exception e) {
        try {
            File n = new File(Constants.EXTERNAL_STORAGE + "/errores.log");
            FileOutputStream fos = new FileOutputStream(n, true);
            PrintWriter pw=new PrintWriter(fos);
            e.printStackTrace(pw);
            pw.flush();
            pw.close();
            fos.flush();
            fos.close();
        }catch(Exception e1){}
    }
    public static void flashLightOn(Context context) {

        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception flashLightOn()",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void flashLightOff(Context context) {
        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception flashLightOff",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void showDialogCamera(final Context context, final File newfilePicture,String inputText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyCustomDialogTheme);

        builder.setTitle(context.getResources().getString(R.string.hint_email));

        // Set up the input
        final EditText input = new EditText(context.getApplicationContext());
        input.setText(inputText);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String regex = "^(.+)@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(input.getText().toString());
                if(matcher.matches()) {
                    if(!checkWifiOnAndConnected(context)){
                        if(checkNetworkOnAndConnected(context)){
                            if(!showDialogConsumoDatos(context,newfilePicture)){
                                    Toast.makeText(context,"Email queued, will be send when Wifi is connected.",Toast.LENGTH_LONG).show();
                                    encolarEnvioFoto(input.getText().toString(),newfilePicture);
                                }else {
                                    sendMail(input.getText().toString(), context.getResources().getString(R.string.subject_Email), context.getResources().getString(R.string.msjEmail), newfilePicture.getAbsolutePath());
                                }
                        }else{
                                Toast.makeText(context,"Email queued, will be send when Wifi is connected.",Toast.LENGTH_LONG).show();
                                encolarEnvioFoto(input.getText().toString(),newfilePicture);
                        }
                    }else {
                        sendMail(input.getText().toString(), context.getResources().getString(R.string.subject_Email), context.getResources().getString(R.string.msjEmail), newfilePicture.getAbsolutePath());
                    }
                }else{
                    Toast.makeText(context,"Please check the email address",Toast.LENGTH_LONG).show();
                    showDialogCamera(context,newfilePicture,input.getText().toString());
                }
                dialog.dismiss();
            }

        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newfilePicture.delete();
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.show();
        input.requestFocus();
    }

    private static boolean checkNetworkOnAndConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static void encolarEnvioFoto(String email,File newFile) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email",email);
            jsonObject.put("file",newFile.getAbsolutePath());
            String message = jsonObject.toString();
            FileOutputStream fos=new FileOutputStream(new File(Constants.EXTERNAL_STORAGE+"/emailencolados.obj"),true);
            fos.write((message+"\n").getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
        }
    }

    public static boolean showDialogConsumoDatos(final Context context, final File newfilePicture) {
        final boolean[] retorno = {false};
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyCustomDialogTheme);
        builder.setTitle(context.getResources().getString(R.string.subject_Email));
        builder.setMessage(context.getResources().getString(R.string.hint_data_consum,convertMB(newfilePicture.getAbsoluteFile().length())));
        // Set up the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                retorno[0] =true;
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                retorno[0] =false;
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return retorno[0];
    }
    public static void sendMail(String email, String subject, String messageBody, String filename) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            //ENVIO DE EMAIL
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    "fraggelillo666@gmail.com", "alfaromeogt");
                        }
                    });
            // TODO Auto-generated method stub
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("roombar@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(messageBody);
            if (!"".equals(filename)) {
                Multipart _multipart = new MimeMultipart();
                BodyPart messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);

                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);

                _multipart.addBodyPart(messageBodyPart);
                message.setContent(_multipart);
            }
            Transport.send(message);

            new File(filename).delete();
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
        }
    }
    public static boolean checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

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
    public static String convertMB(long longitud){
        String tmp="";
        double m = longitud/1024.0;
        double g = longitud/1048576.0;
        double t = longitud/1073741824.0;
        DecimalFormat dec = new DecimalFormat("0.00");
        if (t > 1) {
            tmp = dec.format(t).concat("TB");
        } else if (g > 1) {
            tmp = dec.format(g).concat("MB");
        } else if (m > 1) {
            tmp = dec.format(m).concat("MB");
        } else {
            tmp = dec.format(longitud).concat("KB");
        }
        return tmp;
    }

    public static void enviarEmailsEncolados(Context context) {
        try {
            File f=new File(Constants.EXTERNAL_STORAGE+"/emailencolados.obj");
            if(f.exists()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String linea = br.readLine();
                JSONObject jsonObject = null;
                while (linea != null) {
                    jsonObject = new JSONObject(linea);
                    sendMail(jsonObject.getString("email"), context.getResources().getString(R.string.subject_Email), context.getResources().getString(R.string.msjEmail), jsonObject.getString("file"));
                    linea = br.readLine();
                }
                br.close();
                f.delete();
            }
        } catch (Exception e) {
            Utilidades.escribirLogErrores(e);
        }
    }
}

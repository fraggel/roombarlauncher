package es.tfandroid.roombarlauncher;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SettingsActivity extends AppCompatActivity {
    Button btnWifi=null;
    Button btnTeth=null;
    Button btnGps=null;
    Button btnAdmin=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Configuration");
        addListenerOnButton();
    }
    public void addListenerOnButton() {
        try {

            btnWifi = (Button) findViewById(R.id.btnWifi);
            btnWifi.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Intent wifi = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(wifi);
                    } catch (Exception e) {
                        try {
                            PrintWriter bw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/log.app"));
                            e.printStackTrace(bw);
                        } catch (Exception e2) {

                        }

                    }
                }

            });
            btnTeth = (Button) findViewById(R.id.btnTeth);
            btnTeth.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {

                    try {
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
                        Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                        WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

                        wifiConfig.SSID = "RoomBar"+ MD5(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

                        Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
                        setConfigMethod.invoke(wifiManager, wifiConfig);

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.settings", "com.android.settings.TetherSettings");
                        startActivity(intent);
                    } catch (Exception e) {
                        try {
                            PrintWriter bw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/log.app"));
                            e.printStackTrace(bw);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }

                    }
                }
                public String MD5(String md5) {
                    try {
                        MessageDigest md52 = MessageDigest.getInstance("MD5");
                        md52.update(StandardCharsets.UTF_8.encode(md5));
                        return (String.format("%032x", new BigInteger(1, md52.digest()))).substring(0,6);
                    } catch (java.security.NoSuchAlgorithmException e) {
                    }
                    return null;
                }
            });
            btnGps = (Button) findViewById(R.id.btnGps);
            btnGps.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    try {
                        Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gps);
                    } catch (Exception e) {
                        try {
                            PrintWriter bw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/log.app"));
                            e.printStackTrace(bw);
                        } catch (Exception e2) {

                        }

                    }
                }

            });
        } catch (Exception e) {
            try {
                PrintWriter bw = new PrintWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/log.app"));
                e.printStackTrace(bw);
            } catch (Exception e2) {

            }

        }
        btnAdmin =(Button)findViewById(R.id.btnAdmin);
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            private String m_Text=null;
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle("Enter Password");

// Set up the input
                    final EditText input = new EditText(getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            if ("1234".equals(m_Text)) {
                                m_Text="";
                                Intent settings = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(settings);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

        });

    }
}

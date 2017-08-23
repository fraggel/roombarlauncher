package es.tfandroid.roombarlauncher;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        try {
            if (getClass().getPackage().getName().equals(intent.getPackage())) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if(referenceId==Utilidades.downloadREF) {
                        ZipInputStream zipInputStream = null;
                        zipInputStream = new ZipInputStream(new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/droidphp/localhost.sql.zip")));
                        ZipEntry zipEntry;
                        try {
                            FileOutputStream fout;
                            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                                if (zipEntry.isDirectory()) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/htdocs/" + zipEntry.getName());
                                    if (!file.isDirectory()) file.mkdirs();
                                } else {

                                    fout = new FileOutputStream(Environment.getExternalStorageDirectory() + "/htdocs/" + zipEntry.getName());
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
                    }else if(referenceId==Utilidades.downloadREF2){
                        ZipInputStream zipInputStream = null;
                        zipInputStream = new ZipInputStream(new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/droidphp/sqlwebupdate.zip")));
                        ZipEntry zipEntry;
                        try {
                            FileOutputStream fout;
                            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                                if (zipEntry.isDirectory()) {
                                    File file = new File(Environment.getExternalStorageDirectory() + "/htdocs/" + zipEntry.getName());
                                    if (!file.isDirectory()) file.mkdirs();
                                } else {

                                    fout = new FileOutputStream(Environment.getExternalStorageDirectory() + "/htdocs/" + zipEntry.getName());
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
                    }else if(referenceId==Utilidades.downloadREF3){
                        try {
                            Process proc=Runtime.getRuntime().exec(new String[]{"su","pm install -r "+Environment.getExternalStorageDirectory() + "/droidphp/roombarlauncher.apk"});
                            proc.waitFor();
                            File ff=new File(Environment.getExternalStorageDirectory() + "/roombarlauncher.txt");
                            BufferedWriter brw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ff)));
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                            StrictMode.setThreadPolicy(policy);
                            URL jsonUrl = new URL("http://fraggel/roombarlauncher.txt");
                            BufferedReader in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
                            brw.write(in.readLine());
                            brw.flush();
                            brw.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    String[] baseShell = new String[]{
                            Constants.MYSQL_MONITOR_SBIN_LOCATION, "-h",
                            "127.0.0.1", "-T", "-f", "-r", "-t", "-E", "--disable-pager",
                            "-n", "--user=" + "root", "--password=" + "",
                            "--default-character-set=utf8", "-L"};
                    try {
                        Process process = new ProcessBuilder(baseShell).
                                redirectErrorStream(true).
                                start();
                        OutputStream outputStream = process.getOutputStream();
                        outputStream.write(("source "+Environment.getExternalStorageDirectory() + "/htdocs/localhost_duvfjvdv_apkjiayu.sql\n").getBytes());
                        outputStream.write(("source "+Environment.getExternalStorageDirectory() + "/htdocs/localhost_duvfjvdv_smf.sql\n").getBytes());
                        outputStream.write(("source "+Environment.getExternalStorageDirectory() + "/htdocs/localhost_duvfjvdv_tfweb.sql\n").getBytes());
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File ff=new File(Environment.getExternalStorageDirectory() + "/roombar.txt");
                    BufferedWriter brw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ff)));
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                    StrictMode.setThreadPolicy(policy);
                    URL jsonUrl = new URL("http://fraggel/roombar.txt");
                    BufferedReader in = new BufferedReader(new InputStreamReader(jsonUrl.openStream()));
                    brw.write(in.readLine());
                    brw.flush();
                    brw.close();
                } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                    Intent dm = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                    dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dm);
                }
            }
            NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancelAll();
        } catch (Exception e) {
        }
    }
}

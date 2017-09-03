package es.tfandroid.roombarlauncher;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Process;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DownloadReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        try {
            if (getClass().getPackage().getName().equals(intent.getPackage())) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if(referenceId==Utilidades.downloadREF) {

                    }else if(referenceId==Utilidades.downloadREF2){
                        try {
                            String nombreFichero = "";
                            nombreFichero = InicioActivity.terminalBean.urlROM.split("/")[InicioActivity.terminalBean.urlROM.split("/").length - 1];

                            String cad="";
                            cad= Environment.getExternalStorageDirectory() + "/droidphp/"+nombreFichero;
                            cad=cad.replaceAll(Environment.getExternalStorageDirectory().getAbsolutePath(),InicioActivity.pathRecovery);
                            java.lang.Process proc=Runtime.getRuntime().exec("su");
                            OutputStream outputStream = proc.getOutputStream();
                            outputStream.write("mount -o,remount rw /system\n".getBytes());
                            outputStream.write("chmod -R 777 /cache\n".getBytes());
                            outputStream.write("exit".getBytes());
                            outputStream.flush();
                            outputStream.close();
                            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(new File("/cache/recovery/extendedcommand")));

                            bos.write(("run_program(\"/sbin/umount\",\""+InicioActivity.pathRecovery+"\");\n").getBytes());
                            bos.write(("run_program(\"/sbin/mount,\""+InicioActivity.pathRecovery+"\");\n").getBytes());
                            bos.write(("install_zip(\"" + cad + "\");\n").getBytes());
                            bos.flush();
                            bos.close();

                            BufferedOutputStream bos2=new BufferedOutputStream(new FileOutputStream(new File("/cache/recovery/openrecoveryscript")));

                            bos2.write(("unmount "+InicioActivity.pathRecovery+"\n").getBytes());
                            bos2.write(("mount "+InicioActivity.pathRecovery+"\n").getBytes());
                            bos2.write(("install " + cad + "\n").getBytes());
                            bos2.flush();
                            bos2.close();

                            proc=Runtime.getRuntime().exec("su");
                            outputStream = proc.getOutputStream();
                            outputStream.write("reboot recovery".getBytes());
                            outputStream.flush();
                            outputStream.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(referenceId==Utilidades.downloadREF3){
                            /*try {
                                //Process proc = Runtime.getRuntime().exec(new String[]{"su", "pm install -r " + Environment.getExternalStorageDirectory() + "/droidphp/roombarlauncher.apk"});
                                Process proc = Runtime.getRuntime().exec(new String[]{"su", "source \""+Constants.INTERNAL_LOCATION + "/scripts/reinstallapp.sh\""});
                                proc.waitFor();
                            }catch(Exception e){}
                            */
                        java.lang.Process proc=Runtime.getRuntime().exec("su");
                        OutputStream outputStream = proc.getOutputStream();
                        outputStream.write("mount -o,remount rw /system\n".getBytes());
                        outputStream.write("rm -rf /system/priv-app/roombarlauncher/roombarlauncher.apk\n".getBytes());
                        outputStream.write(("cp -r "+Environment.getExternalStorageDirectory() + "/droidphp/roombarlauncher.apk /system/priv-app/roombarlauncher/roombarlauncher.apk\n").getBytes());
                        outputStream.write("chmod 644 /system/priv-app/roombarlauncher/roombarlauncher.apk\n".getBytes());
                        outputStream.write(("reboot\n").getBytes());
                        outputStream.flush();
                        outputStream.close();
                        InicioActivity.descargaApkLanzada=false;
                    }
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

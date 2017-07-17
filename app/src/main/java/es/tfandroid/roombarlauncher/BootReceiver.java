package es.tfandroid.roombarlauncher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by root on 18/06/17.
 */
public class BootReceiver extends BroadcastReceiver {
    public final static String CHANGE_PERMISSION = "/system/bin/chmod -R 777";
    private File[] permissionRecursive;

    @Override
    public void onReceive(Context context, Intent intent) {
        /*try {
           new File("/sdcard/droidphp/logs").mkdirs();
        }catch(Exception e){}
        try {
            new File("/sdcard/droidphp/tmp").mkdirs();
        }catch(Exception e){}
        try {
            new File("/sdcard/droidphp/sessions").mkdirs();
        }catch(Exception e){}

        try{
            Runtime.getRuntime().exec("su -c \"nohup /system/droidphp/lighttpd/lighttpd -f /system/droidphp/lighttpd/lighttpd.conf -D 1>/dev/null 2>&1\"");
        }catch(Exception e){}
        try{
            Runtime.getRuntime().exec("su -c \"nohup /system/droidphp/php/php-cgi -a -b /sdcard/droidphp/tmp/php.sock /system/droidphp/php/php.ini 1>/dev/null 2>&1\"");
        }catch(Exception e){}

        try{
            Runtime.getRuntime().exec("su -c \"nohup /system/droidphp/mysql/mysqld --defaults-file=/system/droidphp/mysql/mysql.ini --user=root --language=/system/droidphp/mysql/share/mysql/english 1>/dev/null 2>&1\"");
        }catch(Exception e){}
        *//*busybox killall -SIGTERM lighttpd
        busybox killall -3 mysqld
        busybox killall -SIGTERM mysqld
        busybox killall -SIGTERM php-cgi*/

        /*try{
            Runtime.getRuntime().exec("/data/data/org.opendroidphp/scripts/server-sh.sh");
        }catch(Exception e){}*/

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calc = Calendar.getInstance();
        calc.add(Calendar.SECOND,2);
        Intent intent2 = new Intent(context, NotifyService.class);
        PendingIntent pintent = PendingIntent.getService(context, 0, intent2,
                0);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calc.getTimeInMillis(),7200000, pintent);

        Intent myIntent = new Intent(context, InicioActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }
}
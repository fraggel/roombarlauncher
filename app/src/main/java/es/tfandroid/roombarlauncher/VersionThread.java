package es.tfandroid.roombarlauncher;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionThread extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String... params) {
        String retorno="";
        String imei=params[0];
        String imei2=params[1];
        String mac=params[2];
        String mac2=params[3];
        BufferedReader in=null;
        try {

            //URL url = new URL("http://localhost:8080/index.php");
            //URL url =new URL("http://www.roombar.com/App-RoomBar/01/");
            URL url =new URL("http://tfandroid.es/roombar/checkDevice.php?imei="+imei+"&imei2="+imei2+"&mac="+mac+"&mac2="+mac2);
            HttpURLConnection http = null;
            int statusCode=0;
            while(statusCode!=200 || "".equals(retorno.trim())) {
                try {
                    http = (HttpURLConnection) url.openConnection();
                    statusCode = http.getResponseCode();
                    in = new BufferedReader(new InputStreamReader(url.openStream()));
                    retorno=in.readLine();
                }catch(Exception e){
                    statusCode=0;
                }
            }
        }catch(Exception e){
            retorno="";
        }

            return retorno;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}

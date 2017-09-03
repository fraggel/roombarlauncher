package es.tfandroid.roombarlauncher;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionThread extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;
    private Context mContext;

    public VersionThread (Context context){
        mContext = context;
    }
    @Override
    protected String doInBackground(String... params) {
        String retorno="";
        String imei=params[0];
        String imei2=params[1];
        String mac=params[2];
        String mac2=params[3];
        BufferedReader in=null;
        int cont=0;
        try {
            int statusCode=0;
            //URL url = new URL("http://localhost:8080/index.php");
            //URL url =new URL("http://www.roombar.com/App-RoomBar/01/");
            while(statusCode!=200 || "".equals(retorno.trim()) && cont<100) {
                Utilidades.getImei(mContext);
            URL url =new URL("http://tfandroid.es/roombar/checkDevice.php?imei="+InicioActivity.imei+"&imei2="+InicioActivity.imei2+"&mac="+InicioActivity.mac+"&mac2="+InicioActivity.mac2);
            HttpURLConnection http = null;


                try {
                    http = (HttpURLConnection) url.openConnection();
                    statusCode = http.getResponseCode();
                    in = new BufferedReader(new InputStreamReader(url.openStream()));
                    retorno=in.readLine();
                    cont++;
                }catch(Exception e){
                    statusCode=0;
                }
            }
        }catch(Exception e){
            retorno="";
        }
            cont=0;
            return retorno;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}

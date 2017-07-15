package es.tfandroid.roombarlauncher;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class VersionThread extends AsyncTask<String, Void, String> {
    public AsyncResponse delegate = null;

    @Override
    protected String doInBackground(String... params) {
        String retorno="";
        try {
            URL url = new URL("http://localhost:8080/");
            HttpURLConnection http = null;
            int statusCode=0;
            while(statusCode!=200) {
                try {
                    http = (HttpURLConnection) url.openConnection();
                    statusCode = http.getResponseCode();
                    retorno=String.valueOf(statusCode);
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

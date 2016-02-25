package in.easynote.easynote.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import in.easynote.easynote.Listeners.FetchDataListener;
import in.easynote.easynote.Model.Note;

/**
 * Created by AKG002 on 15-02-2016.
 */
public class SendData extends AsyncTask<Void,Void,String> {
    public static final String SERVER_URL = FetchData.SERVER_URL;
    String postEntity;
    Context context;
    OutputStreamWriter writer;
    BufferedReader reader;
    ProgressDialog dialog;
    public static final String SEND_NOTES = SERVER_URL+"/api/get_from_phone.jsp" ;
    FetchDataListener listener;
    public SendData(Context context,FetchDataListener listener,String postEntity){
        this.context = context;
        this.listener = listener;
        this.postEntity = postEntity;
    }

    @Override
    protected String doInBackground(Void[] params) {
        try {
            URL url = new URL(SEND_NOTES);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.connect();
            writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(postEntity);
            writer.close();
            Log.d("EasyNote/SendData", postEntity);
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
            String result = sb.toString();
            Log.d("EasyNote/SendData",result);
            return result;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("Connecting to server");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        listener.postRequest(s);
        dialog.dismiss();
    }
}

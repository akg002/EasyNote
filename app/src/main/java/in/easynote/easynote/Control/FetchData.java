package in.easynote.easynote.Control;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import in.easynote.easynote.Listeners.FetchDataListener;
import in.easynote.easynote.Model.Note;

/**
 * Created by AKG002 on 14-02-2016.
 */
public class FetchData extends AsyncTask<Void,Void,String>{
    Context context;
    FetchDataListener listener;
    public int op;
    String username,password,email;
    ProgressDialog dialog;
    Note note;
    public static final int LOG_IN = 0;
    public static final int SIGN_UP = 1;
    public static final int GET_NOTES = 2;
  /*  public static final String SERVER_URL = "http://easynote-akg002.rhcloud.com";*/
      public static final String SERVER_URL = "http://easynote-sansari.rhcloud.com";
/*    public static final String SERVER_URL = "http://192.168.3.100/easynote";*/


    public FetchData(Context context,FetchDataListener listener,int operation){
        this.context = context;
        this.listener = listener;
        this.op = operation;
    }

    public void setLogInArgs(String username,String password){
        this.username = username;
        this.password = password;
    }

    public void setSignUpArgs(String username,String password,String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public void setGetNotesArgs(String username){
        this.username = username;
    }

    String getURL(int op) {
        String url = "";
        switch (op){
            case LOG_IN : url = SERVER_URL+"/api/login.jsp?username="+username+"&password="+password;
            break;
            case SIGN_UP : url = SERVER_URL+"/api/signup.jsp?username="+username+"&password="+password+"&email="+email;
                break;
            case GET_NOTES : url = SERVER_URL+"/api/send_to_phone.jsp?username="+username;
                break;
        }
        return url ;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = null;
        try{
            URL url = new URL(getURL(op));
            Log.d("EasyNote/FetchData",""+url);
            HttpURLConnection connection= (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
//            Log.d("EasyNote/FetchData", "" + connection.getResponseCode());
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
            result = sb.toString();
            Log.d("EasyNote/FetchData",result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
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

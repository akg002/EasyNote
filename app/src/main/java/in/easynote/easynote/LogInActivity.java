package in.easynote.easynote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import in.easynote.easynote.Control.FetchData;
import in.easynote.easynote.Listeners.FetchDataListener;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener,FetchDataListener{
    SharedPreferences sp;
    EditText username,password;
    TextView notMember;
    FetchData fetchData;
    Button logInBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        logInBtn = (Button) findViewById(R.id.login_btn);
        logInBtn.setOnClickListener(this);
        notMember = (TextView)findViewById(R.id.not_member);
        notMember.setOnClickListener(this);
        sp = getApplicationContext().getSharedPreferences("LogIn",0);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn : fetchData = new FetchData(this,this,FetchData.LOG_IN);
                fetchData.setLogInArgs(username.getText().toString(), password.getText().toString());
                fetchData.execute();
                break;
            case R.id.not_member :
                Intent i = new Intent(this,SignUpActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public void preRequest() {

    }

    @Override
    public void postRequest(String result) {
        if (result != null) {
            if (result.trim().equals("valid")) {
                SharedPreferences.Editor editor = sp.edit();
                Toast.makeText(this, "Logged In", Toast.LENGTH_LONG).show();
                editor.putBoolean("loggedIn", true);
                editor.putString("username", username.getText().toString());
                editor.commit();
                Log.d("EasyNote/Splash", "Loged In :" + sp.getBoolean("loggedIn", false));
                Intent i = new Intent(this,NotesActivity.class);
                startActivity(i);
                finish();
            } else
                Toast.makeText(this, "Invalid password or username", Toast.LENGTH_LONG).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("EasyNote");
            builder.setMessage("Error in connectin");
            builder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    fetchData = new FetchData(LogInActivity.this,LogInActivity.this,FetchData.LOG_IN);
                    fetchData.setLogInArgs(username.getText().toString(), password.getText().toString());
                    fetchData.execute();

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}

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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener,FetchDataListener{
    SharedPreferences sp;
    EditText username,password,email;
    TextView alreadyMember;
    FetchData fetchData;
    Button signUpBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText)findViewById(R.id.email);
        signUpBtn = (Button) findViewById(R.id.signup_btn);
        signUpBtn.setOnClickListener(this);
        alreadyMember = (TextView)findViewById(R.id.already_member);
        alreadyMember.setOnClickListener(this);
        sp = getApplicationContext().getSharedPreferences("LogIn",0);
    }

    @Override
    public void preRequest() {

    }

    @Override
    public void postRequest(String result) {
        if (result != null) {
            if (result.trim().equals("0")) {
                SharedPreferences.Editor editor = sp.edit();
                Toast.makeText(this, "Signed in", Toast.LENGTH_LONG).show();
                editor.putBoolean("loggedIn", true);
                editor.putString("username", username.getText().toString());
                editor.commit();
                Log.d("EasyNote/Splash", "Loged In :" + sp.getBoolean("loggedIn", false));
                Intent i = new Intent(this,NotesActivity.class);
                startActivity(i);
                finish();
            } else if (result.trim().equals("1")){
                Toast.makeText(this, "Email already registered", Toast.LENGTH_LONG).show();
                email.setError("Email already registered");
            }else if (result.trim().equals("2")){
                Toast.makeText(this, "Username already taken", Toast.LENGTH_LONG).show();
                username.setError("Username already taken");
            }
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("EasyNote");
            builder.setMessage("Error in connectin");
            builder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    fetchData = new FetchData(SignUpActivity.this,SignUpActivity.this,FetchData.SIGN_UP);
                    fetchData.setSignUpArgs(username.getText().toString(), password.getText().toString(),
                            email.getText().toString());
                    fetchData.execute();

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_btn : fetchData = new FetchData(this,this,FetchData.SIGN_UP);
                fetchData.setSignUpArgs(username.getText().toString(), password.getText().toString(),
                        email.getText().toString());
                fetchData.execute();
                break;
            case R.id.already_member :
                Intent i = new Intent(this,LogInActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }
}

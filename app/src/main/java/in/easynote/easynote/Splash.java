package in.easynote.easynote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Splash extends AppCompatActivity {
SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp = getApplicationContext().getSharedPreferences("LogIn",0);
       final  Intent i;
        Log.d("EasyNote/Splash","Loged In :"+sp.getBoolean("loggedIn",false));
        if (sp.getBoolean("loggedIn",false)){
            i = new Intent(Splash.this, NotesActivity.class);
        }
        else {
            i = new Intent(Splash.this, LogInActivity.class);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent i = new Intent(Splash.this,LogInActivity.class);

                startActivity(i);
                finish();
            }
        },3500);

    }
}

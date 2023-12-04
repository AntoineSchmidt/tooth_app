package hs_kempten.ibrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import hs_kempten.ibrush.R;
import hs_kempten.ibrush.database.DatabaseHelper;

/**
 * Created by Antoine Schmidt
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SplashDuration = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create Database Instance
        DatabaseHelper.createInstance(getApplicationContext());

        // set the GUI
        setContentView(R.layout.activity_splash);

        // start the other activity after 1500ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // start the mainactivity
                Intent showMainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(showMainActivityIntent);
                // close this activity
                finish();
            }
        }, SplashDuration);
    }

    @Override
    public void onBackPressed() {
        // do nothing. So this Activity cannot be closed by pressing the back-key
    }
}

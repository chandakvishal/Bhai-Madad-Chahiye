package com.bhaimadadchahiye.club;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bhaimadadchahiye.club.login.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity to fire when the application launches.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
            }

        }, 2000);
    }
}

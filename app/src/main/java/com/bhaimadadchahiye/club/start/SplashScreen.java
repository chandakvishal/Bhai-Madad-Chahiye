package com.bhaimadadchahiye.club.start;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.login.LoginActivity;
import com.bhaimadadchahiye.club.login.MyMainActivity;

/**
 * Created by visha on 25-02-2016.
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences

                    DatabaseHandler localDB = new DatabaseHandler(SplashScreen.this);
                    try {
                        int rowCount = localDB.getRowCount();
                        if (rowCount != 0) {
                            finish();
                            Intent loggedIn = new Intent(SplashScreen.this, MyMainActivity.class);
                            startActivity(loggedIn);
                        } else {
                            finish();
                            Intent logIn = new Intent(SplashScreen.this, SplashScr.class);
                            startActivity(logIn);
                        }
                    }
                    finally {
                        localDB.close();
                    }
            }
        });

        // Start the thread
        t.start();
    }

}

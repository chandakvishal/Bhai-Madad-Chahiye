package com.bhaimadadchahiye.club.start;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bhaimadadchahiye.club.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.utils.DatabaseHandler;

import static com.bhaimadadchahiye.club.constants.DB_Constants.TABLE_LOGIN;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        DatabaseHandler localDB = new DatabaseHandler(SplashScreen.this);
        try {
            int rowCount = localDB.getRowCount(TABLE_LOGIN);
            if (rowCount != 0) {
                finish();
                Intent loggedIn = new Intent(SplashScreen.this, MenuActivity.class);
                startActivity(loggedIn);
            } else {
                finish();
                Intent logIn = new Intent(SplashScreen.this, SplashScr.class);
                startActivity(logIn);
            }
        } finally {
            localDB.close();
        }
    }
}


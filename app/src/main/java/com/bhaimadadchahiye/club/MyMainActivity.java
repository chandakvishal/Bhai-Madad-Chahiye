package com.bhaimadadchahiye.club;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;
import com.bhaimadadchahiye.club.location.GPSTracker;
import com.bhaimadadchahiye.club.login.ChangePassword;
import com.bhaimadadchahiye.club.start.SplashScreen;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;

public class MyMainActivity extends AppCompatActivity {
    Button btnLogout;
    Button changePassBtn;
    double latitude, longitude;
    private Boolean exit = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymainactivity);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        changePassBtn = (Button) findViewById(R.id.changePassBtn);
        btnLogout = (Button) findViewById(R.id.logout);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        /**
         * Hashmap to load data from the Sqlite database
         **/
        HashMap user;
        user = db.getUserDetails();

        /**
         * Change Password Activity Started
         **/
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent changePass = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(changePass);
            }

        });

        /**
         *Logout from the User Panel which clears the data in Sqlite database
         **/
        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                UserFunctions logout = new UserFunctions();
                logout.logoutUser(getApplicationContext());
                Intent login = new Intent(getApplicationContext(), SplashScreen.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
            }
        });
/**
 * Sets user first name and last name in text view.
 **/
        final TextView login = (TextView) findViewById(R.id.textwelcome);
        String welcomeText = "Welcome, " + user.get(KEY_FULLNAME);
        login.setText(welcomeText);

        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude(); // returns latitude
            longitude = gps.getLongitude(); // returns longitude
            gps.stopUsingGPS();
        } else {
            gps.showSettingsAlert();
        }
        final TextView gpsLocation = (TextView) findViewById(R.id.textView4);
        String latlong = latitude + " " + longitude;
        gpsLocation.setText(latlong);
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}

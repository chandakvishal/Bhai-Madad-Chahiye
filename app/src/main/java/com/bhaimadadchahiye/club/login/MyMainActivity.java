package com.bhaimadadchahiye.club.login;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;

import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;

import com.bhaimadadchahiye.club.R;

import java.util.HashMap;

public class MyMainActivity extends AppCompatActivity {
    Button btnLogout;
    Button changePassBtn;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymainactivity);

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
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                finish();
            }
        });
/**
 * Sets user first name and last name in text view.
 **/
        final TextView login = (TextView) findViewById(R.id.textwelcome);
        login.setText("Welcome, "+user.get(KEY_FULLNAME));
    }
}

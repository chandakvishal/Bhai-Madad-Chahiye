package com.bhaimadadchahiye.club.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.utils.DatabaseHandler;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_CREATED_AT;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;

public class Registered extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registered);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        HashMap user;
        user = db.getUserDetails();

         // Displays the registration details in Text view
        final TextView fname = (TextView) findViewById(R.id.fullName);
        final TextView lname = (TextView) findViewById(R.id.phoneNumber);
        final TextView uname = (TextView) findViewById(R.id.username);
        final TextView email = (TextView) findViewById(R.id.email);
        final TextView created_at = (TextView) findViewById(R.id.regat);
        fname.setText((CharSequence) user.get(KEY_FULLNAME));
        lname.setText((CharSequence) user.get(KEY_PHONE));
        uname.setText((CharSequence) user.get(KEY_USERNAME));
        email.setText((CharSequence) user.get(KEY_EMAIL));
        created_at.setText((CharSequence) user.get(KEY_CREATED_AT));

        TextView login = (TextView) findViewById(R.id.backToLoginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

    }
}
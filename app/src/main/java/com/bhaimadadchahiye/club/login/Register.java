package com.bhaimadadchahiye.club.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaimadadchahiye.club.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_CREATED_AT;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_ERROR;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_SUCCESS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_UID;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;

public class Register extends AppCompatActivity {

    /**
     * Defining layout items.
     **/

    EditText inputFullName;
    EditText inputPhoneNumber;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
    TextView registerErrorMsg;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        /**
         * Defining all layout items
         **/
        inputFullName = (EditText) findViewById(R.id.register_name);
        inputPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        inputUsername = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.register_email);
        inputPassword = (EditText) findViewById(R.id.register_password);
        btnRegister = (Button) findViewById(R.id.registerBtn);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);

        /**
         * Register Button click event.
         * A Toast is set to alert when the fields are empty.
         * Another toast is set to alert Username must be 5 characters.
         **/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((!inputUsername.getText().toString().equals("")) && (!inputPassword.getText().toString().equals("")) && (!inputFullName.getText().toString().equals("")) && (!inputPhoneNumber.getText().toString().equals("")) && (!inputEmail.getText().toString().equals(""))) {
                    if (inputUsername.getText().toString().length() > 4) {
                        if(inputPassword.getText().toString().length() > 6) {
//                            NetAsync(view);
                            new ProcessRegister().execute();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Hey, aren't you worried about your weak password!!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Async Task to check whether internet connection is working
     **/

    private class NetCheck extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(Register.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                nDialog.dismiss();
                new ProcessRegister().execute();
            } else {
                nDialog.dismiss();
                registerErrorMsg.setText("Error in Network Connection");
            }
        }
    }

    private class ProcessRegister extends AsyncTask<String, Void, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String email, password, fname, phone, uname;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputUsername = (EditText) findViewById(R.id.username);
            inputPassword = (EditText) findViewById(R.id.register_password);
            fname = inputFullName.getText().toString();
            phone = inputPhoneNumber.getText().toString();
            email = inputEmail.getText().toString();
            uname = inputUsername.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.registerUser(fname, phone, email, uname, password);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    registerErrorMsg.setText("");
                    String res = json.getString(KEY_SUCCESS);

                    String red = json.getString(KEY_ERROR);

                    if (Integer.parseInt(res) == 1) {
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");

                        registerErrorMsg.setText("Successfully Registered");

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");

                        /**
                         * Removes all the previous data in the SQlite database
                         **/

                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_FULLNAME), json_user.getString(KEY_PHONE), json_user.getString(KEY_EMAIL), json_user.getString(KEY_USERNAME), json_user.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));
                        /**
                         * Stores registered data in SQlite Database
                         * Launch Registered screen
                         **/

                        Intent registered = new Intent(getApplicationContext(), Registered.class);

                        /**
                         * Close all views before launching Registered screen
                         **/
                        registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pDialog.dismiss();
                        startActivity(registered);

                        finish();
                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        registerErrorMsg.setText("User already exists");
                    } else if (Integer.parseInt(red) == 3) {
                        pDialog.dismiss();
                        registerErrorMsg.setText("Invalid Email id");
                    }

                } else {
                    pDialog.dismiss();
                    Log.d("JSON Registration", String.valueOf(json));
                    registerErrorMsg.setText("Error occured in registration");
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    }

    public void NetAsync(View view) {
        new NetCheck().execute();
    }
}
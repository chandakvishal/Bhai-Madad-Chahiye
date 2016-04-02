package com.bhaimadadchahiye.club.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_CREATED_AT;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_ERROR;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_SUCCESS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_UID;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;

public class Register extends AppCompatActivity {

    EditText inputFullName;
    EditText inputPhoneNumber;
    EditText inputUsername;
    EditText inputEmail;
    EditText inputPassword;
    Button btnRegister;
    private Snackbar snackbar;

    /**
     * Called when the activity is first created.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "Invalid Credentials", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.YellowGreen));

        /**
         * Defining all layout items
         **/
        inputFullName = (EditText) findViewById(R.id.register_name);
        inputPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        inputUsername = (EditText) findViewById(R.id.username);
        inputEmail = (EditText) findViewById(R.id.register_email);
        inputPassword = (EditText) findViewById(R.id.register_password);
        btnRegister = (Button) findViewById(R.id.registerBtn);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**
         * Register Button click event.
         * A Snackbar is set to alert when the fields are empty.
         * Another Snackbar is set to alert Username must be 5 characters.
         **/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //noinspection ConstantConditions
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if ((!inputUsername.getText().toString().equals("")) && (!inputPassword.getText().toString().equals("")) && (!inputFullName.getText().toString().equals("")) && (!inputPhoneNumber.getText().toString().equals("")) && (!inputEmail.getText().toString().equals(""))) {
                    if (inputUsername.getText().toString().length() > 4) {
                        if (inputPassword.getText().toString().length() > 6) {
                            NetAsync(view);
                        } else {
                            snackbar.setText("Hey, Weak Password!!!").show();
                        }
                    } else {
                        snackbar.setText("Username should be minimum 5 characters").show();
                    }
                } else {
                    snackbar.setText("One or more fields are empty").show();
                }
            }
        });
    }

    public void NetAsync(View view) {
        new NetCheck().execute();
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = cm.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = cm.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    try {
                        URL url = new URL("http://www.google.com/");
                        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                        urlc.setConnectTimeout(3000);
                        urlc.connect();
                        if (urlc.getResponseCode() == 200) {
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th) {
                nDialog.dismiss();
                new ProcessRegister().execute();
            } else {
                nDialog.dismiss();
                snackbar.setText("Error in Network Connection").show();
            }
        }
    }

    private class ProcessRegister extends AsyncTask<String, Void, JSONObject> {

        String email, password, fname, phone, uname;
        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

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
            pDialog.setMessage("Registering....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            UserFunctions userFunction = new UserFunctions();
            return userFunction.registerUser(fname, phone, email, uname, password);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    String res = json.getString(KEY_SUCCESS);

                    String red = json.getString(KEY_ERROR);

                    if (Integer.parseInt(res) == 1) {
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");

                        /**
                         * Removes all the previous data in the SQlite database
                         **/
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext(), false);
                        db.addUser(json_user.getString(KEY_FULLNAME), json_user.getString(KEY_PHONE), json_user.getString(KEY_EMAIL), json_user.getString(KEY_USERNAME), json_user.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));
                        /**
                         * Stores registered data in SQlite Database
                         * Launch Registered screen
                         **/
                        Thread.sleep(1000);
                        pDialog.dismiss();
                        final Intent registered = new Intent(getApplicationContext(), MenuActivity.class);
                        /**
                         * Close all views before launching Registered screen
                         **/
                        registered.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        registered.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(registered);
                        finish();
                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        snackbar.setText("User already exists").show();
                    } else if (Integer.parseInt(red) == 3) {
                        pDialog.dismiss();
                        snackbar.setText("Invalid Email id").show();
                    }
                } else {
                    pDialog.dismiss();
                    Log.d("JSON Registration", String.valueOf(json));
                    snackbar.setText("Error occurred in registration").show();
                }
            } catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
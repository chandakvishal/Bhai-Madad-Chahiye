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
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
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
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_SUCCESS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_UID;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    TextView btnLogin;
    TextView passreset;
    // UI references.
    private AutoCompleteTextView inputEmail;
    private EditText inputPass;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "Invalid Credentials", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        //noinspection deprecation
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        //noinspection deprecation
        textView.setTextColor(getResources().getColor(R.color.YellowGreen));

        // Set up the login form.
        inputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        btnLogin = (TextView) findViewById(R.id.email_sign_in_button);
        passreset = (TextView) findViewById(R.id.forgotPass);
        inputPass = (EditText) findViewById(R.id.password);
        //populateAutoComplete();

        /**
         * To change the activity to reset password activity
         */
        passreset.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                Intent myIntent = new Intent(view.getContext(), PasswordReset.class);
                startActivityForResult(myIntent, 0);
                overridePendingTransition(R.anim.animation1, R.anim.animation3);

                //finish();
            }
        });

        /**
         * To change the activity to register new id activity
         */

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings("ConstantConditions")
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if ((!inputEmail.getText().toString().equals("")) && (!inputPass.getText().toString().equals(""))) {
                    NetAsync(view);
                } else if ((!inputEmail.getText().toString().equals(""))) {
                    snackbar.setText("Password field empty").show();
                } else if ((!inputPass.getText().toString().equals(""))) {
                    snackbar.setText("Email field empty").show();
                } else {
                    snackbar.setText("Email and Password field empty").show();
                }
            }
        });
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
    }

    public void NetAsync(View view) {
        new NetCheck().execute();
    }

    /**
     * Async Task to check whether internet connection is working.
     **/
    private class NetCheck extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(LoginActivity.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
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
                new ProcessLogin().execute();
            } else {
                nDialog.dismiss();
                snackbar.setText("Error in Network Connection").show();
            }
        }
    }

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessLogin extends AsyncTask<String, Void, JSONObject> {

        String email, password;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            inputEmail = (AutoCompleteTextView) findViewById(R.id.email);
            inputPass = (EditText) findViewById(R.id.password);
            email = inputEmail.getText().toString();
            password = inputPass.getText().toString();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            return userFunction.loginUser(email, password);
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json == null) {
                    snackbar.setText("Unable to connect to Internet").show();
                    pDialog.dismiss();
                } else {
                    if (json.getString(KEY_SUCCESS) != null) {
                        String res = json.getString(KEY_SUCCESS);
                        if (Integer.parseInt(res) == 1) {
                            pDialog.setMessage("Loading User Space");
                            pDialog.setTitle("Getting Data");
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            JSONObject json_user = json.getJSONObject("user");
                            /**
                             * Clear all previous data in SQlite database.
                             **/
                            UserFunctions logout = new UserFunctions();
                            logout.logoutUser(getApplicationContext(), false);
                            db.addUser(json_user.getString(KEY_FULLNAME), json_user.getString(KEY_PHONE), json_user.getString(KEY_EMAIL), json_user.getString(KEY_USERNAME), json_user.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));
                            /**
                             *If JSON array details are stored in SQlite it launches the User Panel.
                             **/
                            Intent registered = new Intent(getApplicationContext(), MenuActivity.class);
                            registered.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            registered.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pDialog.dismiss();
                            startActivity(registered);
                            /**
                             * Close Login Screen
                             **/
                            finish();
                        } else {

                            pDialog.dismiss();
                            snackbar.setText("Incorrect Username/password").show();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
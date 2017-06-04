package com.bhaimadadchahiye.club.Answers;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bhaimadadchahiye.club.NavigationMenu.Fragments.HomeFragment;
import com.bhaimadadchahiye.club.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.utils.BackHandledFragment;
import com.bhaimadadchahiye.club.utils.DatabaseHandler;
import com.bhaimadadchahiye.club.utils.HideKeyboard;
import com.bhaimadadchahiye.club.utils.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_ERROR;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LATITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LONGITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_SUCCESS;

public class PostQuestion extends BackHandledFragment {

    private EditText qTag, qTitle, qBody;
    protected Button postQuestionBtn;
    private Snackbar snackbar;
    private View inflated;

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final String[] tags = new String[1];
        final String[] title = new String[1];
        final String[] body = new String[1];

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Post Question");

        setHasOptionsMenu(true);

        inflated = inflater.inflate(R.layout.question, container, false);
        //Asks the user for input i.e the question to be posted
        qTag = (EditText) inflated.findViewById(R.id.questionTags);
        qTitle = (EditText) inflated.findViewById(R.id.questionTitle);
        qBody = (EditText) inflated.findViewById(R.id.questionBody);

        //Snackbar Configuration
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) inflated.findViewById(R.id
                .coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "Successfully Posted the Question", Snackbar.LENGTH_LONG);
        final View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.YellowGreen));

        postQuestionBtn = (Button) inflated.findViewById(R.id.postQuestionBtn);
        qBody.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    postQuestionBtn.performClick();
                    return true;
                }
                return false;
            }
        });

        postQuestionBtn.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         try {
                             InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                             //noinspection ConstantConditions
                             imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                         tags[0] = qTag.getText().toString();
                         title[0] = qTitle.getText().toString();
                         body[0] = qBody.getText().toString();

                         Log.d("TAG", tags[0]);
                         Log.d("TITLE", title[0]);
                         Log.d("BODY", body[0]);
                         if (tags[0].length() == 0 || title[0].length() == 0 || body[0].length() == 0) {
                             Log.d("TAG", tags[0]);
                             Log.d("TITLE", title[0]);
                             Log.d("BODY", body[0]);
                             snackBarView.setBackgroundColor(getResources().getColor(R.color.Red));
                             snackbar.setText("Please fill all fields").show();
                         } else {
                             new NetCheck().execute();
                         }
                     }
                 }
                );

        return inflated;
    }

    @Override
    public boolean onBackPressed() {
        HideKeyboard.hideKeyboard(getActivity());
        ((MenuActivity) getActivity()).changeFragment(new HomeFragment(), "home",
                                                      R.anim.enter_anim, R.anim.exit_anim);
        return true;
    }

    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Async Task to check whether internet connection is working
     **/

    private class NetCheck extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(getActivity());
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
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Boolean th) {

            if (th) {
                nDialog.dismiss();
                new PostData().execute();
            } else {
                nDialog.dismiss();
                Snackbar snackbar = Snackbar.make(inflated, "Error in Network Connection", Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.IndianRed))
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new NetCheck().execute();
                            }
                        });
                snackbar.show();
            }
        }
    }

    private class PostData extends AsyncTask<String, Void, JSONObject> {

        private ProgressDialog pDialog;
        String tags, title, body, email, locality;
        Double latitude, longitude;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //This is required to keep track of the user who posted the question
            DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
            HashMap user;
            user = db.getUserDetails();
            email = (String) user.get(KEY_EMAIL);

            HashMap<String, Double> locationData = db.getUserLocation();

            //Getting users current location (Kyu....mat puchna!!!)
            latitude = locationData.get(KEY_LATITUDE);
            longitude = locationData.get(KEY_LONGITUDE);
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                locality = addresses.get(0).getFeatureName();
                if (locality == null) {
                    locality = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            tags = qTag.getText().toString();
            title = qTitle.getText().toString();
            body = qBody.getText().toString();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userFunction = new UserFunctions();
            Log.d("PostQuestion: ", "doInBackground: Sent the question to store");
            return userFunction.postQuestion(tags, title, body, email, latitude, longitude, locality);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);

                    if (Integer.parseInt(res) == 1) {
                        //Dismiss the process dialogs
                        pDialog.dismiss();
                        snackbar.setText("Your Question has been successfully posted").show();
                        qTag.setText("");
                        qTitle.setText("");
                        qBody.setText("");
                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        snackbar.setText("Sorry, some glitch occurred at our end.").show();
                    } else {
                        pDialog.dismiss();
                        snackbar.setText("DAFAQ!!!!").show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

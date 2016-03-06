package com.bhaimadadchahiye.club.location;

import android.os.AsyncTask;
import android.util.Log;

import com.bhaimadadchahiye.club.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LATITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LONGITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.TABLE_LOCATION;

public class HomeLocationRegister extends AsyncTask<Double, Void, JSONObject> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(Double... params) {
        DatabaseHandler db = new DatabaseHandler(MenuActivity.getcontext());
        HashMap<String, String> userData = db.getUserDetails();
        String email = userData.get(KEY_EMAIL);
        Log.i("Email Received:", email);
        UserFunctions userFunctions = new UserFunctions();
        return userFunctions.storeUserHomeLocation(email, params[0], params[1]);
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        Log.d("JSONObject", String.valueOf(json));
        DatabaseHandler db = new DatabaseHandler(MenuActivity.getcontext());
        try {
            if (db.getRowCount(TABLE_LOCATION) == 0) {
                JSONObject json_user = json.getJSONObject("homelocal");
                db.addUser(json_user.getString(KEY_EMAIL), json_user.getDouble(KEY_LATITUDE), json_user.getDouble(KEY_LONGITUDE));
            } else {
                //User is already registered. Thus, leaving the else a No-Op.
                //This else is just a handler for async operations.
                Log.i("Home Location","User's Home location already set");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
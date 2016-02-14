package com.bhaimadadchahiye.club.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import static com.bhaimadadchahiye.club.constants.URIs.*;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PASSWORD;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_TAG;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FORGOT_PASS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_NEW_PASS;

import android.content.Context;

public class UserFunctions {

    private JSONParser jsonParser;

    // constructor
    public UserFunctions() {
        jsonParser = new JSONParser();
    }

    /**
     * Function to Login
     **/
    public JSONObject loginUser(String email, String password) {
        // Building Parameters
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, login_tag);
        dataToSend.put(KEY_EMAIL, email);
        dataToSend.put(KEY_PASSWORD, password);
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, dataToSend);
        return json;
    }

    /**
     * Function to change password
     **/
    public JSONObject chgPass(String newpas, String email) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, chgpass_tag);
        dataToSend.put(KEY_NEW_PASS, newpas);
        dataToSend.put(KEY_EMAIL, email);
        JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, dataToSend);
        return json;
    }

    /**
     * Function to reset the password
     **/

    public JSONObject forPass(String forgotpassword) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, forpass_tag);
        dataToSend.put(KEY_FORGOT_PASS, forgotpassword);
        JSONObject json = jsonParser.getJSONFromUrl(forpassURL, dataToSend);
        return json;
    }

    /**
     * Function to  Register
     **/
    public JSONObject registerUser(String fname, String phone, String email, String uname, String password) {
        // Building Parameters
        HashMap<String, String> dataToSend = new HashMap<>();
        List params = new ArrayList();
        dataToSend.put(KEY_TAG, register_tag);
        dataToSend.put(KEY_FULLNAME, fname);
        dataToSend.put(KEY_PHONE, phone);
        dataToSend.put(KEY_EMAIL, email);
        dataToSend.put(KEY_USERNAME, uname);
        dataToSend.put(KEY_PASSWORD, password);
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, dataToSend);
        return json;
    }

    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     */
    public boolean logoutUser(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }

}
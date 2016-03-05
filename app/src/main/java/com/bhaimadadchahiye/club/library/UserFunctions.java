package com.bhaimadadchahiye.club.library;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FORGOT_PASS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_NEW_PASS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PASSWORD;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_TAG;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;
import static com.bhaimadadchahiye.club.constants.URIs.chgpassURL;
import static com.bhaimadadchahiye.club.constants.URIs.chgpass_tag;
import static com.bhaimadadchahiye.club.constants.URIs.forpassURL;
import static com.bhaimadadchahiye.club.constants.URIs.forpass_tag;
import static com.bhaimadadchahiye.club.constants.URIs.loginURL;
import static com.bhaimadadchahiye.club.constants.URIs.login_tag;
import static com.bhaimadadchahiye.club.constants.URIs.registerURL;
import static com.bhaimadadchahiye.club.constants.URIs.register_tag;

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
        return jsonParser.getJSONFromUrl(loginURL, dataToSend);
    }

    /**
     * Function to change password
     **/
    public JSONObject chgPass(String newpas, String email) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, chgpass_tag);
        dataToSend.put(KEY_NEW_PASS, newpas);
        dataToSend.put(KEY_EMAIL, email);
        return jsonParser.getJSONFromUrl(chgpassURL, dataToSend);
    }

    /**
     * Function to reset the password
     **/

    public JSONObject forPass(String forgotpassword) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, forpass_tag);
        dataToSend.put(KEY_FORGOT_PASS, forgotpassword);
        return jsonParser.getJSONFromUrl(forpassURL, dataToSend);
    }

    /**
     * Function to  Register
     **/
    public JSONObject registerUser(String fname, String phone, String email, String uname, String password) {
        // Building Parameters
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, register_tag);
        dataToSend.put(KEY_FULLNAME, fname);
        dataToSend.put(KEY_PHONE, phone);
        dataToSend.put(KEY_EMAIL, email);
        dataToSend.put(KEY_USERNAME, uname);
        dataToSend.put(KEY_PASSWORD, password);
        return jsonParser.getJSONFromUrl(registerURL, dataToSend);
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
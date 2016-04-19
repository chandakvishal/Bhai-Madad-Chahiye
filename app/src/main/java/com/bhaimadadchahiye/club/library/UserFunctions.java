package com.bhaimadadchahiye.club.library;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FORGOT_PASS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LATITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LONGITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_NEW_PASS;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PASSWORD;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_TAG;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.GET_ANSWER;
import static com.bhaimadadchahiye.club.constants.DB_Constants.POST_ANSWER;
import static com.bhaimadadchahiye.club.constants.DB_Constants._ANSWER;
import static com.bhaimadadchahiye.club.constants.DB_Constants._OFFSET;
import static com.bhaimadadchahiye.club.constants.DB_Constants._QBODY;
import static com.bhaimadadchahiye.club.constants.DB_Constants._QTAG;
import static com.bhaimadadchahiye.club.constants.DB_Constants._QTITLE;
import static com.bhaimadadchahiye.club.constants.URIs.chgpassURL;
import static com.bhaimadadchahiye.club.constants.URIs.chgpass_tag;
import static com.bhaimadadchahiye.club.constants.URIs.forpassURL;
import static com.bhaimadadchahiye.club.constants.URIs.forpass_tag;
import static com.bhaimadadchahiye.club.constants.URIs.home_location_tag;
import static com.bhaimadadchahiye.club.constants.URIs.load_questions;
import static com.bhaimadadchahiye.club.constants.URIs.locationUrl;
import static com.bhaimadadchahiye.club.constants.URIs.loginURL;
import static com.bhaimadadchahiye.club.constants.URIs.login_tag;
import static com.bhaimadadchahiye.club.constants.URIs.post_question_tag;
import static com.bhaimadadchahiye.club.constants.URIs.questionUrl;
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
    public boolean logoutUser(Context context, boolean deleteDB) {
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetUserTables();
        if (deleteDB) {
            db.deleteDatabase(context);
        }
        return true;
    }

    public JSONObject storeUserHomeLocation(String email, double latitude, double longitude) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, home_location_tag);
        dataToSend.put(KEY_EMAIL, email);
        dataToSend.put(KEY_LATITUDE, String.valueOf(latitude));
        dataToSend.put(KEY_LONGITUDE, String.valueOf(longitude));
        return jsonParser.getJSONFromUrl(locationUrl, dataToSend);
    }

    public JSONObject postQuestion(String qTag, String qTitle, String qBody, String email, double latitude, double longitude) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, post_question_tag);
        dataToSend.put(KEY_EMAIL, email);
        dataToSend.put(KEY_LATITUDE, String.valueOf(latitude));
        dataToSend.put(KEY_LONGITUDE, String.valueOf(longitude));
        dataToSend.put(_QTAG, qTag);
        dataToSend.put(_QTITLE, qTitle);
        dataToSend.put(_QBODY, qBody);
        return jsonParser.getJSONFromUrl(questionUrl, dataToSend);
    }

    public JSONObject loadQuestions(double latitude, double longitude, int offset) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, load_questions);
        dataToSend.put(KEY_LATITUDE, String.valueOf(latitude));
        dataToSend.put(KEY_LONGITUDE, String.valueOf(longitude));
        dataToSend.put(_OFFSET, String.valueOf(offset));
        return jsonParser.getJSONFromUrl(questionUrl, dataToSend);
    }

    public JSONObject loadAnswers(double latitude, double longitude, String questionTitle) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, GET_ANSWER);
        dataToSend.put(_QTITLE, questionTitle);
        dataToSend.put(KEY_LATITUDE, String.valueOf(latitude));
        dataToSend.put(KEY_LONGITUDE, String.valueOf(longitude));
        return jsonParser.getJSONFromUrl(questionUrl, dataToSend);
    }

    public JSONObject postAnswer(String email, String answer, String questionTitle) {
        HashMap<String, String> dataToSend = new HashMap<>();
        dataToSend.put(KEY_TAG, POST_ANSWER);
        dataToSend.put(KEY_EMAIL, email);
        dataToSend.put(_ANSWER, answer);
        dataToSend.put(_QTITLE, questionTitle);
        return jsonParser.getJSONFromUrl(questionUrl, dataToSend);
    }
}
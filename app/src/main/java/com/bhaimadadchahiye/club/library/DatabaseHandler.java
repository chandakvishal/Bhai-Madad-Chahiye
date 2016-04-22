package com.bhaimadadchahiye.club.library;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_CREATED_AT;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_ID;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LATITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LONGITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_UID;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.TABLE_CURRENT;
import static com.bhaimadadchahiye.club.constants.DB_Constants.TABLE_LOCATION;
import static com.bhaimadadchahiye.club.constants.DB_Constants.TABLE_LOGIN;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "bmc_local";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FULLNAME + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_USERNAME + " TEXT,"
                + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + " ("
                + KEY_EMAIL + " TEXT NOT NULL,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL,"
                + " FOREIGN KEY (" + KEY_EMAIL + ") REFERENCES " + TABLE_LOGIN + "(" + KEY_EMAIL + "))";
        String CREATE_TABLE_CURRENT = "CREATE TABLE IF NOT EXISTS " + TABLE_CURRENT + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_TABLE_CURRENT);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String fname, String phone, String email, String uname, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FULLNAME, fname); // FirstName
        values.put(KEY_PHONE, phone); // LastName
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_USERNAME, uname); // UserName
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        Log.i("Added User", "Successfully Added User in Login Table");
        db.close(); // Closing database connection
    }

    /**
     * Storing user details in database
     */
    public void addUser(String email, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);

        // Inserting Row
        db.insert(TABLE_LOCATION, null, values);
        Log.i("Add User", "Successfully Added User in Location Table");
        db.close(); // Closing database connection
    }

    /**
     * Storing user current location in database
     */
    public void addUser(double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        Log.d("Latitude", String.valueOf(latitude));
        Log.d("Longitude", String.valueOf(longitude));
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);

        // Inserting Row
        db.insert(TABLE_CURRENT, null, values);
        Log.i("Add User", "Successfully Added User in Current Location Table");
        db.close(); // Closing database connection
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        Log.d("SIZE:", String.valueOf(this.getRowCount(TABLE_LOGIN)));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put(KEY_FULLNAME, cursor.getString(1));
            user.put(KEY_PHONE, cursor.getString(2));
            user.put(KEY_EMAIL, cursor.getString(3));
            user.put(KEY_USERNAME, cursor.getString(4));
            user.put(KEY_UID, cursor.getString(5));
            user.put(KEY_CREATED_AT, cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, Double> getUserLocation() {
        HashMap<String, Double> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CURRENT + " ORDER BY id DESC LIMIT 1";
        Log.d("SIZE:", String.valueOf(this.getRowCount(TABLE_LOGIN)));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put(KEY_LATITUDE, cursor.getDouble(1));
            user.put(KEY_LONGITUDE, cursor.getDouble(2));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    /**
     * Getting user login status
     * return true if rows are there in table
     */
    public int getRowCount(String TABLE) {
        String countQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re create database
     * Delete all tables and create them again
     */
    public void resetUserTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.delete(TABLE_LOCATION, null, null);
        db.close();
    }

    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }

}
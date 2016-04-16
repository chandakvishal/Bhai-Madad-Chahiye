package com.bhaimadadchahiye.club.location;

import android.os.AsyncTask;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.library.DatabaseHandler;

public class CurrentLocationRegister extends AsyncTask<Double, Void, Void> {
    double latitude, longitude;

    @Override
    protected Void doInBackground(Double... params) {
        DatabaseHandler db = new DatabaseHandler(MenuActivity.getcontext());
        latitude = params[0];
        longitude = params[1];
        db.addUser(latitude, longitude);
        return null;
    }
}
package com.bhaimadadchahiye.club.location;

import android.os.AsyncTask;

import com.bhaimadadchahiye.club.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.utils.DatabaseHandler;

class CurrentLocationRegister extends AsyncTask<Double, Void, Void> {

    @Override
    protected Void doInBackground(Double... params) {
        DatabaseHandler db = new DatabaseHandler(MenuActivity.getcontext());
        double latitude = params[0];
        double longitude = params[1];
        db.addUser(latitude, longitude);
        return null;
    }
}
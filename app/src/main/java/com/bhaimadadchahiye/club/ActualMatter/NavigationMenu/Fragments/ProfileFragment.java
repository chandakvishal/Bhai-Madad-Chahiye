package com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.BackHandledFragment;
import com.bhaimadadchahiye.club.library.DatabaseHandler;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_CREATED_AT;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;

public class ProfileFragment extends BackHandledFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());

        HashMap user = db.getUserDetails();

        View inflated = inflater.inflate(R.layout.registered, container, false);

        Log.d("Details: ", String.valueOf(user.values()));

        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        setHasOptionsMenu(true);

        /**
         * Displays the registration details in Text view
         **/
        final TextView fname = (TextView) inflated.findViewById(R.id.fullName);
        final TextView lname = (TextView) inflated.findViewById(R.id.phoneNumber);
        final TextView uname = (TextView) inflated.findViewById(R.id.username);
        final TextView email = (TextView) inflated.findViewById(R.id.email);
        final TextView created_at = (TextView) inflated.findViewById(R.id.regat);
        fname.setText((CharSequence) user.get(KEY_FULLNAME));
        lname.setText((CharSequence) user.get(KEY_PHONE));
        uname.setText((CharSequence) user.get(KEY_USERNAME));
        email.setText((CharSequence) user.get(KEY_EMAIL));
        created_at.setText((CharSequence) user.get(KEY_CREATED_AT));

        return inflated;
    }

    @Override
    public boolean onBackPressed() {
        ((MenuActivity) getActivity()).changeFragment(new HomeFragment(), "home");
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
        } else if (id == R.id.action_user) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}

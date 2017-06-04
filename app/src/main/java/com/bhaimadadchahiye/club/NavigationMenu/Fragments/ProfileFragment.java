package com.bhaimadadchahiye.club.NavigationMenu.Fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bhaimadadchahiye.club.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.utils.BackHandledFragment;
import com.bhaimadadchahiye.club.utils.DatabaseHandler;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_CREATED_AT;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_FULLNAME;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_PHONE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_USERNAME;

public class ProfileFragment extends BackHandledFragment {

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());

        HashMap user = db.getUserDetails();

        View inflated = inflater.inflate(R.layout.registered, container, false);

        Log.d("Details: ", String.valueOf(user.values()));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

        setHasOptionsMenu(true);

        /*
         * Displays the registration details in Text view
         **/
        final TextView firstName = (TextView) inflated.findViewById(R.id.fullName);
        final TextView lastName = (TextView) inflated.findViewById(R.id.phoneNumber);
        final TextView userName = (TextView) inflated.findViewById(R.id.username);
        final TextView email = (TextView) inflated.findViewById(R.id.email);
        final TextView created_at = (TextView) inflated.findViewById(R.id.regat);
        firstName.setText((CharSequence) user.get(KEY_FULLNAME));
        lastName.setText((CharSequence) user.get(KEY_PHONE));
        userName.setText((CharSequence) user.get(KEY_USERNAME));
        email.setText((CharSequence) user.get(KEY_EMAIL));
        created_at.setText((CharSequence) user.get(KEY_CREATED_AT));

        return inflated;
    }

    @Override
    public boolean onBackPressed() {
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
        } else if (id == R.id.action_user) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

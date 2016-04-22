package com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.BackHandledFragment;
import com.bhaimadadchahiye.club.library.UserFunctions;
import com.bhaimadadchahiye.club.login.ChangePassword;
import com.bhaimadadchahiye.club.start.SplashScreen;

public class SettingsFragment extends BackHandledFragment {

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.settings, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");

        setHasOptionsMenu(true);

        Button changePassBtn = (Button) inflatedView.findViewById(R.id.changePassBtn);
        Button btnLogout = (Button) inflatedView.findViewById(R.id.logout);


        /**
         * Change Password Activity Started
         **/
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent changePass = new Intent(getActivity(), ChangePassword.class);
                startActivity(changePass);
            }

        });

        /**
         *Logout from the User Panel which clears the data in Sqlite database
         **/
        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                UserFunctions logout = new UserFunctions();
                logout.logoutUser(getActivity(), true);
                Intent login = new Intent(getActivity(), SplashScreen.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login);
                getActivity().finish();
            }
        });

        return inflatedView;
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
        }
        return super.onOptionsItemSelected(item);
    }
}

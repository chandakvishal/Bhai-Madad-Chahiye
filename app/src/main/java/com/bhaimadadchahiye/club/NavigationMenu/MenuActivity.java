package com.bhaimadadchahiye.club.NavigationMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaimadadchahiye.club.NavigationMenu.Fragments.Feedback;
import com.bhaimadadchahiye.club.NavigationMenu.Fragments.HomeFragment;
import com.bhaimadadchahiye.club.NavigationMenu.Fragments.ProfileFragment;
import com.bhaimadadchahiye.club.NavigationMenu.Fragments.SettingsFragment;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.utils.BackHandledFragment;
import com.bhaimadadchahiye.club.location.GPSTracker;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, BackHandledFragment.BackHandlerInterface, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int REQUEST_LOCATION = 199;
    private static MenuActivity mContext;
    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemFeedback;
    private ResideMenuItem itemSettings;
    private GPSTracker gpsTracker;
    private BackHandledFragment selectedFragment;
    Snackbar snackbar;

    public static Context getcontext() {
        return mContext;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
        setUpMenu();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_fragment);
        frameLayout.getForeground().setAlpha(0);

        CoordinatorLayout mLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        snackbar = Snackbar.make(mLayout, "Invalid Credentials", Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        //noinspection deprecation
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        //noinspection deprecation
        textView.setTextColor(getResources().getColor(R.color.YellowGreen));
        if (savedInstanceState == null)
            changeFragment(new HomeFragment(), "home");

        gpsTracker = new GPSTracker(MenuActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (selectedFragment == null || !selectedFragment.onOptionsItemSelected(item)) {
            if (id == R.id.action_user) {
                changeFragment(new ProfileFragment(), "profile");
            } else if (id == R.id.action_help) {
                changeFragment(new Feedback(), "feedback");
            } else if (id == R.id.notification) {
                snackbar.setText("No new notifications to show").show();
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setUse3D(true);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome = new ResideMenuItem(this, R.drawable.icon_home, "Home");
        itemProfile = new ResideMenuItem(this, R.drawable.icon_profile, "Profile");
        itemFeedback = new ResideMenuItem(this, R.drawable.ic_feedback, "About Us");
        itemSettings = new ResideMenuItem(this, R.drawable.ic_build, "Settings");

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemFeedback.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFeedback, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome) {
            changeFragment(new HomeFragment(), "home");
        } else if (view == itemProfile) {
            changeFragment(new ProfileFragment(), "profile");
        } else if (view == itemSettings) {
            changeFragment(new SettingsFragment(), "settings");
        } else if (view == itemFeedback) {
            changeFragment(new Feedback(), "feedback");
        }

        resideMenu.closeMenu();
    }

    public void changeFragment(Fragment targetFragment, String tag) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.animation2, R.anim.animation4)
                .replace(R.id.main_fragment, targetFragment, tag)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(tag)
                .commit();
    }

    public void changeFragment(Fragment targetFragment, String tag, int animation1, int animation2) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(animation1, animation2)
                .replace(R.id.main_fragment, targetFragment, tag)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));
        Log.d("Request Code", String.valueOf(requestCode));
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        gpsTracker.fetchLocation();
                        Toast.makeText(MenuActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(mContext, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {

        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            // Selected fragment did not consume the back press event.
            super.onBackPressed();
        }
    }
}
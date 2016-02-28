package com.bhaimadadchahiye.club.start;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.login.LoginActivity;
import com.bhaimadadchahiye.club.login.Register;
import com.bhaimadadchahiye.club.start.SplashResource.AppIntro;

public class SplashScr extends AppIntro {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.intro1));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));

        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));
        showStatusBar(false);
        setVibrate(true);
        setVibrateIntensity(30);
    }

    public void loadLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        overridePendingTransition(R.animator.animation1,R.animator.animation3);
            }

    public void loadRegisterActivity() {
        Intent registerIntent = new Intent(this, Register.class);
        startActivity(registerIntent);
        overridePendingTransition(R.animator.animation1, R.animator.animation3);
    }

    @Override
    public void onSkipPressed() {
        loadLoginActivity();
    }

    @Override
    public void onNextPressed() {
        loadRegisterActivity();
    }

    @Override
    public void onDonePressed() {
        loadRegisterActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    public void getStarted(View v) {
        loadLoginActivity();
    }
}
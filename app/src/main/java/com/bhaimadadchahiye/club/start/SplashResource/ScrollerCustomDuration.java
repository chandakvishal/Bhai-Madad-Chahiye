package com.bhaimadadchahiye.club.start.SplashResource;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class ScrollerCustomDuration extends Scroller {

    private double mScrollFactor = 6;

    public ScrollerCustomDuration(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        mScrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, (int) (duration * mScrollFactor));
    }

}
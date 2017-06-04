package com.bhaimadadchahiye.club.Answers;

import android.view.View;

public interface ClickListener {

    /**
     * Listens to the click performed on the elements on the View
     *
     * @param view view to listen the click for
     * @param position position of the click
     */
    void onClick(View view, int position);

    /**
     * Listens to Click-and-Hold Event on the view.
     * Used to show various popups when the user holds and element
     *
     * @param view view to listen the click for
     * @param position position of the click
     */
    void onLongClick(View view, int position);
}
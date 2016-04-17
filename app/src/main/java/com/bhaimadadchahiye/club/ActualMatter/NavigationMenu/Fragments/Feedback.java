package com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.BackHandledFragment;

public class Feedback extends BackHandledFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View parentView = inflater.inflate(R.layout.feedback, container, false);

        parentView.findViewById(R.id.github_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/chandakvishal"));
                startActivity(browserIntent);
            }
        });

        parentView.findViewById(R.id.linkedin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://in.linkedin.com/in/vishal-chandak-a73865b5"));
                startActivity(browserIntent);
            }
        });

        parentView.findViewById(R.id.btn_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback();
            }
        });
        return parentView;
    }

    //Start a new activity for sending a feedback email
    private void sendFeedback() {
        final Intent _Intent = new Intent(android.content.Intent.ACTION_SEND);
        _Intent.setType("text/html");
        _Intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.mail_feedback_email)});
        _Intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.mail_feedback_subject));
        _Intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.mail_feedback_message));
        startActivity(Intent.createChooser(_Intent, getString(R.string.title_send_feedback)));
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
        }
        return super.onOptionsItemSelected(item);
    }
}

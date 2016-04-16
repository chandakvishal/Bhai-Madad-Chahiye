package com.bhaimadadchahiye.club.ActualMatter.Answers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments.HomeFragment;
import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.BackHandledFragment;
import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_EMAIL;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_ERROR;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_SUCCESS;


public class PostAnswer extends BackHandledFragment {

    private static String questionTitle = null;
    private EditText answer_received;
    protected Button postAnswerBtn;
    private Snackbar snackbar;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final String[] answer = new String[1];

        View inflated = inflater.inflate(R.layout.post_answer, container, false);
        //Asks the user for input i.e the question to be posted
        answer_received = (EditText) inflated.findViewById(R.id.answer_received);

        //Snackbar Configuration
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) inflated.findViewById(R.id
                .coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "Successfully Posted the Question", Snackbar.LENGTH_LONG);
        final View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.YellowGreen));

        postAnswerBtn = (Button) inflated.findViewById(R.id.postAnswerBtn);

        postAnswerBtn.setOnClickListener
                (new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         try {
                             InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                             //noinspection ConstantConditions
                             imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                         answer[0] = answer_received.getText().toString();

                         if (answer[0].length() == 0) {
                             snackBarView.setBackgroundColor(getResources().getColor(R.color.Red));
                             snackbar.setText("Please fill all fields").show();
                         } else {
                             new PostData().execute();
                         }
                     }
                 }
                );

        return inflated;
    }

    public void setQuestionTitle(String questionTitle) {
        PostAnswer.questionTitle = questionTitle;
    }

    //// TODO: 14-04-2016 Change back button behaviour
    @Override
    public boolean onBackPressed() {
        ((MenuActivity) getActivity()).changeFragment(new HomeFragment(), "home");
        return true;
    }

    private class PostData extends AsyncTask<String, Void, JSONObject> {

        private ProgressDialog pDialog;
        String answer, email, questionTitle;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //This is required to keep track of the user who posted the question
            DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
            HashMap user;
            user = db.getUserDetails();
            email = (String) user.get(KEY_EMAIL);

            questionTitle = PostAnswer.questionTitle;

            answer = answer_received.getText().toString();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userFunction = new UserFunctions();
            Log.d("PostAnswer: ", "doInBackground: Sent the answer to store");
            return userFunction.postAnswer(email, answer, questionTitle);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);

                    if (Integer.parseInt(res) == 1) {
                        //Dismiss the process dialogs
                        pDialog.dismiss();
                        snackbar.setText("Your Answer has been successfully posted").show();
                    } else if (Integer.parseInt(red) == 2) {
                        pDialog.dismiss();
                        snackbar.setText("Sorry, some glitch occurred at our end.").show();
                    } else {
                        pDialog.dismiss();
                        snackbar.setText("DAFAQ!!!!").show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.bhaimadadchahiye.club.ActualMatter.Answers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments.HomeFragment;
import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.BackHandledFragment;
import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_ERROR;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LATITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LONGITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_SUCCESS;

public class Answers extends BackHandledFragment {

    private AnswersAdapter mAdapter;

    private static String questionTitle = null;

    private List<Answer> questionList = new ArrayList<>();

    private String TAG = MenuActivity.class.getSimpleName();

    private FloatingActionButton menuFAB;

    private Handler mUiHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.answers, container, false);

        RecyclerView recyclerView = (RecyclerView) parentView.findViewById(R.id.recycler_view_for_answers);

        //Floating Action Button Menu Configuration
        menuFAB = (FloatingActionButton) parentView.findViewById(R.id.post_answer_fab);

        mAdapter = new AnswersAdapter(getActivity(), questionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        getAnswer();

        return parentView;
    }

    @Override
    public boolean onBackPressed() {
        ((MenuActivity) getActivity()).changeFragment(new HomeFragment(), "home");
        return true;
    }

    public void setQuestionTitle(String questionTitle) {
        Answers.questionTitle = questionTitle;
        Log.d(TAG, "setQuestionTitle: " + questionTitle);
    }

    public void getAnswer() {
        new LoadAnswers().execute();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        menuFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostAnswer postAnswer = new PostAnswer();
                postAnswer.setQuestionTitle(questionTitle);
                ((MenuActivity) getActivity()).changeFragment(new PostAnswer(), "answers");
            }
        });
    }

    private class LoadAnswers extends AsyncTask<String, Void, JSONObject> {

        Double latitude, longitude;

        String questionTitle;

        @Override
        protected void onPreExecute() {

//            Date date = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//            formattedDate = sdf.format(date);

            DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
            HashMap<String, Double> locationData = db.getUserLocation();

            questionTitle = Answers.questionTitle;
            //Getting users current location (Kyu....mat puchna!!!)
            latitude = locationData.get(KEY_LATITUDE);
            longitude = locationData.get(KEY_LONGITUDE);
            if (latitude == null || longitude == null) {
                latitude = 12.827561378;
                longitude = 80.050422668;
            }
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userFunction = new UserFunctions();
            Log.d("PostQuestion: ", "doInBackground: Sent the question to store");
            return userFunction.loadAnswers(latitude, longitude, questionTitle);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                Log.d(TAG, "onPostExecute: " + json);
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);

                    if (Integer.parseInt(res) == 1) {

                        //Dismiss the process dialogs
                        JSONArray response = (JSONArray) json.get("answers");

                        if (response.length() > 0) {
                            Log.d("ANSWER: ", "onPostExecute: " + response.toString());

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject questionTitlesJsonObject = response.getJSONObject(i);
                                String title = String.valueOf(questionTitlesJsonObject.get("answer"));
                                int rank = questionTitlesJsonObject.getInt("rank");
                                Answer m = new Answer(rank, title);

                                questionList.add(0, m);
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                    } else if (Integer.parseInt(red) == 2) {

                        String error_msg = json.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();

                    } else {
                        Log.d(TAG, "onPostExecute: LOG2");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

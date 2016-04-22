package com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bhaimadadchahiye.club.ActualMatter.Answers.Answers;
import com.bhaimadadchahiye.club.ActualMatter.Answers.ClickListener;
import com.bhaimadadchahiye.club.ActualMatter.Answers.DividerItemDecoration;
import com.bhaimadadchahiye.club.ActualMatter.Answers.PostQuestion;
import com.bhaimadadchahiye.club.ActualMatter.Answers.Question;
import com.bhaimadadchahiye.club.ActualMatter.Answers.QuestionAdapter;
import com.bhaimadadchahiye.club.ActualMatter.Answers.RecyclerTouchListener;
import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.ResideMenu;
import com.bhaimadadchahiye.club.ActualMatter.Search.Search;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.BackHandledFragment;
import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.bhaimadadchahiye.club.library.UserFunctions;
import com.bhaimadadchahiye.club.location.GPSTracker;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_ERROR;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LATITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_LONGITUDE;
import static com.bhaimadadchahiye.club.constants.DB_Constants.KEY_SUCCESS;

public class HomeFragment extends BackHandledFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private FloatingActionMenu menuFAB;

    private FloatingActionButton fab1;

    private FloatingActionButton fab2;

    private Handler mUiHandler = new Handler();

    public ArrayList<Question> questionList = new ArrayList<>();

    private QuestionAdapter mAdapter;

    private String TAG = MenuActivity.class.getSimpleName();

    private int offset;

    private SwipeRefreshLayout swipeRefreshLayout;

    private GoogleApiClient mGoogleApiClient;

    private GPSTracker gpsTracker;

    private Boolean exit = false;

    private String savedJson;

    private ResideMenu resideMenu;

    public Answers ans = new Answers();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.home, container, false);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gpsTracker = new GPSTracker(getActivity());

        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        setHasOptionsMenu(true);

        MenuActivity parentActivity = (MenuActivity) getActivity();

        resideMenu = parentActivity.getResideMenu();

        //Floating Action Button Menu Configuration
        menuFAB = (FloatingActionMenu) parentView.findViewById(R.id.menu_red);
        menuFAB.setClosedOnTouchOutside(true);
        menuFAB.hideMenuButton(false);

        RecyclerView recyclerView = (RecyclerView) parentView.findViewById(R.id.recycler_view_for_questions);

        mAdapter = new QuestionAdapter(getActivity(), questionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchAnswers();
            }
        });

        fab1 = (FloatingActionButton) parentView.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) parentView.findViewById(R.id.fab2);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        final SharedPreferences questionData = getActivity().getSharedPreferences("order", Context.MODE_APPEND);
        Log.d("Value", String.valueOf(questionData.getBoolean("saved", false)));
        Log.d("JSON", questionData.getString("questionTitleList", ""));
        if (questionData.getBoolean("saved", false)) {
            loadSavedData(questionData);
            mAdapter.notifyDataSetChanged();

        } else {
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            fetchAnswers();
                                        }
                                    }
            );
        }

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Question question = questionList.get(position);

                ans.setQuestion(question);

                ((MenuActivity) getActivity()).changeFragment(new Answers(), "home");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);

        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                menuFAB.showMenuButton(true);
            }
        });
        menuFAB.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                menuFAB.toggle(true);
            }
        });
    }

    protected void startLocationUpdates() {
        gpsTracker.locationChecker(mGoogleApiClient, getActivity());
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            stopLocationUpdates();
        }
        saveData();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        startLocationUpdates();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onBackPressed() {
        if (exit) {
            getActivity().finish(); // finish activity
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
        return true;
    }

    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetching answers from Database
     */
    public void fetchAnswers() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "fetchAnswers: Trying to load Answers");
        new LoadQuestions().execute();
    }

    /**
     * Save the Data fetched via PHP to Local Storage.
     */
    public void saveData() {
        SharedPreferences.Editor outState = getActivity().getSharedPreferences("order", Context.MODE_APPEND).edit();
        outState.putString("questionTitleList", savedJson);
        Log.d(TAG, "******SAVED******");
        if (savedJson != null)
            Log.d("SAVED JSON", savedJson);
        outState.putBoolean("saved", true);
        outState.apply();
    }

    public void loadSavedData(SharedPreferences questionData) {
        JSONArray response = null;
        String res = questionData.getString("questionTitleList", "");
        try {
            response = new JSONArray(res);
            savedJson = response.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < (response != null ? response.length() : 0); i++) {
            try {
                JSONObject savedQuestionObject = response.getJSONObject(i);

                int rank = savedQuestionObject.getInt("rank");
                String title = savedQuestionObject.getString("questionTitle");
                String body = savedQuestionObject.getString("questionBody");
                String email = savedQuestionObject.getString("email");
                Question m = new Question(rank, title, email, body);
                questionList.add(0, m);
            } catch (JSONException e) {
                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
            }
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab1:
                    Intent myIntent = new Intent(getActivity(), Search.class);
                    Bundle b = new Bundle();
                    b.putParcelableArrayList("data", questionList);
                    myIntent.putExtras(b);
                    startActivityForResult(myIntent, 0);
                    getActivity().overridePendingTransition(R.anim.animation1, R.anim.animation3);
                    break;
                case R.id.fab2:
                    ((MenuActivity) getActivity()).changeFragment(new PostQuestion(), "home");
                    break;
            }
        }
    };

    private class LoadQuestions extends AsyncTask<String, Void, JSONObject> {

        Double latitude, longitude;

        String locality;

        @Override
        protected void onPreExecute() {

//            Date date = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
//            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//            formattedDate = sdf.format(date);

            DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
            HashMap<String, Double> locationData = db.getUserLocation();
            //Getting users current location (Kyu....mat puchna!!!)
            latitude = locationData.get(KEY_LATITUDE);
            longitude = locationData.get(KEY_LONGITUDE);
            if (latitude == null || longitude == null) {
                latitude = 12.827561378;
                longitude = 80.050422668;
            }
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Log.d(TAG, "onPreExecute: " + addresses.get(0));
                locality = addresses.get(0).getFeatureName();
                if (locality == null) {
                    locality = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            UserFunctions userFunction = new UserFunctions();
            Log.d("PostQuestion: ", "doInBackground: Sent the question to store");
            return userFunction.loadQuestions(latitude, longitude, offset, locality);
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                Log.d(TAG, "onPostExecute: " + json);
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);

                    if (Integer.parseInt(res) == 1) {
                        questionList.clear();

                        //Dismiss the process dialogs
                        JSONArray response = (JSONArray) json.get("questions");

                        if (response.length() > 0) {
                            savedJson = response.toString();
                            Log.d(TAG, "onPostExecute: " + response.toString());

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject questionTitlesJsonObject = response.getJSONObject(i);
                                String title = questionTitlesJsonObject.getString("questionTitle");
                                String body = questionTitlesJsonObject.getString("questionBody");
                                String email = questionTitlesJsonObject.getString("email");
                                int rank = questionTitlesJsonObject.getInt("rank");
                                offset = rank > offset ? rank : offset;
                                Question m = new Question(rank, title, email, body);

                                questionList.add(0, m);
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    } else if (Integer.parseInt(red) == 2) {

                        String error_msg = json.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(), error_msg, Toast.LENGTH_LONG).show();

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);
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

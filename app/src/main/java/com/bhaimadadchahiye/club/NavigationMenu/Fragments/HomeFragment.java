package com.bhaimadadchahiye.club.NavigationMenu.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaimadadchahiye.club.Answers.Answers;
import com.bhaimadadchahiye.club.Answers.ClickListener;
//import com.bhaimadadchahiye.club.Answers.DividerItemDecoration;
import com.bhaimadadchahiye.club.Answers.DividerItemDecoration;
import com.bhaimadadchahiye.club.Answers.PostQuestion;
import com.bhaimadadchahiye.club.Answers.Question;
import com.bhaimadadchahiye.club.Answers.QuestionAdapter;
import com.bhaimadadchahiye.club.Answers.RecyclerTouchListener;
import com.bhaimadadchahiye.club.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.NavigationMenu.ResideMenu;
import com.bhaimadadchahiye.club.Search.Search;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.utils.BackHandledFragment;
import com.bhaimadadchahiye.club.utils.DatabaseHandler;
import com.bhaimadadchahiye.club.utils.UserFunctions;
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
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static String savedJson;

    private ResideMenu resideMenu;

    public Answers ans = new Answers();

    private View parentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.home, container, false);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        gpsTracker = new GPSTracker(getActivity());

        final CoordinatorLayout coordinatorLayout =
                (CoordinatorLayout) parentView.findViewById(R.id.coordinatorLayoutHome);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Invalid Credentials", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.Black));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.YellowGreen));

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(15.0f);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Questions");

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
        registerForContextMenu(recyclerView);
        recyclerView.setAdapter(mAdapter);

        //Swipe to dismiss functionality
        ItemTouchHelper.Callback callback = new QuestionTouchHelper(mAdapter, recyclerView);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

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

        /*
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

    /**
     * Check for the location, as soon as the app starts.
     */
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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
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

    /**
     * Get location if the app is resumed from Background
     */
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

    /**
     * This method just handled the back button so that the use get's a prompt like:
     * "Press Back again to exit.."
     *
     * @return is back pressed twice consecutively
     */
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
        new NetCheck().execute();
    }

    /**
     * Save the Data fetched via PHP to Local Storage.
     */
    public void saveData() {
        SharedPreferences.Editor outState = getActivity().getSharedPreferences("order", Context.MODE_APPEND).edit();
        outState.putString("questionTitleList", savedJson);
        Log.d(TAG, "******STATE SAVED******");
        if (savedJson != null) {
            Log.d("SAVED JSON", savedJson);
        }
        outState.putBoolean("saved", true);
        outState.apply();
    }

    /**
     * In case when the user is traversing between the views, we save the data locally
     * in SharedPreferences and load it back when the fragment changes back to current
     *
     * @param questionData questions to load
     */
    public void loadSavedData(SharedPreferences questionData) {
        JSONArray response = null;
        String res = questionData.getString("questionTitleList", "");
        try {
            response = new JSONArray(res);
            //This is used to save the data when the
            // activity is loaded after being closed for a while
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
                int imageId = savedQuestionObject.getInt("imageId");
                Question m = new Question(rank, title, email, body, imageId);
                questionList.add(0, m);
            } catch (JSONException e) {
                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
            }
        }
    }

    /**
     * Handle Floating Action button clicks
     * 1. Search
     * 2. Post a Question
     */
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

    /**
     * Save Questions as JSON
     *
     * @param questionList list of questions to save as JSON
     */
    public void setSavedJson(List<Question> questionList) {
        JSONArray jsonArray = new JSONArray();

        for (Question q : questionList) {
            try {
                JSONObject jObject = new JSONObject();

                jObject.put("questionTitle", q.title);
                jObject.put("questionBody", q.body);
                jObject.put("rank", q.id);
                jObject.put("imageId", q.imageId);
                jObject.put("email", q.email);

                jsonArray.put(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        savedJson = jsonArray.toString();
    }

    /**
     * Async Task to check whether internet connection is working
     **/
    private class NetCheck extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(getActivity());
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            /*
             * TODO: Change the implementation to a more reliable approach
             * Gets current device state and checks for working internet connection by trying Google.
             */
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = cm.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = cm.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            } else {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    try {
                        URL url = new URL("http://www.google.com/");
                        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                        urlc.setConnectTimeout(3000);
                        urlc.connect();
                        if (urlc.getResponseCode() == 200) {
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Boolean th) {

            if (th) {
                nDialog.dismiss();
                new LoadQuestions().execute();
            } else {
                nDialog.dismiss();
                Snackbar snackbar = Snackbar.make(parentView, "Error in Network Connection", Snackbar.LENGTH_LONG)
                                            .setActionTextColor(getResources().getColor(R.color.IndianRed))
                                            .setAction("Retry", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    new NetCheck().execute();
                                                }
                                            });
                snackbar.show();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class LoadQuestions extends AsyncTask<String, Void, JSONObject> {

        Double latitude, longitude;

        String locality;

        @Override
        protected void onPreExecute() {
            DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
            HashMap<String, Double> locationData = db.getUserLocation();
            //Getting users current location (Kyu....mat puchna!!!)
            latitude = locationData.get(KEY_LATITUDE);
            longitude = locationData.get(KEY_LONGITUDE);
            if (latitude == null || longitude == null) {
                // Magic Number - Location where the App was made
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

                        JSONArray savedResponse = new JSONArray();

                        if (response.length() > 0) {
                            Log.d(TAG, "onPostExecute: " + response.toString());

                            final TypedArray images = getActivity().getApplicationContext().getResources().obtainTypedArray(R.array.userArray);

                            for (int i = 0; i < response.length(); i++) {

                                int j = i <= images.length() ? i : 1;
                                @SuppressWarnings("ResourceType")
                                final int resID = images.getResourceId(j, 1);

                                JSONObject questionTitlesJsonObject = response.getJSONObject(i);
                                String title = questionTitlesJsonObject.getString("questionTitle");
                                String body = questionTitlesJsonObject.getString("questionBody");
                                String email = questionTitlesJsonObject.getString("email");
                                int rank = questionTitlesJsonObject.getInt("rank");
                                offset = rank > offset ? rank : offset;

                                questionTitlesJsonObject.put("imageId", resID);

                                Question m = new Question(i, title, email, body, resID);
                                savedResponse.put(questionTitlesJsonObject);

                                questionList.add(0, m);
                            }
                            images.recycle();
                            savedJson = savedResponse.toString();
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
            } finally {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}

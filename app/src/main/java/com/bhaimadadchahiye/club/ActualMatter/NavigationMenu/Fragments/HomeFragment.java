package com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bhaimadadchahiye.club.ActualMatter.Answers.DividerItemDecoration;
import com.bhaimadadchahiye.club.ActualMatter.Answers.Movie;
import com.bhaimadadchahiye.club.ActualMatter.Answers.MoviesAdapter;
import com.bhaimadadchahiye.club.ActualMatter.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.MyApplication;
import com.bhaimadadchahiye.club.R;
import com.bhaimadadchahiye.club.library.BackHandledFragment;
import com.bhaimadadchahiye.club.location.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BackHandledFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private List<Movie> movieList = new ArrayList<>();
    private MoviesAdapter mAdapter;

    private String TAG = MenuActivity.class.getSimpleName();

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;

    private SwipeRefreshLayout swipeRefreshLayout;

    private GoogleApiClient mGoogleApiClient;
    private GPSTracker gpsTracker;
    private Boolean exit = false;

    private String savedJson;

    public HomeFragment() {
        setArguments(new Bundle());
    }

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

        RecyclerView recyclerView = (RecyclerView) parentView.findViewById(R.id.recycler_view);

        mAdapter = new MoviesAdapter(getActivity(), movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) parentView.findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchMovies();
            }
        });

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        SharedPreferences movieData = getActivity().getSharedPreferences("order", Context.MODE_APPEND);
        Log.d("Value", String.valueOf(movieData.getBoolean("saved", false)));
        Log.d("JSON", movieData.getString("movieList", ""));
        if (movieData.getBoolean("saved", false)) {
            JSONArray response = null;
            String res = movieData.getString("movieList", "");
            try {
                response = new JSONArray(res);
                savedJson = response.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < (response != null ? response.length() : 0); i++) {
                try {
                    JSONObject movieObj = response.getJSONObject(i);

                    int rank = movieObj.getInt("rank");
                    String title = movieObj.getString("title");

                    Movie m = new Movie(rank, title);

                    movieList.add(0, m);

                    // updating offset value to highest value
                    if (rank >= offSet)
                        offSet = rank;

                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                }
            }

            mAdapter.notifyDataSetChanged();

        } else {
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            fetchMovies();
                                        }
                                    }
            );
        }

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return parentView;
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
     * Fetching movies json by making http call
     */
    private void fetchMovies() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String URL_TOP_250 = "http://api.androidhive.info/json/imdb_top_250.php?offset=";
        String url = URL_TOP_250 + offSet;

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response.length() > 0) {
                            savedJson = response.toString();
                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject movieObj = response.getJSONObject(i);

                                    int rank = movieObj.getInt("rank");
                                    String title = movieObj.getString("title");

                                    Movie m = new Movie(rank, title);

                                    movieList.add(0, m);

                                    // updating offset value to highest value
                                    if (rank >= offSet)
                                        offSet = rank;

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            mAdapter.notifyDataSetChanged();
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }

    public void saveData() {
        SharedPreferences.Editor outState = getActivity().getSharedPreferences("order", Context.MODE_APPEND).edit();
        outState.putString("movieList", savedJson);
        Log.d(TAG, "******SAVED******");
        Log.d("SAVED JSON", savedJson);
        outState.putBoolean("saved", true);
        outState.apply();
    }
}

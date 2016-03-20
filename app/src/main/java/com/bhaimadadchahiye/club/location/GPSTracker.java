package com.bhaimadadchahiye.club.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bhaimadadchahiye.club.NavigationMenu.MenuActivity;
import com.bhaimadadchahiye.club.library.DatabaseHandler;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static com.bhaimadadchahiye.club.constants.DB_Constants.TABLE_LOCATION;

public class GPSTracker extends AppCompatActivity{

    private Context mContext;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    private Location currentBestLocation = null;
    private ServiceLocationListener gpsLocationListener;
    private ServiceLocationListener networkLocationListener;
    private ServiceLocationListener passiveLocationListener;

    public static final int REQUEST_LOCATION = 199;

    private Handler handler = new Handler();

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
    }

    /**
     * Prompt user to enable GPS and Location Services
     *
     * @param mGoogleApiClient Google Client API
     * @param activity         Activity of parent method
     */
    public void locationChecker(GoogleApiClient mGoogleApiClient, final Activity activity) {
        Log.d("Enter Location Checker", activity.getLocalClassName());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(60 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                Log.d("Status Code: ", String.valueOf(status.getStatusCode()));
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        fetchLocation();
                        //new ServiceLocationListener().setLocation(currentBestLocation);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public void fetchLocation() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Log.d("Entered Loc Fetcher","Entered Loc Fetcher");
        try {
            LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            LocationProvider networkProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
            LocationProvider passiveProvider = locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);

            //Figure out if we have a location somewhere that we can use as a current best location
            if (gpsProvider != null) {
                Location lastKnownGPSLocation = locationManager.getLastKnownLocation(gpsProvider.getName());
                if (lastKnownGPSLocation != null) {
                    if (isBetterLocation(lastKnownGPSLocation, currentBestLocation))
                        currentBestLocation = lastKnownGPSLocation;
                }
            }

            if (networkProvider != null) {
                Location lastKnownNetworkLocation = locationManager.getLastKnownLocation(networkProvider.getName());
                if (lastKnownNetworkLocation != null) {
                    if (isBetterLocation(lastKnownNetworkLocation, currentBestLocation))
                        currentBestLocation = lastKnownNetworkLocation;
                }
            }

            if (passiveProvider != null) {
                Location lastKnownPassiveLocation = locationManager.getLastKnownLocation(passiveProvider.getName());
                if (lastKnownPassiveLocation != null) {
                    if (isBetterLocation(lastKnownPassiveLocation, currentBestLocation)) {
                        currentBestLocation = lastKnownPassiveLocation;
                    }
                }
            }

            gpsLocationListener = new ServiceLocationListener();
            networkLocationListener = new ServiceLocationListener();
            passiveLocationListener = new ServiceLocationListener();

            if (gpsProvider != null) {
                locationManager.requestLocationUpdates(gpsProvider.getName(), 0l, 0.0f, gpsLocationListener);
            }

            if (networkProvider != null) {
                locationManager.requestLocationUpdates(networkProvider.getName(), 0l, 0.0f, networkLocationListener);
            }

            if (passiveProvider != null) {
                locationManager.requestLocationUpdates(passiveProvider.getName(), 0l, 0.0f, passiveLocationListener);
            }

            if (gpsProvider != null || networkProvider != null || passiveProvider != null) {
                handler.postDelayed(timerRunnable, 2 * 60 * 1000);
            } else {
                handler.post(timerRunnable);
            }
            Log.d("Exited Location Fetcher","Exiting fetch Location Method");
        } catch (SecurityException se) {
            finish();
        }
    }

    public synchronized void finish() {
        handler.removeCallbacks(timerRunnable);
        handler.post(timerRunnable);
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        Log.i("Location: ", location.toString());
        Log.i("CurrentBestLocation: ", currentBestLocation.toString());
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MIN_TIME_BW_UPDATES;
        boolean isSignificantlyOlder = timeDelta < -MIN_TIME_BW_UPDATES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(mContext.getPackageName() + ".action.LOCATION_FOUND");
            if (currentBestLocation != null) {
                intent.putExtra(LocationManager.KEY_LOCATION_CHANGED, currentBestLocation);
                locationManager.removeUpdates(gpsLocationListener);
                locationManager.removeUpdates(networkLocationListener);
                locationManager.removeUpdates(passiveLocationListener);
            }
        }
    };

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public class ServiceLocationListener implements android.location.LocationListener {

        private Location finalLocation;

        @Override
        public void onLocationChanged(Location newLocation) {
            synchronized (this) {
                if (isBetterLocation(newLocation, currentBestLocation)) {
                    currentBestLocation = newLocation;
                    Log.d("Current Best Location: ", currentBestLocation.toString());
                    setLocation(currentBestLocation);
                    storeLocation();
                    Toast.makeText(MenuActivity.getcontext(), " Your Location is " + currentBestLocation.getLatitude(), Toast.LENGTH_LONG).show();
                    if (currentBestLocation.hasAccuracy() && currentBestLocation.getAccuracy() <= 100) {
                        Log.d("Executed...", "");
                        finish();
                    }
                }
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }

        public void setLocation(Location finalLocation) {
            this.finalLocation = finalLocation;
        }

        public Location getLocation() {
            return finalLocation;
        }

        public void storeLocation() {
            Log.d("Entered Stored Location","");
            Location currentBestLocation = getLocation();
            DatabaseHandler localDB = new DatabaseHandler(MenuActivity.getcontext());
            //Check for home location
            Log.d("localDB.getRowCount", String.valueOf(localDB.getRowCount(TABLE_LOCATION)));
            if (localDB.getRowCount(TABLE_LOCATION) == 0) {
                new HomeLocationRegister().execute(currentBestLocation.getLatitude(), currentBestLocation.getLongitude());
            }
        }
    }
}


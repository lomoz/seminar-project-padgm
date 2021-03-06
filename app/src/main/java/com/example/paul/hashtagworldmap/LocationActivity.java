package com.example.paul.hashtagworldmap;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 *
 * Folgende Lösung (Zeile 34-185)für die Ermittlung des Standortes habe ich einem Tutorial auf blog.teamtreehouse.com
 * entnommen und etwas modifiziert. Link: http://blog.teamtreehouse.com/beginners-guide-location-android
 *
 */

public class LocationActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private TextView latInfo;
    private ProgressBar progressBar;
    public double currentLatitude;
    public double currentLongitude;
    private String locationString;
    private android.location.Address adress;

    public static final String TAG = MapsActivity.class.getSimpleName();

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private int TAG_CODE_PERMISSION_LOCATION = 1252;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        progressBar = (android.widget.ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(20);
    }

    @Override
    protected void onResume() {
        System.out.println("latitude1: " + this.currentLatitude);
        progressBar.setProgress(40);
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    private void handleNewLocation(Location location) throws IOException {
        Log.d(TAG, location.toString());

        System.out.println("latitude2: " + this.currentLatitude);
        progressBar.setProgress(80);
        this.currentLatitude = location.getLatitude();
        this.currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(this.currentLatitude, this.currentLongitude);

        setCurLoc(latLng);
    }

    @Override
    public void onConnected(Bundle bundle){

        //checks permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            System.out.println("test");
            progressBar.setProgress(60);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            try {
                System.out.println("latitude3: " + this.currentLatitude);
                progressBar.setProgress(60);
                handleNewLocation(location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */

            } catch (IntentSender.SendIntentException e) {
                        // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */

            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            handleNewLocation(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ------ WEITERE METHODEN -------

    //startet MapActivity und setzt ProgressBar auf 100
    public void startMap() {

        progressBar.setProgress(100);
        Intent startMap = new Intent(this, MapsActivity.class);
        startActivity(startMap);

    }

    //setzt CurrentLocation von dem ermittelten Ort und progress auf 90
    public void setCurLoc(LatLng curLocLatLng) {

        progressBar.setProgress(90);
        CurrentLocation.setCurLoc(curLocLatLng);
        startMap();

    }

}
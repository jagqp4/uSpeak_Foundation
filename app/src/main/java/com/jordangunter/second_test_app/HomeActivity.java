package com.jordangunter.second_test_app;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    public static boolean googlePlayServicesActive = true;
    public boolean locationUsable = false;
    public static final int REQUEST_CHECK_SETTINGS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this is needed to set location settings to ACCESS_COARSE_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }

        setContentView(R.layout.activity_home);
        //Initialize toolbar
        Toolbar homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        homeToolbar.setTitle("uSpeak");
        setSupportActionBar(homeToolbar);

        //Setup button listeners
        final Button speakButton = findViewById(R.id.speak_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switchToSpeakPage();
            }
        });

        final Button repButton = findViewById(R.id.rep_button);
        repButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switchToRepPage();
            }
        });

        final Button aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switchToAboutPage();
            }
        });

        //setup location services client
        if(HomeActivity.googlePlayServicesActive) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        String locationString = "\n\n\n\n" + location.toString() + "\n\n\n\n";
                        Log.d("TEST","\n\n\nAre we in here?\n\n\n");
                        Log.d("LOC",locationString);
                    }
                }
            });
        }

        //setup location update intervals and priority
        createLocationRequest();
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    private boolean  createLocationRequest(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //first we need to determine the device's location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        //get the current state of all location related settings on device
        //LocationSettingsStates state = task.getResult().getLocationSettingsStates();
        //locationUsable = state.isLocationUsable();

        //determine if the settings will allow a location request
        //case 1: the task object returns with success
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        //case 2: the task object returned failure
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
        return true;
    }

    //Initializes the navigation menu which is a child of the AppBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.home_menu, menu);
    /*
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView =
            (SearchView) MenuItemCompat.getActionView(searchItem);

    // Configure the search info and add any event listeners...
    OnActionExpandListener expandListener = new OnActionExpandListener(){
        @Override
        public boolean onMenuItemActionCollapse(MenuItem item){
            return true;
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item){
            return true;
        }
    };
    */
    return super.onCreateOptionsMenu(menu);
}

    //Handles the logic for when a user selects an option from the Navigation menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                super.onOptionsItemSelected(item);
                return true;
            case R.id.action_speak:
                switchToSpeakPage();
                return true;
            case R.id.action_rep:
                switchToRepPage();
                return true;
            case R.id.action_about:
                switchToAboutPage();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean switchToSpeakPage(){
        Intent speakIntent = new Intent(this,SpeakActivity.class);
        startActivity(speakIntent);
        return true;
    }

    private boolean switchToRepPage(){
        Intent repIntent = new Intent(this,RepresentativeActivity.class);
        startActivity(repIntent);
        return true;
    }

    private boolean switchToAboutPage(){
        Intent aboutIntent = new Intent(this,AboutActivity.class);
        startActivity(aboutIntent);
        return true;
    }
}

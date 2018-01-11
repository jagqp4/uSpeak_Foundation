package com.jordangunter.second_test_app;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends AppCompatActivity implements LoginDialogFragment.NoticeDialogListener{

    public static final int REQUEST_CHECK_SETTINGS = 2;
    public static final int RC_SIGN_IN = 3;
    public static boolean googlePlayServicesActive = true;

    public boolean locationUsable = false;
    private boolean isSignedInGoogle = false;

    private GoogleSignInClient mGoogleSignInClient;
    private LoginDialogFragment mloginDialog;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location lastLocation;
    private AddressResultsReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("@@@@@@", "In my home activity");
        //this is needed to set location settings to: ACCESS_FINE_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            checkPermission();
        }

        setContentView(R.layout.activity_home);

        //setup google sign in client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            isSignedInGoogle = true;
            findViewById(R.id.sign_in_google).setVisibility(View.INVISIBLE);
        }

        //Initialize toolbar
        Toolbar homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        homeToolbar.setTitle("uSpeak");
        setSupportActionBar(homeToolbar);

        setupButtons();

        mResultReceiver = new AddressResultsReceiver(new android.os.Handler());

        if(HomeActivity.googlePlayServicesActive) {
            //setup location services client
            fetchLastLocation();
        }

        //setup location update intervals and priority
        //createLocationRequest();
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

    //The coarse and fine location are considered 'dangerous' permissions. Therefore you have to ask for them on app launch
    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }
    }

    private void fetchLastLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lastLocation = location;
                            // Logic to handle location object
                            String locationString = "\n\n\n\n" + location.toString() + "\n\n\n\n";
                            Log.d("LOC",locationString);
                            if(!Geocoder.isPresent()){
                                Toast.makeText(HomeActivity.this,
                                        R.string.no_geocoder_available,
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            //start background service to convert location -> address
                            startIntentService();
                        }
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        TextView t = findViewById(R.id.debug);
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            t.setText("sign_in_success");
            findViewById(R.id.sign_in_google).setVisibility(View.INVISIBLE);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("__ERROR__", "signInResult:failed code=" + e.getStatusCode());
            t.setText("sign_in_error");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
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

    private void setupButtons() {
        // Set the dimensions of the sign-in button
        SignInButton googleButton = findViewById(R.id.sign_in_google);
        googleButton.setSize(SignInButton.SIZE_STANDARD);

        //add onClick listeners to buttons...
        googleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signInGoogle();
            }
        });

        final Button otherButton = findViewById(R.id.sign_in_other);
        otherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startLoginDialogue();
            }
        });

        final Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startRegisterDialogue();
            }
        });

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
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //call to .getFromLocation() may take a long time to execute so this function
    //starts a FetchAddressIntentService to get a usable address from our location
    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        startService(intent);
    }

    private void switchToSpeakPage(){
        Intent speakIntent = new Intent(this,SpeakActivity.class);
        startActivity(speakIntent);
    }

    private void switchToRepPage(){
        Intent repIntent = new Intent(this,RepresentativeActivity.class);
        startActivity(repIntent);
    }

    private void switchToAboutPage(){
        Intent aboutIntent = new Intent(this,AboutActivity.class);
        startActivity(aboutIntent);
    }

    /*  Methods needed for login dialog   */

    private void startLoginDialogue(){
        LoginDialogFragment loginFragment = new LoginDialogFragment();
        loginFragment.show(getSupportFragmentManager(), "login");
        mloginDialog = loginFragment;
    }

    private void startRegisterDialogue(){

    }

    public void showLoginDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "LoginDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String user = mloginDialog.getUsername();
        String pass = mloginDialog.getPassword();
        Log.d("__USER__", user);
        Log.d("__PASS__", pass);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }


    //This class allows communication between HomeActivity and FetchAddressIntentService
    class AddressResultsReceiver extends ResultReceiver{

        public AddressResultsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string
            // or an error message sent from the intent service.
            //mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //showToast(getString(R.string.address_found));
                Toast.makeText(HomeActivity.this, R.string.address_found, Toast.LENGTH_LONG);
            }

            String fragmentString = resultData.getString(Constants.RESULT_DATA_KEY);
            Log.d("STUFF", fragmentString);
            for(int i=0; i<10; i++) {
                Log.d("HELLO", "whats happening");
            }
        }
    }
}

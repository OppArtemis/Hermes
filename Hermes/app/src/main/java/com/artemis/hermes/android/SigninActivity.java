package com.artemis.hermes.android;

/**
 * Class that performs the sign-in and sign-out actions.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.ActivityInfo;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;

// Firebase libraries
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

// Location services libraries
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.content.pm.PackageManager;

/**
 * SigninActivity class that handles specific activity for users who are signed in,
 * which consists of:
 * - Allow users to logout
 * - Allow users to view his/her profile information
 *
 * @author  Jonathan Lin & Jorge Quan
 * @since   2017-09-03
 */
public class SigninActivity extends AppCompatActivity {

    // Stores the information of user name to be displayed.
    private TextView profileName;

    // Stores the information of location info to be displayed.
    private TextView locationInfo;

    private FirebaseAuth auth;

    // Stores User ID (unique key for database)
    private String mUserId;

    // --- Database ---
    // Stores the reference to the database
    private DatabaseReference mDatabase;

    // Location provider
    private FusedLocationProviderClient mFusedLocationClient;
    private int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 0;
    private String locationString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null)
        {
            signOut();
        }

        // Store information in the database
        storeBasicInfoIntoDatabase();

        // Set up the profile page
        setContentView(R.layout.activity_signin);
        setTitle(getString(R.string.profile_title));
        displayLoginUserProfileName();

        // request location services permission
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_ACCESS_COARSE_LOCATION);

        // poll last known location
        setupLocationServices();

        // Button to update user's location
        Button updateLocation = (Button)findViewById(R.id.update_location);
        updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupLocationServices();
            }
        });

        // Button to logout
        Button logoutButton = (Button)findViewById(R.id.sign_out);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(SigninActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    signOut();
                                }else {
                                    displayMessage(getString(R.string.sign_out_error));
                                }
                            }
                        });
            }
        });

        // Button to delete user
        Button deleteUserButton = (Button)findViewById(R.id.delete_user);
        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            signOut();
                        }else{
                            displayMessage(getString(R.string.user_deletion_error));
                        }
                    }
                });
            }
        });

        // Button to update user's location
        Button showMapButton = (Button)findViewById(R.id.view_map);
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMap();
            }
        });
    }

    /**
     * This method starts a new "Activity" for users that are logout.
     *
     */
    private void signOut(){
        Intent signOutIntent = new Intent(this, MainActivity.class);
        startActivity(signOutIntent);
        finish();
    }

    /**
     * Helper method to display a message on screen.
     *
     * @param message This is the string to display.
     */
    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Helper method to set user_name on profile page.
     *
     */
    private void displayLoginUserProfileName(){
        FirebaseUser mUser = auth.getCurrentUser();
        profileName = (TextView)findViewById(R.id.user_name);
        if(mUser != null){
            profileName.setText(TextUtils.isEmpty(mUser.getDisplayName())? "No name found" : mUser.getDisplayName());
        }
    }

    /**
     * This method gets the location string from Location Services
     * that will be stored on a member variable.
     *
     */
    public void setupLocationServices() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationString = "Latitude: " + location.getLatitude() +
                                    " Longitude: " + location.getLongitude();

                            Log.d("LocationServices", "Location found: " + locationString);

                            // Update database
                            updateLocationInDatabase();

                            // Display location on profile page.
                            displayLoginUserLocationInfo();
                        }
                    }
                });
    }

    /**
     * Helper method to set location_info on profile page.
     *
     */
    private void displayLoginUserLocationInfo(){
        locationInfo = (TextView)findViewById(R.id.location_info);
        locationInfo.setText(TextUtils.isEmpty(locationString)? "Could not find location" : locationString);
    }
    
    private void goToMap(){
        Intent mapIntent = new Intent(this, MapsActivityCurrentPlace.class);
        startActivity(mapIntent);
        finish();
    }

    /**
     * Helper method store basic information onto database.
     *
     */
    private void storeBasicInfoIntoDatabase(){
        // Instantiate a reference to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // User ID acts as the key to the database
        mUserId = auth.getCurrentUser().getUid();

        Date currentTime = Calendar.getInstance().getTime();
        String userName = auth.getCurrentUser().getDisplayName();

        mDatabase.child("users").
                child(mUserId).
                child("Name").
                setValue(userName);
        mDatabase.child("users").
                child(mUserId).
                child("lastLoginTime").
                setValue(String.valueOf(currentTime));
    }

    /**
     * Helper method to set location_info on database.
     *
     */
    private void updateLocationInDatabase(){
        mDatabase.child("users").
                child(mUserId).
                child("location").
                setValue(locationString);
    }
}

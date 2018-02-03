/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.artemis.hermes.android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

//Firebase database packages
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;


// Required for Yelp API
import retrofit2.Call;
import retrofit2.Callback;

import com.google.firebase.database.ValueEventListener;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Getting the Location Address.
 *
 * Demonstrates how to use the {@link android.location.Geocoder} API and reverse geocoding to
 * display a device's location as an address. Uses an IntentService to fetch the location address,
 * and a ResultReceiver to process results sent by the IntentService.
 *
 * Android has two location request settings:
 * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
 * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
 * the AndroidManifest.xml.
 *
 * For a starter example that displays the last known location of a device using a longitude and latitude,
 * see https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 *
 * For an example that shows location updates using the Fused Location Provider API, see
 * https://github.com/googlesamples/android-play-location/tree/master/LocationUpdates.
 */
public class Naviations extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    private Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    private String mCurrentAddressOutput;

    private String targetAddress = mCurrentAddressOutput;

    private List<RestaurantGoogleHandle> foundLocations;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * Displays the location address.
     */
    private TextView mLocationAddressTextView;

    /**
     * Kicks off the request to fetch an address when pressed.
     */
    private Button mFetchAddressButton;

    private EditText mLocationTargetEditText;
    private Button mStartSearch;
    private Button mStartSearchGroup;
    private ListView mRetrievedRestaurants;
    private ArrayAdapter<String> adapter;

    // Yelp client API object
    private YelpFusionApi mYelpFusionApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = findViewById(R.id.location_address_view);
        mFetchAddressButton = findViewById(R.id.fetch_address_button);
        mLocationTargetEditText = findViewById(R.id.location_target_edit);
        mStartSearch = findViewById(R.id.button_startSearch);
        mStartSearchGroup = findViewById(R.id.button_startSearchGroup);

        mRetrievedRestaurants = findViewById(R.id.list_retrievedRestaurants);

        // Create a new Adapter
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);

        // Assign adapter to ListView
        mRetrievedRestaurants.setAdapter(adapter);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mCurrentAddressOutput = getString(R.string.current_location_searching);
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fetchAddressButtonHandler(null);
        setHandles();
    }

    public void setHandles() {
        mFetchAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAddressButtonHandler(null);
                copyCurrentLocationToTargetLocation();
            }
        });

        mStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startSearchWithGoogle();
                startSearchWithYelp();
            }
        });

        mStartSearchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startSearchWithGoogle();
                startSearchWithYelpGroup();
            }
        });

        mRetrievedRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String polledString = String.valueOf(adapterView.getItemAtPosition(i));

                // In the event that there is "|" separators, we just need the first index of the
                // string for the address
                String [] polledStringSplit = polledString.split("\\|");
                navigateToLocation(polledStringSplit[0]);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mCurrentAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    /**
     * Runs when user clicks the Fetch Address button.
     */
    public void fetchAddressButtonHandler(View view) {
        if (mLastLocation != null) {
            startIntentService();
            return;
        }

        // If we have not yet retrieved the user location, we process the user's request by setting
        // mAddressRequested to true. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Gets the address for the last known location.
     */
    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");
                            return;
                        }

                        mLastLocation = location;

                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }

                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.
                        if (mAddressRequested) {
                            startIntentService();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    /**
     * Updates the address in the UI.
     */
    private void displayAddressOutput() {
        String outputStr = getString(R.string.current_location_start) + " " + mCurrentAddressOutput;
        mLocationAddressTextView.setText(outputStr);
    }

    /**
     * Helper method to set location on database.
     *
     */
    private void updateLocationInDatabase(){
        // Instantiate a reference to the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String userId = bundle.getString("userId");

        mDatabase.child("users").
                child(userId).
                child("address").
                setValue(mCurrentAddressOutput);

        // Store the raw location (latitude and longitude just in case)
        if (mLastLocation != null) {
            String locationString = mLastLocation.getLatitude() +
                    "," + mLastLocation.getLongitude();

            mDatabase.child("users").
                    child(userId).
                    child("location").
                    setValue(locationString);
        }
    }

    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mCurrentAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mCurrentAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            displayAddressOutput();
            updateLocationInDatabase();
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(Naviations.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(Naviations.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    private void copyCurrentLocationToTargetLocation(){
        mLocationTargetEditText.setText(mCurrentAddressOutput);
    }

    /**
     * Hides software keyboard.
     */
    private void hideSoftwareKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * Instantiates the Yelp client API.
     */
    private void instantiateYelpApi(){
        if (mYelpFusionApi == null) {

            InitiateYelpApi initiateYelpApiObj = new InitiateYelpApi();
            initiateYelpApiObj.execute();

            // Try polling for asynch task to finish
            try {
                initiateYelpApiObj.get(3000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startSearchWithYelpGroup() {
        // find nearby people
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUserNode = database.getReference("users" + "/");

        // Attach a listener to read the data at our posts reference
        refUserNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onUserDataRead(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void onUserDataRead(DataSnapshot dataSnapshot) {
        UserProfile currentUser = new UserProfile();
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        for (DataSnapshot child: dataSnapshot.getChildren()) {
            UserProfile newPost = child.getValue(UserProfile.class);

            if (newPost.getName().equals(currentUserName)) {
                currentUser = newPost;
                break;
            }
        }

        List<UserProfile> closebyUsers = new ArrayList<>();
        double[] currentLocation = currentUser.retrieveLocation();
        double disTol = 0.5; // 0.5 km
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            UserProfile newPost = child.getValue(UserProfile.class);
            double[] newLocation = newPost.retrieveLocation();

//            if (checkDistance(newLocation, currentLocation) < disTol) {
//                closebyUsers.add(newPost);
//            }
        }

        // return a list of users
        int la = 0;
    }

//    public void double checkDistance(double[] latlong1, double[] latlong2) {
//
//    }

    /**
     * Method to search for restaurants with Yelp API.
     *
     * It will set the adapter with search results.
     *
     */
    private void startSearchWithYelp(){
        hideSoftwareKeyboard();

        String targetAddressFieldStr = mLocationTargetEditText.getText().toString();
        if (targetAddressFieldStr.length() > 0) {
            targetAddress = targetAddressFieldStr;
        } else {
            targetAddress = mCurrentAddressOutput;
        }

        instantiateYelpApi();

        // This value will not be null if the instantiation worked.
        if (mYelpFusionApi != null) {

            Map<String, String> params = new HashMap<>();

            // Enter filter on search.
            // Limit the radius to 4km.
            // To do: if 4km radius returns less than a threshold number of restaurants, then
            // the program should be smart to search a larger radius.
            params.put("term", "Restaurants");
            params.put("radius", "4000");
            params.put("sort_by", "distance");

            // Use the target location from the address field as a first option.
            if (targetAddress != null) {
                params.put("location", targetAddress);
                showSnackbar("Searching at: " + targetAddress);
            } else if (mLastLocation != null) {
                String latitude = String.valueOf(mLastLocation.getLatitude());
                String longitude = String.valueOf(mLastLocation.getLongitude());
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                showSnackbar("Searching at: " + latitude + ", " + longitude);
            }

            Call<SearchResponse> call = mYelpFusionApi.getBusinessSearch(params);

            // Need a callback to make the call asynchronous
            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call,
                                       retrofit2.Response<SearchResponse> response) {
                    if (response.isSuccessful()) {

                        // Clear the default list of info.
                        adapter.clear();

                        // Get all the businesses from response.
                        SearchResponse searchResponse = response.body();
                        List<Business> businesses = searchResponse.getBusinesses();

                        List<RestaurantYelpHandle> restaurantObjects = new ArrayList<>();

                        // Iterate through each restaurant, and get information from each.
                        for (int i = 0; i < businesses.size(); i++) {
                            restaurantObjects.add(new RestaurantYelpHandle(businesses.get(i)));
                        }

                        // Sort restaurant list
                        sortRetrievedYelpRestaurants(restaurantObjects);

                        // Add new list to the listview adapter
                        for (int i = 0; i < businesses.size(); i++) {
                            adapter.add(restaurantObjects.get(i).toFullString());
                        }
                    } else {
                        adapter.add("HTTP Response was not successful: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                    adapter.add("Cannot find restaurants due to HTTP error");
                    Log.d(TAG, "Failure on Yelp Search API:\n" + t.toString());
                }
            };

            call.enqueue(callback);
        }
    }

    private List<RestaurantYelpHandle> sortRetrievedYelpRestaurants(List<RestaurantYelpHandle> inputArray) {
        // sort by rating
        Collections.sort(inputArray, RestaurantYelpHandle.COMPARE_BY_RATING);
        for (int i = 0; i < inputArray.size(); i++) {
            inputArray.get(i).addToSortScore(i);
        }

        // sort by distance
        Collections.sort(inputArray, RestaurantYelpHandle.COMPARE_BY_DISTANCE);
        for (int i = 0; i < inputArray.size(); i++) {
            inputArray.get(i).addToSortScore(i);
        }

        // finally, sort by the sort score
        Collections.sort(inputArray, RestaurantYelpHandle.COMPARE_BY_SORTSCORE);

        return inputArray;
    }

    /**
     * Method to search for restaurants with Google API.
     *
     * It will set the adapter with search results.
     *
     */
    @SuppressWarnings("unused")
    private void startSearchWithGoogle() {
        hideSoftwareKeyboard();

        // check input address to make sure there's something
        String targetAddressFieldStr = mLocationTargetEditText.getText().toString();
        if (targetAddressFieldStr.length() > 0) {
            targetAddress = targetAddressFieldStr;
        } else if (mLastLocation != null) {
            targetAddress = String.valueOf(mLastLocation.getLatitude()) + ", " +
                    String.valueOf(mLastLocation.getLongitude());
        } else {
            targetAddress = mCurrentAddressOutput;
        }

        showSnackbar("Searching at: " + targetAddress);

        String googleMapApiKey = getString(R.string.google_maps_api_key);
        String locationType = "restaurant";
        String targetLocation = targetAddress;

        String mapUrl = "https://maps.googleapis.com/maps/api/place/textsearch/" +
                "json?key=" + googleMapApiKey +
                "&query=" + targetLocation.replace(" ", "+") +
                "&type=" + locationType;

        // Defining the Volley request queue that handles the URL request concurrently
        RequestQueue requestQueue;

        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        foundLocations = null;

        // Creating the JsonObjectRequest class called obreq, passing required parameters:
        //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, mapUrl,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONObject>() {
                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonResponseArray = response.getJSONArray("results");

                            adapter.clear();

                            foundLocations = new ArrayList<>();
                            for (int i = 0; i < jsonResponseArray.length(); i++) {
                                JSONObject currResponse = jsonResponseArray.getJSONObject(i);
                                foundLocations.add(new RestaurantGoogleHandle(currResponse));

                                adapter.add(foundLocations.get(i).toString());
                            }
                        }
                        // Try and catch are included to handle any errors due to JSON
                        catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                        }
                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        // Adds the JSON object request "obreq" to the request queue
        requestQueue.add(obreq);
    }

    private void navigateToLocation(String targetLocation) {
        String sourceLocation = mCurrentAddressOutput;
//        String targetLocation = targetAddress;
        String transportationMode = "driving";

        String mapUrl = "https://www.google.com/maps/dir/?api=1" +
                "&origin=" + sourceLocation +
                "&destination=" + targetLocation +
                "&travelmode=" + transportationMode;

        Uri location = Uri.parse(mapUrl);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent);
    }

    /**
     * InitiateYelpApi class that initiates Yelp API
     *
     * @author  Jorge Quan
     * @since   2017-09-34
     */
    private class InitiateYelpApi extends AsyncTask<String, Void, String> {

        /**
         * Method that performs the call to the API
         *
         * @param params to enter as input for API
         *
         * @return data of the API
         */
        @Override
        protected String doInBackground(String... params) {

            // Authenticate and use API
            String appId = getString(R.string.yelp_api_id);
            String appSecret = getString(R.string.yelp_api_secret);
            YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();

            try {
                mYelpFusionApi = apiFactory.createAPI(appId, appSecret);
            } catch (IOException e){
                e.printStackTrace();
                return "Bad";
            }

            return "Good";
        }

        /**
         * Post execute after API call.
         *
         * @param result is the string output of API
         */
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Result of instantiating Yelp client API: " + result);
        }
    }

}
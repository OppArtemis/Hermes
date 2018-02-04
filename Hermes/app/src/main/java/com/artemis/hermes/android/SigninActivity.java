package com.artemis.hermes.android;

/**
 * Class that performs the sign-in and sign-out actions.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

// Firebase libraries
import com.artemis.hermes.backend.myApi.MyApi;
import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

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

    // For logging.
    private static final String TAG = SigninActivity.class.getSimpleName();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() == null)
        {
            signOut();
        }

        // Store information in the database
        mUserId = auth.getCurrentUser().getUid();

        // Set up the profile page
        setContentView(R.layout.activity_signin);
        setTitle(getString(R.string.profile_title));
        displayLoginUserProfileName();

        // Button to logout
        Button logoutButton = findViewById(R.id.sign_out);
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

        // Button to update user's location
        Button showMapButton = findViewById(R.id.view_map);
        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLocationServices();
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
        profileName = findViewById(R.id.user_name);
        if(mUser != null){
            profileName.setText(TextUtils.isEmpty(mUser.getDisplayName())? "No name found" : mUser.getDisplayName());

            // Play with endpoints
            new EndpointsSayHi().execute(mUser.getDisplayName());

            new EndpointsReadDatabaseData().execute(mUserId);
        }
    }

    /**
     * Helper method to start an activity on Restaurant Location services.
     *
     */
    private void goToLocationServices(){
        Intent mapIntent = new Intent(this, Naviations.class);
        mapIntent.putExtra("userId", mUserId);
        startActivity(mapIntent);
    }

    /**
     * EndpointsSayHi class that talks to the backend logic endpoints
     * which returns a messsage to the user.
     *
     * @author  Jorge Quan
     * @since   2017-09-14
     */
    private class EndpointsSayHi extends AsyncTask<String, Void, String> {
        private MyApi myApiService = null;

        /**
         * Method that performs the call to the API
         *
         * @param params to enter as input for API
         *
         * @return data of the API
         */
        @Override
        protected String doInBackground(String... params) {

            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null)
                        .setRootUrl(Constants.API_ROOT_URL)
                        .setApplicationName("@string/app_name")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });

                myApiService = builder.build();
            }

            try {
                return myApiService.sayHi(params[0]).execute().getData();
            } catch (IOException e) {
                // Check if it is a HTTP response error
                if(e instanceof HttpResponseException) {
                    int statusCode = ((HttpResponseException) e).getStatusCode();
                    // 404 is not found, so likely the server is down
                    if (statusCode == 404) {
                        return "Restaurant Searcher brain is not found (404)";
                    }
                }
                // return the raw message
                return e.getMessage();
            }
        }

        /**
         * Method that puts the result of the API on a message.
         *
         * @param result is the string output of API
         */
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            TextView customMessage = findViewById(R.id.custom_message);
            customMessage.setText(result);
        }
    }


    /**
     * EndpointsReadDatabaseData class that talks to the backend logic endpoints
     * which returns a messsage to the user.
     *
     * @author  Jorge Quan
     * @since   2017-09-14
     */
    private class EndpointsReadDatabaseData extends AsyncTask<String, Void, String> {
        private MyApi myApiService = null;

        /**
         * Method that performs the call to the API
         *
         * @param params to enter as input for API
         *
         * @return data of the API
         */
        @Override
        protected String doInBackground(String... params) {

            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null)
                        .setRootUrl(Constants.API_ROOT_URL)
                        .setApplicationName("@string/app_name")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });

                myApiService = builder.build();
            }

            try {
                return myApiService.getDatabaseInfo(params[0]).execute().getData();
            } catch (IOException e) {
                // Check if it is a HTTP response error
                if(e instanceof HttpResponseException) {
                    int statusCode = ((HttpResponseException) e).getStatusCode();
                    // 404 is not found, so likely the server is down
                    if (statusCode == 404) {
                        return "Restaurant Searcher brain is not found (404)";
                    }
                }
                // return the raw message
                return e.getMessage();
            }
        }

        /**
         * Method that puts the result of the API on a message.
         *
         * @param result is the string output of API
         */
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}

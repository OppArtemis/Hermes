package com.artemis.hermes.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

// Firebase UI libraries
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ErrorCodes;

// General Firebase libraries
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


// General libraries
import android.content.Intent;
import java.util.Arrays;

// Debugging
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    RC_SIGN_IN);
            finish();
            return;
       } else {
            // already signed in, do nothing
            String userName = user.getDisplayName();
            String myEmail = user.getEmail();

            Log.d("My name", userName);
            Log.d("my email", myEmail);

            setContentView(R.layout.login_info);
            TextView textView1 = (TextView) this.findViewById(R.id.userName);
            textView1.setText(String.valueOf(userName));

            TextView textView2 = (TextView) this.findViewById(R.id.myEmail);
            textView2.setText(String.valueOf(myEmail));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Set up my return variable
        String result = "";

        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                result="OK";
            } else {
                // Sign in failed
                if (response == null) {
                    result="Null";
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    result="No Network";
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    result="Unknown Error";
                }
            }

            result="Internal Error";

            Log.d("the result", result);

            setContentView(R.layout.activity_main);
            TextView textView = (TextView) this.findViewById(R.id.signInResult);
            textView.setText(String.valueOf(result));
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    public void getLocation() {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//    }
}

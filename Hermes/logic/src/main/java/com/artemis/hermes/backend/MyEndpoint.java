/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.artemis.hermes.backend;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.hermes.artemis.com",
                ownerName = "backend.hermes.artemis.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    private String mDatabaseInfo = "initial_mock_value";

    private String mUserId;

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();

        // Generate a random number
        Random rand = new Random();
        int dice = rand.nextInt(6) + 1;

        String greetingMessage;

        if (dice == 6) {
            greetingMessage = "wanna go to McDonald's?";
        } else if (dice == 5) {
            greetingMessage = "cooking at home saves money.";
        } else if (dice == 4) {
            greetingMessage = "you should go on a diet.";
        } else {
            greetingMessage = "what would you like to eat?";
        }

        response.setData("Hi, " + name + ", " + greetingMessage);
        return response;
    }

    /**
     * A simple endpoint method read database info, and return it
     */
    @ApiMethod(name = "getDatabaseInfo")
    public MyBean getDatabaseInfo(@Named("userId") String userId) {
        MyBean response = new MyBean();

        // Generate a random number
        Random rand = new Random();
        int dice = rand.nextInt(6) + 1;

        String greetingMessage;

        if (dice == 6) {
            greetingMessage = "going for a snack at this time: ";
        } else if (dice == 5) {
            greetingMessage = "what a weird time to eat: ";
        } else if (dice == 4) {
            greetingMessage = "it is not meal time yet: ";
        } else {
            greetingMessage = "you are hungry at this time: ";
        }

        mUserId = userId;
        readDatabaseData();

        response.setData(greetingMessage + mDatabaseInfo);
        return response;
    }

    /**
     * Sample method to read field from database, and display the message with it.
     *
     */
    public void readDatabaseData()
    {
        // Fetch the service account key JSON file contents for authentication
        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("WEB-INF/Hermes Restaurant Search-408023883e26.json");
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (serviceAccount != null) {
            FirebaseOptions options = null;

            try {
                options = new FirebaseOptions.Builder()
                        .setDatabaseUrl("https://hermes-c1b9b.firebaseio.com")
                        .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                        .build();

                // If the App is initialized already, don't need to do it again.
                List apps = FirebaseApp.getApps();
                if (apps.size() <= 0) {
                    FirebaseApp.initializeApp(options);
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        // Get the reference of the database, in this case use the set path to return the login time
        String path = "users" + "/" + mUserId + "/" + "lastLoginTime";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);

        // To read data from firebase database, you need a ValueEventListener
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                mDatabaseInfo = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Do nothing
            }
        });
    }

}

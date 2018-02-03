package com.artemis.hermes.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 *  Activity that allows user to rate the restaurant.
 *
 * @author  Jorge Quan
 * @since   2017-10-14
 */
public class RestaurantFeedbackActivity extends AppCompatActivity {

    private RatingBar restaurantRatingBar;
    private TextView restaurantRatingValue;

    private RatingBar cuisineRatingBar;
    private TextView cuisineRatingValue;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_feedback);

        displayRestaurantInfo();
        addListenerOnRatingBar();
        addListenerOnButton();
    }

    /**
     *  Displays information of the restaurant on the TextView
     *
     */
    public void displayRestaurantInfo() {

        TextView restaurantInfo = findViewById(R.id.restaurant_info);

        RestaurantAbstract restaurantObj =
                (RestaurantAbstract) getIntent().getSerializableExtra("serialize_data");

        if (restaurantObj != null) {
            restaurantInfo.setText(restaurantObj.toFullString());
        } else {
            restaurantInfo.setText("Restaurant info: pending");
        }
    }

    /**
     *  Listens to the rating bars so that it updates the rating value
     *
     */
    public void addListenerOnRatingBar() {

        restaurantRatingBar = findViewById(R.id.restaurant_rating_bar);
        restaurantRatingValue = findViewById(R.id.restaurant_rating_value);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        restaurantRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                restaurantRatingValue.setText(String.valueOf(rating));
            }
        });

        cuisineRatingBar = findViewById(R.id.cuisine_rating_bar);
        cuisineRatingValue = findViewById(R.id.cuisine_rating_value);

        cuisineRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                cuisineRatingValue.setText(String.valueOf(rating));
            }
        });
    }

    /**
     *  Listens to the submit button
     *
     */
    public void addListenerOnButton() {

        restaurantRatingBar = findViewById(R.id.restaurant_rating_bar);
        cuisineRatingBar = findViewById(R.id.cuisine_rating_bar);
        submitButton = findViewById(R.id.btnSubmit);

        //if click on me, then display the current rating value.
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            float restaurantRating = restaurantRatingBar.getRating();
            float cuisineRating = cuisineRatingBar.getRating();

            Toast.makeText(RestaurantFeedbackActivity.this,
                    "Restaurant rating: " + String.valueOf(restaurantRating) + "\n" +
                            "Cuisine rating: " + String.valueOf(cuisineRating),
                    Toast.LENGTH_SHORT).show();

            String userId = getIntent().getExtras().getString("userId");

            RestaurantAbstract restaurantObj =
                    (RestaurantAbstract) getIntent().getSerializableExtra("serialize_data");

            if (userId != null && restaurantObj != null) {

                RestaurantFeedback restaurantFeedbackObj =
                        new RestaurantFeedback(restaurantObj.toShortUniqueName(), restaurantRating, cuisineRating);

                // Instantiate database object
                FirebaseDatabase databaseObj = FirebaseDatabase.getInstance();

                // Write to restaurant feedback into database
                restaurantFeedbackObj.addSelfToDatabase(databaseObj, userId);
                restaurantObj.addSelfToDatabase(databaseObj);
            }
            }
        });
    }
}

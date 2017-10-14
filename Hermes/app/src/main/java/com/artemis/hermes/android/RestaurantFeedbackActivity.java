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

/**
 *  Activity that allows user to rate the restaurant.
 *
 * @author  Jorge Quan
 * @since   2017-10-14
 */
public class RestaurantFeedbackActivity extends AppCompatActivity {

    private RatingBar restaurantRatingBar;
    private TextView restaurantRatingValue;

    private RatingBar branchRatingBar;
    private TextView branchRatingValue;

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

        branchRatingBar = findViewById(R.id.branch_rating_bar);
        branchRatingValue = findViewById(R.id.branch_rating_value);

        branchRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                branchRatingValue.setText(String.valueOf(rating));
            }
        });
    }

    /**
     *  Listens to the submit button
     *
     */
    public void addListenerOnButton() {

        restaurantRatingBar = findViewById(R.id.restaurant_rating_bar);
        branchRatingBar = findViewById(R.id.branch_rating_bar);
        submitButton = findViewById(R.id.btnSubmit);

        //if click on me, then display the current rating value.
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(RestaurantFeedbackActivity.this,
                        "Restaurant rating: " + String.valueOf(restaurantRatingBar.getRating()) + "\n" +
                        "Branch rating: " + String.valueOf(branchRatingBar.getRating()),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

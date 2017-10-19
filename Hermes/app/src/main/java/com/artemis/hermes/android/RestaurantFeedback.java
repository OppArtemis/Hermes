package com.artemis.hermes.android;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class to stores information from Restaurant feedback.
 *
 * @author  Jorge Quan
 * @since   2017-10-18
 */

public class RestaurantFeedback {
    private String name;
    private float restaurantRating;
    private float cuisineRating;

    RestaurantFeedback (String name, float restaurantRating, float cuisineRating) {
        this.name = name;
        this.restaurantRating = restaurantRating;
        this.cuisineRating = cuisineRating;
    }

    public String getName(){
        return this.name;
    }

    public float getRestaurantRating(){
        return this.restaurantRating;
    }

    public float getCuisineRating(){
        return this.cuisineRating;
    }

    public void setName (String name){
        this.name = name;
    }

    public void setRestaurantRating(float restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public void setCuisineRating(float cuisineRating) {
        this.cuisineRating = cuisineRating;
    }

    /**
     *  Writes content of itself to the database
     *
     *  @param database Firebase database object
     *  @param userId User ID
     *
     */
    public void addSelfToDatabase (FirebaseDatabase database, String userId) {
        DatabaseReference databaseRef =
                database.getReference("restaurant_feedback" + "/" + userId + "/" + getName() + "/");
        databaseRef.setValue(this);
    }
}

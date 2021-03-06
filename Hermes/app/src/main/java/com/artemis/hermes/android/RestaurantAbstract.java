package com.artemis.hermes.android;

import com.google.common.base.Joiner;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Interface class for Restaurants
 *
 * @author  Jorge Quan
 * @since   2017-09-25
 */

public abstract class RestaurantAbstract implements Serializable {
    protected String name;
    protected String addressFull;
    protected Boolean openNow;
    protected double rating;

    // in yelp only
    protected double reviewCount;
    protected double distance;
    protected ArrayList<String> categories;

    protected int sortScore = 0;

    public String toShortUniqueName() {
        return this.getName() + " (" + this.getAddress() + ")";
    }

    public String toString() {
        return this.getName() + " (" + this.getAddress() + ") " +
                "| " + Joiner.on(",").join(this.getCategories());
    }

    public String toFullString() {
        return "Name: " + this.getName() + "\n" +
                "Address: " + this.getAddress() + "\n" +
                "Rating: " + this.getRating() + "\n" +
                "Category: " + Joiner.on(",").join(this.getCategories());
    }

    public static Comparator<RestaurantYelpHandle> COMPARE_BY_DISTANCE = new Comparator<RestaurantYelpHandle>() {
        public int compare(RestaurantYelpHandle one, RestaurantYelpHandle other) {
            return Double.compare(one.getDistance(), other.getDistance());
        }
    };

    public static Comparator<RestaurantYelpHandle> COMPARE_BY_RATING = new Comparator<RestaurantYelpHandle>() {
        public int compare(RestaurantYelpHandle one, RestaurantYelpHandle other) {
            int compared = Double.compare(other.getRating(), one.getRating());

            if (compared == 0) {
                compared = Double.compare(other.getReviewCount(), one.getReviewCount());
            }

            return compared;
        }
    };

    public static Comparator<RestaurantYelpHandle> COMPARE_BY_SORTSCORE = new Comparator<RestaurantYelpHandle>() {
        public int compare(RestaurantYelpHandle one, RestaurantYelpHandle other) {
            return Double.compare(one.getSortScore(), other.getSortScore());
        }
    };

    // --- GETTERS ---

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        if (this.addressFull != null) {
            return this.addressFull;
        } else {
            return "";
        }
    }

    public double getRating() { return this.rating; }

    public double getReviewCount() {
        return this.reviewCount;
    }

    public double getDistance() {
        return this.distance;
    }

    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public Boolean getOpenNow() {
        return this.openNow;
    }

    public int getSortScore() { return this.sortScore; }


    // --- SETTERS ---
    public void addToSortScore(int sortScore) { this.sortScore = this.sortScore + sortScore; }

    // --- OTHER METHODS ---

    /**
     *  Writes content of itself to the database
     *
     *  @param database Firebase database object
     *
     */
    public void addSelfToDatabase (FirebaseDatabase database) {
        DatabaseReference databaseRef =
                database.getReference("restaurant" + "/" + toShortUniqueName() + "/");
        databaseRef.setValue(this);
    }
}

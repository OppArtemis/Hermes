package com.artemis.hermes.android;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Interface class for Restaurants
 *
 * @author  Jorge Quan
 * @since   2017-09-25
 */

public abstract class RestaurantAbstract {
    protected String name;
    protected String addressFull;
    protected Boolean openNow;
    protected double rating;

    // in yelp only
    protected double reviewCount;
    protected double distance;
    protected ArrayList<String> categories;

    protected int sortScore = 0;

    public String toString() {
        return this.getName() + " (" + this.getAddress() + ") " + "| " + this.getRating();
    }

    public String toFullString() {
        return this.getName() + " (" + this.getAddress() + ") " +
                "| " + this.getRating() +
                "| " + Joiner.on(",").join(this.getCategories());
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
}

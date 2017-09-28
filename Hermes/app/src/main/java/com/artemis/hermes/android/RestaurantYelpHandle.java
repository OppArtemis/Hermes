package com.artemis.hermes.android;

import com.google.common.base.Joiner;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;

import java.util.ArrayList;

/**
 * Implements a class to handle Yelp restaurants
 *
 * @author  Jorge Quan
 * @since   2017-09-25
 */

public class RestaurantYelpHandle implements RestaurantBase {

    private String name;
    private String addressFull;
    private Boolean openNow;
    private double rating;
    private double reviewCount;
    private double distance;
    private ArrayList<String> categories;

    RestaurantYelpHandle(Business business){
        this.name = business.getName();

        com.yelp.fusion.client.models.Location businessLocation =
                business.getLocation();
        this.addressFull = businessLocation.getAddress1();

        this.rating = business.getRating();
        this.reviewCount = business.getReviewCount();

        this.distance = business.getDistance();
        this.openNow = !business.getIsClosed();

        this.categories = new ArrayList<>();

        // Iterate through each category to get the "alias", which are
        // tag that identifies a restaurant (e.g. cuisine, cafe, etc)
        ArrayList<Category> categories = business.getCategories();
        for (int j = 0; j < categories.size(); j++){
            Category currentCategory = categories.get(j);
            this.categories.add(currentCategory.getAlias());
        }
    }

    public String toString() {
        return this.getName() + " (" + this.getAddress() + ") " + "| " + this.getRating();
    }

    public String toFullString() {
        return this.getName() + " (" + this.getAddress() + ") " +
                "| " + this.getRating() +
                "| " + Joiner.on(",").join(this.getCategories());
    }

    // ---- Getters -----

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

    public double getRating() {
        return this.rating;
    }

    public double getReviewCount() {
        return this.reviewCount;
    }

    public double getDistance() {
        return this.distance;
    }

    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public Boolean isOpenNow() {
        return this.openNow;
    }
}

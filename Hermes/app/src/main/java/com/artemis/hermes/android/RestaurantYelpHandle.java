package com.artemis.hermes.android;

import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;

import java.util.ArrayList;

/**
 * Implements a class to handle Yelp restaurants
 *
 * @author  Jorge Quan
 * @since   2017-09-25
 */

public class RestaurantYelpHandle extends RestaurantAbstract {

    RestaurantYelpHandle(Business business){
        this.name = business.getName();

        com.yelp.fusion.client.models.Location businessLocation = business.getLocation();
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
}

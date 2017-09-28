package com.artemis.hermes.android;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Implements a class to handle Google Api restaurants
 *
 * @author  Jonathan Lin
 * @since   2017-09-23
 */

public class RestaurantGoogleHandle extends RestaurantAbstract {
    RestaurantGoogleHandle(JSONObject jsonResult) {
        try {
            this.name = jsonResult.getString("name");
            this.addressFull = jsonResult.getString("formatted_address");

            try {
                this.rating = Double.parseDouble(jsonResult.getString("rating"));
            } catch (NumberFormatException e){
                this.rating = 0.0;
            }

            // in yelp only
            this.reviewCount = 0;
            this.distance = 0;
            this.categories = new ArrayList<>();

            JSONObject openingHoursObj = jsonResult.getJSONObject("opening_hours");
            this.openNow = Boolean.parseBoolean(openingHoursObj.getString("open_now"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

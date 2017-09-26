package com.artemis.hermes.android;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implements a class to handle Google Api restaurants
 *
 * @author  Jonathan Lin
 * @since   2017-09-23
 */

public class RestaurantGoogleHandle implements RestaurantBase {
    private String name;
    private String addressFull;
    private String addressShort;
    private Boolean openNow;
    private double rating;

    RestaurantGoogleHandle(JSONObject jsonResult) {
        try {
            this.name = jsonResult.getString("name");
            this.addressFull = jsonResult.getString("formatted_address");

            // To do, determine if we need 2 address types?
            this.addressShort =  this.addressFull;

            try {
                this.rating = Double.parseDouble(jsonResult.getString("rating"));
            } catch (NumberFormatException e){
                this.rating = 0.0;
            }

            JSONObject openingHoursObj = jsonResult.getJSONObject("opening_hours");
            this.openNow = Boolean.parseBoolean(openingHoursObj.getString("open_now"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return this.getName() + " (" + this.getAddress() + ")" + "|" + this.getRating();
    }

    // ---- Getters -----

    public String getName(){
        return this.name;
    }

    public String getAddress(){
        return this.addressShort;
    }

    public double getRating() {
        return this.rating;
    }

    public Boolean isOpenNow() {
        return this.openNow;
    }
}

package com.artemis.hermes.android;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jf2lin on 09/23/2017.
 */

public class RestaurantHandle {
    String name;
    String address_full;
    String address_short;
//    String openNow;
    String rating;

    RestaurantHandle(JSONObject jsonResult) {
        try {
            this.name = jsonResult.getString("name");
            this.address_full = jsonResult.getString("formatted_address");
//            this.openNow = jsonResult.getString("opening_hours");
            this.rating = jsonResult.getString("rating");

            this.address_short =  this.address_full;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return name + " (" + address_short + ")";
    }
}

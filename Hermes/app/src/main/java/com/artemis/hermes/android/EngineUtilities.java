package com.artemis.hermes.android;

import java.util.Collections;
import java.util.List;

/**
 * Utilities class to help for the Engine algorithm.
 *
 * @author  Jorge Quan
 * @since   2018-02-08
 */

@SuppressWarnings("WeakerAccess")
public final class EngineUtilities {

    public static List<RestaurantYelpHandle> sortRetrievedYelpRestaurants(List<RestaurantYelpHandle> inputArray) {
        // sort by rating
        Collections.sort(inputArray, RestaurantYelpHandle.COMPARE_BY_RATING);
        for (int i = 0; i < inputArray.size(); i++) {
            inputArray.get(i).addToSortScore(i);
        }

        // sort by distance
        Collections.sort(inputArray, RestaurantYelpHandle.COMPARE_BY_DISTANCE);
        for (int i = 0; i < inputArray.size(); i++) {
            inputArray.get(i).addToSortScore(i);
        }

        // finally, sort by the sort score
        Collections.sort(inputArray, RestaurantYelpHandle.COMPARE_BY_SORTSCORE);

        return inputArray;
    }
}


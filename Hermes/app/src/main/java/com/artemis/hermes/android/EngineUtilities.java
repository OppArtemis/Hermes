package com.artemis.hermes.android;

import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utilities class to help for the Engine algorithm.
 *
 * @author  Jorge Quan
 * @since   2018-02-08
 */

@SuppressWarnings("WeakerAccess")
public final class EngineUtilities {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Sorts restaurant objects by placing the best ones first.
     *
     * @param inputArray list of RestaurantYelpHandle objects.
     *
     */
    public static void sortRetrievedYelpRestaurants(List<RestaurantYelpHandle> inputArray) {
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
    }

    /**
     * Get average from a list of floats.
     *
     * @param floatList List of floats.
     *
     */
    public static double getAverageFromList(List<Float> floatList) {

        if (floatList.size() == 0 ) {
            return 0.0;
        }
        double sum = 0;
        for (int h = 0; h < floatList.size(); h++) {
            sum += floatList.get(h);
        }
        return sum / floatList.size();
    }

    /**
     * Actual logic for analyzeRestaurantsWithUserRating.
     *
     * @param restaurantObjs List of RestaurantYelpHandle objects.
     * @param ratingByRestaurant Map of list of float values (representing user's rating) by
     *                           restaurant unique name.
     *
     */
    public static void addUserRatingsIntoRestaurants(List<RestaurantYelpHandle> restaurantObjs,
                                                     Map<String, List<Float>> ratingByRestaurant,
                                                     int numberOfUsers) {
        // Iterate through all the scoped restaurants
        for (int i = 0; i < restaurantObjs.size(); i++) {
            RestaurantAbstract currentRestaurantObj = restaurantObjs.get(i);

            // Short unique name is by which the RestaurantFeedback used to save restaurant's name
            String restaurantUniqueName = currentRestaurantObj.toShortUniqueName();

            double internetRating = currentRestaurantObj.getRating();

            double finalRating = internetRating;

            List<Float> userRestaurantRatings = ratingByRestaurant.get(restaurantUniqueName);

            // Case:
            // 1. None of the users have been to restaurant
            //      User internet's rating
            if (userRestaurantRatings != null) {

                // Case:
                // 2. Most users have been to restaurant
                //      Take average
                double threshold = 0.6 * numberOfUsers;
                if ( userRestaurantRatings.size() > threshold) {
                    finalRating = getAverageFromList(userRestaurantRatings);
                    Log.d(TAG, "--- Use average user's ratings on restaurant --- " +
                            restaurantUniqueName);
                } else {
                    // 3. Most users have not been to restaurant
                    //      Take weighted average from users' and internet's
                    double userRatings = getAverageFromList(userRestaurantRatings);
                    finalRating = userRatings*0.4 + internetRating*0.6;
                    Log.d(TAG, "--- Use average user's and internet's ratings on restaurant --- " +
                            restaurantUniqueName +
                            " there are number of ratings " + userRestaurantRatings.size() +
                            " there are number of users " + numberOfUsers);
                }

                // Set the rating with user's feedback
                currentRestaurantObj.setRating(finalRating);
            }
        }
    }
}


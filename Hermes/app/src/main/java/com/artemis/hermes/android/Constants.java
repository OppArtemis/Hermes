package com.artemis.hermes.android;

import java.text.SimpleDateFormat;

/**
 *  Class to store constants.
 *
 * @author  Jonathan Lin
 * @since   2017-09-13
 */
public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    // URL of the project API
    public static final String API_ROOT_URL = "https://hermes-c1b9b.appspot.com/_ah/api/";

    public static final double DISTANCE_THRESHOLD = 500; // 0.5 km
    public static final double TIME_THRESHOLD = 60*20; // 20 min

    public static String timeString(long timeInMs) {
        SimpleDateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormatLong.format(timeInMs);
    }
}

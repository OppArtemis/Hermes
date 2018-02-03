package com.artemis.hermes.android;

/**
 * Created by jf2lin on 02/03/2018.
 */

public class UserProfile {
    private String name;
    private String address;
    private String lastLoginTime;
    private String location;
    private double[] locationLatLng = new double[2];

    UserProfile() {
        // blank constructor for Firebase needs
    }

    public void init() {
        String[] locationStrSplit = location.split(", ");
        locationLatLng[0] = Double.parseDouble(locationStrSplit[0]);
        locationLatLng[1] = Double.parseDouble(locationStrSplit[1]);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public String getLocation() {
        return location;
    }

    public double[] retrieveLatLng() {
        return locationLatLng;
    }
}

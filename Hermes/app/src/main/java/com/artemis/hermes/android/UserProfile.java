package com.artemis.hermes.android;

/**
 * Created by jf2lin on 02/03/2018.
 */

public class UserProfile {
    private String name;
    private String address;
    private String lastLoginTime;
    private String location;
    private double locationLong;
    private double locationLat;

    UserProfile() {
        // blank constructor for Firebase needs
        init();
    }

    public void init() {
        String[] locationStrSplit = location.split(", ");
        locationLong = Double.parseDouble(locationStrSplit[0]);
        locationLat = Double.parseDouble(locationStrSplit[1]);
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

    public double[] retrieveLocation() {
        double[] locationDouble = new double[2];
        locationDouble[0] = locationLong;
        locationDouble[1] = locationLat;

        return locationDouble;
    }
}

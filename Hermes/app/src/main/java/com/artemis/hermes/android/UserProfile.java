package com.artemis.hermes.android;

import android.location.Location;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jf2lin on 02/03/2018.
 */

public class UserProfile {
    private String id;
    private String name;
    private String address;
    private long lastLoginTime;
    private List<Double> locationLatLng;

    UserProfile() {
        // blank constructor for Firebase needs
    }

    UserProfile(FirebaseUser user, String address, Location location, Calendar lastLoginTime) {
        this.id = user.getUid();
        this.name = user.getDisplayName();
        this.address = address;
        this.locationLatLng = locationToDoubleArray(location);
        this.lastLoginTime = lastLoginTime.getTimeInMillis();
    }

    public List<Double> locationToDoubleArray(Location location) {
        List<Double> newArray = new ArrayList<>();
        newArray.add(location.getLatitude());
        newArray.add(location.getLongitude());

        return newArray;
    }

    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public List<Double> getLocationLatLng() {
        return locationLatLng;
    }
}

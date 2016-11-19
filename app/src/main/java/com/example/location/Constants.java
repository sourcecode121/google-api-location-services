package com.example.location;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Anand on 19/11/2016.
 */

public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.example.location";
    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final long DETECTION_INTERVAL = 0;

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 1;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<>();
    static {
        LANDMARKS.put("GOOGLE", new LatLng(37.422611,-122.0840577));
        LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));
    }
}

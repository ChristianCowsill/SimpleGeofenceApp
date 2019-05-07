package com.cowsill.geofenceapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Constants {

    public static final int LOCATION_REQUEST_ID = 1;
    public static final int SERVICE_ID = 1;
    public static final int PERMISSION_REQUEST_CODE = 1000;
    public static final int GEOFENCE_NOTIFICATION_ID = 1;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000; // 12 hours
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<>();

    static {
        LANDMARKS.put("CN Tower", new LatLng(43.642564,-79.387087));
        LANDMARKS.put("Queens Park", new LatLng(43.6686,-79.3941));
    }
}

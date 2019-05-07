package com.cowsill.geofenceapp;


import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocationRequestService extends Service {

    FusedLocationProviderClient mLocationProviderClient;
    LocationRequest mLocationRequest;
    LocationProviderThread thread;
    private boolean threadIsRunning = false;
    LocationCallback mLocationCallback;

    public static final String TAG = "LocReqService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(Constants.SERVICE_ID,
                createNotification());
        getLocationUpdates();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.i(TAG, "onLocationResult: " + locationResult.getLastLocation().toString());
            }
        };
        threadIsRunning = true;
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, GeofenceApp.LOCATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_searching_black_24dp)
                .setContentTitle("Location Services")
                .setContentText("Currently monitoring location")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return builder.build();
    }

    private void getLocationUpdates() {

        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);

        thread = new LocationProviderThread();
        thread.start();

    }

    class LocationProviderThread extends Thread {

        Looper looper;

        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(LocationRequestService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            looper = Looper.myLooper();
            looper.prepare();
            mLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    looper
            );

            looper.loop();
        }
    }

    @Override
    public void onDestroy() {
        mLocationProviderClient.removeLocationUpdates(mLocationCallback);
        thread.interrupt();
        Log.i(TAG, "onDestroy: Location Updates Removed" );
    }
}




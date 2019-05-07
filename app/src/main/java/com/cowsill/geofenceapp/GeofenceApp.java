package com.cowsill.geofenceapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class GeofenceApp extends Application {

    public static final String LOCATION_CHANNEL_ID = "Location Notification Channel";
    public static final String GEOFENCE_CHANNEL_ID = "Geofence Notification Channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels() {

        // Check for API += 26
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(
                    createIndividualChannel(
                            getString(R.string.channel1_name),
                            getString(R.string.channel1_description),
                            NotificationManager.IMPORTANCE_DEFAULT,
                            LOCATION_CHANNEL_ID
                    )
            );

            notificationManager.createNotificationChannel(
                    createIndividualChannel(
                            getString(R.string.channel2_name),
                            getString(R.string.channel2_description),
                            NotificationManager.IMPORTANCE_HIGH,
                            GEOFENCE_CHANNEL_ID
                    )
            );


        }

    }

    private  NotificationChannel createIndividualChannel(String name, String description, int importance, String channelId){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    channelId,
                    name,
                    importance
            );

            notificationChannel.setDescription(description);

            return notificationChannel;
        }

        else {
            return null;
        }
    }
}

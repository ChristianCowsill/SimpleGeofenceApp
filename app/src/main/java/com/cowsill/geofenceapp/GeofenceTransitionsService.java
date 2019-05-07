package com.cowsill.geofenceapp;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsService extends IntentService {

    public static final String TAG = "GeofenceTransServ";
    private boolean geofenceTransitionResult;

    public GeofenceTransitionsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "onHandleIntent: STARTED" );

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.i(TAG, "onHandleIntent: " + geofencingEvent.getErrorCode());
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
        geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){

            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
                geofenceTransitionResult = true;
            } else {
                geofenceTransitionResult = false;
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(Constants.GEOFENCE_NOTIFICATION_ID,createNotification(geofenceTransitionResult));
        }

    }

    private Notification createNotification(boolean result) {

        String contentString;

        if(result){
            contentString = "You entered a geofence.";
        } else {
            contentString = "You exited a geofence.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, GeofenceApp.GEOFENCE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_searching_black_24dp)
                .setContentTitle("Geofence Transition Event")
                .setContentText(contentString)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }
}

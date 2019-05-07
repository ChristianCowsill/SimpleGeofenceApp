package com.cowsill.geofenceapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ToggleButton tbLocationMonitoring;
    private ToggleButton tbGeofenceMonitoring;
    private boolean mLocationMonitoring;
    private boolean mGeofenceMonitoring;
    private HashMap<String, Object> entry;
    private ArrayList<Geofence> geofenceArrayList;
    PendingIntent geofencePendingIntent;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate boolean variables to track services running
        mLocationMonitoring = false;
        mGeofenceMonitoring = false;

        // Instantiate UI and set listeners
        tbLocationMonitoring = findViewById(R.id.tbLocationMonitoring);
        tbGeofenceMonitoring = findViewById(R.id.tbGeofenceMonitoring);

        // Instantiate geofenceList
        geofenceArrayList = new ArrayList<>();

        // Set listeners
        tbLocationMonitoring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        // toggle enabled
                        mLocationMonitoring = true;
                        Toast.makeText(MainActivity.this,
                                "Location Monitoring: " + mLocationMonitoring,
                                Toast.LENGTH_SHORT)
                                .show();
                        startLocationMonitoring();
                    } else {
                        Intent stopLocationIntent = new Intent(
                                MainActivity.this,
                                LocationRequestService.class
                        );
                        // toggle disabled
                        if (mGeofenceMonitoring) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Geofence monitoring will now be turned off",
                                    Toast.LENGTH_SHORT
                            ).show();
                            mLocationMonitoring = false;
                            mGeofenceMonitoring = false;
                            tbGeofenceMonitoring.setChecked(false);
                            tbGeofenceMonitoring.setTextOff("Off");

                        } else {
                            mLocationMonitoring = false;
                            Toast.makeText(MainActivity.this,
                                    "Location Monitoring: " + mLocationMonitoring,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }

                        stopService(stopLocationIntent);
                    }
                }
            }
        });

        tbGeofenceMonitoring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        if (mLocationMonitoring) {
                            mGeofenceMonitoring = true;
                            startGeofenceMonitoring();
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Sorry, location monitoring must be enabled for this app to work.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            buttonView.setChecked(false);
                        }

                    } else {
                        // toggle disabled
                        Toast.makeText(MainActivity.this,
                                "Geofence Button not checked",
                                Toast.LENGTH_SHORT)
                                .show();
                        mGeofenceMonitoring = false;
                    }
                }
            }
        });

        checkForLocationPermission();

    }

    private void startGeofenceMonitoring() {

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceArrayList = new ArrayList<>();
        populateGeofenceList();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(
                                MainActivity.this,
                                "Geofence Added Successfully",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                MainActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

    }

    private GeofencingRequest getGeofencingRequest(){

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceArrayList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){

        if(geofencePendingIntent != null){
            return geofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsService.class);
        geofencePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;

    }

    private void populateGeofenceList() {

        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            geofenceArrayList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(
                            Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT
                    )
                    .build());
        }
    }


    private void startLocationMonitoring(){

        Intent intent = new Intent(this, LocationRequestService.class);
        startService(intent);
    }

    private void checkForLocationPermission(){

        // Check permission status;  if permissions are not given, request them

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.PERMISSION_REQUEST_CODE);
            }
        }
    }
}

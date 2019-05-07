# SimpleGeofenceApp

This is a simple (and I mean SIMPLE!) geofence app that I created just to get a handle for the technology.

The geofences are hard-coded in the Constants.java file.  These can be changed to whatever coordinates the developer wants.

There are two buttons.  One enables a foreground service which starts a new thread and gets current location.  (The Geofence API 
requires at least one application on the device to be polling location updates.)  The seconds button creates the geofences
and starts monitoring for transition events.  (Currently, the app uses entry and exit events only.)

When the device enters/exits a geofence, they receive a notification saying that they have entered/exited a geofence.

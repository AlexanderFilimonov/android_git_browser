package com.afilimonov.gitbrowser.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-18.
 * helper class to work with gps or network location detection
 * it helps to work with permissions
 */
public class LocationServiceHelper {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 110;
    /** in millis */
    public static final int REQUEST_LOCATION_INTERVAL = 10000;
    /** in millis */
    public static final int REQUEST_LOCATION_FASTEST_INTERVAL = 5000;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private LocationListener locationListener;

    private Activity activity;

    private static LocationServiceHelper instance;

    public static LocationServiceHelper getInstance(Activity activity) {
        if (instance == null) {
            instance = new LocationServiceHelper(activity);
        }
        return instance;
    }

    private LocationServiceHelper(Activity activity) {
        this.activity = activity;
        createGoogleApiClient();
    }

    /** connect to location service and subscribe for location changes */
    public void connect(LocationListener locationListener) {
        this.locationListener = locationListener;
        googleApiClient.connect();
    }

    /** disconnect from location service */
    public void disconnect() {
        googleApiClient.disconnect();
    }

    /** returns last known location */
    public Location getLastLocation() {
        return lastLocation;
    }

    private Context getContext() {
        return activity;
    }

    private void createGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(new ConnectionCallbacks())
                    .addOnConnectionFailedListener(new OnConnectionFailedListener())
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    protected void startLocationUpdates() {
        Logger.d("GoogleApiClient.startLocationUpdates");
        if (isPermissionGranted()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient, getLocationRequest(), new LocationListenerImpl()
                );
            } catch (SecurityException e) {
                Logger.e(e);
            }
        } else {
            onPermissionsNotGranted();
        }
    }

    private void onPermissionsNotGranted() {
        Logger.d("location permission not granted");
        if (shouldShowRequestPermissionRationale()) {
            Logger.d("location shouldShowRequestPermissionRationale");
            Toast.makeText(activity, "Show an explanation to the user", Toast.LENGTH_SHORT).show(); // debug
        } else {
            requestPermissions();
        }
    }

    protected LocationRequest getLocationRequest() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(REQUEST_LOCATION_INTERVAL);
            locationRequest.setFastestInterval(REQUEST_LOCATION_FASTEST_INTERVAL);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        return locationRequest;
    }

    private boolean isPermissionGranted() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        Logger.d("request location permissions");
        if (activity instanceof ActivityListeners) {
            ((ActivityListeners) activity).getContainer().getPermissionListeners().add(new ActivityListeners.RequestPermissionListener() {
                @Override
                public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                    ((ActivityListeners) activity).getContainer().getPermissionListeners().remove(this);
                    LocationServiceHelper.this.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            });
        }

        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[i])) {
                    Logger.d("onRequestPermissionsResult for location");
                    int result = grantResults[i];
                    if (PackageManager.PERMISSION_GRANTED == result) {
                        Logger.d("location permisson granted");
                    } else {
                        Logger.d("location permisson not granted");
                        Toast.makeText(activity, "Permisson not granted, unable to receive location", Toast.LENGTH_SHORT).show(); // debug
                    }
                    break;
                }
            }
        }
    }

    private class LocationListenerImpl implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Logger.d("onLocationChanged " + location);
            if (location != null) {
                lastLocation = location;
                if (locationListener != null) {
                    locationListener.onLocationChanged(location);
                }
            }
        }
    }

    private class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Logger.d("GoogleApiClient.onConnected");

            if (isPermissionGranted()) {
                try {
                    lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if (lastLocation != null) {
                        Logger.d(String.valueOf(lastLocation.getLatitude()) + " " + String.valueOf(lastLocation.getLongitude()));
                    }
                    startLocationUpdates();
                } catch (SecurityException e) {
                    Logger.e(e);
                }
            } else {
                onPermissionsNotGranted();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Logger.d("GoogleApiClient.onConnectionSuspended");
        }
    }

    private class OnConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Logger.d("GoogleApiClient.onConnectionFailed");
        }
    }
}

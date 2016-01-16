package com.afilimonov.gitbrowser.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.utils.CameraHelper;
import com.afilimonov.gitbrowser.utils.Constants;
import com.afilimonov.gitbrowser.utils.Logger;
import com.afilimonov.gitbrowser.utils.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-15.
 * screen with changing user name
 */
public class ChangeUserNameFragment extends SetUserNameFragment {

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;

    public static ChangeUserNameFragment newInstance() {
        return new ChangeUserNameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLastLocation();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getLastLocation() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Logger.d("onConnected");

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                return;
                            }
                            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                            if (lastLocation != null) {
                                Logger.d(String.valueOf(lastLocation.getLatitude())
                                        + " "
                                        + String.valueOf(lastLocation.getLongitude()));

                            }
                            startLocationUpdates();
                        }

                        protected void startLocationUpdates() {
                            Logger.d("startLocationUpdates");

                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                return;
                            }
                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    googleApiClient, getLocationRequest(), new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            Logger.d("onLocationChanged " + location);
                                        }
                                    });
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Logger.d("onConnectionSuspended");
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Logger.d("onConnectionFailed");
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected LocationRequest getLocationRequest() {
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        return locationRequest;
    }

    @Override
    public void onResume() {
        googleApiClient.connect();
        Logger.d("googleApiClient.connect();");
        super.onResume();
    }

    @Override
    public void onPause() {
        googleApiClient.disconnect();
        Logger.d("googleApiClient.disconnect();");
        super.onPause();
    }

    protected void initLoadReposButton() {
        loadReposButtonProgressBar = view.findViewById(R.id.loadReposButtonProgressBar);
        loadReposButton = view.findViewById(R.id.loadReposButton);
        ((TextView) loadReposButton).setText(getString(R.string.saveButtonTitle));
        loadReposButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameField.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    userNameField.setError(getString(R.string.userNameFieldEmptyErrorText));

                } else {
                    Preferences.putString(Constants.USER_NAME_CHANGED_KEY, userName, getContext());

                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
}

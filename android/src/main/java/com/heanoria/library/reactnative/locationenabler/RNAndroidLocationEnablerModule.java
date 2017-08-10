package com.heanoria.library.reactnative.locationenabler;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import static android.app.Activity.RESULT_OK;

/**
 * RNAndroidLocationEnablerModule class offers a single feature: prompts a popup like Google Maps to the user.
 * User can enable GPS localisation directly in the App, this is a good choice for user experience because one stays in the App.
 *
 * @author Richou
 * @since 2016-12
 */
public class RNAndroidLocationEnablerModule extends ReactContextBaseJavaModule implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, ActivityEventListener {

    private static final String SELF_MODULE_NAME = "RNAndroidLocationEnabler";
    private static final String LOCATION_INTERVAL_DURATION_PARAMS_KEY = "interval";
    private static final String LOCATION_FAST_INTERVAL_DURATION_PARAMS_KEY = "fastInterval";
    private static final String TAG = RNAndroidLocationEnablerModule.class.getName();
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int DEFAULT_INTERVAL_DURATION = 10000;
    private static final int DEFAULT_FAST_INTERVAL_DURATION = DEFAULT_INTERVAL_DURATION / 2 ;

    private static final String ERR_USER_DENIED_CODE = "ERR00";
    private static final String ERR_SETTINGS_CHANGE_UNAVAILABLE_CODE = "ERR01";
    private static final String ERR_FAILED_OPEN_DIALOG_CODE = "ERR02";

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Promise promise;


    public RNAndroidLocationEnablerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @ReactMethod
    public void promptForEnableLocationIfNeeded(ReadableMap params, Promise promise) {
        if (getCurrentActivity() == null || params == null || promise == null) return;

        this.promise = promise;

        googleApiClient = new GoogleApiClient.Builder(getCurrentActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(params.hasKey(LOCATION_INTERVAL_DURATION_PARAMS_KEY) ? params.getInt(LOCATION_INTERVAL_DURATION_PARAMS_KEY) : DEFAULT_INTERVAL_DURATION);
        locationRequest.setFastestInterval(params.hasKey(LOCATION_FAST_INTERVAL_DURATION_PARAMS_KEY) ? params.getInt(LOCATION_FAST_INTERVAL_DURATION_PARAMS_KEY) : DEFAULT_FAST_INTERVAL_DURATION);
    }

    @Override
    public String getName() {
        return SELF_MODULE_NAME;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                if (promise != null) promise.resolve("already-enabled");
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(getCurrentActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException exception) {
                    Log.e(TAG, "Failed to show dialog", exception);
                    if (promise != null) promise.reject(ERR_FAILED_OPEN_DIALOG_CODE, new RNAndroidLocationEnablerException("Failed to show dialog", exception));
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                if (promise != null) promise.reject(ERR_SETTINGS_CHANGE_UNAVAILABLE_CODE, new RNAndroidLocationEnablerException("Settings change unavailable"));
                break;
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && promise != null) {
            if (resultCode == RESULT_OK ) {
                promise.resolve("enabled");
            } else {
                promise.reject(ERR_USER_DENIED_CODE, new RNAndroidLocationEnablerException("denied"));
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
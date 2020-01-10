package com.heanoria.library.reactnative.locationenabler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.app.Activity.RESULT_OK;

/**
 * RNAndroidLocationEnablerModule class offers a single feature: prompts a popup like Google Maps to the user.
 * User can enable GPS localisation directly in the App, this is a good choice for user experience because one stays in the App.
 *
 * @author Richou
 * @since 2016-12
 */
public class RNAndroidLocationEnablerModule extends ReactContextBaseJavaModule implements ActivityEventListener, OnCompleteListener<LocationSettingsResponse> {

    private static final String SELF_MODULE_NAME = "RNAndroidLocationEnabler";
    private static final String LOCATION_INTERVAL_DURATION_PARAMS_KEY = "interval";
    private static final String LOCATION_FAST_INTERVAL_DURATION_PARAMS_KEY = "fastInterval";
    private static final String TAG = RNAndroidLocationEnablerModule.class.getName();
    private static final int REQUEST_CHECK_SETTINGS = 42;
    private static final int DEFAULT_INTERVAL_DURATION = 10000;
    private static final int DEFAULT_FAST_INTERVAL_DURATION = DEFAULT_INTERVAL_DURATION / 2 ;

    private static final String ERR_USER_DENIED_CODE = "ERR00";
    private static final String ERR_SETTINGS_CHANGE_UNAVAILABLE_CODE = "ERR01";
    private static final String ERR_FAILED_OPEN_DIALOG_CODE = "ERR02";
    private static final String ERR_INTERNAL_ERROR = "ERR03";

    private Promise promise;


    public RNAndroidLocationEnablerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @ReactMethod
    public void promptForEnableLocationIfNeeded(ReadableMap params, Promise promise) {
        if (getCurrentActivity() == null || params == null || promise == null) return;

        this.promise = promise;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(params.hasKey(LOCATION_INTERVAL_DURATION_PARAMS_KEY) ? params.getInt(LOCATION_INTERVAL_DURATION_PARAMS_KEY) : DEFAULT_INTERVAL_DURATION);
        locationRequest.setFastestInterval(params.hasKey(LOCATION_FAST_INTERVAL_DURATION_PARAMS_KEY) ? params.getInt(LOCATION_FAST_INTERVAL_DURATION_PARAMS_KEY) : DEFAULT_FAST_INTERVAL_DURATION);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(getCurrentActivity()).checkLocationSettings(builder.build());
        task.addOnCompleteListener(this);
    }

    @Override
    public String getName() {
        return SELF_MODULE_NAME;
    }

    private boolean isLocationProviderEnabled() {
        if (getCurrentActivity() != null) {
            LocationManager locationManager = (LocationManager) getCurrentActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS && promise != null) {
            if (resultCode == RESULT_OK || isLocationProviderEnabled()) {
                promise.resolve("enabled");
            } else {
                promise.reject(ERR_USER_DENIED_CODE, new RNAndroidLocationEnablerException("denied"));
            }
            this.promise = null;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
        try {
            task.getResult(ApiException.class);
            if (promise != null) promise.resolve("already-enabled");
            promise = null;
        } catch (ApiException exception) {
            switch (exception.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) exception;
                        resolvable.startResolutionForResult(getCurrentActivity(), REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.e(TAG, "Failed to show dialog", sendEx);
                        if (promise != null) promise.reject(ERR_FAILED_OPEN_DIALOG_CODE, new RNAndroidLocationEnablerException("Failed to show dialog", sendEx));
                        this.promise = null;
                    } catch (ClassCastException classCast) {
                        if (promise != null) promise.reject(ERR_INTERNAL_ERROR, new RNAndroidLocationEnablerException("Internal error", classCast));
                        this.promise = null;
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    if (promise != null) promise.reject(ERR_SETTINGS_CHANGE_UNAVAILABLE_CODE, new RNAndroidLocationEnablerException("Settings change unavailable"));
                    this.promise = null;
                    break;
            }
        }
    }
}

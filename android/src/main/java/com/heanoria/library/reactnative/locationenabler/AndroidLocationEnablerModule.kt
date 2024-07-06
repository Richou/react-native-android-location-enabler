package com.heanoria.library.reactnative.locationenabler

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.util.Log
import androidx.core.location.LocationManagerCompat
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task


class AndroidLocationEnablerModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), ActivityEventListener, OnCompleteListener<LocationSettingsResponse> {

  private val context: ReactApplicationContext
  private var promise: Promise? = null

  init {
      context = reactContext
      context.addActivityEventListener(this)
  }

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  fun isLocationEnabled(promise: Promise) {
    if (currentActivity == null) return

    val locationManager = this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)

    promise.resolve(isLocationEnabled)
  }

  @ReactMethod
  fun hasPlayServices(promise: Promise) {
    val state = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
    promise.resolve(state == ConnectionResult.SUCCESS);
  }

  @ReactMethod
  fun promptForEnableLocationIfNeeded(params: ReadableMap?, promise: Promise) {
    if (currentActivity == null) return

    this.promise = promise
    val canPrompt = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    if (!canPrompt) {
      this.promise?.reject(AndroidLocationEnablerException(ERR_NO_GOOGLE_SERVICES))
      this.promise = null
      return
    }

    val interval = if (params?.hasKey(LOCATION_INTERVAL_DURATION_PARAMS_KEY) == true) params.getInt(LOCATION_INTERVAL_DURATION_PARAMS_KEY) else DEFAULT_INTERVAL_DURATION
    val waitForAccurate = if (params?.hasKey(LOCATION_WAIT_FOR_ACCURATE_PARAMS_KEY) == true) params.getBoolean(LOCATION_WAIT_FOR_ACCURATE_PARAMS_KEY) else DEFAULT_WAIT_FOR_ACCURATE
    Log.i(TAG, "passed interval $interval")
    val locationRequest = createRequest(interval.toLong(), waitForAccurate)
    val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true).build()
    val task: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(context).checkLocationSettings(locationSettingsRequest)
    task.addOnCompleteListener(this)
  }

  private fun createRequest(timeInterval: Long, waitForAccurate: Boolean): LocationRequest =
    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
      setWaitForAccurateLocation(waitForAccurate)
    }.build()



  companion object {
    const val NAME = "AndroidLocationEnabler"
    const val LOCATION_INTERVAL_DURATION_PARAMS_KEY = "interval"
    const val LOCATION_WAIT_FOR_ACCURATE_PARAMS_KEY = "waitForAccurate"
    const val DEFAULT_INTERVAL_DURATION = 10000
    const val DEFAULT_WAIT_FOR_ACCURATE = true
    const val REQUEST_CHECK_SETTINGS = 42
    const val TAG = "LocationEnablerModule"

    const val ERR_USER_DENIED_CODE = "ERR00"
    const val ERR_SETTINGS_CHANGE_UNAVAILABLE_CODE = "ERR01"
    const val ERR_FAILED_OPEN_DIALOG_CODE = "ERR02"
    const val ERR_INTERNAL_ERROR = "ERR03"
    const val ERR_NO_GOOGLE_SERVICES = "ERR04"
  }

  override fun onComplete(task: Task<LocationSettingsResponse>) {
    Log.i(TAG, "OnComplete")
    try {
      task.getResult(ApiException::class.java)
      this.promise?.resolve("already-enabled")
    } catch(exception: ApiException) {
      when (exception.statusCode) {
        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
          try {
            val resolvable = exception as ResolvableApiException
            context.currentActivity?.let { resolvable.startResolutionForResult(it, REQUEST_CHECK_SETTINGS) }
          } catch (sendEx: IntentSender.SendIntentException) {
            Log.e(TAG, "Failed to show dialog", sendEx)
            this.promise?.reject(AndroidLocationEnablerException(ERR_FAILED_OPEN_DIALOG_CODE, sendEx))
            this.promise = null
          } catch (classCast: ClassCastException) {
            this.promise?.reject(AndroidLocationEnablerException(ERR_INTERNAL_ERROR, classCast))
            this.promise = null
          }
        }
        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
          this.promise?.reject(AndroidLocationEnablerException(ERR_SETTINGS_CHANGE_UNAVAILABLE_CODE))
          this.promise = null
        }
      }
    }
  }

  private fun isLocationProviderEnabled(): Boolean {
    val locationManager = currentActivity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      return true
    }

    return false
  }

  override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, intent: Intent?) {
    Log.i(TAG, "On activityResult : $requestCode")
    if (requestCode == REQUEST_CHECK_SETTINGS) {
      if (resultCode == RESULT_OK || isLocationProviderEnabled()) {
        Log.i(TAG, "User has enabled the location service")
        this.promise?.resolve("enabled")
      } else {
        this.promise?.reject(AndroidLocationEnablerException(ERR_USER_DENIED_CODE))
      }
      this.promise = null
    }
  }

  override fun onNewIntent(intent: Intent?) {
    // Nothing to do
  }
}

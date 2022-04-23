package app.com.ast.speedtest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*

class LocationUtils(context: Activity, locationInterface: LocationInterface) :
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, PermissionInterface {

    override fun allowed(allow: Boolean) {
    }

    companion object {
        var latitude = 0.0
        var longitude = 0.0
    }

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var mGoogleApiClient: GoogleApiClient? = null


    var mContext = context

    var pd: ProgressDialog? = null
    val permissions =
        arrayOf<String>(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    var editor: SharedPreferences.Editor? = null
    var prefs: SharedPreferences? = null
    private var mLocationInterface = locationInterface

    fun initPermission() {

        editor = mContext.getSharedPreferences(Constants.APP_PREFS, AppCompatActivity.MODE_PRIVATE)
            .edit()
        prefs = mContext.getSharedPreferences(Constants.APP_PREFS, AppCompatActivity.MODE_PRIVATE)

        if (!prefs!!.getBoolean(Constants.LOC_PREFS, false)) {

            AlertDialog.Builder(mContext)
                .setTitle("Permission")
                .setMessage("Location permission is required to provide you the awesome features through this app.")
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        // Continue with delete operation
                        dialog.dismiss()
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Toast.makeText(mContext, "Permission denied!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        mContext.finish()
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .show()
        } else {
            initLocation(mContext)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocation(mContext: Activity) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
            mGoogleApiClient?.connect()
        } else {
            mGoogleApiClient?.connect()
        }
    }


    override fun onConnected(p0: Bundle?) {

        locationRequest = LocationRequest.create()
        locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest?.setInterval(0)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient!!, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
            override fun onResult(result: LocationSettingsResult) {
                val status = result.getStatus()
                val state = result.getLocationSettingsStates()
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        // islocationEnabled = true
                        requestLocation()
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            //     islocationEnabled = false
                            status.startResolutionForResult(
                                mContext,
                                Constants.REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        // islocationEnabled = false
                        Toast.makeText(
                            mContext,
                            "Your device does not support gps",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })

    }

    override fun onConnectionSuspended(p0: Int) {
        /*  if (pd != null) {
              pd?.dismiss()
          }

          Toast.makeText(this@VoiceNavActivity, "Your Gps is not working.", Toast.LENGTH_SHORT).show();
  */

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

        /* if (pd != null) {
             pd?.dismiss()
         }

         Toast.makeText(this@VoiceNavActivity, "Your Gps is not working.", Toast.LENGTH_SHORT).show();
 */

    }

    @SuppressLint("MissingPermission")
    public fun requestLocation() {
        try {

            pd = ProgressDialog(mContext)
            pd?.setMessage("Fetching Location...")
            pd?.setCanceledOnTouchOutside(false)
            pd?.show()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.equals(null)) {
                    return
                }
                for (location in locationResult.getLocations()) {
                    if (location != null) {
                        try {
                            pd?.dismiss()
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        latitude = location.getLatitude()
                        longitude = location.getLongitude()

                        mLocationInterface.getLocation(location)

                        Toast.makeText(mContext, "Location Fetched", Toast.LENGTH_SHORT).show();
                        mFusedLocationClient?.removeLocationUpdates(locationCallback!!)
                    }
                }
            }
        }
        mFusedLocationClient?.requestLocationUpdates(
            locationRequest!!,
            locationCallback as LocationCallback,
            Looper.myLooper()!!
        )
    }


}
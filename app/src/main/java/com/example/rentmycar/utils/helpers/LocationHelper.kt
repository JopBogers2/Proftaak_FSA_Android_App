package com.example.rentmycar.utils.helpers

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class LocationHelper(private val context: Context, private val activity: Activity? = null) {
    private val requiredPermissions = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION,
    )

    /**
     * Returns true when both fine & coarse location permissions are granted.
     */
    fun hasLocationPermissions() = requiredPermissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
    }

    /**
     * Returns true in case one of the required location permissions was denied by user
     * so that the user needs to be educated about the advantages of location permissions.
     */
    private fun shouldShowEducationalUI() =
        requiredPermissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity ?: throw Exception(), permission)
    }

    /**
     * Get last user's location.
     */
    @SuppressLint("MissingPermission")
    fun getUserLocation(onLocationFetched: (Location?) -> Unit) {
        val locationClient = LocationServices.getFusedLocationProviderClient(context)

        locationClient.lastLocation.addOnSuccessListener { location ->
            onLocationFetched(location)
        }.addOnFailureListener {
            onLocationFetched(null)
        }
    }

    /**
     * Handle all the possible situations which may occur regarding the permissions
     */
    fun checkAndRequestPermissions(requestPermissionLauncher: ActivityResultLauncher<Array<String>>) {
        if (this.hasLocationPermissions()) {
            // Handle the situation where the location permissions are granted
            onPermissionsGranted()
        } else {
            if (this.shouldShowEducationalUI()) {
                // Show educational UI in case it's needed
                onShowEducationalUI {
                    requestPermissionLauncher.launch(requiredPermissions)
                }
            }

            // Request the location permissions
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    /**
     * Handle the situation where the permissions are granted
     */
    private fun onPermissionsGranted() {
        Toast.makeText(context, "Permissions granted!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Display the educational UI to the user
     */
     private fun onShowEducationalUI(onAgree: () -> Unit) {
        AlertDialog
            .Builder(context)
            .setTitle("We need the location permission")
            .setMessage("Please provide the application with the location permissions. It is very important to make your experience better.")
            .setPositiveButton("I agree") { _, _ ->
                onAgree()
            }.show()
    }

    /**
     * Handle the situation where the location permissions are denied
     */
    private fun onPermissionsDenied() {
        Toast.makeText(context, "Permissions denied! Gracefully degrading the app's experience....", Toast.LENGTH_SHORT).show()
    }

    /**
     * Handle the request permission launcher's initialization
     */
    fun onRequestPermissionLaunched(allGranted: Boolean) {
        if (allGranted) {
            this.onPermissionsGranted()
        } else {
            this.onPermissionsDenied()
        }
    }
}
package com.weather.weatherapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.weather.weatherapp.R
import com.weather.weatherapp.presentation.screens.SplashScreen
import com.weather.weatherapp.presentation.ui.theme.WeatherAppTheme
import com.weather.weatherapp.utility.Utils
import java.util.Locale

@SuppressLint("CustomSplashScreen")
class CustomSplashActivity : ComponentActivity() {

    private var locality: String? = null
    private var locationState: Location? = null

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fineLocationGranted && coarseLocationGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                SplashScreen()
                if (Utils.getInstance(this).isOnline(this)) {
                    getCurrentLocation()
                } else {
                    Utils.getInstance(this).DialogPop(getString(R.string.error),getString(R.string.no_internet)) {
                        finish()
                    }
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Try to get last known location first
        fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
            if (lastLocation != null) {
                updateLocation(lastLocation)
            } else {
                // If no last known location, fall back to getCurrentLocation()
                fetchFreshLocation(fusedLocationClient)
            }
        }.addOnFailureListener {
            fetchFreshLocation(fusedLocationClient) // Ensure fresh location is attempted
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchFreshLocation(fusedLocationClient: FusedLocationProviderClient) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener { currentLocation ->
            if (currentLocation != null) {
                updateLocation(currentLocation)
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch location please restart your application", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLocation(location: Location) {
        locality = getAreaName(location.latitude, location.longitude)
        locationState = location

        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("locality", locality)
            putExtra("locationState", locationState)
        })
    }

    private fun getAreaName(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return (addresses?.firstOrNull()?.subLocality + ", " + addresses?.firstOrNull()?.locality)
    }
}


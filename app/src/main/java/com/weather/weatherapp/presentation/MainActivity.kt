package com.weather.weatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherState
import com.weather.weatherapp.presentation.screens.HomeScreen
import com.weather.weatherapp.presentation.screens.NavigationItem
import com.weather.weatherapp.presentation.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var response: WeatherResponseApi? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locality: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation {
            setContent {
                FetchApiData(it)
            }
        }
    }

    private fun getLastLocation(onclick: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onclick(location)
                    locality = getAreaName(location.latitude, location.longitude)
                }
            }.addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
        }
    }

    private fun getAreaName(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) addresses[0].locality else null
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    fun FetchApiData(location: Location) {
        val viewModel: WeatherViewModel = hiltViewModel()
        val weatherState by viewModel.weatherState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.getWeatherResponse(location.latitude, location.longitude)
        }

        when (weatherState) {
            is WeatherState.Error -> {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

            WeatherState.Loading -> {
                Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
            }

            is WeatherState.Success -> {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                response = (weatherState as WeatherState.Success).data
                NavController()
            }
        }
    }

    @Composable
    fun NavController() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = NavigationItem.Home.route) {
            composable(NavigationItem.Home.route) {
                response?.let { it1 ->
                    HomeScreen(weatherResponseApi = it1, locality = locality!!) {

                    }
                }
            }
            composable(NavigationItem.Splash.route) {
                // SplashScreen()
            }
        }
    }
}



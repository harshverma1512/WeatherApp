package com.weather.weatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherState
import com.weather.weatherapp.presentation.screens.HomeScreen
import com.weather.weatherapp.presentation.screens.NavigationItem
import com.weather.weatherapp.presentation.screens.Screens
import com.weather.weatherapp.presentation.screens.SplashScreen
import com.weather.weatherapp.presentation.theme.WeatherAppTheme
import com.weather.weatherapp.presentation.viewModel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var response: WeatherResponseApi? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locality: String? = null
    private lateinit var navController: NavHostController
    private val isLoading = mutableStateOf(true)
    private var locationState: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContent {
            getCurrentLocation { location ->
                locationState = location
                isLoading.value = false
            }
            NavController()
            if (isLoading.value) {
                SplashScreen() // Show splash screen while loading
            } else {
                locationState?.let {
                    FetchApiData(it) // Call FetchApiData with valid location
                } ?: run {
                    DialogPop() // Show an error dialog if location is not fetched
                }
            }
        }
    }

    private fun getCurrentLocation(onClick: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Request the current location
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null // CancellationToken (optional)
            ).addOnSuccessListener { currentLocation ->
                if (currentLocation != null) {
                    onClick(currentLocation)
                    locality = getAreaName(currentLocation.latitude, currentLocation.longitude)
                } else {
                    onClick(null)
                    Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                onClick(null)
                Toast.makeText(this, "Failed to fetch location", Toast.LENGTH_SHORT).show()
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

    @Composable
    fun DialogPop() {
        val openDialog = remember {
            mutableStateOf(true)
        }

        if (openDialog.value) {
            AlertDialog(onDismissRequest = { },
                confirmButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                        finish()
                    }) {
                        Text(text = "OK")
                    }
                },
                title = { Text(text = "Error") },
                text = { Text(text = "Please check whether your internet connection or GPS is enabled.") })

        }
    }


    private fun getAreaName(latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) addresses[0].subLocality else null
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    fun FetchApiData(location: Location) {
        val viewModel: WeatherViewModel = hiltViewModel()
        val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.getWeatherResponse(location.latitude, location.longitude) }

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
                navController.navigate(NavigationItem.Home.route)
            }
        }
    }

    @Composable
    fun NavController() {
        navController = rememberNavController()
        NavHost(
            navController = navController, startDestination = NavigationItem.SplashScreen.route
        ) {
            composable(NavigationItem.Home.route) {
                locality?.let { locality ->
                    response?.let { response ->
                        HomeScreen(weatherResponseApi = response,
                            locality = locality,
                            navigation = { route ->
                                navController.navigate(route)
                            })
                    }
                }
            }
            composable(NavigationItem.SplashScreen.route) {
                SplashScreen()
            }
        }
    }
}


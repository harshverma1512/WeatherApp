package com.weather.weatherapp.presentation

import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.isPopupLayout
import androidx.core.os.BundleCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherState
import com.weather.weatherapp.presentation.screens.HomeScreen
import com.weather.weatherapp.presentation.screens.NavigationItem
import com.weather.weatherapp.presentation.screens.Screens
import com.weather.weatherapp.presentation.screens.SearchScreen
import com.weather.weatherapp.presentation.screens.SplashScreen
import com.weather.weatherapp.presentation.screens.WeeklyForCaste
import com.weather.weatherapp.presentation.viewModel.WeatherViewModel
import com.weather.weatherapp.utility.Utils
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var response: WeatherResponseApi? = null
    private var navController: NavHostController? = null
    private var locality: String? = null
    private var locationState: Location? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.extras != null) {
            locality = intent.getStringExtra("locality")
            locationState = intent?.extras?.let {
                BundleCompat.getParcelable(it, "locationState", Location::class.java)
            }

        }
        setContent {
            locationState?.let {
                FetchApiData(it) // Call FetchApiData with valid location
                Scaffold(bottomBar = {
                }) { innerPadding ->
                    NavigationGraph(modifier = Modifier.padding(innerPadding))
                }

            } ?: run {
                Utils.getInstance(this).DialogPop(getString(R.string.error), getString(R.string.some_thing_went_wrong)) {
                    finish()
                }
            }
        }
    }


    @Composable
    fun FetchApiData(location: Location) {
        val viewModel: WeatherViewModel = hiltViewModel()
        val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()

        LaunchedEffect(location) { viewModel.getWeatherResponse(location.latitude, location.longitude) }

        when (weatherState) {
            is WeatherState.Error -> {
                Timber.e("MainActivity", "Error: ${(weatherState as WeatherState.Error).message}")
            }

            WeatherState.Loading -> {
                Timber.tag("MainActivity").d("Loading")
            }

            is WeatherState.Success -> {
                Timber.d("MainActivity", "Success: ${(weatherState as WeatherState.Success).data}")
                response = (weatherState as WeatherState.Success).data
                response?.let { viewModel.setWeatherResponse(it) }
                navController?.navigate(NavigationItem.Home.route)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun NavigationGraph(modifier: Modifier = Modifier) {
        navController = rememberNavController()
        NavHost(
            navController = navController ?: rememberNavController(),
            startDestination = NavigationItem.Home.route,
            modifier = modifier
        ) {
            composable(NavigationItem.Home.route) {
                locality?.let { locality ->
                    response?.let { response ->
                        HomeScreen(weatherResponseApi = response,
                            locality = locality,
                            navigation = { route ->
                                navController?.navigate(route)
                            })
                    }
                }
            }
            composable(NavigationItem.SplashScreen.route) {
                SplashScreen()
            }
            composable(NavigationItem.Weekly.route) {
                WeeklyForCaste(
                    locality = locality,
                    navigation = { route ->
                        if (route == Screens.Back.name) {
                            navController?.popBackStack()
                        } else {
                            navController?.navigate(route)
                        }
                    })
            }
            composable(NavigationItem.Search.route) {
                SearchScreen{
                    if (it == Screens.Back.name) {
                        navController?.popBackStack()
                    } else {
                        navController?.navigate(it)
                    }
                }
            }
        }
    }
}

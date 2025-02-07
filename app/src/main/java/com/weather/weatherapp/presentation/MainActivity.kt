package com.weather.weatherapp.presentation

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherState
import com.weather.weatherapp.presentation.screens.BottomNavigationRedirection
import com.weather.weatherapp.presentation.screens.HomeScreen
import com.weather.weatherapp.presentation.screens.NavigationItem
import com.weather.weatherapp.presentation.screens.SearchScreen
import com.weather.weatherapp.presentation.screens.SplashScreen
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

        if (intent?.extras != null){
           locality = intent.getStringExtra("locality")
           locationState = intent.getParcelableExtra("locationState")
       }
        setContent {
                locationState?.let {
                    FetchApiData(it) // Call FetchApiData with valid location
                    Scaffold(bottomBar = { navController?.let {
                        BottomNavigationBar(navigationController = it) } }) { innerPadding ->
                        NavController(modifier = Modifier.padding(innerPadding))
                    }

                } ?: run {
                    Utils.getInstance(this).DialogPop {
                        finish()
                    } // Show an error dialog if location is not fetched
                }
            }
        }


    @Composable
    fun FetchApiData(location: Location) {
        val viewModel: WeatherViewModel = hiltViewModel()
        val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.getWeatherResponse(location.latitude, location.longitude) }

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
                navController?.navigate(NavigationItem.Home.route)
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navigationController: NavController) {
        val list = listOf(BottomNavigationRedirection.Home, BottomNavigationRedirection.Search)

        BottomNavigation(
            contentColor = Color.White,
            backgroundColor = colorResource(id = R.color.app_color),
            modifier = Modifier.navigationBarsPadding()
        ) {
            val currentRoute by navigationController.currentBackStackEntryAsState()
            val currentDestination = currentRoute?.destination?.route

            list.forEach { item ->
                BottomNavigationItem(
                    selected = item.route == currentDestination,
                    onClick = { navigationController.navigate(item.route) },
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(text = item.title) },
                    alwaysShowLabel = true
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun NavController(modifier: Modifier) {
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
            composable(NavigationItem.Search.route) {
                SearchScreen()
            }
        }
    }
}


package com.weather.weatherapp.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector


enum class Screens {
    Splash,HomeScreen,Search
}

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screens.HomeScreen.name)
    data object SplashScreen : NavigationItem(Screens.Splash.name)
    data object Search : NavigationItem(Screens.Search.name)
}


sealed class BottomNavigationRedirection(val route: String ,  val title: String , val icon: ImageVector){
    data object Home : BottomNavigationRedirection(Screens.HomeScreen.name , "Home" , Icons.Default.Home)
    data object Search : BottomNavigationRedirection(Screens.Search.name , "Search" , Icons.Default.Search)
}
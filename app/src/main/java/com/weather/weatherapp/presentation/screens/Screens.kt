package com.weather.weatherapp.presentation.screens


enum class Screens {
    Splash,HomeScreen,Search
}

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screens.HomeScreen.name)
    data object SplashScreen : NavigationItem(Screens.Splash.name)
    data object SearchScreen : NavigationItem(Screens.Splash.name)
}
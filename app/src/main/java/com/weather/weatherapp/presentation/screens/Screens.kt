package com.weather.weatherapp.presentation.screens


enum class Screens {
    Splash,HomeScreen
}

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screens.HomeScreen.name)
    data object SplashScreen : NavigationItem(Screens.Splash.name)
}
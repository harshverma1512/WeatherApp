package com.weather.weatherapp.presentation

import android.location.Geocoder
import androidx.activity.ComponentActivity
import java.util.Locale

class SearchActivity : ComponentActivity() {

    private val geocoder = Geocoder(this, Locale.getDefault())


}
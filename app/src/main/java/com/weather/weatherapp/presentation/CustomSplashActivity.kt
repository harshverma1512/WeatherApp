package com.weather.weatherapp.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.weather.weatherapp.presentation.screens.SplashScreen
import com.weather.weatherapp.presentation.ui.theme.WeatherAppTheme
import com.weather.weatherapp.utility.Utils

@SuppressLint("CustomSplashScreen")
class CustomSplashActivity : ComponentActivity() {

    private var locality: String? = null
    private var locationState: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            WeatherAppTheme {
                SplashScreen()
                if (Utils.getInstance(this).isOnline(this)) {
                    Utils.getInstance(this).getCurrentLocation { location, locality ->
                        if (locality != null && location != null){
                            locationState = location
                            this.locality = locality
                            startActivity(Intent(this, MainActivity::class.java).apply {
                                putExtra("locality", locality)
                                putExtra("locationState", location)
                            })
                        }
                    }
                } else {
                    Utils.getInstance(this).DialogPop {
                        finish()
                    }
                }
            }
        }
    }
}


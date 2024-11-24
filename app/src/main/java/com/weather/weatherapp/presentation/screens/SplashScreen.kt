package com.weather.weatherapp.presentation.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.weatherapp.R

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {

    Box(modifier.fillMaxSize()){
        Image(painter = painterResource(id = R.drawable.splashscreenbackground),
            contentDescription = "SplashScreenBackground", modifier.matchParentSize())
        Column(modifier.padding(20.dp).fillMaxSize(),verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Never get caught in the rain again",
                style = TextStyle(color = Color.DarkGray, fontSize = 40.sp, fontWeight = Bold)
            )
            Text(
                text = "Stay ahead of the weather with our weather forecast",
                modifier.padding(top = 10.dp),
                style = TextStyle(color = Color.DarkGray, fontSize = 18.sp)
            )
            Spacer(modifier = modifier.padding(bottom = 60.dp))
        }
    }
}

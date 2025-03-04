package com.weather.weatherapp.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.presentation.ui.theme.backgroundView
import com.weather.weatherapp.utility.Utils


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherResponseApi: WeatherResponseApi,
    locality: String,
    navigation: (String) -> Unit,
) {
    val dayNight = Utils.getInstance(LocalContext.current).getDayOrNight()
    ChangeStatusBarColor()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(color = backgroundView), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background), // Replace with actual image
                contentDescription = "Weather Background",
                modifier = modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = modifier.height(20.dp))
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = locality,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cloud Icon",
                        tint = Color.White,
                        modifier = modifier.size(24.dp)
                    )
                }

                Row(
                    modifier
                        .padding(top = 60.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = weatherResponseApi.current?.temperature2m.toString() + "°",
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )

                    Image(
                        painter = if (dayNight == "Day") painterResource(id = R.drawable.cloud_sun) else painterResource(
                            id = R.drawable.moon
                        ),
                        contentDescription = "Sun",
                        modifier = modifier.size(90.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }

                Text(
                    text = Utils.getInstance(LocalContext.current).getFormattedDate(),
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = modifier
                        .padding(top = 150.dp)
                        .align(Alignment.Start)
                )
            }
        }
        WeeklyCard(weatherResponseApi.hourly)
        WeatherInfoStatus(modifier, weatherResponseApi)
    }
}

@Composable
fun ChangeStatusBarColor() {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color(0xFF704bd2) // Set your desired color

    // Apply status bar color
    SideEffect {
        systemUiController.setStatusBarColor(color = statusBarColor)
    }
}

@Composable
private fun Statics(
    image: Painter,
    staticsTitle: String,
    staticsValue: String,
    duration: String = "",
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = colorResource(id = R.color.statistics))
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .width(170.dp)
        ) {
            Image(
                painter = image, contentDescription = "", modifier = Modifier.size(28.dp)
            )
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .padding(5.dp)
            ) {
                Text(text = staticsTitle, color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(top = 4.dp))
                Text(text = staticsValue, color = Color.Black, fontSize = 16.sp)
            }
            Text(text = duration, Modifier.align(Alignment.Bottom), color = Color.Black)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeatherInfoStatus(
    modifier: Modifier = Modifier,
    weatherResponseApi: WeatherResponseApi,
) {
    val item = Utils.getInstance(context = LocalContext.current)
        .calculateTempAccordingHour(weatherResponseApi.hourly)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Statics(
                painterResource(id = R.drawable.wind),
                "Wind Speed",
                "${weatherResponseApi.current?.windSpeed10m.toString()} km/h"
            )
            Statics(
                painterResource(id = R.drawable.uv_index),
                "UV Index",
                weatherResponseApi.hourly?.uvIndex?.get(0).toString()
            )
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Statics(
                painterResource(id = R.drawable.rain),
                "Rain chance",
                "${weatherResponseApi.daily.rain_sum[0]} %"
            )
            Statics(painterResource(id = R.drawable.humidity), "Humidity", "${item[0].humidity} %")

        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Statics(
                painterResource(id = R.drawable.sunset), "Sunset", "6:30 PM", "in 4h"
            )
            Statics(
                painterResource(id = R.drawable.sunrise), "Sunrise", "5:00 AM", "in 7h ago"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Stable
private fun WeeklyCard(hourly: WeatherResponseApi.Hourly?) {
    val item = Utils.getInstance(context = LocalContext.current).calculateTempAccordingHour(hourly)

    val daySelection = remember {
        mutableStateOf("Today")
    }
    val isWeeklySelected = remember {
        mutableStateOf(false)
    }
    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = {
                    daySelection.value = "Today"
                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (daySelection.value == "Today") colorResource(id = R.color.app_color) else colorResource(
                        id = R.color.white
                    ), contentColor = colorResource(id = R.color.black)
                )
            ) {
                Text(text = "Today")
            }

            Button(
                onClick = {
                    daySelection.value = "Tomorrow"
                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (daySelection.value == "Tomorrow") colorResource(id = R.color.app_color) else colorResource(
                        id = R.color.white
                    ), contentColor = colorResource(id = R.color.black)
                )
            ) {
                Text(text = "Tomorrow")
            }

            Button(
                onClick = {
                    isWeeklySelected.value = true
                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (daySelection.value == "7 days") colorResource(id = R.color.app_color) else colorResource(
                        id = R.color.white
                    ), contentColor = colorResource(id = R.color.black)
                )
            ) {
                Text(text = "7 days")
            }

        }

        if (isWeeklySelected.value) {
            WeeklyForCaste()
        }
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = colorResource(id = R.color.statistics)
            ), modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hour),
                    contentDescription = "Hourly Forecast Image",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = "Hourly Forecast", color = colorResource(id = R.color.black))
            }
            LazyRow {
                items(item.filter { daySelection.value == it.day }) { item ->
                    Column(
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = item.time, color = colorResource(id = R.color.black))
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        Image(
                            painter = if (Utils.getInstance(LocalContext.current)
                                    .getTimeOfDay(item.time) == "Morning"
                            ) painterResource(id = R.drawable.sun) else painterResource(
                                id = R.drawable.moon
                            ), modifier = Modifier.size(30.dp), contentDescription = ""
                        )
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Text(
                            text = item.temperature2m.toString() + "°",
                            color = colorResource(id = R.color.black),
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

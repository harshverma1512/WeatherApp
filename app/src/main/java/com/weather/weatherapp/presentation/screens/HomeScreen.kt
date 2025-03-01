package com.weather.weatherapp.presentation.screens

import android.icu.text.SimpleDateFormat
import android.media.Image
import android.os.Build
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.HourlyTemp
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.presentation.ui.theme.backgroundView
import com.weather.weatherapp.utility.Utils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherResponseApi: WeatherResponseApi,
    locality: String,
    navigation: (String) -> Unit,
) {
    val dayNight = Utils.getInstance(LocalContext.current).getDayOrNight()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
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
                    text = "January 18, 16:14",
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
private fun WeatherInfoStatus(
    modifier: Modifier = Modifier,
    weatherResponseApi: WeatherResponseApi,
) {
    Row(
        modifier = modifier
            .padding(20.dp)
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = modifier.padding(3.dp))

        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = colorResource(id = R.color.app_color)
            ), modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 20.dp)
                .wrapContentWidth()
        ) {
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.humidity),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 3.dp)
                        .size(20.dp)
                )
                Text(
                    text = "90%",
                    color = colorResource(id = R.color.white),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 18.sp
                )
                Spacer(modifier = modifier.padding(3.dp))
                Image(
                    painter = painterResource(id = R.drawable.wind),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 3.dp)
                        .size(20.dp)
                )
                Text(
                    text = " ${weatherResponseApi.current?.windSpeed10m.toString()} km/h",
                    color = colorResource(id = R.color.white),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun OpenSearch(modifier: Modifier = Modifier, navigation: @Composable () -> Unit) {
    Card(
        onClick = {
            navigation
        },
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(colorResource(id = R.color.app_color)),
        modifier = modifier
    ) {
        Row(
            modifier = modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.flight),
                contentDescription = "Search Module"
            )
            Spacer(modifier = modifier.size(10.dp))
            Text(text = "Want to check other location", color = Color.White)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeeklyCard(hourly: WeatherResponseApi.Hourly?) {
    val item = Utils.getInstance(context = LocalContext.current).calculateTempAccordingHour(hourly)

    val daySelection = remember {
        mutableStateOf("Today")
    }
    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
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
                    daySelection.value = "7 days"
                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (daySelection.value == "7 days") colorResource(id = R.color.app_color) else colorResource(
                        id = R.color.white
                    ), contentColor = colorResource(id = R.color.black)
                )
            ) {
                Text(text = "7 days")
            }

        }

        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = colorResource(id = R.color.app_color)
            ), modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 20.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hour),
                    contentDescription = "Hourly Forecast Image",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(3.dp))
                Text(text = "Hourly Forecast", color = colorResource(id = R.color.white))
            }
            LazyRow {
                items(item.filter { daySelection.value == it.day }) { item ->
                    Column(
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = item.time, color = colorResource(id = R.color.white))
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        Image(
                            painter = if (getTimeOfDay(item.time) == "Morning") painterResource(id = R.drawable.sun) else painterResource(
                                id = R.drawable.moon
                            ), modifier = Modifier.size(30.dp), contentDescription = ""
                        )
                        Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(
                            text = item.temperature2m.toString() + "°",
                            color = colorResource(id = R.color.white),
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet(modifier: Modifier = Modifier) {
    val bottomSheetHeight = 200.dp
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
}

fun getTimeOfDay(time: String): String {
    return when {
        time.contains("AM", ignoreCase = true) -> {
            val hour = time.substringBefore("AM").trim().toIntOrNull()
            if (hour == 12 || hour in 1..4) "Night"
            else if (hour in 5..11) "Morning"
            else "Invalid time"
        }

        time.contains("PM", ignoreCase = true) -> {
            val hour = time.substringBefore("PM").trim().toIntOrNull()
            if (hour == 12) "Morning"
            else if (hour in 1..5) "Morning"
            else if (hour in 6..11) "Night"
            else "Invalid time"
        }

        else -> "Invalid time format"
    }
}

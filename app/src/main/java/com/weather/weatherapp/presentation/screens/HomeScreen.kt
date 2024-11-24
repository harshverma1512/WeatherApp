package com.weather.weatherapp.presentation.screens

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.HourlyTemp
import com.weather.weatherapp.data.dto.WeatherResponseApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.Vector

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherResponseApi: WeatherResponseApi,
    locality: String,
    navigation: () -> Unit,
) {
    val dayNight = getDayOrNight()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF304FFE), Color(0xFF1C1B75))))
            .padding(top = 60.dp, start = 10.dp, end = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = locality,
            color = colorResource(id = R.color.white),
            modifier = modifier,
            style = MaterialTheme.typography.headlineLarge
        )
        Row(
            modifier = modifier
                .padding(top = 10.dp)
                .wrapContentSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.currentlocation),
                contentDescription = "current location icon",
                modifier = modifier.size(24.dp)
            )
            Spacer(modifier = modifier.padding(3.dp))
            Text(
                text = "Current Location",
                color = colorResource(id = R.color.white),
                modifier = modifier,
                fontSize = 12.sp
            )
        }

        Row(
            modifier = modifier
                .padding(20.dp)
                .wrapContentSize(), verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (dayNight == "Day") painterResource(id = R.drawable.cloud_sun) else painterResource(id = R.drawable.moon),
                contentDescription = "Sun",
                modifier = modifier.size(90.dp),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = modifier.padding(3.dp))
            Text(
                text = weatherResponseApi.current?.temperature2m.toString() + "°",
                color = colorResource(id = R.color.white),
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 90.sp
            )
        }

        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = colorResource(id = R.color.app_color)
            ),
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 20.dp)
                .wrapContentWidth()
        ) {
            Row(
                modifier = modifier.padding(8.dp)
                    .wrapContentSize(), verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(id = R.drawable.humidity), contentDescription = "" , modifier = Modifier.padding(end = 3.dp).size(20.dp))
                Text(
                    text = "90%",
                    color = colorResource(id = R.color.white),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 18.sp
                )
                Spacer(modifier = modifier.padding(3.dp))
                Image(painter = painterResource(id = R.drawable.wind), contentDescription = "" , modifier = Modifier.padding(end = 3.dp).size(20.dp))
                Text(
                    text = " ${weatherResponseApi.current?.windSpeed10m.toString()} km/h",
                    color = colorResource(id = R.color.white),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 18.sp
                )
            }
        }
        WeeklyCard(weatherResponseApi.hourly)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeeklyCard(hourly: WeatherResponseApi.Hourly?) {
    val item = calculateTempAccordingHour(hourly)

    val daySelection = remember {
        mutableStateOf("Today")
    }
    Column {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    daySelection.value = "Today"
                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (daySelection.value == "Today") colorResource(id = R.color.app_color) else colorResource(
                        id = R.color.background_color
                    ),
                    contentColor = colorResource(id = R.color.white)
                )
            ) {
                Text(text = "Today")
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Button(
                onClick = {
                    daySelection.value = "Tomorrow"
                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = if (daySelection.value == "Tomorrow") colorResource(id = R.color.app_color) else colorResource(
                        id = R.color.background_color
                    ),
                    contentColor = colorResource(id = R.color.white)
                )
            ) {
                Text(text = "Tomorrow")
            }
        }

        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = colorResource(id = R.color.app_color)
            ),
            modifier = Modifier
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
                            painter = if (getTimeOfDay(item.time) == "Morning") painterResource(id = R.drawable.sun) else painterResource(id = R.drawable.moon),
                            modifier = Modifier.size(30.dp),
                            contentDescription = ""
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


@RequiresApi(Build.VERSION_CODES.O)
private fun calculateTempAccordingHour(hourly: WeatherResponseApi.Hourly?): List<HourlyTemp> {
    val list = mutableListOf<HourlyTemp>()
    val currentDateTime = LocalDateTime.now()
    val currentDate = currentDateTime.toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    for (i in 0..<hourly?.time?.size!!) {

        val dateTimeStr = hourly.time[i]
        val tempDateTime = LocalDateTime.parse(dateTimeStr, formatter)
        val tempDate = tempDateTime.toLocalDate()
        val time = checkHour(hourly.time[i]!!)

        val day = when {
            tempDate.isEqual(currentDate) -> "Today"
            tempDate.isEqual(currentDate.plusDays(1)) -> "Tomorrow"
            else -> tempDate.toString()
        }

        if (time.isNotEmpty()) {
            list.add(
                HourlyTemp(
                    day = day,
                    time = time,
                    temperature2m = hourly.temperature2m?.get(i)!!,
                    humidity = hourly.relativeHumidity2m?.get(i)!!
                )
            )
        }
    }
    return list

}

fun getDayOrNight(): String {
    val currentTime = Calendar.getInstance()
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)

    return if (hour in 5..18) {
        "Day"
    } else {
        "Night"
    }
}

fun checkHour(inputTime: String): String {
    // Parse the input time
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    val date = inputFormat.parse(inputTime)
    val currentCalendar = Calendar.getInstance()

    // Set up a calendar for the input date
    val inputCalendar = Calendar.getInstance()
    inputCalendar.time = date

    // Compare current time with input time
    return if (currentCalendar.after(inputCalendar)) {
        "" // Return empty if the current time is greater
    } else {
        // Format the input time to 12-hour clock with AM/PM
        val hour = inputCalendar.get(Calendar.HOUR)
        val isAM = inputCalendar.get(Calendar.AM_PM) == Calendar.AM
        "${if (hour == 0) 12 else hour} ${if (isAM) "AM" else "PM"}"
    }
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

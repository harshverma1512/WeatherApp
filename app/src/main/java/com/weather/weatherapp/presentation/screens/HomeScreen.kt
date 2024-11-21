package com.weather.weatherapp.presentation.screens

import android.os.Build
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
import androidx.compose.foundation.lazy.LazyRow
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, weatherResponseApi: WeatherResponseApi, locality : String, navigation: () -> Unit){

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF304FFE), Color(0xFF1C1B75))))
            .padding(top = 40.dp, start = 10.dp, end = 10.dp),
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
                painter = painterResource(id = R.drawable.sun),
                contentDescription = "Sun",
                modifier = modifier.size(90.dp),
                contentScale = ContentScale.FillBounds
            )
            Spacer(modifier = modifier.padding(3.dp))
            Text(
                text = weatherResponseApi.current?.temperature2m.toString() +  "°",
                color = colorResource(id = R.color.white),
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 90.sp
            )
        }
        Row(
            modifier = modifier
                .wrapContentSize(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hum: 26°",
                color = colorResource(id = R.color.white),
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 18.sp
            )
            Spacer(modifier = modifier.padding(3.dp))
            Text(
                text = "Wind: ${weatherResponseApi.current?.windSpeed10m.toString()} km/h",
                color = colorResource(id = R.color.white),
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 18.sp
            )
        }
        WeeklyCard(weatherResponseApi.hourly)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeeklyCard(hourly: WeatherResponseApi.Hourly?) {
    val item = calculateTempAccordingHour(hourly)
    val colorState by remember {
        mutableStateOf(false)
    }

    Column {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {

                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = colorResource(id = R.color.app_color),
                    contentColor = colorResource(id = R.color.white)
                )
            ) {
                Text(text = "Today")
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Button(
                onClick = {

                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = colorResource(id = R.color.app_color),
                    contentColor = colorResource(id = R.color.white)
                )
            ) {
                Text(text = "Tomorrow")
            }
        }

        LazyRow {
            items(item.size) {
                Card(
                    colors = CardDefaults.cardColors().copy(
                        containerColor = colorResource(id = R.color.app_color)
                    ), modifier = Modifier.padding(start = 10.dp , top = 20.dp), shape = RoundedCornerShape(50.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Now", color = colorResource(id = R.color.white))
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        Image(
                            painter = painterResource(id = R.drawable.sun),
                            modifier = Modifier.size(30.dp),
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.padding(top = 10.dp))
                        Text(
                            text = item[it].temperature2m.toString() + "°",
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

    for (i in 0..hourly?.time?.size!!) {

        val dateTimeStr = hourly.time[i]

        val tempDateTime = LocalDateTime.parse(dateTimeStr, formatter)
        val tempDate = tempDateTime.toLocalDate()

        val day = when {
            tempDate.isEqual(currentDate) -> "Today"
            tempDate.isEqual(currentDate.plusDays(1)) -> "tomorrow"
            else -> tempDate.toString()
        }

        list.add(HourlyTemp(day = day, time = "", temperature2m = hourly.temperature2m?.get(i)!!))
    }
    return list

}
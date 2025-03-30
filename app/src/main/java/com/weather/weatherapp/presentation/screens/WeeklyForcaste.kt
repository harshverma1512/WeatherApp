package com.weather.weatherapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.presentation.viewModel.WeatherViewModel
import com.weather.weatherapp.utility.Utils
@Composable
fun WeeklyForCaste(
    modifier: Modifier = Modifier,
    locality: String?,
    navigation: (String) -> Unit
) {
    ChangeStatusBarColor(0xD0BCFF)
    // Ensure that locality and weatherResponseApi are not triggering recompositions
    val rememberedLocality = remember(locality) { locality }
    val viewModel: WeatherViewModel = hiltViewModel()
    val rememberedWeather = remember { viewModel.getWeatherResponse() }

    Column(modifier = modifier.fillMaxSize()) {
        rememberedLocality?.let {
            Header(locality = it, weatherResponseApi = rememberedWeather, navigation = navigation)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.background_color))
        ) {
            items(7){
                ForecastItem(daily = rememberedWeather?.daily, position = it)
            }
        }
    }
}

@Composable
fun ForecastItem(daily: WeatherResponseApi.Daily?, position: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(20.dp)
            .background(
                color = colorResource(id = R.color.statistics),
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(fraction = 0.7F)
                ) {
                    Text(
                        text = daily?.time?.get(position).toString(), fontWeight = FontWeight.Medium, fontSize = 14.sp
                    )
                    Text(text = daily?.temperature_2m_max?.get(position).toString())
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(fraction = 0.7F)
                ) {
                    Text(
                        text = "Cloudy and Sunny",
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp
                    )
                    Text(text = daily?.rain_sum?.get(position).toString())
                }
            }

            Spacer(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
                    .background(color = colorResource(id = R.color.black))
            )

            Image(
                painter = painterResource(id = R.drawable.cloud_sun),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = painterResource(id = R.drawable.expand_more),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    locality: String,
    weatherResponseApi: WeatherResponseApi?,
    navigation: (String) -> Unit
) {
    val context = LocalContext.current
    val dayNight = remember { Utils.getInstance(context).getDayOrNight() }

    // Store only selected value in a state
    val daySelection = remember { mutableStateOf("7 days") }
    val days = listOf("Today", "Tomorrow", "7 days")

    // Avoid passing the whole object, extract only needed values
    val temperature = remember(weatherResponseApi) { weatherResponseApi?.current?.temperature2m ?: "--" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.statistics))
            .padding(20.dp)
            .wrapContentHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = locality,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp).clickable {
                    navigation(Screens.Search.name)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = "$temperature°", // Dynamic temperature
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "feels like 10",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .align(Alignment.Bottom)
                )
            }

            Image(
                painter = painterResource(id = if (dayNight == "Day") R.drawable.cloud_sun else R.drawable.moon),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.FillBounds
            )
        }

        if (daySelection.value == "Today"){
            navigation(Screens.Back.name)
        }else{

        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            itemsIndexed(days) { _, day ->
                DaySelectionButton(
                    text = day,
                    isSelected = daySelection.value == day,
                    onClick = { daySelection.value = day }
                )
            }
        }
    }
}


@Composable
fun DaySelectionButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) colorResource(id = R.color.purple_200)
            else colorResource(id = R.color.white),
            contentColor = colorResource(id = R.color.black)
        )
    ) {
        Text(text = text)
    }
}

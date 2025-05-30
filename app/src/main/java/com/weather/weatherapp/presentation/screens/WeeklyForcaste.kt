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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherState
import com.weather.weatherapp.presentation.viewModel.WeatherViewModel
import com.weather.weatherapp.utility.Utils
@Composable
fun WeeklyForCaste(
    modifier: Modifier = Modifier,
    locality: String?,
    navigation: NavHostController?
) {
    val viewModel: WeatherViewModel = hiltViewModel()
    ChangeStatusBarColor(0xFF704bd2)
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()

    val weatherResponse = if (weatherState is WeatherState.Success) {
        (weatherState as WeatherState.Success).data
    } else null

    Column(modifier = modifier.fillMaxSize()) {
        locality?.let {
            if (navigation != null) {
                Header(locality = it, weatherResponseApi = weatherResponse, navigation = navigation)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.background_color))
        ) {
            items(7) { index ->
                ForecastItem(daily = weatherResponse?.daily, position = index)
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
                color = colorResource(id = R.color.statistics), shape = RoundedCornerShape(15.dp)
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
                    Text(text = daily?.temperature_2m_max?.get(position).toString().plus("°C"))
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
    navigation: NavHostController
) {
    val context = LocalContext.current
    val dayNight = remember { Utils.getInstance(context).getDayOrNight() }

    val daySelection = remember { mutableStateOf("7 days") }
    val days = listOf("Today", "Tomorrow", "7 days")

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
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        navigation.navigate(Screens.Search.name)
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
                    text = "$temperature°C",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "feels like 35°C",
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
                    onClick = { daySelection.value = day
                        if (day == "Today" || day == "Tomorrow") {
                            navigation.previousBackStackEntry?.savedStateHandle?.set("day", day)
                            navigation.popBackStack()
                        }
                    }
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

package com.weather.weatherapp.presentation.screens

import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries,UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.weather.weatherapp.R
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherState
import com.weather.weatherapp.presentation.viewModel.WeatherViewModel
import com.weather.weatherapp.utility.Utils
import java.util.Locale

@Composable
fun SearchScreen(modifier: Modifier = Modifier, navigation: (String) -> Unit) {
    val context = LocalContext.current
    val viewModel: WeatherViewModel = hiltViewModel()
    val latLong = viewModel.latLong.collectAsState()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val weatherResponseList : MutableList<WeatherResponseApi> = emptyList<WeatherResponseApi>().toMutableList()
    viewModel.getWeatherResponse()?.let { weatherResponseList.add(it) }
    var searchKeyWord = ""
    var isGifVisible by remember { mutableStateOf(false) }

    if (isGifVisible) {
        GifLoader()
    }

    if (latLong.value != null){
        LaunchedEffect(latLong) {
            viewModel.getWeatherResponse(latLong.value?.first!!, latLong.value?.second!!)
        }
    }


    when (weatherState) {
        is WeatherState.Success -> {
            val weatherResponse = (weatherState as WeatherState.Success).data
            viewModel.setWeatherResponse(weatherResponse)
            isGifVisible = false
        }

        is WeatherState.Error -> {
            isGifVisible = false
            Utils.getInstance(LocalContext.current)
                .DialogPop("", LocalContext.current.getString(R.string.some_thing_went_wrong)) {
                    navigation.invoke(Screens.Back.name)
                }
        }

        WeatherState.Loading -> {
            isGifVisible = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.background_color))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.ArrowBack,
                contentDescription = "",
                modifier = modifier.clickable {
                    navigation.invoke(Screens.Back.name)
                })
            Text(
                text = "Weather",
                modifier = Modifier.padding(start = 10.dp),
                fontWeight = Bold,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        SimpleSearchBar { query ->
            isGifVisible = true
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(query, 1)
            if (!addresses.isNullOrEmpty()) {
                 searchKeyWord = query
                val address = addresses[0]
                viewModel.setLatLong(address.latitude, address.longitude)
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 10.dp)
        ) {
            items(weatherResponseList.size) { index ->
                SearchItems(weatherResponseList[index], searchKeyWord)
            }
        }
    }
}

@Composable
fun SearchItems(weatherResponseApi: WeatherResponseApi, searchKeyWord: String) {
    val context = LocalContext.current
    val dayNight = remember { Utils.getInstance(context).getDayOrNight() }

    Spacer(
        modifier = Modifier.height(16.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x668C6FD6), Color(0xFF704bd2)
                    )
                ), shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = weatherResponseApi.current?.temperature2m.toString() + "°", fontSize = 50.sp, fontWeight = Bold, color = Color.White)
            Row {
                Text(text = "H:${weatherResponseApi.hourlyUnits?.relativeHumidity2m}", fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "L:${weatherResponseApi.hourlyUnits?.windSpeed10m}", fontSize = 16.sp, color = Color.White)
            }
            Text(
                text = searchKeyWord.ifEmpty { "" },
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, end = 24.dp)
        ) {
            Image(
                painter = painterResource(id = if (dayNight == "Day") R.drawable.cloud_sun else R.drawable.moon),
                contentDescription = "",
                Modifier.size(80.dp)
            )
            Text(
                text = "Mostly Sunny",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun SimpleSearchBar( onSearch: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(value = searchQuery,
        onValueChange = {
            searchQuery = it
        },
        label = { Text("Search") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color(0xFF704bd2)
        ),
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search Icon")
        },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
        // Trigger onSearch when the user presses the search action on the keyboard
        keyboardActions = KeyboardActions(onSearch = { onSearch(searchQuery.text) }, onDone = {
            onSearch(searchQuery.text)
        })
    )
}

@Composable
fun GifLoader() {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://media.tenor.com/3f5dQ8GuQF8AAAAj/magnifying-glass-searching.gif")
            .crossfade(true)
            .allowHardware(false) // IMPORTANT for GIFs
            .build(),
        contentDescription = "Searching animation",
        modifier = Modifier.size(150.dp)
    )
}

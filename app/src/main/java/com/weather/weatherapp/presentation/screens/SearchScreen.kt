package com.weather.weatherapp.presentation.screens

import android.content.Context
import android.location.Geocoder
import android.util.Log
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
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.weatherapp.R
import java.util.Locale

@Composable

fun SearchScreen(modifier: Modifier = Modifier, navigation: (String) -> Unit) {
    val searchText by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
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
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(query, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
               Log.d("SearchScreen", "Latitude: ${address.latitude}, Longitude: ${address.longitude}")
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 10.dp)
        ) {
            items(3) {
                SearchItems()
            }
        }
    }
}

@Composable
fun SearchItems() {
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
            Text(text = "24" + "°", fontSize = 50.sp, fontWeight = Bold, color = Color.White)
            Row {
                Text(text = "H:12", fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "L:12", fontSize = 16.sp, color = Color.White)
            }
            Text(
                text = "Tokyo , japan",
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
                painter = painterResource(id = R.drawable.sun),
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
fun getLatLongFromLocation(context: Context, locationName: String): Pair<Double, Double>? {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocationName(locationName, 1)
    return if (!addresses.isNullOrEmpty()) {
        val address = addresses[0]
        Pair(address.latitude, address.longitude)
    } else {
        null // Location not found
    }
}


@Composable
fun SimpleSearchBar(onSearch: (String) -> Unit) {
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
            Icon(Icons.Default.Search, contentDescription = "Search Icon", modifier = Modifier.clickable { onSearch(searchQuery.text) })
        },
        // Trigger onSearch when the user presses the search action on the keyboard
        keyboardActions = KeyboardActions(
            onSearch = { onSearch(searchQuery.text) }
        )
    )
}
package com.weather.weatherapp.presentation.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weather.weatherapp.R
import com.weather.weatherapp.utility.Utils

@Composable
fun WeeklyForCaste(modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxSize()) {
        Header()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.background_color)),
        ) {
            items(5) {
                Box(
                    modifier = modifier
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
                        modifier = modifier.fillMaxWidth()
                    ) {
                        Column(modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = modifier
                                    .padding()
                                    .fillMaxWidth(fraction = 0.7F)
                            ) {
                                Text(
                                    text = "Today", fontWeight = FontWeight.Medium, fontSize = 14.sp
                                )
                                Text(text = "3")

                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = modifier
                                    .padding(top = 5.dp)
                                    .fillMaxWidth(fraction = 0.7F)
                            ) {
                                Text(
                                    text = "Cloudy and Sunny",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                                Text(text = "3")

                            }
                        }
                        Spacer(
                            modifier = modifier
                                .height(30.dp)
                                .width(1.dp)
                                .background(
                                    color = colorResource(
                                        id = R.color.black
                                    )
                                )
                        )
                        Image(
                            painter = if ("Night" == "Day") painterResource(id = R.drawable.cloud_sun) else painterResource(
                                id = R.drawable.moon
                            ),
                            contentDescription = "Sun",
                            modifier = modifier.size(40.dp),
                            contentScale = ContentScale.FillBounds
                        )

                        Image(
                            painter = painterResource(id = R.drawable.expand_more),
                            contentDescription = "",
                            modifier = modifier
                                .align(Alignment.Top)
                                .padding(8.dp)
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun Header(modifier: Modifier = Modifier, locality: String = "Harsh") {
    val dayNight = Utils.getInstance(LocalContext.current).getDayOrNight()
    val daySelection = remember {
        mutableStateOf("7 days")
    }
    val item = mutableListOf("Today", "Tomorrow", "7 days")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = colorResource(id = R.color.statistics))
            .padding(20.dp)
            .wrapContentHeight()
    ) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = locality,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cloud Icon",
                tint = Color.White,
                modifier = modifier.size(24.dp)
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = "3°",// weatherResponseApi.current?.temperature2m.toString() + "°",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = modifier.padding(top = 10.dp)
                )
                Text(
                    text = "feels like 10",// weatherResponseApi.current?.temperature2m.toString() + "°",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = modifier
                        .padding(top = 10.dp)
                        .align(Alignment.Bottom)
                )
            }
            Image(
                painter = if (dayNight == "Day") painterResource(id = R.drawable.cloud_sun) else painterResource(
                    id = R.drawable.moon
                ),
                contentDescription = "Sun",
                modifier = modifier.size(60.dp),
                contentScale = ContentScale.FillBounds
            )
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            itemsIndexed(item) { index, _ ->
                Button(
                    onClick = {
                        daySelection.value = item[index]
                    }, colors = ButtonDefaults.buttonColors().copy(
                        containerColor = if (daySelection.value == item[index]) colorResource(id = R.color.purple_200) else colorResource(
                            id = R.color.white
                        ), contentColor = colorResource(id = R.color.black)
                    )
                ) {
                    Text(text = item[index])
                }
            }
        }
    }
}
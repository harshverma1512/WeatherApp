package com.weather.weatherapp.utility

import android.annotation.SuppressLint
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.weather.weatherapp.data.dto.HourlyTemp
import com.weather.weatherapp.data.dto.WeatherResponseApi
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class Utils {

    @Composable
    fun CircularProgressBar() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            CircularProgressIndicator(
                color = Color(0xFF704bd2), // Purple-ish tone (match your theme)
                strokeWidth = 4.dp,
                modifier = Modifier.size(64.dp)
            )
        }
    }

    @Composable
    fun DialogPop(title : String , msg : String , finish: () -> Unit) {
        val openDialog = remember {
            mutableStateOf(true)
        }

        if (openDialog.value) {
            AlertDialog(onDismissRequest = { },
                confirmButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                        finish()
                    }) {
                        Text(text = "OK")
                    }
                },
                title = { Text(text = "Error") },
                text = { Text(text = "Please check whether your internet connection or GPS is enabled.") })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale.ENGLISH)
        return currentDate.format(formatter)
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



    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Timber.tag("Internet").i("NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateTempAccordingHour(hourly: WeatherResponseApi.Hourly?): List<HourlyTemp> {
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
                        humidity = hourly.relativeHumidity2m?.get(i)!!,
                        windSpeed10m = hourly.windSpeed10m?.get(i)!!,
                        uvIndex = hourly.uvIndex?.get(i)!!
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


    private fun checkHour(inputTime: String): String {
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


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: Utils? = null

        fun getInstance(context: Context): Utils {
            return instance ?: synchronized(this) {
                instance ?: Utils().also { instance = it }
            }
        }
    }
}

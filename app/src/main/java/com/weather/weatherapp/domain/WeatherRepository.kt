package com.weather.weatherapp.domain

import com.weather.weatherapp.data.dto.WeatherResponseApi
import kotlinx.coroutines.flow.Flow

import com.weather.weatherapp.data.retrofit.ApiInterface
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiInterface: ApiInterface){

    suspend fun getWeatherResponse(latitude: Double, longitude: Double): Flow<WeatherState> {
        return flow {
            try {
                val response = apiInterface.getWeatherForecast(latitude, longitude,"temperature_2m,wind_speed_10m","temperature_2m,relative_humidity_2m,wind_speed_10m")
                if (response.isSuccessful && response.body() != null) {
                    emit(WeatherState.Success(response.body()!!))
                } else {
                    emit(WeatherState.Error(response.message()))
                }
            } catch (e: Exception) {
                emit(WeatherState.Error("Failed to fetch weather data: ${e.localizedMessage}"))
            }
        }
    }
}
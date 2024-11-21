package com.weather.weatherapp.domain

import com.weather.weatherapp.data.dto.WeatherResponseApi

sealed class WeatherState {
    data object Loading : WeatherState()
    data class Success(val data: WeatherResponseApi) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

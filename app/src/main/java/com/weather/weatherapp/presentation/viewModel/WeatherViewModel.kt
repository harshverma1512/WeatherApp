package com.weather.weatherapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherRepository
import com.weather.weatherapp.domain.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val weatherStateResponse = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = weatherStateResponse

    private var weatherResponseApi: WeatherResponseApi? = null

    fun getWeatherResponse(latitude: Double, longitude: Double) {
        viewModelScope.launch{
            weatherStateResponse.value = WeatherState.Loading
            weatherRepository.getWeatherResponse(latitude, longitude).collect{
                weatherStateResponse.value = it
            }
        }
    }

    fun setWeatherResponse(weatherResponseApi: WeatherResponseApi) {
        this.weatherResponseApi = weatherResponseApi
    }

    fun getWeatherResponse(): WeatherResponseApi? {
        return weatherResponseApi
    }
}
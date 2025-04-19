package com.weather.weatherapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weather.weatherapp.data.dto.WeatherResponseApi
import com.weather.weatherapp.domain.WeatherRepository
import com.weather.weatherapp.domain.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val weatherStateResponse = MutableSharedFlow<WeatherState>()
    val weatherState: StateFlow<WeatherState> = weatherStateResponse.stateIn(viewModelScope, SharingStarted.Lazily, WeatherState.Loading)

    private val _latLong = MutableStateFlow<Pair<Double, Double>?>(null)
    val latLong: StateFlow<Pair<Double, Double>?> = _latLong.asStateFlow()

    fun setLatLong(latitude: Double, longitude: Double) {
        _latLong.value = Pair(latitude, longitude)
    }

    fun getWeatherResponse(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherStateResponse.emit( WeatherState.Loading)
            weatherRepository.getWeatherResponse(latitude, longitude).collect {
                weatherStateResponse.emit(it)
            }
        }
    }
}
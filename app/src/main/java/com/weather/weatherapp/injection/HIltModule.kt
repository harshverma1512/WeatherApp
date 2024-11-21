package com.weather.weatherapp.injection

import com.weather.weatherapp.data.retrofit.ApiInterface
import com.weather.weatherapp.data.retrofit.RetrofitPool
import com.weather.weatherapp.domain.WeatherRepository
import com.weather.weatherapp.presentation.viewModel.WeatherViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HIltModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): ApiInterface {
        return RetrofitPool.getRetrofitInstance("https://api.open-meteo.com/v1/")
    }

    @Provides
    @Singleton
    fun provideRepository(apiInterface: ApiInterface): WeatherRepository {
        return WeatherRepository(apiInterface)
    }


    @Provides
    @Singleton
    fun provideWeatherViewModel(weatherRepository: WeatherRepository): WeatherViewModel {
        return WeatherViewModel(weatherRepository)
    }




}
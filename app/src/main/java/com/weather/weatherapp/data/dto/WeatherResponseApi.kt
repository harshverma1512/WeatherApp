package com.weather.weatherapp.data.dto


import com.google.gson.annotations.SerializedName

data class WeatherResponseApi(
    @SerializedName("current")
    val current: Current?,
    @SerializedName("current_units")
    val currentUnits: CurrentUnits?,
    @SerializedName("elevation")
    val elevation: Int?,
    @SerializedName("generationtime_ms")
    val generationtimeMs: Double?,
    @SerializedName("hourly")
    val hourly: Hourly?,
    @SerializedName("hourly_units")
    val hourlyUnits: HourlyUnits?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("timezone")
    val timezone: String?,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String?,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int?,
    @SerializedName("daily")
    val daily : Daily
) {
    data class Daily(
        @SerializedName("time") val time: List<String>?,
        @SerializedName("rain_sum") val rain_sum: List<Int>,
    )

    data class Current(
        @SerializedName("interval")
        val interval: Int?,
        @SerializedName("temperature_2m")
        val temperature2m: Double?,
        @SerializedName("time")
        val time: String?,
        @SerializedName("wind_speed_10m")
        val windSpeed10m: Double?
    )

    data class CurrentUnits(
        @SerializedName("interval")
        val interval: String?,
        @SerializedName("temperature_2m")
        val temperature2m: String?,
        @SerializedName("time")
        val time: String?,
        @SerializedName("wind_speed_10m")
        val windSpeed10m: String?
    )

    data class Hourly(
        @SerializedName("relative_humidity_2m")
        val relativeHumidity2m: List<Int?>?,
        @SerializedName("temperature_2m")
        val temperature2m: List<Double?>?,
        @SerializedName("time")
        val time: List<String?>?,
        @SerializedName("wind_speed_10m")
        val windSpeed10m: List<Double?>?,
        @SerializedName("uv_index")
        val uvIndex: List<Double?>?
    )

    data class HourlyUnits(
        @SerializedName("relative_humidity_2m")
        val relativeHumidity2m: String?,
        @SerializedName("temperature_2m")
        val temperature2m: String?,
        @SerializedName("time")
        val time: String?,
        @SerializedName("wind_speed_10m")
        val windSpeed10m: String?
    )
}
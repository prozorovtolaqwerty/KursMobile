package com.example.weather.data.remote

data class ForecastResponse(
    val current: CurrentWeatherDto?,
    val daily: DailyWeatherDto?
)

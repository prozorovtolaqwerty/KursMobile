package com.example.weather.domain.model

data class CityForecast(
    val current: CurrentWeather,
    val days: List<DailyForecast>
)

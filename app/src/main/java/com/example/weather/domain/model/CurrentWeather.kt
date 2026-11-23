package com.example.weather.domain.model

data class CurrentWeather(
    val temperature: Double,
    val weatherCode: Int,
    val windSpeed: Double
)

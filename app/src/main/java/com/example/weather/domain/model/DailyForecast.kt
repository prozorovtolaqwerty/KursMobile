package com.example.weather.domain.model

data class DailyForecast(
    val date: String,
    val weatherCode: Int,
    val tempMax: Double,
    val tempMin: Double,
    val precipitationProbability: Int
)

package com.example.weather.ui

import com.example.weather.domain.model.DailyForecast

data class ForecastUiState(
    val title: String = "",
    val temperature: Double? = null,
    val weatherCode: Int? = null,
    val windSpeed: Double? = null,
    val days: List<DailyForecast> = emptyList(),
    val loading: Boolean = true,
    val errorRes: Int? = null
)

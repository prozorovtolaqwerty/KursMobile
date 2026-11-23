package com.example.weather.ui

import com.example.weather.domain.model.SavedCity

data class CityListItem(
    val city: SavedCity,
    val temperature: Double?,
    val weatherCode: Int?
)

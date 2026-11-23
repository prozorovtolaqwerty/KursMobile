package com.example.weather.domain.model

data class SavedCity(
    val id: Long,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)

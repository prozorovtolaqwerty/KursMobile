package com.example.weather.ui

import com.example.weather.domain.model.CitySuggestion

data class MainUiState(
    val suggestions: List<CitySuggestion> = emptyList(),
    val cities: List<CityListItem> = emptyList(),
    val searching: Boolean = false,
    val errorRes: Int? = null,
    val showEmpty: Boolean = true
)

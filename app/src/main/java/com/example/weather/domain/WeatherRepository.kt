package com.example.weather.domain

import com.example.weather.domain.model.CityForecast
import com.example.weather.domain.model.CitySuggestion
import com.example.weather.domain.model.CurrentWeather
import com.example.weather.domain.model.SavedCity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observeCities(): Flow<List<SavedCity>>
    suspend fun search(query: String): LoadResult<List<CitySuggestion>>
    suspend fun add(suggestion: CitySuggestion): LoadResult<Unit>
    suspend fun delete(cityId: Long)
    suspend fun city(cityId: Long): SavedCity?
    suspend fun current(city: SavedCity): LoadResult<CurrentWeather>
    suspend fun forecast(city: SavedCity): LoadResult<CityForecast>
}

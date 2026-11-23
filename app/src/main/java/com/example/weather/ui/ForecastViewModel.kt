package com.example.weather.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.BuildConfig
import com.example.weather.R
import com.example.weather.data.local.WeatherDatabase
import com.example.weather.data.remote.WeatherNetwork
import com.example.weather.data.repository.WeatherRepositoryImpl
import com.example.weather.domain.FailureReason
import com.example.weather.domain.LoadResult
import com.example.weather.domain.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForecastViewModel(
    application: Application,
    private val cityId: Long
) : AndroidViewModel(application) {
    private val repository: WeatherRepository = WeatherRepositoryImpl(
        database = WeatherDatabase.getInstance(application),
        network = WeatherNetwork(BuildConfig.NINJAS_API_KEY),
        apiKey = BuildConfig.NINJAS_API_KEY
    )

    private val state = MutableStateFlow(ForecastUiState())
    val uiState: StateFlow<ForecastUiState> = state.asStateFlow()

    init {
        viewModelScope.launch {
            val city = repository.city(cityId)
            if (city == null) {
                state.value = ForecastUiState(loading = false, errorRes = R.string.error_not_found)
                return@launch
            }
            val title = if (city.country.isBlank()) city.name else "${city.name}, ${city.country}"
            when (val result = repository.forecast(city)) {
                is LoadResult.Success -> state.value = ForecastUiState(
                    title = title,
                    temperature = result.data.current.temperature,
                    weatherCode = result.data.current.weatherCode,
                    windSpeed = result.data.current.windSpeed,
                    days = result.data.days,
                    loading = false
                )
                is LoadResult.Failure -> state.value = ForecastUiState(
                    title = title,
                    loading = false,
                    errorRes = messageRes(result.reason)
                )
            }
        }
    }

    private fun messageRes(reason: FailureReason): Int {
        return when (reason) {
            FailureReason.NETWORK -> R.string.error_network
            FailureReason.API_KEY -> R.string.error_api_key
            FailureReason.NOT_FOUND -> R.string.error_not_found
            FailureReason.DUPLICATE -> R.string.error_duplicate
            FailureReason.UNKNOWN -> R.string.error_unknown
        }
    }
}

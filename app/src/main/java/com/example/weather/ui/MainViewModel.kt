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
import com.example.weather.domain.model.CitySuggestion
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WeatherRepository = WeatherRepositoryImpl(
        database = WeatherDatabase.getInstance(application),
        network = WeatherNetwork(BuildConfig.NINJAS_API_KEY),
        apiKey = BuildConfig.NINJAS_API_KEY
    )

    private val suggestions = MutableStateFlow<List<CitySuggestion>>(emptyList())
    private val cities = MutableStateFlow<List<CityListItem>>(emptyList())
    private val searching = MutableStateFlow(false)
    private val errorRes = MutableStateFlow<Int?>(null)
    private val query = MutableStateFlow("")

    val uiState: StateFlow<MainUiState> = combine(
        suggestions,
        cities,
        searching,
        errorRes
    ) { found, saved, search, error ->
        MainUiState(
            suggestions = found,
            cities = saved,
            searching = search,
            errorRes = error,
            showEmpty = saved.isEmpty() && found.isEmpty() && !search
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    init {
        viewModelScope.launch {
            repository.observeCities().collect { saved ->
                val placeholders = saved.map { CityListItem(it, null, null) }
                cities.value = placeholders
                val loaded = loadWeather(placeholders)
                if (cities.value.map { it.city.id } == saved.map { it.id }) {
                    cities.value = loaded
                }
            }
        }
        viewModelScope.launch {
            query.debounce(300).distinctUntilChanged().collect { text ->
                if (text.isBlank()) {
                    searching.value = false
                    suggestions.value = emptyList()
                    return@collect
                }
                searching.value = true
                when (val result = repository.search(text)) {
                    is LoadResult.Success -> {
                        suggestions.value = result.data
                        errorRes.value = if (result.data.isEmpty()) R.string.search_empty else null
                    }
                    is LoadResult.Failure -> {
                        suggestions.value = emptyList()
                        errorRes.value = messageRes(result.reason)
                    }
                }
                searching.value = false
            }
        }
    }

    fun onQueryChanged(text: String) {
        query.value = text
        if (text.isBlank()) {
            errorRes.value = null
        }
    }

    fun add(suggestion: CitySuggestion) {
        viewModelScope.launch {
            when (val result = repository.add(suggestion)) {
                is LoadResult.Success -> errorRes.value = null
                is LoadResult.Failure -> errorRes.value = messageRes(result.reason)
            }
        }
    }

    fun delete(cityId: Long) {
        viewModelScope.launch {
            repository.delete(cityId)
        }
    }

    private suspend fun loadWeather(items: List<CityListItem>): List<CityListItem> {
        return coroutineScope {
            items.map { item ->
                async {
                    when (val result = repository.current(item.city)) {
                        is LoadResult.Success -> item.copy(
                            temperature = result.data.temperature,
                            weatherCode = result.data.weatherCode
                        )
                        is LoadResult.Failure -> item
                    }
                }
            }.awaitAll()
        }
    }

    private fun messageRes(reason: FailureReason): Int {
        return when (reason) {
            FailureReason.NETWORK -> R.string.error_network
            FailureReason.API_KEY -> R.string.error_api_key
            FailureReason.NOT_FOUND -> R.string.search_empty
            FailureReason.DUPLICATE -> R.string.error_duplicate
            FailureReason.UNKNOWN -> R.string.error_unknown
        }
    }
}

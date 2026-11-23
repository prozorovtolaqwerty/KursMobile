package com.example.weather.data.repository

import com.example.weather.data.local.CityEntity
import com.example.weather.data.local.WeatherDatabase
import com.example.weather.data.remote.CurrentWeatherDto
import com.example.weather.data.remote.ForecastResponse
import com.example.weather.data.remote.WeatherNetwork
import com.example.weather.domain.FailureReason
import com.example.weather.domain.LoadResult
import com.example.weather.domain.WeatherRepository
import com.example.weather.domain.model.CityForecast
import com.example.weather.domain.model.CitySuggestion
import com.example.weather.domain.model.CurrentWeather
import com.example.weather.domain.model.DailyForecast
import com.example.weather.domain.model.SavedCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class WeatherRepositoryImpl(
    private val database: WeatherDatabase,
    private val network: WeatherNetwork,
    private val apiKey: String
) : WeatherRepository {

    override fun observeCities(): Flow<List<SavedCity>> {
        return database.cityDao().observeAll().map { cities ->
            cities.map { it.toSaved() }
        }
    }

    override suspend fun search(query: String): LoadResult<List<CitySuggestion>> {
        val text = query.trim()
        if (text.isEmpty()) {
            return LoadResult.Success(emptyList())
        }
        if (apiKey.isBlank()) {
            return LoadResult.Failure(FailureReason.API_KEY)
        }
        return call {
            network.ninjas.cities(text)
                .take(5)
                .mapNotNull { dto ->
                    val name = dto.name ?: return@mapNotNull null
                    val latitude = dto.latitude ?: return@mapNotNull null
                    val longitude = dto.longitude ?: return@mapNotNull null
                    CitySuggestion(
                        name = name,
                        country = dto.country.orEmpty(),
                        latitude = latitude,
                        longitude = longitude
                    )
                }
        }
    }

    override suspend fun add(suggestion: CitySuggestion): LoadResult<Unit> {
        val rowId = database.cityDao().insert(
            CityEntity(
                name = suggestion.name,
                country = suggestion.country,
                latitude = suggestion.latitude,
                longitude = suggestion.longitude
            )
        )
        return if (rowId == -1L) {
            LoadResult.Failure(FailureReason.DUPLICATE)
        } else {
            LoadResult.Success(Unit)
        }
    }

    override suspend fun delete(cityId: Long) {
        database.cityDao().delete(cityId)
    }

    override suspend fun city(cityId: Long): SavedCity? {
        return database.cityDao().getById(cityId)?.toSaved()
    }

    override suspend fun current(city: SavedCity): LoadResult<CurrentWeather> {
        return call {
            val response = network.meteo.current(city.latitude, city.longitude)
            response.toCurrent() ?: throw IllegalStateException()
        }
    }

    override suspend fun forecast(city: SavedCity): LoadResult<CityForecast> {
        return call {
            val response = network.meteo.forecast(city.latitude, city.longitude)
            val current = response.toCurrent() ?: throw IllegalStateException()
            CityForecast(current = current, days = response.toDays())
        }
    }

    private fun CityEntity.toSaved(): SavedCity {
        return SavedCity(id, name, country, latitude, longitude)
    }

    private fun ForecastResponse.toCurrent(): CurrentWeather? {
        val dto = current ?: return null
        return dto.toWeather()
    }

    private fun CurrentWeatherDto.toWeather(): CurrentWeather? {
        val temperature = temperature ?: return null
        val code = weatherCode ?: return null
        val wind = windSpeed ?: return null
        return CurrentWeather(temperature, code, wind)
    }

    private fun ForecastResponse.toDays(): List<DailyForecast> {
        val daily = daily ?: return emptyList()
        val dates = daily.time.orEmpty()
        val codes = daily.weatherCode.orEmpty()
        val max = daily.temperatureMax.orEmpty()
        val min = daily.temperatureMin.orEmpty()
        val precipitation = daily.precipitationProbability.orEmpty()
        val count = minOf(dates.size, codes.size, max.size, min.size)
        return (0 until count).map { index ->
            DailyForecast(
                date = dates[index],
                weatherCode = codes[index],
                tempMax = max[index],
                tempMin = min[index],
                precipitationProbability = precipitation.getOrElse(index) { 0 }
            )
        }
    }

    private suspend fun <T> call(block: suspend () -> T): LoadResult<T> {
        return try {
            LoadResult.Success(block())
        } catch (error: IOException) {
            LoadResult.Failure(FailureReason.NETWORK)
        } catch (error: HttpException) {
            val reason = if (error.code() == 401 || error.code() == 403) {
                FailureReason.API_KEY
            } else {
                FailureReason.UNKNOWN
            }
            LoadResult.Failure(reason)
        } catch (error: IllegalStateException) {
            LoadResult.Failure(FailureReason.UNKNOWN)
        }
    }
}

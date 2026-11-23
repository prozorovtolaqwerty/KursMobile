package com.example.weather.ui

import com.example.weather.R

object WeatherText {
    fun descriptionRes(code: Int): Int {
        return when (code) {
            0 -> R.string.weather_clear
            1 -> R.string.weather_mainly_clear
            2 -> R.string.weather_partly_cloudy
            3 -> R.string.weather_cloudy
            45, 48 -> R.string.weather_fog
            51, 53, 55, 56, 57 -> R.string.weather_drizzle
            61, 63, 65, 66, 67, 80, 81, 82 -> R.string.weather_rain
            71, 73, 75, 77, 85, 86 -> R.string.weather_snow
            95, 96, 99 -> R.string.weather_thunder
            else -> R.string.weather_unknown
        }
    }
}

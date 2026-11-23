package com.example.weather.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.domain.model.DailyForecast
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val date: TextView = itemView.findViewById(R.id.dayDate)
    private val description: TextView = itemView.findViewById(R.id.dayDescription)
    private val temps: TextView = itemView.findViewById(R.id.dayTemps)
    private val precipitation: TextView = itemView.findViewById(R.id.dayPrecipitation)

    fun bind(day: DailyForecast) {
        val context = itemView.context
        val parsed = LocalDate.parse(day.date)
        date.text = if (parsed == LocalDate.now()) {
            context.getString(R.string.today)
        } else {
            parsed.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        }
        description.setText(WeatherText.descriptionRes(day.weatherCode))
        temps.text = context.getString(R.string.temp_range, day.tempMin, day.tempMax)
        precipitation.text = context.getString(R.string.precipitation, day.precipitationProbability)
    }
}

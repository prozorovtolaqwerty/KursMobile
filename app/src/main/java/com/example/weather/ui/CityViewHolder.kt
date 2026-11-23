package com.example.weather.ui

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R

class CityViewHolder(
    itemView: View,
    onClick: (CityListItem) -> Unit,
    onDelete: (CityListItem) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.cityName)
    private val details: TextView = itemView.findViewById(R.id.cityDetails)
    private val temperature: TextView = itemView.findViewById(R.id.cityTemp)
    private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    private var item: CityListItem? = null

    init {
        itemView.setOnClickListener {
            item?.let(onClick)
        }
        deleteButton.setOnClickListener {
            item?.let(onDelete)
        }
    }

    fun bind(cityItem: CityListItem) {
        item = cityItem
        val city = cityItem.city
        name.text = city.name
        val description = cityItem.weatherCode?.let { code ->
            itemView.context.getString(WeatherText.descriptionRes(code))
        }
        details.text = listOf(city.country, description)
            .filter { !it.isNullOrBlank() }
            .joinToString(", ")
        temperature.text = if (cityItem.temperature == null) {
            itemView.context.getString(R.string.temperature_unknown)
        } else {
            itemView.context.getString(R.string.temperature, cityItem.temperature)
        }
    }
}

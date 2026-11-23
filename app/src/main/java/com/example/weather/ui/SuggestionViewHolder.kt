package com.example.weather.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.domain.model.CitySuggestion

class SuggestionViewHolder(
    itemView: View,
    onClick: (CitySuggestion) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.suggestionName)
    private var suggestion: CitySuggestion? = null

    init {
        itemView.setOnClickListener {
            suggestion?.let(onClick)
        }
    }

    fun bind(item: CitySuggestion) {
        suggestion = item
        name.text = if (item.country.isBlank()) {
            item.name
        } else {
            "${item.name}, ${item.country}"
        }
    }
}

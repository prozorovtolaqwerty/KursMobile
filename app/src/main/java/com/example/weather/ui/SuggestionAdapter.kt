package com.example.weather.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.domain.model.CitySuggestion

class SuggestionAdapter(
    private val onClick: (CitySuggestion) -> Unit
) : RecyclerView.Adapter<SuggestionViewHolder>() {
    private val items = mutableListOf<CitySuggestion>()

    fun submit(suggestions: List<CitySuggestion>) {
        items.clear()
        items.addAll(suggestions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

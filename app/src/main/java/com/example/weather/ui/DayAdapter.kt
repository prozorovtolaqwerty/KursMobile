package com.example.weather.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.domain.model.DailyForecast

class DayAdapter : RecyclerView.Adapter<DayViewHolder>() {
    private val items = mutableListOf<DailyForecast>()

    fun submit(days: List<DailyForecast>) {
        items.clear()
        items.addAll(days)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

package com.example.weather.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R

class CityAdapter(
    private val onClick: (CityListItem) -> Unit,
    private val onDelete: (CityListItem) -> Unit
) : RecyclerView.Adapter<CityViewHolder>() {
    private val items = mutableListOf<CityListItem>()

    fun submit(cities: List<CityListItem>) {
        items.clear()
        items.addAll(cities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view, onClick, onDelete)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

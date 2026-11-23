package com.example.weather.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var cityAdapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchInput = findViewById<TextInputEditText>(R.id.searchInput)
        val errorText = findViewById<TextView>(R.id.errorText)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val emptyText = findViewById<TextView>(R.id.emptyText)
        val suggestionList = findViewById<RecyclerView>(R.id.suggestionList)
        val cityList = findViewById<RecyclerView>(R.id.cityList)

        suggestionAdapter = SuggestionAdapter { suggestion ->
            viewModel.add(suggestion)
            searchInput.setText("")
        }
        cityAdapter = CityAdapter(
            onClick = { item ->
                startActivity(
                    Intent(this, ForecastActivity::class.java)
                        .putExtra(ForecastActivity.EXTRA_CITY_ID, item.city.id)
                )
            },
            onDelete = { item -> viewModel.delete(item.city.id) }
        )
        suggestionList.layoutManager = LinearLayoutManager(this)
        suggestionList.adapter = suggestionAdapter
        cityList.layoutManager = LinearLayoutManager(this)
        cityList.adapter = cityAdapter

        searchInput.doAfterTextChanged { text ->
            viewModel.onQueryChanged(text?.toString().orEmpty())
        }

        viewModel.uiState.observeState(this) { state ->
            suggestionAdapter.submit(state.suggestions)
            cityAdapter.submit(state.cities)
            suggestionList.visibility = if (state.suggestions.isEmpty()) View.GONE else View.VISIBLE
            emptyText.visibility = if (state.showEmpty) View.VISIBLE else View.GONE
            progress.visibility = if (state.searching) View.VISIBLE else View.GONE
            if (state.errorRes == null) {
                errorText.visibility = View.GONE
            } else {
                errorText.visibility = View.VISIBLE
                errorText.setText(state.errorRes)
            }
        }
    }
}

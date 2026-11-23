package com.example.weather.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.google.android.material.appbar.MaterialToolbar

class ForecastActivity : AppCompatActivity() {
    private val cityId: Long by lazy { intent.getLongExtra(EXTRA_CITY_ID, 0L) }
    private val viewModel: ForecastViewModel by viewModels {
        ForecastViewModelFactory(application, cityId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val cityName = findViewById<TextView>(R.id.cityName)
        val currentTemp = findViewById<TextView>(R.id.currentTemp)
        val currentDescription = findViewById<TextView>(R.id.currentDescription)
        val windText = findViewById<TextView>(R.id.windText)
        val errorText = findViewById<TextView>(R.id.errorText)
        val progress = findViewById<ProgressBar>(R.id.progress)
        val dayList = findViewById<RecyclerView>(R.id.dayList)
        val dayAdapter = DayAdapter()

        toolbar.setNavigationOnClickListener { finish() }
        toolbar.title = getString(R.string.app_name)
        dayList.layoutManager = LinearLayoutManager(this)
        dayList.adapter = dayAdapter

        viewModel.uiState.observeState(this) { state ->
            toolbar.title = state.title.ifBlank { getString(R.string.app_name) }
            cityName.text = state.title
            currentTemp.text = if (state.temperature == null) {
                getString(R.string.temperature_unknown)
            } else {
                getString(R.string.temperature, state.temperature)
            }
            currentDescription.text = state.weatherCode?.let { getString(WeatherText.descriptionRes(it)) }.orEmpty()
            windText.text = if (state.windSpeed == null) {
                ""
            } else {
                getString(R.string.wind, state.windSpeed)
            }
            dayAdapter.submit(state.days)
            progress.visibility = if (state.loading) View.VISIBLE else View.GONE
            if (state.errorRes == null) {
                errorText.visibility = View.GONE
            } else {
                errorText.visibility = View.VISIBLE
                errorText.setText(state.errorRes)
            }
        }
    }

    companion object {
        const val EXTRA_CITY_ID = "city_id"
    }
}

package com.example.weather.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WeatherNetwork(apiKey: String) {
    val ninjas: NinjasApi
    val meteo: OpenMeteoApi

    init {
        val ninjasClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("X-Api-Key", apiKey)
                    .build()
                chain.proceed(request)
            }
            .build()
        val meteoClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        ninjas = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .client(ninjasClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NinjasApi::class.java)
        meteo = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(meteoClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}

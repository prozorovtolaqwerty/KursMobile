package com.example.weather.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NinjasApi {
    @GET("v1/city")
    suspend fun cities(@Query("name") name: String): List<CityDto>
}

package com.example.climaapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    suspend fun getWeather(city: String): WeatherResponse {
        return api.getWeather(
            city = city,
            apiKey = "f572619a3cb69ddf85f2fad662b34c91"
        )
    }
}
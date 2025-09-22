package com.example.climaapp.uifactures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climaapp.data.WeatherRepository
import com.example.climaapp.data.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState = _weatherState.asStateFlow()

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                val weather = repository.getWeather(city)
                _weatherState.value = WeatherState.Success(weather)
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

// Estados posibles de la UI
sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherResponse) : WeatherState()
    data class Error(val message: String) : WeatherState()
}
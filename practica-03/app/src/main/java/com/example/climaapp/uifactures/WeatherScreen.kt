package com.example.climaapp.uifactures

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climaapp.data.WeatherResponse

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    // 1. Estado modificado para usar remember + mutableStateOf
    var city by remember { mutableStateOf("Madrid") }
    // 2. Estado del ViewModel convertido a StateFlow/collectAsState
    val weatherState by viewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 3. TextField con control mejorado
        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Botón con control de estado
        Button(
            onClick = {
                if (city.isNotBlank()) {
                    viewModel.fetchWeather(city.trim())
                }
            },
            enabled = city.isNotBlank() // Desactiva si está vacío
        ) {
            Text("Buscar Clima")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. Manejo de estados mejorado
        when (weatherState) {
            is WeatherState.Loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cargando...")
                }
            }
            is WeatherState.Success -> WeatherInfo((weatherState as WeatherState.Success).data)
            is WeatherState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❌ Error: ${(weatherState as WeatherState.Error).message}", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.fetchWeather(city) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherInfo(data: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Temperatura: ${data.main.temp}°C", style = MaterialTheme.typography.headlineSmall)
            Text("Humedad: ${data.main.humidity}%")
            Text("Estado: ${data.weather[0].description.replaceFirstChar { it.uppercase() }}")
        }
    }
}
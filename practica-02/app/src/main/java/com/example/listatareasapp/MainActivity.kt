package com.example.listatareasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.listatareasapp.ui.theme.ListaTareasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListaTareasAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ListaTareasApp()
                }
            }
        }
    }
}

@Composable
fun ListaTareasApp() {
    var tarea by remember { mutableStateOf("") }
    val listaTareas = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        // Input para añadir tareas
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = tarea,
                onValueChange = { tarea = it },
                label = { Text("Nueva tarea") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (tarea.isNotBlank()) {
                    listaTareas.add(tarea)
                    tarea = ""
                }
            }) {
                Text("+")
            }
        }

        // Lista de tareas
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listaTareas) { tarea ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(tarea)
                        IconButton(onClick = { listaTareas.remove(tarea) }) {
                            Text("❌")
                        }
                    }
                }
            }
        }
    }
}
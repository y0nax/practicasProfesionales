package com.jonathan.cleancity.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.jonathan.cleancity.firebase.FirebaseService
import com.jonathan.cleancity.ui.model.Report

@Composable
fun FormularioScreen(
    navController: NavController,
    latitud: Double,
    longitud: Double,
    auth: FirebaseAuth
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Basura") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val categorias = listOf("Basura", "Bache", "Fuga", "Otro")

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imagenUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Nuevo Reporte", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título del reporte") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Categoría:")
        DropdownMenuCategoria(categoria, categorias) { seleccion ->
            categoria = seleccion
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { galeriaLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Imagen")
        }

        imagenUri?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val user = auth.currentUser
                val reporte = user?.email?.let {
                    Report(
                        title = titulo,
                        description = descripcion,
                        category = categoria,
                        longitude = latitud,
                        latitude = longitud,
                        email = it,
                        timestamp = System.currentTimeMillis()
                    )
                }

                if (reporte != null) {
                    FirebaseService.subirReporte(
                        reporte = reporte,
                        imagenUri = imagenUri,
                        onSuccess = {
                            Toast.makeText(context, "Reporte guardado exitosamente", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("formulario") { inclusive = true }
                            }
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Reporte")
        }
    }
}

@Composable
fun DropdownMenuCategoria(
    categoriaSeleccionada: String,
    categorias: List<String>,
    onCategoriaSeleccionada: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = categoriaSeleccionada,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir menú")
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria) },
                    onClick = {
                        onCategoriaSeleccionada(categoria)
                        expanded = false
                    }
                )
            }
        }
    }
}


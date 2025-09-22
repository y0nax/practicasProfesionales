package com.jonathan.cleancity.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.jonathan.cleancity.ui.model.Report
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsMapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val reportes = remember { mutableStateListOf<Report>() }
    val initialPosition = LatLng(19.0438, -98.1982) // Puebla centro
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 12f)
    }

    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    /*RECUPERA LA POSICIÓN EN TIEMPO REAL DEL USUARIO, PERO SE COMENTA POR QUE COMO SE EMULA DA UBUCAIÓN DE EE.UU.*/
    /*LaunchedEffect(hasPermission) {
        if (hasPermission) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                @SuppressLint("MissingPermission")
                val location = fusedLocationClient.lastLocation.await()

                location?.let {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.longitude, it.latitude),
                            15f
                        ),
                        durationMs = 1000
                    )
                }
            } catch (e: Exception) {
                Log.e("Map", "No se pudo obtener ubicación actual", e)
            }
        }
    }*/

    LaunchedEffect(Unit) {
        db.collection("reports")
            .get()
            .addOnSuccessListener { result ->
                Log.d("Firestore", "Reportes encontrados: ${result.size()}")
                reportes.clear()
                for (document in result) {
                    val lat = document.getDouble("latitude")
                    val lon = document.getDouble("longitude")
                    val titulo = document.getString("title") ?: "Sin título"
                    val descripcion = document.getString("description") ?: ""
                    val imagenUrl = document.getString("imagenUrl") ?: ""

                    if (lat != null && lon != null) {
                        reportes.add(
                            Report(
                                title = titulo,
                                description = descripcion,
                                imagenUrl = imagenUrl,
                                latitude = lat,
                                longitude = lon
                            )
                        )
                    }
                }
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error al cargar reportes", it)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Reportes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasPermission)
        ) {
            reportes.forEach { reporte ->
                Marker(
                    state = MarkerState(position = LatLng(reporte.longitude, reporte.latitude)),
                    title = reporte.title,
                    snippet = reporte.description
                )
            }
        }
    }

}

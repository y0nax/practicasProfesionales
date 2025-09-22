package com.jonathan.cleancity.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.jonathan.cleancity.ui.model.Report
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun ReportHistoryScreen(navController: NavController, auth: FirebaseAuth) {
    val context = LocalContext.current
    val db = Firebase.firestore
    val user = auth.currentUser
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }

    LaunchedEffect(user) {
        user?.email?.let { email ->
            db.collection("reports")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    val fetchedReports = documents.map { doc ->
                        Report(
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            category = doc.getString("category") ?: "",
                            email = doc.getString("email") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            imagenUrl = doc.getString("imagenUrl") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0
                        )
                    }
                    reports = fetchedReports
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al cargar reportes", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Reportes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (reports.isEmpty()) {
                Text("No hay reportes aún.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn {
                    items(reports) { report ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Título: ${report.title}", style = MaterialTheme.typography.titleMedium)
                                Text("Descripción: ${report.description}")
                                Text("Categoría: ${report.category}")
                                Text("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(report.timestamp))}")
                                if (report.imagenUrl.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Imagen adjunta:", style = MaterialTheme.typography.bodyMedium)
                                    AsyncImage(
                                        model = report.imagenUrl,
                                        contentDescription = "Imagen del reporte",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                    )
                                }

                                if (report.latitude != 0.0 && report.longitude != 0.0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Ubicación: (${report.latitude}, ${report.longitude})",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

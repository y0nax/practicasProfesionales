package com.jonathan.cleancity.ui.model

data class Report(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val email: String = "",
    val timestamp: Long = 0L,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imagenUrl: String = ""
)
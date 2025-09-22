package com.jonathan.cleancity.firebase

import android.annotation.SuppressLint
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jonathan.cleancity.ui.model.Report
import java.util.*

object FirebaseService {

    @SuppressLint("StaticFieldLeak")
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    fun subirReporte(
        reporte: Report,
        imagenUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (imagenUri != null) {
            val nombreImagen = UUID.randomUUID().toString()
            val refImagen = storage.child("report_images/$nombreImagen")

            refImagen.putFile(imagenUri)
                .addOnSuccessListener {
                    refImagen.downloadUrl.addOnSuccessListener { uriDescarga ->
                        val reporteConImagen = reporte.copy(imagenUrl = uriDescarga.toString())
                        guardarReporteEnFirestore(reporteConImagen, onSuccess, onError)
                    }
                }
                .addOnFailureListener {
                    onError("Error al subir imagen: ${it.message}")
                }

        } else {
            guardarReporteEnFirestore(reporte, onSuccess, onError)
        }
    }

    private fun guardarReporteEnFirestore(
        reporte: Report,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("reports")
            .add(reporte)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError("Error al guardar reporte: ${it.message}") }
    }
}

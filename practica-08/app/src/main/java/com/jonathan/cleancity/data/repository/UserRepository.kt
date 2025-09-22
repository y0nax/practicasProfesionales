package com.jonathan.cleancity.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jonathan.cleancity.ui.model.User
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getCurrentUser(): User? {
        val currentUser = auth.currentUser ?: return null
        val uid = currentUser.uid

        val userDoc = firestore.collection("users").document(uid).get().await()

        if (userDoc.exists()) {
            val name = userDoc.getString("nombre") ?: "" // campo real en tu base
            val email = userDoc.getString("correo") ?: currentUser.email.orEmpty()

            return User(
                uid = uid,
                name = name,
                email = email
            )
        }

        return null
    }
}

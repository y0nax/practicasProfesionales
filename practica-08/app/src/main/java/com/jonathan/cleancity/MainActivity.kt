package com.jonathan.cleancity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.jonathan.cleancity.ui.screens.NavigationGraph
import com.jonathan.cleancity.ui.theme.CleanCityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CleanCityTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val auth = FirebaseAuth.getInstance()
                    NavigationGraph(navController, auth)
                }
            }
        }
    }
}

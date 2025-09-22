package com.jonathan.cleancity.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jonathan.cleancity.data.repository.UserRepository

@Composable
fun NavigationGraph(navController: NavHostController, auth: FirebaseAuth) {
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, auth) }
        composable("register") { RegisterScreen(navController, auth) }
        composable("home") {
            val userRepository = UserRepository(
                auth = auth,
                firestore = FirebaseFirestore.getInstance()
            )
            HomeScreen(navController, auth, userRepository)
        }
        composable("forgot_password") { ForgotPasswordScreen(navController, auth) }
        composable("report") { ReportScreen(navController, auth) }
        composable("history") { ReportHistoryScreen(navController, auth) }
        composable("profile") {
            val userRepository = UserRepository(
                auth = auth,
                firestore = FirebaseFirestore.getInstance()
            )
            ProfileScreen(navController, auth, userRepository)
        }
        composable("map") { MapScreen(navController) }
        composable(
            route = "formulario/{lat}/{lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType }
            )
        ) {
            val lat = it.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = it.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0
            FormularioScreen(navController, lat, lng, auth)
        }
        composable("reports_map") { ReportsMapScreen(navController) }
    }
}
package com.jonathan.expensetrackerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jonathan.expensetrackerapp.ui.screens.AddEditTransactionScreen
import com.jonathan.expensetrackerapp.ui.screens.FilterScreen
import com.jonathan.expensetrackerapp.ui.screens.HomeScreen

object Routes {
    const val HOME = "home"
    const val ADD_EDIT = "add_edit"
    const val FILTER = "filter"
}

private const val KEY_FILTER_START = "filter_start"
private const val KEY_FILTER_END = "filter_end"

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) { backStackEntry ->
            val startFlow = backStackEntry.savedStateHandle.getStateFlow<String?>(KEY_FILTER_START, null)
            val endFlow   = backStackEntry.savedStateHandle.getStateFlow<String?>(KEY_FILTER_END, null)

            val onClearFilter: () -> Unit = {
                navController.currentBackStackEntry?.savedStateHandle?.set(KEY_FILTER_START, null as String?)
                navController.currentBackStackEntry?.savedStateHandle?.set(KEY_FILTER_END, null as String?)
            }

            HomeScreen(
                onAddClick = { navController.navigate(Routes.ADD_EDIT) },
                onOpenFilter = { navController.navigate(Routes.FILTER) },
                startFilterFlow = startFlow,
                endFilterFlow = endFlow,
                onClearFilter = onClearFilter
            )
        }

        composable(Routes.ADD_EDIT) {
            AddEditTransactionScreen(
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(Routes.FILTER) {
            FilterScreen(
                onApply = { start, end ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(KEY_FILTER_START, start)
                    navController.previousBackStackEntry?.savedStateHandle?.set(KEY_FILTER_END, end)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}

package com.example.oftapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.oftapp.viewmodel.ExamenViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard/{rol}") {
        fun createRoute(rol: String) = "dashboard/$rol"
    }
    object RegistroExamen : Screen("registro_examen")
    object Historial : Screen("historial")
}

@Composable
fun OftAppNavGraph(navController: NavHostController) {
    // Instancia única del ViewModel compartida para la navegación
    val examenViewModel: ExamenViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { rolSeleccionado ->
                    navController.navigate(Screen.Dashboard.createRoute(rolSeleccionado)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(navArgument("rol") { type = NavType.StringType })
        ) { backStackEntry ->
            val rol = backStackEntry.arguments?.getString("rol") ?: "TECNOLOGO"
            DashboardScreen(
                rol = rol,
                onNavigateToRegistro = { navController.navigate(Screen.RegistroExamen.route) },
                onNavigateToHistorial = { navController.navigate(Screen.Historial.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.RegistroExamen.route) {
            RegistroExamenScreen(
                examenViewModel = examenViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Screen.Historial.route) {
            HistorialScreen(
                examenViewModel = examenViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
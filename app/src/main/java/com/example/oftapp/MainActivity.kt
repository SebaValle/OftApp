package com.example.oftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oftapp.data.repository.ExamenRepository
import com.example.oftapp.ui.screens.DashboardScreen
import com.example.oftapp.ui.screens.FormularioExamenScreen
import com.example.oftapp.ui.screens.HistorialExamenesScreen
import com.example.oftapp.ui.theme.OftAppTheme
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.ExamenViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ExamenRepository()
        val factory = ExamenViewModelFactory(repository)

        setContent {
            OftAppTheme {
                val navController = rememberNavController()
                val viewModel: ExamenViewModel = viewModel(factory = factory)

                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavegarFormulario = { navController.navigate("formulario") },
                            onNavegarHistorial = { navController.navigate("historial") }
                        )
                    }
                    composable("formulario") {
                        FormularioExamenScreen(
                            viewModel = viewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    }
                    composable("historial") {
                        HistorialExamenesScreen(
                            viewModel = viewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
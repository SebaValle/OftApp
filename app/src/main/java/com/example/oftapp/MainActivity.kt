package com.example.oftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.oftapp.data.repository.ExamenRepository
import com.example.oftapp.ui.screens.DashboardScreen
import com.example.oftapp.ui.screens.FormularioExamenScreen
import com.example.oftapp.ui.screens.HistorialExamenesScreen
import com.example.oftapp.ui.screens.PortalPacienteScreen
import com.example.oftapp.ui.theme.OftAppTheme
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ExamenRepository()
        val viewModel = ExamenViewModel(repository)

        setContent {
            OftAppTheme {
                val uiState by viewModel.uiState.collectAsState()
                var pantallaActual by remember { mutableStateOf("dashboard") }

                // Redirección según pantalla solicitada
                when (pantallaActual) {
                    "dashboard" -> {
                        // Si es paciente y ya inició sesión, va a su portal
                        if (uiState.sesionIniciada && uiState.rolSeleccionado == RolUsuario.PACIENTE) {
                            PortalPacienteScreen(
                                viewModel = viewModel,
                                onVerMisExamenes = { pantallaActual = "historial" },
                                onVolver = {
                                    viewModel.cerrarSesion()
                                    pantallaActual = "dashboard"
                                }
                            )
                        } else {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavegarFormulario = { pantallaActual = "formulario" },
                                onNavegarHistorial = { pantallaActual = "historial" }
                            )
                        }
                    }
                    "formulario" -> {
                        FormularioExamenScreen(
                            viewModel = viewModel,
                            onGuardadoExitoso = { pantallaActual = "historial" },
                            onVolver = { pantallaActual = "dashboard" }
                        )
                    }
                    "historial" -> {
                        HistorialExamenesScreen(
                            viewModel = viewModel,
                            onVolver = { pantallaActual = "dashboard" }
                        )
                    }
                }
            }
        }
    }
}
package com.example.oftapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
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
                val densidadActual = LocalDensity.current

                // Escala global de fuente para toda la aplicación
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = densidadActual.density,
                        fontScale = densidadActual.fontScale * uiState.escalaTexto
                    )
                ) {
                    when (pantallaActual) {
                        "dashboard" -> {
                            if (uiState.sesionIniciada && uiState.rolSeleccionado == RolUsuario.PACIENTE) {
                                PortalPacienteScreen(
                                    viewModel = viewModel,
                                    onNavegarInicio = { pantallaActual = "dashboard" },
                                    onNavegarFormulario = { pantallaActual = "formulario" },
                                    onNavegarHistorial = { pantallaActual = "historial" },
                                    onCerrarSesion = { pantallaActual = "dashboard" }
                                )
                            } else {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavegarInicio = { pantallaActual = "dashboard" },
                                    onNavegarFormulario = { pantallaActual = "formulario" },
                                    onNavegarHistorial = { pantallaActual = "historial" },
                                    onCerrarSesion = { pantallaActual = "dashboard" }
                                )
                            }
                        }
                        "formulario" -> {
                            FormularioExamenScreen(
                                viewModel = viewModel,
                                onNavegarInicio = { pantallaActual = "dashboard" },
                                onNavegarFormulario = { pantallaActual = "formulario" },
                                onNavegarHistorial = { pantallaActual = "historial" },
                                onCerrarSesion = { pantallaActual = "dashboard" }
                            )
                        }
                        "historial" -> {
                            HistorialExamenesScreen(
                                viewModel = viewModel,
                                onNavegarInicio = { pantallaActual = "dashboard" },
                                onNavegarFormulario = { pantallaActual = "formulario" },
                                onNavegarHistorial = { pantallaActual = "historial" },
                                onCerrarSesion = { pantallaActual = "dashboard" }
                            )
                        }
                    }
                }
            }
        }
    }
}
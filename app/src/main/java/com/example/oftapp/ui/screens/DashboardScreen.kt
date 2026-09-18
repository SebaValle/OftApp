package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oftapp.ui.theme.AzulMedico

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    rol: String = "TECNOLOGO", // Recibe el rol seleccionado
    onNavigateToRegistro: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onLogout: () -> Unit
) {
    // Definimos qué título mostrar según el rol
    val tituloPanel = when (rol.uppercase()) {
        "PACIENTE" -> "OftApp - Portal Paciente"
        "MEDICO", "OFTALMOLOGO" -> "OftApp - Panel Médico"
        "TECNOLOGO" -> "OftApp - Panel Tecnólogo"
        else -> "OftApp - Panel Principal"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloPanel) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulMedico,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // MOSTRAR "REGISTRAR NUEVO EXAMEN" SOLO SI ES TECNÓLOGO O ADMINISTRADOR
            if (rol.uppercase() == "TECNOLOGO" || rol.uppercase() == "ADMINISTRADOR") {
                Button(
                    onClick = onNavigateToRegistro,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Registrar Nuevo Examen")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // OPCIÓN CONSULTAR HISTORIAL (Disponible para todos los roles)
            OutlinedButton(
                onClick = onNavigateToHistorial,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Consultar Historial de Exámenes")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN CERRAR SESIÓN
            TextButton(onClick = onLogout) {
                Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
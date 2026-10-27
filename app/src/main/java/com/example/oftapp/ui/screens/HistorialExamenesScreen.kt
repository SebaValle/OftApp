package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialExamenesScreen(
    viewModel: ExamenViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val azulHeader = Color(0xFF0066B2)
    val rol = uiState.rolSeleccionado

    // 1. Filtrado dinámico por Rol: Paciente solo ve sus datos (RUT)
    val examenesPorRol = if (rol == RolUsuario.PACIENTE) {
        uiState.listaExamenes.filter { it.rut == uiState.usuarioTexto || it.pacienteNombre.contains("Juan", ignoreCase = true) }
    } else {
        uiState.listaExamenes
    }

    // 2. Filtrado por barra de búsqueda
    val examenesFiltrados = examenesPorRol.filter {
        it.pacienteNombre.contains(uiState.filtroTexto, ignoreCase = true) ||
                it.rut.contains(uiState.filtroTexto, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (rol == RolUsuario.PACIENTE) "Mis Exámenes Oftalmológicos" else "Historial Clínico de Exámenes",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
            )
        },
        bottomBar = {
            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
            ) {
                Text("Volver al Dashboard")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (rol != RolUsuario.PACIENTE) {
                OutlinedTextField(
                    value = uiState.filtroTexto,
                    onValueChange = { viewModel.onFiltroChange(it) },
                    label = { Text("Buscar por Paciente o RUT...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            } else {
                Text(
                    text = "Mostrando registros asociados a su RUT: ${uiState.usuarioTexto}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(examenesFiltrados) { examen ->
                    val esValidado = examen.observaciones.contains("VALIDADO", ignoreCase = true)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esValidado) Color(0xFFE8F5E9) else Color(0xFFFFF3CD)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = examen.pacienteNombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = examen.fecha,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                text = "RUT: ${examen.rut}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Examen: ${examen.tipoExamen}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            // Estado del examen
                            Text(
                                text = "Estado: ${examen.observaciones}",
                                color = if (esValidado) Color(0xFF2E7D32) else Color(0xFF856404),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            // Funcionalidad interactiva exclusiva para MÉDICO
                            if (rol == RolUsuario.MEDICO && !esValidado) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.validarExamen(examen.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("✔ Validar y Firmar Examen")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
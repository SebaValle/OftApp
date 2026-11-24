package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.components.OftAppScaffold
import com.example.oftapp.ui.viewmodel.ExamenViewModel

@Composable
fun PortalPacienteScreen(
    viewModel: ExamenViewModel,
    onNavegarInicio: () -> Unit,
    onNavegarFormulario: () -> Unit,
    onNavegarHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val azulHeader = Color(0xFF0066B2)

    val nombrePaciente = uiState.usuarioNombreCompleto.ifBlank { uiState.usuarioTexto.ifBlank { "Paciente" } }
    val rutPaciente = uiState.usuarioRut

    val misExamenes = uiState.listaExamenes.filter {
        it.rut.equals(rutPaciente, ignoreCase = true)
    }

    OftAppScaffold(
        tituloBarra = "OftApp - Portal Paciente",
        viewModel = viewModel,
        pantallaActual = "dashboard",
        onNavegarInicio = onNavegarInicio,
        onNavegarFormulario = onNavegarFormulario,
        onNavegarHistorial = onNavegarHistorial,
        onCerrarSesion = onCerrarSesion
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👤", fontSize = 36.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Hola, $nombrePaciente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = azulHeader
                        )
                        if (rutPaciente.isNotBlank()) {
                            Text(
                                text = "RUT: $rutPaciente",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                        Text(
                            text = "Tienes ${misExamenes.size} examen(es) registrado(s)",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Button(
                onClick = onNavegarHistorial,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
            ) {
                Text("👁️ Ver Mis Resultados y Documentos", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (misExamenes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sin exámenes clínicos aún",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = azulHeader
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cuando el equipo oftalmológico registre un examen asociado a tu RUT ($rutPaciente), aparecerá automáticamente aquí.",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            } else {
                Text(
                    text = "Resumen de tu Última Atención",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = azulHeader,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                val ultimo = misExamenes.first()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(ultimo.tipoExamen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Fecha: ${ultimo.fecha}", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ultimo.observaciones,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ultimo.observaciones.contains("VALIDADO")) Color(0xFF2E7D32) else Color(0xFF856404)
                        )
                    }
                }
            }
        }
    }
}
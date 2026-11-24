package com.example.oftapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.ui.components.OftAppScaffold
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario

@Composable
fun HistorialExamenesScreen(
    viewModel: ExamenViewModel,
    onVerDetalleExamen: (ExamenEntity) -> Unit,
    onNavegarInicio: () -> Unit,
    onNavegarFormulario: () -> Unit,
    onNavegarHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val sincronizando by viewModel.sincronizando.collectAsState()
    val mensajeSync by viewModel.mensajeSync.collectAsState()

    val esAltoContraste = uiState.altoContraste
    val azulHeader = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF0066B2)
    val rol = uiState.rolSeleccionado

    val examenesPorRol = if (rol == RolUsuario.PACIENTE) {
        uiState.listaExamenes.filter {
            it.rut.equals(uiState.usuarioRut, ignoreCase = true)
        }
    } else {
        uiState.listaExamenes
    }

    val examenesFiltrados = examenesPorRol.filter {
        val q = uiState.filtroTexto.trim()
        q.isBlank() ||
                it.pacienteNombre.contains(q, ignoreCase = true) ||
                it.rut.contains(q, ignoreCase = true) ||
                it.fecha.contains(q, ignoreCase = true) ||
                it.tipoExamen.contains(q, ignoreCase = true)
    }

    val pendientesSyncCount = uiState.listaExamenes.count { !it.sincronizado }

    OftAppScaffold(
        tituloBarra = if (rol == RolUsuario.PACIENTE) "OftApp - Mis Exámenes" else "OftApp - Historial",
        viewModel = viewModel,
        pantallaActual = "historial",
        onNavegarInicio = onNavegarInicio,
        onNavegarFormulario = onNavegarFormulario,
        onNavegarHistorial = onNavegarHistorial,
        onCerrarSesion = onCerrarSesion,
        bottomBar = {
            Button(
                onClick = onNavegarInicio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azulHeader,
                    contentColor = if (esAltoContraste) Color.Black else Color.White
                )
            ) {
                Text("Volver al Inicio", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = { viewModel.sincronizarConNube() },
                enabled = !sincronizando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (esAltoContraste) Color(0xFFFFD600)
                    else if (pendientesSyncCount > 0) Color(0xFF1565C0) else Color(0xFF2E7D32),
                    contentColor = if (esAltoContraste) Color.Black else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (sincronizando) {
                    CircularProgressIndicator(
                        color = if (esAltoContraste) Color.Black else Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sincronizando...")
                } else {
                    Text(
                        text = if (pendientesSyncCount > 0)
                            "☁️ Sincronizar con InsForge ($pendientesSyncCount pendiente/s)"
                        else
                            "☁️ Todo Sincronizado • Verificar HAPI FHIR",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (mensajeSync != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = if (esAltoContraste) Color(0xFF1E1E1E) else Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mensajeSync ?: "",
                            fontSize = 12.sp,
                            color = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF0D47A1),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.limpiarMensajeSync() }) {
                            Text("OK", fontWeight = FontWeight.Bold, color = azulHeader)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (rol == RolUsuario.MEDICO) {
                Surface(
                    color = if (esAltoContraste) Color(0xFF1E1E1E) else Color(0xFFF3E5F5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "Pendientes por validar: ${uiState.totalPendientes} | Validados: ${uiState.totalValidados}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF6A1B9A),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.filtroTexto,
                onValueChange = { viewModel.onFiltroChange(it) },
                label = {
                    Text(
                        if (rol == RolUsuario.PACIENTE)
                            "Buscar por tipo de examen o fecha..."
                        else
                            "Buscar por Paciente, RUT, Fecha o Tipo..."
                    )
                },
                colors = if (esAltoContraste) OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFFD600),
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color(0xFFFFD600),
                    unfocusedLabelColor = Color.LightGray
                ) else OutlinedTextFieldDefaults.colors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .semantics {
                        contentDescription = "Campo de búsqueda por nombre, RUT, fecha o tipo de examen"
                    }
            )

            if (rol == RolUsuario.PACIENTE) {
                Text(
                    text = if (examenesFiltrados.isEmpty())
                        "No se encontraron exámenes para el RUT ${uiState.usuarioRut}."
                    else
                        "Toca cualquier examen para ver el detalle, documento adjunto o escucharlo por voz:",
                    fontSize = 13.sp,
                    color = if (esAltoContraste) Color.White else Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(examenesFiltrados) { examen ->
                    val esValidado = examen.observaciones.contains("VALIDADO", ignoreCase = true)

                    Card(
                        onClick = { onVerDetalleExamen(examen) },
                        border = if (esAltoContraste) BorderStroke(2.dp, Color(0xFFFFD600)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Examen ${examen.tipoExamen} de ${examen.pacienteNombre}, fecha ${examen.fecha}, estado ${examen.observaciones}."
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (esAltoContraste) Color(0xFF121212)
                            else if (esValidado) Color(0xFFE8F5E9) else Color(0xFFFFF3CD)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = examen.pacienteNombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (esAltoContraste) Color.White else Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = if (esAltoContraste) Color.Black
                                    else if (examen.sincronizado) Color(0xFFE3F2FD) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (examen.sincronizado) "☁️ Sincronizado" else "⏳ Pendiente Sync",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (esAltoContraste) Color(0xFFFFD600)
                                        else if (examen.sincronizado) Color(0xFF1565C0) else Color(0xFFC62828),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "RUT: ${examen.rut} • Fecha: ${examen.fecha}",
                                fontSize = 12.sp,
                                color = if (esAltoContraste) Color(0xFFFFF59D) else Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Examen: ${examen.tipoExamen}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (esAltoContraste) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Estado: ${examen.observaciones}",
                                color = if (esAltoContraste) Color(0xFFFFD600)
                                else if (esValidado) Color(0xFF2E7D32) else Color(0xFF856404),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (examen.documentoUri != null)
                                    "📎 Tiene documento adjunto • Toca para visualizar y escuchar ›"
                                else
                                    "🔊 Toca para ver ficha clínica y lectura en voz alta ›",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = azulHeader
                            )

                            if (rol == RolUsuario.MEDICO && !esValidado) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.validarExamen(examen.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF2E7D32),
                                        contentColor = if (esAltoContraste) Color.Black else Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text("✔ Validar y Firmar Examen", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
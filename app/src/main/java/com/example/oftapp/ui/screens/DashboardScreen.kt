package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.components.OftAppScaffold
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExamenViewModel,
    onNavegarInicio: () -> Unit,
    onNavegarFormulario: () -> Unit,
    onNavegarHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val azulHeader = Color(0xFF0066B2)

    if (!uiState.sesionIniciada) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("OftApp - Autenticación VcM", color = Color.White) },
                    actions = {
                        TextButton(onClick = {
                            val siguiente = when (uiState.escalaTexto) {
                                1.0f -> 1.25f
                                1.25f -> 1.5f
                                else -> 1.0f
                            }
                            viewModel.onCambiarEscalaTexto(siguiente)
                        }) {
                            Text("A+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "OftApp VcM", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = azulHeader)
                Text(
                    text = "Plataforma de Trazabilidad Oftalmológica",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🔑 Accesos Directos (Clave: 1234):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = azulHeader)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• pedro → Paciente (Pedro Gómez)", fontSize = 11.sp)
                        Text("• tecno → Tecnólogo (Registrar examen)", fontSize = 11.sp)
                        Text("• medico → Médico (Validar y firmar)", fontSize = 11.sp)
                        Text("• admin → Administrador (Dashboard KPIs)", fontSize = 11.sp)
                    }
                }

                OutlinedTextField(
                    value = uiState.usuarioTexto,
                    onValueChange = { viewModel.onUsuarioTextoChange(it) },
                    label = { Text("Usuario") },
                    isError = uiState.errorLogin != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = uiState.passwordTexto,
                    onValueChange = { viewModel.onPasswordTextoChange(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = uiState.errorLogin != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                if (uiState.errorLogin != null) {
                    Text(
                        text = uiState.errorLogin!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = { viewModel.iniciarSesion() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                ) {
                    Text("Iniciar Sesión", fontSize = 16.sp)
                }
            }
        }
    } else {
        val rol = uiState.rolSeleccionado

        OftAppScaffold(
            tituloBarra = "OftApp - Panel ${rol.name}",
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
                when (rol) {
                    RolUsuario.TECNOLOGO -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("👨‍⚕️", fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("Hola, Tecnólogo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = azulHeader)
                                    Text("¿Qué deseas hacer hoy en terreno?", fontSize = 13.sp, color = Color.DarkGray)
                                }
                            }
                        }

                        Button(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                        ) {
                            Text("➕ Registrar Nuevo Examen", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📋 Consultar Historial Técnico")
                        }

                        val ultimoExamen = uiState.listaExamenes.firstOrNull()
                        if (ultimoExamen != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Última Atención Registrada",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = azulHeader,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(ultimoExamen.pacienteNombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("Examen: ${ultimoExamen.tipoExamen} • ${ultimoExamen.fecha}", fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = ultimoExamen.observaciones,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ultimoExamen.observaciones.contains("VALIDADO")) Color(0xFF2E7D32) else Color(0xFF856404)
                                    )
                                }
                            }
                        }
                    }

                    RolUsuario.MEDICO -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🩺", fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("Hola, Dr. Martínez", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF6A1B9A))
                                    Text("Módulo Médico Evaluador", fontSize = 13.sp, color = Color.DarkGray)
                                }
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (uiState.totalPendientes > 0) Color(0xFFFFF3CD) else Color(0xFFE8F5E9)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (uiState.totalPendientes > 0)
                                        "⚠️ Tienes ${uiState.totalPendientes} examen(es) pendiente(s) de firma"
                                    else
                                        "✔ Todos los exámenes están validados",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (uiState.totalPendientes > 0) Color(0xFF856404) else Color(0xFF2E7D32)
                                )
                            }
                        }

                        Button(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                        ) {
                            Text("✔ Validar y Firmar Exámenes (${uiState.totalPendientes})", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📝 Ingresar Diagnóstico / Examen")
                        }
                    }

                    RolUsuario.ADMINISTRADOR -> {
                        Text(
                            text = "Resumen del Operativo VcM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = azulHeader,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total", fontSize = 11.sp, color = Color.Gray)
                                    Text("${uiState.totalExamenes}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = azulHeader)
                                    Text("Exámenes", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Pendientes", fontSize = 11.sp, color = Color.Gray)
                                    Text("${uiState.totalPendientes}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF856404))
                                    Text("Por Firmar", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Validados", fontSize = 11.sp, color = Color.Gray)
                                    Text("${uiState.totalValidados}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                    Text("Con Firmas", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Avance de Trazabilidad VcM", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("${(uiState.porcentajeAvance * 100).toInt()}%", fontWeight = FontWeight.Bold, color = azulHeader, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { uiState.porcentajeAvance },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = Color(0xFF2E7D32),
                                    trackColor = Color(0xFFE0E0E0),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Acciones de Control",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = azulHeader,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        Button(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                        ) {
                            Text("📊 Auditar Historial Completo")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("➕ Registrar Atención como Superusuario")
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
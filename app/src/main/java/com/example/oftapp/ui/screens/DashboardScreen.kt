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
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExamenViewModel,
    onNavegarFormulario: () -> Unit,
    onNavegarHistorial: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val azulHeader = Color(0xFF0066B2)

    if (!uiState.sesionIniciada) {
        // Vista 1: Login Directo
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("OftApp - Autenticación VcM", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "OftApp VcM",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulHeader
                )
                Text(
                    text = "Plataforma de Trazabilidad Oftalmológica",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                ) {
                    Text("Iniciar Sesión", fontSize = 16.sp)
                }
            }
        }
    } else {
        // Vista 2: Dashboards por Rol
        val rol = uiState.rolSeleccionado

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("OftApp - Panel ${rol.name}", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
                )
            }
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
                    RolUsuario.PACIENTE -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Portal del Paciente: Pedro Gómez", fontWeight = FontWeight.Bold, color = azulHeader)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Consulte el estado de sus atenciones registradas.", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                        ) {
                            Text("Consultar Mis Exámenes")
                        }
                    }

                    RolUsuario.TECNOLOGO -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Módulo Tecnólogo Operativo", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Captura en terreno con cámara y selector de fecha nativo.", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                        ) {
                            Text("Registrar Nuevo Examen")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Consultar Historial Técnico")
                        }
                    }

                    RolUsuario.MEDICO -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Módulo Médico Evaluador", fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Revisión de exámenes pendientes y firma de diagnósticos.", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                        ) {
                            Text("Validar y Firmar Exámenes")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Ingresar Diagnóstico / Examen")
                        }
                    }

                    RolUsuario.ADMINISTRADOR -> {
                        // DASHBOARD DE ADMINISTRADOR (KPIs + Avance + Acciones)
                        Text(
                            text = "Resumen del Operativo VcM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = azulHeader,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        // 1. Tarjetas KPI
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

                        // 2. Barra de Avance
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

                        // 3. Botones de Control
                        Button(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                        ) {
                            Text("📊 Auditar Historial Completo")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("➕ Registrar Atención como Superusuario")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                TextButton(
                    onClick = { viewModel.cerrarSesion() }
                ) {
                    Text("Cerrar Sesión", color = Color.Red, fontSize = 16.sp)
                }
            }
        }
    }
}
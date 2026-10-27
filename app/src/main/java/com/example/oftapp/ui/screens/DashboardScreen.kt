package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
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
        // Vista 1: Login
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
                    text = "Bienvenido a OftApp",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = azulHeader
                )
                Text(
                    text = "Sistema de Trazabilidad de Exámenes",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Tarjeta guía de credenciales simplificadas
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🔑 Usuarios de Prueba (Clave general: 1234):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("• Paciente: pedro", fontSize = 11.sp)
                        Text("• Tecnólogo: tecno", fontSize = 11.sp)
                        Text("• Médico: medico", fontSize = 11.sp)
                        Text("• Administrador: admin", fontSize = 11.sp)
                    }
                }

                OutlinedTextField(
                    value = uiState.usuarioTexto,
                    onValueChange = { viewModel.onUsuarioTextoChange(it) },
                    label = { Text("Usuario") },
                    isError = uiState.errorLogin != null,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = uiState.passwordTexto,
                    onValueChange = { viewModel.onPasswordTextoChange(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = uiState.errorLogin != null,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
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
                    Text("Ingresar")
                }
            }
        }
    } else {
        // Vista 2: Paneles por Rol
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (rol) {
                    RolUsuario.PACIENTE -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Portal de Paciente: Pedro Gómez", fontWeight = FontWeight.Bold, color = azulHeader)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Revisión exclusiva de sus exámenes oftálmicos registrados.", fontSize = 13.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
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
                                Text("Módulo Tecnólogo", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Registrar exámenes en terreno con fecha y captura de cámara.", fontSize = 13.sp)
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
                                Text("Módulo Médico", fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Revisar exámenes pendientes y firmar/validar diagnósticos.", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                        ) {
                            Text("Ingresar Diagnóstico / Examen")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Validar Exámenes y Diagnósticos")
                        }
                    }

                    RolUsuario.ADMINISTRADOR -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Panel de Administración VcM", fontWeight = FontWeight.Bold, color = Color(0xFF856404))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Supervisión global de registros y trazabilidad del operativo.", fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = onNavegarFormulario,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                        ) {
                            Text("Registrar Examen (Superusuario)")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onNavegarHistorial,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Auditar Historial Completo")
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
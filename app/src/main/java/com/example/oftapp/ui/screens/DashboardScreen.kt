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

    var mostrarRegistroPaciente by remember { mutableStateOf(false) }
    var regNombre by remember { mutableStateOf("") }
    var regRut by remember { mutableStateOf("") }
    var regUsuario by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regError by remember { mutableStateOf<String?>(null) }
    var registrandoPaciente by remember { mutableStateOf(false) }

    var mostrarDialogoPersonal by remember { mutableStateOf(false) }
    var admNombre by remember { mutableStateOf("") }
    var admRut by remember { mutableStateOf("") }
    var admUsuario by remember { mutableStateOf("") }
    var admPassword by remember { mutableStateOf("") }
    var admRolSeleccionado by remember { mutableStateOf("MEDICO") }
    var admGuardando by remember { mutableStateOf(false) }
    var admError by remember { mutableStateOf<String?>(null) }
    var bannerAdmin by remember { mutableStateOf<String?>(null) }

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
                    modifier = Modifier.padding(bottom = 24.dp)
                )

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

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        regNombre = ""
                        regRut = ""
                        regUsuario = ""
                        regPassword = ""
                        regError = null
                        mostrarRegistroPaciente = true
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("¿Eres paciente nuevo? Crear mi cuenta")
                }
            }
        }

        if (mostrarRegistroPaciente) {
            AlertDialog(
                onDismissRequest = { if (!registrandoPaciente) mostrarRegistroPaciente = false },
                title = { Text("Registro de Nuevo Paciente", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regNombre,
                            onValueChange = { regNombre = it },
                            label = { Text("Nombre Completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = regRut,
                            onValueChange = { regRut = it },
                            label = { Text("RUT (ej: 12.345.678-9)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = regUsuario,
                            onValueChange = { regUsuario = it },
                            label = { Text("Nombre de Usuario") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (regError != null) {
                            Text(
                                text = regError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = !registrandoPaciente,
                        colors = ButtonDefaults.buttonColors(containerColor = azulHeader),
                        onClick = {
                            registrandoPaciente = true
                            regError = null
                            viewModel.crearCuentaUsuario(
                                username = regUsuario,
                                password = regPassword,
                                nombreCompleto = regNombre,
                                rut = regRut,
                                rol = "PACIENTE",
                                iniciarSesionAutomatico = true
                            ) { exito, mensaje ->
                                registrandoPaciente = false
                                if (exito) {
                                    mostrarRegistroPaciente = false
                                } else {
                                    regError = mensaje
                                }
                            }
                        }
                    ) {
                        Text(if (registrandoPaciente) "Creando..." else "Registrarme")
                    }
                },
                dismissButton = {
                    TextButton(
                        enabled = !registrandoPaciente,
                        onClick = { mostrarRegistroPaciente = false }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    } else {
        val rol = uiState.rolSeleccionado
        val nombreActual = uiState.usuarioNombreCompleto.ifBlank { uiState.usuarioTexto }

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
                                    Text("Hola, $nombreActual", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = azulHeader)
                                    Text("Tecnólogo Médico • RUT: ${uiState.usuarioRut}", fontSize = 12.sp, color = Color.DarkGray)
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
                                    Text("Hola, $nombreActual", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF6A1B9A))
                                    Text("Médico Evaluador • RUT: ${uiState.usuarioRut}", fontSize = 12.sp, color = Color.DarkGray)
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
                            text = "Resumen del Operativo VcM ($nombreActual)",
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

                        if (bannerAdmin != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = bannerAdmin ?: "",
                                        color = Color(0xFF2E7D32),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = { bannerAdmin = null }) {
                                        Text("OK")
                                    }
                                }
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
                            onClick = {
                                admNombre = ""
                                admRut = ""
                                admUsuario = ""
                                admPassword = ""
                                admRolSeleccionado = "MEDICO"
                                admError = null
                                mostrarDialogoPersonal = true
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("👥 Crear Médico / Tecnólogo en InsForge", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

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

                        if (mostrarDialogoPersonal) {
                            AlertDialog(
                                onDismissRequest = { if (!admGuardando) mostrarDialogoPersonal = false },
                                title = { Text("Registrar Personal Clínico", fontWeight = FontWeight.Bold) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Rol asignado:", fontSize = 13.sp)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            FilterChip(
                                                selected = admRolSeleccionado == "MEDICO",
                                                onClick = { admRolSeleccionado = "MEDICO" },
                                                label = { Text("Médico") }
                                            )
                                            FilterChip(
                                                selected = admRolSeleccionado == "TECNOLOGO",
                                                onClick = { admRolSeleccionado = "TECNOLOGO" },
                                                label = { Text("Tecnólogo") }
                                            )
                                        }
                                        OutlinedTextField(
                                            value = admNombre,
                                            onValueChange = { admNombre = it },
                                            label = { Text("Nombre Completo") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = admRut,
                                            onValueChange = { admRut = it },
                                            label = { Text("RUT Profesional") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = admUsuario,
                                            onValueChange = { admUsuario = it },
                                            label = { Text("Usuario de Acceso") },
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = admPassword,
                                            onValueChange = { admPassword = it },
                                            label = { Text("Contraseña Inicial") },
                                            visualTransformation = PasswordVisualTransformation(),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        if (admError != null) {
                                            Text(
                                                text = admError ?: "",
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        enabled = !admGuardando,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        onClick = {
                                            admGuardando = true
                                            admError = null
                                            viewModel.crearCuentaUsuario(
                                                username = admUsuario,
                                                password = admPassword,
                                                nombreCompleto = admNombre,
                                                rut = admRut,
                                                rol = admRolSeleccionado,
                                                iniciarSesionAutomatico = false
                                            ) { exito, mensaje ->
                                                admGuardando = false
                                                if (exito) {
                                                    bannerAdmin = mensaje
                                                    mostrarDialogoPersonal = false
                                                } else {
                                                    admError = mensaje
                                                }
                                            }
                                        }
                                    ) {
                                        Text(if (admGuardando) "Guardando..." else "Crear Usuario")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        enabled = !admGuardando,
                                        onClick = { mostrarDialogoPersonal = false }
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
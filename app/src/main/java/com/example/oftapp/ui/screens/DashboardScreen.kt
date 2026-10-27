package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    var usuarioText by remember { mutableStateOf("") }
    var passText by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }

    val azulHeader = Color(0xFF0066B2)

    if (!uiState.sesionIniciada) {
        // Vista 1: Inicio de Sesión / Selección de Roles
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("OftApp - Inicio de Sesión", color = Color.White) },
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
                    text = "Consulta y trazabilidad de exámenes oftalmológicos",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = usuarioText,
                    onValueChange = { usuarioText = it },
                    label = { Text("Usuario (ficticio)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = passText,
                    onValueChange = { passText = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = menuExpanded,
                    onExpandedChange = { menuExpanded = !menuExpanded },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.rolSeleccionado.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol de usuario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        RolUsuario.values().forEach { rol ->
                            DropdownMenuItem(
                                text = { Text(rol.name) },
                                onClick = {
                                    viewModel.seleccionarRol(rol)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
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
        // Vista 2: Panel según Rol (Paciente o Tecnólogo/Médico)
        val esPaciente = uiState.rolSeleccionado == RolUsuario.PACIENTE
        val tituloPanel = if (esPaciente) "OftApp - Portal Paciente" else "OftApp - Panel ${uiState.rolSeleccionado.name.lowercase().capitalize()}"

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(tituloPanel, color = Color.White) },
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
                if (!esPaciente) {
                    Button(
                        onClick = onNavegarFormulario,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
                    ) {
                        Text("Registrar Nuevo Examen")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedButton(
                    onClick = onNavegarHistorial,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Consultar Historial de Exámenes")
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(
                    onClick = { viewModel.cerrarSesion() }
                ) {
                    Text("Cerrar Sesión", color = Color.Red, fontSize = 16.sp)
                }
            }
        }
    }
}
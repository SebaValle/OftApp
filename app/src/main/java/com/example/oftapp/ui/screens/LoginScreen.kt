package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.oftapp.ui.theme.AzulMedico

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit // Ahora envía el rol como String
) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf("TECNOLOGO") }
    var expandedRol by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    val rolesDisponibles = listOf("TECNOLOGO", "MEDICO", "PACIENTE", "ADMINISTRADOR")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OftApp - Inicio de Sesión") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido a OftApp",
                style = MaterialTheme.typography.headlineMedium,
                color = AzulMedico
            )
            Text(
                text = "Consulta y trazabilidad de exámenes oftalmológicos",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario (ficticio)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Rol
            ExposedDropdownMenuBox(
                expanded = expandedRol,
                onExpandedChange = { expandedRol = !expandedRol },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = rolSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol de usuario") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRol,
                    onDismissRequest = { expandedRol = false }
                ) {
                    rolesDisponibles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol) },
                            onClick = {
                                rolSeleccionado = rol
                                expandedRol = false
                            }
                        )
                    }
                }
            }

            if (mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (usuario.isNotBlank() && clave.isNotBlank()) {
                        onLoginSuccess(rolSeleccionado) // Pasa el rol seleccionado
                    } else {
                        mensajeError = "Por favor ingrese usuario y contraseña"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ingresar")
            }
        }
    }
}
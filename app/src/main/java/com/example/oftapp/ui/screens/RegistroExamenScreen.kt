package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oftapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroExamenScreen(
    onVolver: () -> Unit
) {
    var paciente by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Examen") },
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
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = paciente,
                onValueChange = { paciente = it },
                label = { Text("Nombre / ID Ficticio del Paciente") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones del procedimiento") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { mensajeExito = true },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Guardar Examen")
            }

            if (mensajeExito) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("¡Examen registrado con éxito (Simulado)!", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Dashboard")
            }
        }
    }
}
package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oftapp.ui.theme.*
import com.example.oftapp.viewmodel.ExamenViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroExamenScreen(
    examenViewModel: ExamenViewModel,
    onVolver: () -> Unit
) {
    var pacienteNombre by remember { mutableStateOf("") }
    var pacienteRut by remember { mutableStateOf("") }
    var tipoExamen by remember { mutableStateOf("") }
    var ojoEvaluado by remember { mutableStateOf("Ambos (AO)") }
    var sucursal by remember { mutableStateOf("Sucursal Central") }
    var profesional by remember { mutableStateOf("Tecnólogo Médico") }
    var observaciones by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Examen") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulMedico,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = pacienteNombre,
                    onValueChange = { pacienteNombre = it },
                    label = { Text("Nombre Completo del Paciente") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = pacienteRut,
                    onValueChange = { pacienteRut = it },
                    label = { Text("RUT del Paciente") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = tipoExamen,
                    onValueChange = { tipoExamen = it },
                    label = { Text("Tipo de Examen (ej: Campimetría, OCT, Tonometría)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ojoEvaluado,
                    onValueChange = { ojoEvaluado = it },
                    label = { Text("Ojo Evaluado (OD / OI / AO)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = sucursal,
                    onValueChange = { sucursal = it },
                    label = { Text("Sucursal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Observaciones clínicas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (mensajeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(mensajeError, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (pacienteNombre.isBlank() || pacienteRut.isBlank() || tipoExamen.isBlank()) {
                            mensajeError = "Por favor completa el Nombre, RUT y Tipo de Examen."
                        } else {
                            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                            examenViewModel.guardarExamen(
                                pacienteId = pacienteRut,
                                pacienteNombre = pacienteNombre,
                                tipoExamen = tipoExamen,
                                ojoEvaluado = ojoEvaluado,
                                fecha = fechaActual,
                                sucursal = sucursal,
                                profesional = profesional,
                                estado = "Completado",
                                observaciones = observaciones,
                                onExito = {
                                    onVolver()
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Guardar Examen Real")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Dashboard")
                }
            }
        }
    )
}
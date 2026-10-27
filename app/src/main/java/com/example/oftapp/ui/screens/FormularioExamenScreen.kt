package com.example.oftapp.ui.screens

import android.app.DatePickerDialog
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioExamenScreen(
    viewModel: ExamenViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val azulHeader = Color(0xFF0066B2)

    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher para recurso nativo: Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoBitmap = bitmap
        }
    }

    // Selector nativo de fecha
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val fechaSel = "$dayOfMonth/${month + 1}/$year"
            viewModel.onFechaChange(fechaSel)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Examen", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.pacienteNombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre / ID Ficticio del Paciente") },
                isError = uiState.errorNombre != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.errorNombre != null) {
                Text(uiState.errorNombre!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = uiState.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT del Paciente") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones del procedimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4
            )

            // Recurso Nativo 1: DatePicker
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.fecha.isBlank()) "Seleccionar Fecha del Examen" else "Fecha: ${uiState.fecha}")
            }

            // Recurso Nativo 2: Cámara del dispositivo
            Button(
                onClick = { cameraLauncher.launch() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6572))
            ) {
                Text("📷 Tomar Captura / Foto del Examen")
            }

            fotoBitmap?.let { bitmap ->
                Text("Evidencia Capturada:", style = MaterialTheme.typography.labelLarge)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captura de Examen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.guardarExamen {
                        onVolver()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
            ) {
                Text("Guardar Examen")
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Volver al Dashboard")
            }
        }
    }
}
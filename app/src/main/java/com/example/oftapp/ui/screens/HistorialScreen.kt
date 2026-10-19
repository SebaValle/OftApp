package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oftapp.ui.theme.*
import com.example.oftapp.viewmodel.ExamenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    examenViewModel: ExamenViewModel,
    onVolver: () -> Unit
) {
    // Escucha la lista de exámenes desde Room en tiempo real
    val listaExamenes by examenViewModel.examenes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Exámenes") },
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
            if (listaExamenes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay exámenes registrados aún.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(listaExamenes) { examen ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = examen.pacienteNombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(text = "RUT: ${examen.pacienteRut}")
                                Text(text = "Examen: ${examen.tipoExamen} (${examen.ojoEvaluado})")
                                Text(text = "Fecha: ${examen.fecha}")
                                Text(text = "Sucursal: ${examen.sucursal}")
                                Text(
                                    text = "Estado: ${examen.estado}",
                                    color = AzulMedico,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                if (examen.observaciones.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Obs: ${examen.observaciones}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Dashboard")
            }
        }
    }
}
package com.example.oftapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OftAppScaffold(
    tituloBarra: String,
    viewModel: ExamenViewModel,
    pantallaActual: String,
    onNavegarInicio: () -> Unit,
    onNavegarFormulario: () -> Unit,
    onNavegarHistorial: () -> Unit,
    onCerrarSesion: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val rol = uiState.rolSeleccionado
    val escala = uiState.escalaTexto
    val esAltoContraste = uiState.altoContraste

    val azulHeader = if (esAltoContraste) Color(0xFF000000) else Color(0xFF0066B2)
    val acentoTexto = if (esAltoContraste) Color(0xFFFFD600) else Color.White
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = if (esAltoContraste) Color(0xFF121212) else MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "OftApp VcM",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF0066B2)
                    )
                    Text(
                        text = "Rol: ${rol.name}",
                        fontSize = 13.sp,
                        color = if (esAltoContraste) Color(0xFFFFF59D) else Color.Gray
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (esAltoContraste) Color(0xFFFFD600) else DividerDefaults.color
                )

                // Opción 1: Inicio (Para todos los roles)
                NavigationDrawerItem(
                    icon = { Text("🏠", fontSize = 18.sp) },
                    label = {
                        Text(
                            "Inicio / Panel Principal",
                            color = if (esAltoContraste) Color.White else Color.Unspecified
                        )
                    },
                    selected = pantallaActual == "dashboard",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavegarInicio()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Opción 2: Registrar Examen (Solo Tecnólogo, Médico y Admin)
                if (rol != RolUsuario.PACIENTE) {
                    NavigationDrawerItem(
                        icon = { Text("➕", fontSize = 18.sp) },
                        label = {
                            Text(
                                "Registrar Nuevo Examen",
                                color = if (esAltoContraste) Color.White else Color.Unspecified
                            )
                        },
                        selected = pantallaActual == "formulario",
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavegarFormulario()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                // Opción 3: Consultar / Validar Exámenes (Para todos los roles)
                NavigationDrawerItem(
                    icon = { Text("📋", fontSize = 18.sp) },
                    label = {
                        Text(
                            when (rol) {
                                RolUsuario.PACIENTE -> "Consultar Mis Exámenes"
                                RolUsuario.MEDICO -> "Validar y Firmar Exámenes"
                                else -> "Historial Clínico"
                            },
                            color = if (esAltoContraste) Color.White else Color.Unspecified
                        )
                    },
                    selected = pantallaActual == "historial",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavegarHistorial()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (esAltoContraste) Color(0xFFFFD600) else DividerDefaults.color
                )

                // Sección Accesibilidad: Tamaño de Fuente y Alto Contraste
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "👁️ Accesibilidad: Tamaño de Texto",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF0066B2),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BotonTamanoTexto(
                            etiqueta = "Normal",
                            seleccionado = escala == 1.0f,
                            esAltoContraste = esAltoContraste,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.onCambiarEscalaTexto(1.0f) }
                        )
                        BotonTamanoTexto(
                            etiqueta = "Grande",
                            seleccionado = escala == 1.25f,
                            esAltoContraste = esAltoContraste,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.onCambiarEscalaTexto(1.25f) }
                        )
                        BotonTamanoTexto(
                            etiqueta = "Extra",
                            seleccionado = escala == 1.5f,
                            esAltoContraste = esAltoContraste,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.onCambiarEscalaTexto(1.5f) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "🌗 Contraste Visual (WCAG 2.2 AA)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF0066B2),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedButton(
                        onClick = { viewModel.onToggleAltoContraste() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .semantics {
                                contentDescription = if (esAltoContraste)
                                    "Desactivar modo alto contraste"
                                else
                                    "Activar modo alto contraste para baja visión"
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (esAltoContraste) Color(0xFFFFD600) else Color.Transparent,
                            contentColor = if (esAltoContraste) Color.Black else Color(0xFF0066B2)
                        )
                    ) {
                        Text(
                            text = if (esAltoContraste) "☀️ Alto Contraste: ACTIVADO" else "🌗 Activar Alto Contraste",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(color = if (esAltoContraste) Color(0xFFFFD600) else DividerDefaults.color)

                // Cerrar Sesión unificado
                NavigationDrawerItem(
                    icon = { Text("🚪", fontSize = 18.sp) },
                    label = {
                        Text(
                            "Cerrar Sesión",
                            color = if (esAltoContraste) Color(0xFFFF5252) else Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.cerrarSesion()
                        onCerrarSesion()
                    },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = if (esAltoContraste) Color(0xFF000000) else MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text(tituloBarra, color = acentoTexto, maxLines = 1) },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.semantics {
                                contentDescription = "Abrir menú de navegación y accesibilidad"
                            }
                        ) {
                            Text("☰", color = acentoTexto, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
                                val siguienteEscala = when (escala) {
                                    1.0f -> 1.25f
                                    1.25f -> 1.5f
                                    else -> 1.0f
                                }
                                viewModel.onCambiarEscalaTexto(siguienteEscala)
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Cambiar tamaño de letra. Tamaño actual ${(escala * 100).toInt()} por ciento"
                            }
                        ) {
                            Text(
                                text = "A+",
                                color = acentoTexto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
                )
            },
            bottomBar = bottomBar,
            content = content
        )
    }
}

@Composable
private fun BotonTamanoTexto(
    etiqueta: String,
    seleccionado: Boolean,
    esAltoContraste: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorActivo = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF0066B2)
    val colorTextoActivo = if (esAltoContraste) Color.Black else Color.White
    val colorTextoInactivo = if (esAltoContraste) Color.White else Color(0xFF0066B2)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (seleccionado) colorActivo else Color.Transparent,
            contentColor = if (seleccionado) colorTextoActivo else colorTextoInactivo
        )
    ) {
        Text(text = etiqueta, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
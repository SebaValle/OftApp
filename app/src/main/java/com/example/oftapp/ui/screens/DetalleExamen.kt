package com.example.oftapp.ui.screens

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.ui.components.OftAppScaffold
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@Composable
fun DetalleExamenScreen(
    examen: ExamenEntity,
    viewModel: ExamenViewModel,
    onVolverHistorial: () -> Unit,
    onNavegarInicio: () -> Unit,
    onNavegarFormulario: () -> Unit,
    onNavegarHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val esAltoContraste = uiState.altoContraste
    val azulHeader = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF0066B2)
    val rol = uiState.rolSeleccionado

    val examenActual = uiState.listaExamenes.find { it.id == examen.id } ?: examen
    val esValidado = examenActual.observaciones.contains("VALIDADO", ignoreCase = true)

    // Motor TextToSpeech nativo para lectura accesible de resultados
    var ttsListo by remember { mutableStateOf(false) }
    var leyendoVoz by remember { mutableStateOf(false) }

    val tts = remember {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val resCl = engine?.setLanguage(Locale.forLanguageTag("es-CL"))
                if (resCl == TextToSpeech.LANG_MISSING_DATA || resCl == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine?.setLanguage(Locale.forLanguageTag("es-ES"))
                }
                ttsListo = true
            }
        }
        engine
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val textoLecturaAccesible = buildString {
        append("Detalle clínico de examen oftalmológico. ")
        append("Tipo de examen: ${examenActual.tipoExamen}. ")
        append("Paciente: ${examenActual.pacienteNombre}, RUT ${examenActual.rut}. ")
        append("Fecha de atención: ${examenActual.fecha}. ")
        append("Estado y observaciones: ${examenActual.observaciones}.")
    }

    var bitmapDocumento by remember { mutableStateOf<Bitmap?>(null) }
    var esPdf by remember { mutableStateOf(false) }
    var paginaActual by remember { mutableIntStateOf(0) }
    var totalPaginas by remember { mutableIntStateOf(0) }
    var escalaZoom by remember { mutableFloatStateOf(1f) }
    var desplazamiento by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(examenActual.documentoUri, paginaActual) {
        val uriString = examenActual.documentoUri ?: return@LaunchedEffect
        try {
            val uri = Uri.parse(uriString)
            val mimeType = context.contentResolver.getType(uri) ?: ""
            if (mimeType.contains("pdf") || uriString.lowercase().endsWith(".pdf")) {
                esPdf = true
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    val renderer = PdfRenderer(pfd)
                    totalPaginas = renderer.pageCount
                    if (paginaActual in 0 until totalPaginas) {
                        val page = renderer.openPage(paginaActual)
                        val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                        bitmapDocumento = bmp
                    }
                    renderer.close()
                    pfd.close()
                }
            } else {
                esPdf = false
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    bitmapDocumento = BitmapFactory.decodeStream(stream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    OftAppScaffold(
        tituloBarra = "OftApp - Detalle Clínico",
        viewModel = viewModel,
        pantallaActual = "historial",
        onNavegarInicio = onNavegarInicio,
        onNavegarFormulario = onNavegarFormulario,
        onNavegarHistorial = onNavegarHistorial,
        onCerrarSesion = onCerrarSesion,
        bottomBar = {
            Button(
                onClick = {
                    tts.stop()
                    onVolverHistorial()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = azulHeader,
                    contentColor = if (esAltoContraste) Color.Black else Color.White
                )
            ) {
                Text("Volver al Historial", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Botón de Lectura en Voz Alta (TextToSpeech - Accesibilidad WCAG 2.2 AA)
            Button(
                onClick = {
                    if (leyendoVoz) {
                        tts.stop()
                        leyendoVoz = false
                    } else if (ttsListo) {
                        leyendoVoz = true
                        tts.speak(textoLecturaAccesible, TextToSpeech.QUEUE_FLUSH, null, "examen_${examenActual.id}")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (esAltoContraste) Color(0xFFFFD600)
                    else if (leyendoVoz) Color(0xFFC62828) else Color(0xFF1565C0),
                    contentColor = if (esAltoContraste) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics {
                        contentDescription = if (leyendoVoz)
                            "Detener lectura en voz alta"
                        else
                            "Escuchar resultado del examen en voz alta"
                    }
            ) {
                Text(
                    text = if (leyendoVoz) "⏹️ Detener Lectura en Voz Alta" else "🔊 Escuchar Resultado en Voz Alta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 1. Ficha de Datos Clínicos del Examen
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) {
                        contentDescription = textoLecturaAccesible
                    },
                border = if (esAltoContraste) BorderStroke(2.dp, Color(0xFFFFD600)) else null,
                colors = CardDefaults.cardColors(
                    containerColor = if (esAltoContraste) Color(0xFF121212) else Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = examenActual.tipoExamen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = azulHeader,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = if (esAltoContraste) Color.Black
                            else if (esValidado) Color(0xFFE8F5E9) else Color(0xFFFFF3CD),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (esValidado) "VALIDADO" else "PENDIENTE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (esAltoContraste) Color(0xFFFFD600)
                                else if (esValidado) Color(0xFF2E7D32) else Color(0xFF856404),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = if (esAltoContraste) Color(0xFFFFD600) else DividerDefaults.color
                    )

                    Text(
                        "Paciente: ${examenActual.pacienteNombre}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (esAltoContraste) Color.White else Color.Black
                    )
                    Text(
                        "RUT: ${examenActual.rut}",
                        fontSize = 13.sp,
                        color = if (esAltoContraste) Color(0xFFFFF59D) else Color.Gray
                    )
                    Text(
                        "Fecha de Atención: ${examenActual.fecha}",
                        fontSize = 13.sp,
                        color = if (esAltoContraste) Color(0xFFFFF59D) else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Observaciones / Estado:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (esAltoContraste) Color(0xFFFFD600) else Color.Black
                    )
                    Text(
                        examenActual.observaciones,
                        fontSize = 14.sp,
                        color = if (esAltoContraste) Color.White else Color.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = {
                            generarYDescargarPdfExamen(context, examenActual, bitmapDocumento)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("📥 Descargar Informe en PDF", fontWeight = FontWeight.Bold, color = azulHeader)
                    }

                    if (rol == RolUsuario.MEDICO && !esValidado) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.validarExamen(examenActual.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (esAltoContraste) Color(0xFFFFD600) else Color(0xFF2E7D32),
                                contentColor = if (esAltoContraste) Color.Black else Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("✔ Validar y Firmar este Examen", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Visor Clínico de Documento (PDF o Imagen con Zoom)
            Text(
                text = "Documento / Evidencia Clínica Adjunta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = azulHeader
            )

            if (bitmapDocumento != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (esPdf) "Visor PDF (Pág. ${paginaActual + 1}/$totalPaginas)" else "Imagen Clínica (${(escalaZoom * 100).toInt()}%)",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilledTonalButton(
                                    onClick = { escalaZoom = (escalaZoom - 0.5f).coerceAtLeast(1f) },
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) { Text("- Zoom") }

                                FilledTonalButton(
                                    onClick = { escalaZoom = (escalaZoom + 0.5f).coerceAtMost(4f) },
                                    contentPadding = PaddingValues(horizontal = 10.dp)
                                ) { Text("+ Zoom") }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(340.dp)
                                .background(Color.Black)
                                .clipToBounds()
                                .semantics {
                                    contentDescription = "Visualizador clínico del examen ${examenActual.tipoExamen} de ${examenActual.pacienteNombre}."
                                }
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        escalaZoom = (escalaZoom * zoom).coerceIn(1f, 4f)
                                        if (escalaZoom > 1f) {
                                            desplazamiento += pan
                                        } else {
                                            desplazamiento = Offset.Zero
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = bitmapDocumento!!.asImageBitmap(),
                                contentDescription = "Documento clínico adjunto",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(
                                        scaleX = escalaZoom,
                                        scaleY = escalaZoom,
                                        translationX = desplazamiento.x,
                                        translationY = desplazamiento.y
                                    )
                            )
                        }

                        if (esPdf && totalPaginas > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { if (paginaActual > 0) paginaActual-- },
                                    enabled = paginaActual > 0
                                ) { Text("◀ Anterior") }

                                Button(
                                    onClick = { if (paginaActual < totalPaginas - 1) paginaActual++ },
                                    enabled = paginaActual < totalPaginas - 1
                                ) { Text("Siguiente ▶") }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (esAltoContraste) Color(0xFF121212) else Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📄", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Este examen no tiene imagen adjunta, pero puedes descargar su informe oficial en PDF arriba.",
                            fontSize = 13.sp,
                            color = if (esAltoContraste) Color.White else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

private fun generarYDescargarPdfExamen(
    context: Context,
    examen: ExamenEntity,
    imagenClinica: Bitmap?
) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val tituloPaint = Paint().apply {
        color = android.graphics.Color.rgb(0, 102, 178)
        textSize = 22f
        isFakeBoldText = true
    }
    val subtituloPaint = Paint().apply {
        color = android.graphics.Color.DKGRAY
        textSize = 14f
        isFakeBoldText = true
    }
    val textoPaint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 13f
    }
    val notaPaint = Paint().apply {
        color = android.graphics.Color.GRAY
        textSize = 11f
    }

    var y = 60f
    canvas.drawText("OftApp VcM - Informe Clínico Oftalmológico", 40f, y, tituloPaint)
    y += 24f
    canvas.drawText("Comprobante de Atención y Resultados Simulados", 40f, y, notaPaint)
    y += 35f

    canvas.drawText("DATOS DEL PACIENTE", 40f, y, subtituloPaint)
    y += 22f
    canvas.drawText("Nombre Completo: ${examen.pacienteNombre}", 40f, y, textoPaint)
    y += 20f
    canvas.drawText("RUT: ${examen.rut}", 40f, y, textoPaint)
    y += 35f

    canvas.drawText("DETALLE DEL EXAMEN OFTALMOLÓGICO", 40f, y, subtituloPaint)
    y += 22f
    canvas.drawText("Tipo de Examen: ${examen.tipoExamen}", 40f, y, textoPaint)
    y += 20f
    canvas.drawText("Fecha de Atención: ${examen.fecha}", 40f, y, textoPaint)
    y += 20f
    canvas.drawText("Estado / Observaciones: ${examen.observaciones}", 40f, y, textoPaint)
    y += 35f

    if (imagenClinica != null) {
        canvas.drawText("EVIDENCIA / IMAGEN CLÍNICA ADJUNTA", 40f, y, subtituloPaint)
        y += 16f

        val anchoMaximo = 515f
        val altoMaximo = 340f
        val escala = minOf(
            anchoMaximo / imagenClinica.width.toFloat(),
            altoMaximo / imagenClinica.height.toFloat()
        )
        val anchoFinal = imagenClinica.width * escala
        val altoFinal = imagenClinica.height * escala
        val margenIzquierdo = (595f - anchoFinal) / 2f

        val rectDestino = RectF(
            margenIzquierdo,
            y,
            margenIzquierdo + anchoFinal,
            y + altoFinal
        )
        canvas.drawBitmap(imagenClinica, null, rectDestino, null)
        y += altoFinal + 25f
    } else {
        y += 10f
    }

    canvas.drawText("Aviso Académico: Documento generado con fines educativos.", 40f, y, notaPaint)
    y += 16f
    canvas.drawText("No constituye un diagnóstico médico real.", 40f, y, notaPaint)

    pdfDocument.finishPage(page)

    val nombreArchivo = "OftApp_Examen_${examen.id}_${examen.rut.replace(".", "")}.pdf"

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                Toast.makeText(context, "PDF con imagen guardado en Descargas: $nombreArchivo", Toast.LENGTH_LONG).show()
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, nombreArchivo)
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            Toast.makeText(context, "PDF con imagen guardado en Descargas: $nombreArchivo", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}
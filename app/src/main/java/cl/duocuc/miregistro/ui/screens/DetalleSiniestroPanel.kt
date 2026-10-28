package cl.duocuc.miregistro.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.model.DetalleSiniestro
import cl.duocuc.miregistro.model.ESTADOS

private val Verde = Color(0xFF15803D)
private val Naranja = Color(0xFFF59E0B)
private val Gris = Color(0xFF9CA3AF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSiniestroPanel(
    d: DetalleSiniestro,
    mensaje: String?,
    onGuardarEstado: (String) -> Unit,           // Solo guarda el estado
    onEnviarComentario: (String) -> Unit,        // Solo envía el comentario
    onSolicitarDocumento: (String) -> Unit
) {
    var estado by remember(d.numero, d.estado) { mutableStateOf(d.estado) }
    var comentario by remember(d.numero, d.comentario) { mutableStateOf(d.comentario) }
    var expandido by remember { mutableStateOf(false) }
    var errorComentario by remember { mutableStateOf(false) }
    var mensajeExitoComentario by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Detalle del siniestro", fontSize = 12.sp)
            Text(d.numero, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("${d.cliente}  ·  Placa ${d.placa}  ·  Prioridad ${d.prioridad}", fontSize = 13.sp)

            Text("Estado actual", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                    ESTADOS.forEach { e ->
                        DropdownMenuItem(text = { Text(e) }, onClick = { estado = e; expandido = false })
                    }
                }
            }

            // BOTÓN GUARDAR CAMBIOS (Solo afecta al estado, sin tocar el comentario)
            Button(
                onClick = { onGuardarEstado(estado) },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Guardar cambios de estado", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("Documentos recibidos", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            d.documentos.forEach { doc ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (doc.recibido) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = if (doc.recibido) Verde else Naranja,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = if (doc.recibido) doc.nombre else "Falta documento: ${doc.nombre}", color = if (doc.recibido) Verde else Naranja, fontSize = 13.sp)
                }
            }

            Text("Evidencias adjuntas por el asegurado", fontWeight = FontWeight.Bold, fontSize = 13.sp)

            if (d.evidencias.isEmpty()) {
                Text(text = "No se han adjuntado fotos ni evidencias aún.", fontSize = 12.sp, color = Gris)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(d.evidencias) { ev ->
                        if (ev.esImagen) {
                            val bitmap = remember(ev.bytes) { BitmapFactory.decodeByteArray(ev.bytes, 0, ev.bytes.size)?.asImageBitmap() }
                            if (bitmap != null) {
                                Image(bitmap = bitmap, contentDescription = "Foto Evidencia", modifier = Modifier.size(90.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                            }
                        } else {
                            Card(
                                modifier = Modifier.size(90.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
                            ) {
                                Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Default.Description, contentDescription = "Documento", tint = Color(0xFF4B5563), modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = ev.nombre, fontSize = 10.sp, color = Color(0xFF4B5563), maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }

            Text("Comentario para el usuario", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = comentario,
                onValueChange = {
                    comentario = it
                    if (it.isNotBlank()) errorComentario = false
                },
                minLines = 3,
                isError = errorComentario,
                supportingText = {
                    if (errorComentario) {
                        Text("El comentario no puede estar vacío para enviarlo", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // BOTÓN ENVIAR COMENTARIO (Valida obligatoriamente que no esté vacío)
                OutlinedButton(
                    onClick = {
                        if (comentario.isBlank()) {
                            errorComentario = true
                        } else {
                            errorComentario = false
                            onEnviarComentario(comentario)
                            mensajeExitoComentario = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Enviar comentario", fontSize = 12.sp) }

                OutlinedButton(
                    onClick = { onSolicitarDocumento("Presupuesto de reparación") },
                    modifier = Modifier.weight(1f)
                ) { Text("Solicitar documento", fontSize = 12.sp) }
            }

            if (mensajeExitoComentario || mensaje != null) {
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp)).padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Verde, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Guardado correctamente", color = Verde, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
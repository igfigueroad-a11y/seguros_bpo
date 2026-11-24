package cl.duocuc.miregistro.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import cl.duocuc.miregistro.data.AjustesTema
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.DetalleSiniestro
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSiniestroPanel(
    d: DetalleSiniestro,
    estadoSolicitado: String? = null,
    mensaje: String?,
    onGuardarEstado: (String) -> Unit,
    onEnviarComentario: (String) -> Unit,
    onSolicitarDocumento: (String) -> Unit
) {
    var estado by remember(d.numero, d.estado) { mutableStateOf(d.estado) }
    var comentario by remember(d.numero, d.comentario) { mutableStateOf(d.comentario) }
    var expandido by remember { mutableStateOf(false) }
    var errorComentario by remember { mutableStateOf(false) }
    var mensajeExitoComentario by remember(d.numero) { mutableStateOf(false) }

    val estadosPermitidos = remember(d.estado) {
        UsuarioDbHelper.estadosPermitidosDesde(d.estado)
    }
    val estaCerrado = d.estado.equals("Cerrado", ignoreCase = true)
    val tieneSolicitudPendiente = !estadoSolicitado.isNullOrBlank()
    val bloqueoEstado = estaCerrado || tieneSolicitudPendiente

    val colorPrincipal = MaterialTheme.colorScheme.primary
    val colorVerde = if (AjustesTema.oscuro) Color(0xFF4ADE80) else Color(0xFF15803D)
    val colorNaranja = if (AjustesTema.oscuro) Color(0xFFFBBF24) else Color(0xFFD97706)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Detalle del siniestro",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = d.numero,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colorPrincipal
            )
            Text(
                text = "${d.cliente}  ·  Placa ${d.placa}  ·  Prioridad ${d.prioridad}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (tieneSolicitudPendiente) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (AjustesTema.oscuro) Color(0xFF78350F) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Cambio a \"$estadoSolicitado\" pendiente de aprobación por un analista.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (AjustesTema.oscuro) Color(0xFFFDE68A) else Color(0xFF92400E)
                    )
                }
            }

            Text(
                text = "Estado actual",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            ExposedDropdownMenuBox(
                expanded = expandido && !bloqueoEstado,
                onExpandedChange = { if (!bloqueoEstado) expandido = it }
            ) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = {},
                    readOnly = true,
                    enabled = !bloqueoEstado,
                    trailingIcon = {
                        if (bloqueoEstado) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            ExposedDropdownMenuDefaults.TrailingIcon(expandido)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorPrincipal,
                        unfocusedBorderColor = colorPrincipal.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandido && !bloqueoEstado,
                    onDismissRequest = { expandido = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    estadosPermitidos.forEach { e ->
                        DropdownMenuItem(
                            text = { Text(e, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                estado = e
                                expandido = false
                            }
                        )
                    }
                }
            }

            Text(
                text = when {
                    estaCerrado -> "Este siniestro está cerrado y no admite cambios de estado."
                    tieneSolicitudPendiente -> "Debes esperar la respuesta del analista antes de solicitar otro cambio."
                    else -> "Todo cambio de estado requiere aprobación previa de un analista."
                },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = { onGuardarEstado(estado) },
                enabled = !bloqueoEstado && estado != d.estado,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
            ) {
                Text("Solicitar cambio de estado", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Documentos recibidos",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            d.documentos.forEach { doc ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (doc.recibido) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = if (doc.recibido) colorVerde else colorNaranja,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (doc.recibido) doc.nombre else "Falta documento: ${doc.nombre}",
                        color = if (doc.recibido) colorVerde else colorNaranja,
                        fontSize = 13.sp
                    )
                }
            }

            Text(
                text = "Evidencias adjuntas por el asegurado",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (d.evidencias.isEmpty()) {
                Text(
                    text = "No se han adjuntado fotos ni evidencias aún.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(d.evidencias) { ev ->
                        if (ev.esImagen) {
                            val bitmap = remember(ev.urlArchivo) {
                                val ruta = Uri.parse(ev.urlArchivo).path
                                if (!ruta.isNullOrBlank() && File(ruta).exists()) {
                                    BitmapFactory.decodeFile(ruta)?.asImageBitmap()
                                } else {
                                    null
                                }
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = ev.nombre,
                                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Card(
                                    modifier = Modifier.size(90.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Image,
                                            contentDescription = "Imagen",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = ev.nombre,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.size(90.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = "Documento",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = ev.nombre,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "Comentario para el usuario",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedTextField(
                value = comentario,
                onValueChange = {
                    comentario = it
                    if (it.isNotBlank()) errorComentario = false
                },
                minLines = 3,
                isError = errorComentario,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorPrincipal,
                    unfocusedBorderColor = colorPrincipal.copy(alpha = 0.6f)
                ),
                supportingText = {
                    if (errorComentario) {
                        Text("El comentario no puede estar vacío para enviarlo", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    border = BorderStroke(1.dp, colorPrincipal),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                    modifier = Modifier.weight(1f)
                ) { Text("Enviar comentario", fontSize = 12.sp) }

                OutlinedButton(
                    onClick = { onSolicitarDocumento("Presupuesto de reparación") },
                    border = BorderStroke(1.dp, colorPrincipal),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                    modifier = Modifier.weight(1f)
                ) { Text("Solicitar documento", fontSize = 12.sp) }
            }

            if (mensajeExitoComentario || mensaje != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (AjustesTema.oscuro) Color(0xFF14532D) else Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = colorVerde, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = mensaje ?: "Guardado correctamente",
                            color = colorVerde,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
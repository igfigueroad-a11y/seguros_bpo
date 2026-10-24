package cl.duocuc.miregistro.ui.screens


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onGuardar: (String, String) -> Unit,
    onSolicitarDocumento: (String) -> Unit
) {
    var estado by remember(d.numero, d.estado) { mutableStateOf(d.estado) }
    var comentario by remember(d.numero, d.comentario) { mutableStateOf(d.comentario) }
    var expandido by remember { mutableStateOf(false) }

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
                        DropdownMenuItem(
                            text = { Text(e) },
                            onClick = { estado = e; expandido = false }
                        )
                    }
                }
            }

            Text("Línea de tiempo del siniestro", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            LineaDeTiempo(d.estado)

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
                    Text(
                        text = if (doc.recibido) doc.nombre else "Falta documento: ${doc.nombre}",
                        color = if (doc.recibido) Verde else Naranja,
                        fontSize = 13.sp
                    )
                }
            }

            Text("Comentario para el usuario", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onGuardar(estado, comentario) },
                    modifier = Modifier.weight(1f)
                ) { Text("Enviar comentario", fontSize = 12.sp) }
                OutlinedButton(
                    onClick = { onSolicitarDocumento("Presupuesto de reparación") },
                    modifier = Modifier.weight(1f)
                ) { Text("Solicitar documento", fontSize = 12.sp) }
            }

            Button(
                onClick = { onGuardar(estado, comentario) },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) { Text("Guardar cambios") }

            mensaje?.let { Text(it, color = Verde, fontSize = 13.sp) }
        }
    }
}

@Composable
private fun LineaDeTiempo(estadoActual: String) {
    val indiceActual = ESTADOS.indexOf(estadoActual)
    ESTADOS.forEachIndexed { i, nombre ->
        val color = when {
            i < indiceActual -> Verde
            i == indiceActual -> Naranja
            else -> Gris
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            if (i < indiceActual) {
                Icon(Icons.Filled.CheckCircle, null, tint = color, modifier = Modifier.size(20.dp))
            } else {
                Box(Modifier.size(20.dp).border(2.dp, color, CircleShape))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(nombre, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    text = when {
                        i < indiceActual -> "Completado"
                        i == indiceActual -> "Estado actual"
                        else -> "Pendiente"
                    },
                    fontSize = 11.sp,
                    color = Gris
                )
            }
        }
    }
}
package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.model.SiniestroResumen
import cl.duocuc.miregistro.viewmodel.PanelLiquidadorViewModel

@Composable
fun PanelLiquidadorScreen(
    idLiquidador: Long,
    onCerrarSesion: () -> Unit = {},
    viewModel: PanelLiquidadorViewModel = viewModel()
) {
    val ui by viewModel.ui.collectAsState()
    LaunchedEffect(idLiquidador) { viewModel.cargar(idLiquidador) }

    if (ui.cargando) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Panel del Liquidador", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Seguimiento y gestión de siniestros", style = MaterialTheme.typography.bodySmall)
                }
                TextButton(onClick = onCerrarSesion) {
                    Text("Salir", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaResumen("Siniestros asignados", ui.resumen.asignados, Color(0xFF3B1FD1), Modifier.weight(1f))
                    TarjetaResumen("En evaluación", ui.resumen.enEvaluacion, Color(0xFFF59E0B), Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaResumen("Pendientes de documentos", ui.resumen.pendientesDocs, Color(0xFFDC2626), Modifier.weight(1f))
                    TarjetaResumen("Cerrados este mes", ui.resumen.cerradosMes, Color(0xFF15803D), Modifier.weight(1f))
                }
            }
        }
        item { Text("Siniestros asignados", fontWeight = FontWeight.Bold) }
        items(ui.siniestros, key = { it.numero }) { s ->
            FilaSiniestro(
                s = s,
                seleccionada = s.numero == ui.seleccionado?.numero,
                onClick = viewModel::seleccionar
            )
        }
        ui.seleccionado?.let { detalle ->
            item {
                DetalleSiniestroPanel(
                    d = detalle,
                    mensaje = ui.mensaje,
                    onGuardarEstado = { nuevoEstado -> viewModel.guardarEstado(nuevoEstado) },
                    onEnviarComentario = { nuevoComentario -> viewModel.guardarComentario(nuevoComentario) },
                    onSolicitarDocumento = viewModel::solicitarDocumento
                )
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun TarjetaResumen(titulo: String, valor: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(titulo, fontSize = 12.sp)
            Text(valor.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun FilaSiniestro(
    s: SiniestroResumen,
    seleccionada: Boolean,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(s.numero) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) Color(0xFFE8F0FE) else Color.White
        )
    ) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(s.numero, fontWeight = FontWeight.Bold, color = Color(0xFF3B1FD1))
                Text(s.cliente, fontSize = 13.sp)
                Text("${s.tipo} · ${s.fecha}", fontSize = 12.sp)
            }
            EstadoChip(s.estado)
        }
    }
}

@Composable
private fun EstadoChip(estado: String) {
    val (fondo, texto) = when (estado) {
        "En evaluación" -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        "En liquidación" -> Color(0xFFE0E7FF) to Color(0xFF3730A3)
        "Cerrado" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        else -> Color(0xFFF3F4F6) to Color(0xFF4B5563)
    }
    Text(
        estado,
        fontSize = 12.sp,
        color = texto,
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(fondo).padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
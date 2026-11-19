package cl.duocuc.miregistro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.data.SiniestroAdmin
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.theme.Exito
import cl.duocuc.miregistro.ui.theme.FondoClaro
import cl.duocuc.miregistro.ui.theme.Primario
import cl.duocuc.miregistro.ui.theme.PrimarioClaro

private enum class FiltroAdmin(val etiqueta: String) {
    TODOS("Todos"),
    SIN_ASIGNAR("Sin asignar"),
    ASIGNADOS("Asignados")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    idAdmin: Long,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { UsuarioDbHelper(context) }

    var recargar by remember { mutableIntStateOf(0) }
    var filtro by remember { mutableStateOf(FiltroAdmin.TODOS) }
    var siniestroAAsignar by remember { mutableStateOf<SiniestroAdmin?>(null) }

    val todosSiniestros = remember(recargar) { db.todosLosSiniestrosAdmin() }
    val liquidadores = remember(recargar) { db.listarLiquidadores() }

    val sinAsignarCount = todosSiniestros.count { it.liquidadorId == null }
    val asignadosCount = todosSiniestros.count { it.liquidadorId != null && it.estado != "Cerrado" }
    val cerradosCount = todosSiniestros.count { it.estado == "Cerrado" }

    val listaFiltrada = remember(todosSiniestros, filtro) {
        when (filtro) {
            FiltroAdmin.TODOS -> todosSiniestros
            FiltroAdmin.SIN_ASIGNAR -> todosSiniestros.filter { it.liquidadorId == null }
            FiltroAdmin.ASIGNADOS -> todosSiniestros.filter { it.liquidadorId != null }
        }
    }

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Administración", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = Color.White)
                        Text("Control y asignación de siniestros", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primario)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TarjetaMetricaAdmin("Total", todosSiniestros.size, Primario, Modifier.weight(1f))
                    TarjetaMetricaAdmin("Sin asignar", sinAsignarCount, Color(0xFFD97706), Modifier.weight(1f))
                    TarjetaMetricaAdmin("Activos", asignadosCount, Color(0xFF0288D1), Modifier.weight(1f))
                    TarjetaMetricaAdmin("Cerrados", cerradosCount, Exito, Modifier.weight(1f))
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FiltroAdmin.entries.forEach { opcion ->
                        FilterChip(
                            selected = filtro == opcion,
                            onClick = { filtro = opcion },
                            label = { Text(opcion.etiqueta) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimarioClaro.copy(alpha = 0.6f),
                                selectedLabelColor = Primario
                            )
                        )
                    }
                }
            }

            if (listaFiltrada.isEmpty()) {
                item {
                    Text(
                        text = "No hay siniestros en esta categoría.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else {
                items(listaFiltrada, key = { it.numero }) { siniestro ->
                    TarjetaSiniestroAdmin(
                        siniestro = siniestro,
                        onAsignarClick = { siniestroAAsignar = siniestro }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    siniestroAAsignar?.let { target ->
        DialogoAsignarLiquidador(
            siniestro = target,
            liquidadores = liquidadores,
            onDismiss = { siniestroAAsignar = null },
            onConfirmar = { idLiquidadorSeleccionado ->
                val ok = db.asignarLiquidador(target.numero, idLiquidadorSeleccionado)
                if (ok) {
                    recargar++
                    Toast.makeText(context, "Siniestro ${target.numero} asignado", Toast.LENGTH_SHORT).show()
                }
                siniestroAAsignar = null
            }
        )
    }
}

@Composable
private fun TarjetaMetricaAdmin(
    titulo: String,
    valor: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(titulo, fontSize = 11.sp, color = Color(0xFF5F6368))
        }
    }
}

@Composable
private fun TarjetaSiniestroAdmin(
    siniestro: SiniestroAdmin,
    onAsignarClick: () -> Unit
) {
    val sinAsignar = siniestro.liquidadorId == null
    val estaCerrado = siniestro.estado.equals("Cerrado", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = siniestro.numero,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Primario
                )
                ChipEstadoAdmin(siniestro.estado)
            }

            Text(
                text = "Asegurado: ${siniestro.aseguradoNombre}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1C1B22)
            )

            Text(
                text = "${siniestro.tipo} · Placa ${siniestro.placa.ifBlank { "N/A" }} · Prioridad ${siniestro.prioridad}",
                fontSize = 12.sp,
                color = Color(0xFF5F6368)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (sinAsignar) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (sinAsignar) Color(0xFFD97706) else Exito,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (sinAsignar) "Sin liquidador asignado" else "Liquidador: ${siniestro.liquidadorNombre}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (sinAsignar) Color(0xFFB45309) else Color(0xFF1C1B22)
                    )
                }

                if (!estaCerrado) {
                    OutlinedButton(
                        onClick = onAsignarClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Primario),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primario)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AssignmentInd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (sinAsignar) "Asignar" else "Reasignar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipEstadoAdmin(estado: String) {
    val (fondo, texto) = when (estado) {
        "En evaluación" -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        "En liquidación" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        "Cerrado" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        else -> Color(0xFFF3F4F6) to Color(0xFF4B5563)
    }
    Text(
        text = estado,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = texto,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(fondo)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun DialogoAsignarLiquidador(
    siniestro: SiniestroAdmin,
    liquidadores: List<Usuario>,
    onDismiss: () -> Unit,
    onConfirmar: (Long) -> Unit
) {
    var seleccionadoId by remember { mutableStateOf(siniestro.liquidadorId ?: liquidadores.firstOrNull()?.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Asignar ${siniestro.numero}",
                fontWeight = FontWeight.Bold,
                color = Primario
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Selecciona el liquidador responsable:",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (liquidadores.isEmpty()) {
                    Text(
                        text = "No hay liquidadores registrados en el sistema.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    liquidadores.forEach { liq ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { seleccionadoId = liq.id }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = seleccionadoId == liq.id,
                                onClick = { seleccionadoId = liq.id },
                                colors = RadioButtonDefaults.colors(selectedColor = Primario)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Primario,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(liq.nombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(liq.correo, fontSize = 12.sp, color = Color(0xFF5F6368))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = seleccionadoId != null,
                onClick = { seleccionadoId?.let(onConfirmar) }
            ) {
                Text("Confirmar asignación", color = Primario, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
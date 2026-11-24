package cl.duocuc.miregistro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.ResumenPanel
import cl.duocuc.miregistro.model.SiniestroResumen
import cl.duocuc.miregistro.ui.components.ContenedorConMenuLateral
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.ui.theme.Primario
import cl.duocuc.miregistro.viewmodel.PanelLiquidadorViewModel

private enum class SeccionLiquidador(val rutaMenu: String, val tituloBarra: String) {
    INICIO("liquidador_inicio", "Panel Liquidador"),
    TODOS("liquidador_todos", "Todos mis casos"),
    ACTIVOS("liquidador_activos", "En evaluación y activos"),
    PENDIENTES("liquidador_pendientes", "Pendientes de docs."),
    CERRADOS("liquidador_cerrados", "Casos cerrados")
}

@Composable
fun PanelLiquidadorScreen(
    idLiquidador: Long,
    onEditarPerfil: (Long) -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: PanelLiquidadorViewModel = viewModel()
) {
    val context = LocalContext.current
    val db = remember { UsuarioDbHelper(context) }
    val ui by viewModel.ui.collectAsState()
    val solicitudesPendientes by viewModel.solicitudesPendientes.collectAsState()
    var seccionActual by remember { mutableStateOf(SeccionLiquidador.INICIO) }

    LaunchedEffect(idLiquidador) { viewModel.cargar(idLiquidador) }

    BackHandler(enabled = seccionActual != SeccionLiquidador.INICIO) {
        seccionActual = SeccionLiquidador.INICIO
    }

    val casosActivos = remember(ui.siniestros) {
        ui.siniestros.filter { !it.estado.equals("Cerrado", ignoreCase = true) }
    }

    val casosCerrados = remember(ui.siniestros) {
        ui.siniestros.filter { it.estado.equals("Cerrado", ignoreCase = true) }
    }

    val casosConDocsPendientes = remember(ui.siniestros, ui.resumen.pendientesDocs) {
        ui.siniestros.filter { s ->
            db.detalleSiniestro(s.numero)?.documentos?.any { !it.recibido } == true
        }
    }

    val listaFiltrada = when (seccionActual) {
        SeccionLiquidador.TODOS -> ui.siniestros
        SeccionLiquidador.ACTIVOS -> casosActivos
        SeccionLiquidador.PENDIENTES -> casosConDocsPendientes
        SeccionLiquidador.CERRADOS -> casosCerrados
        SeccionLiquidador.INICIO -> emptyList()
    }

    val listState = rememberLazyListState()
    LaunchedEffect(ui.seleccionado?.numero, seccionActual) {
        if (seccionActual != SeccionLiquidador.INICIO && ui.seleccionado != null && listaFiltrada.isNotEmpty()) {
            listState.animateScrollToItem(listaFiltrada.size + 1)
        }
    }

    ContenedorConMenuLateral(
        idUsuario = idLiquidador,
        titulo = seccionActual.tituloBarra,
        rutaActual = seccionActual.rutaMenu,
        onIrPanelPrincipal = { seccionActual = SeccionLiquidador.INICIO },
        onIrTodosCasosLiquidador = { seccionActual = SeccionLiquidador.TODOS },
        onIrActivosLiquidador = { seccionActual = SeccionLiquidador.ACTIVOS },
        onIrPendientesLiquidador = { seccionActual = SeccionLiquidador.PENDIENTES },
        onIrCerradosLiquidador = { seccionActual = SeccionLiquidador.CERRADOS },
        badgeCasosLiquidador = ui.resumen.asignados,
        badgeActivosLiquidador = casosActivos.size,
        badgePendientesLiquidador = ui.resumen.pendientesDocs,
        onEditarPerfil = { onEditarPerfil(idLiquidador) },
        onCerrarSesion = onCerrarSesion
    ) { innerPadding ->
        if (ui.cargando) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@ContenedorConMenuLateral
        }

        if (seccionActual == SeccionLiquidador.INICIO) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.espacioPantalla),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    CabeceraResumenLiquidador(resumen = ui.resumen)
                }

                item {
                    Text(
                        text = "Apartados de gestión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    TarjetaApartadoLiquidador(
                        titulo = "Todos mis casos",
                        descripcion = "Revisa el listado completo de siniestros asignados a tu cartera.",
                        cantidad = ui.resumen.asignados,
                        etiquetaCantidad = if (ui.resumen.asignados == 1) "caso" else "casos",
                        icono = Icons.AutoMirrored.Filled.Assignment,
                        colorAcento = Primario,
                        onClick = { seccionActual = SeccionLiquidador.TODOS }
                    )
                }

                item {
                    TarjetaApartadoLiquidador(
                        titulo = "En evaluación y activos",
                        descripcion = "Gestiona estados, comentarios y evidencias de casos en curso.",
                        cantidad = casosActivos.size,
                        etiquetaCantidad = if (casosActivos.size == 1) "activo" else "activos",
                        icono = Icons.Default.Schedule,
                        colorAcento = Color(0xFFD97706),
                        onClick = { seccionActual = SeccionLiquidador.ACTIVOS }
                    )
                }

                item {
                    TarjetaApartadoLiquidador(
                        titulo = "Pendientes de documentos",
                        descripcion = "Casos que requieren documentos adicionales del asegurado.",
                        cantidad = ui.resumen.pendientesDocs,
                        etiquetaCantidad = if (ui.resumen.pendientesDocs == 1) "pendiente" else "pendientes",
                        icono = Icons.Default.Warning,
                        colorAcento = Color(0xFFDC2626),
                        onClick = { seccionActual = SeccionLiquidador.PENDIENTES }
                    )
                }

                item {
                    TarjetaApartadoLiquidador(
                        titulo = "Casos cerrados",
                        descripcion = "Historial de siniestros liquidados y finalizados.",
                        cantidad = casosCerrados.size,
                        etiquetaCantidad = if (casosCerrados.size == 1) "cerrado" else "cerrados",
                        icono = Icons.Default.CheckCircle,
                        colorAcento = Color(0xFF15803D),
                        onClick = { seccionActual = SeccionLiquidador.CERRADOS }
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Dimens.espacioPantalla),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    BarraVolverInicioLiquidador(
                        subtitulo = "${listaFiltrada.size} ${if (listaFiltrada.size == 1) "caso" else "casos"} en este apartado",
                        onVolver = { seccionActual = SeccionLiquidador.INICIO }
                    )
                }

                if (listaFiltrada.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "No hay siniestros registrados en este apartado.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(listaFiltrada, key = { it.numero }) { s ->
                        FilaSiniestro(
                            s = s,
                            estadoSolicitado = solicitudesPendientes[s.numero],
                            seleccionada = s.numero == ui.seleccionado?.numero,
                            onClick = viewModel::seleccionar
                        )
                    }
                }

                ui.seleccionado?.let { detalle ->
                    if (listaFiltrada.any { it.numero == detalle.numero }) {
                        item {
                            DetalleSiniestroPanel(
                                d = detalle,
                                estadoSolicitado = solicitudesPendientes[detalle.numero],
                                mensaje = ui.mensaje,
                                onGuardarEstado = { nuevoEstado -> viewModel.guardarEstado(nuevoEstado) },
                                onEnviarComentario = { nuevoComentario -> viewModel.guardarComentario(nuevoComentario) },
                                onSolicitarDocumento = viewModel::solicitarDocumento
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun CabeceraResumenLiquidador(resumen: ResumenPanel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                )
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column {
                Text(
                    text = "Gestión de liquidación",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Selecciona un apartado para revisar tus casos asignados",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PildoraMetricaLiquidador("Asignados", resumen.asignados, Modifier.weight(1f))
                PildoraMetricaLiquidador("Evaluación", resumen.enEvaluacion, Modifier.weight(1f))
                PildoraMetricaLiquidador("Docs. pend.", resumen.pendientesDocs, Modifier.weight(1f))
                PildoraMetricaLiquidador("Cerrados", resumen.cerradosMes, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PildoraMetricaLiquidador(
    etiqueta: String,
    valor: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.16f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = etiqueta,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TarjetaApartadoLiquidador(
    titulo: String,
    descripcion: String,
    cantidad: Int,
    etiquetaCantidad: String,
    icono: ImageVector,
    colorAcento: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, colorAcento.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorAcento.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = colorAcento,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = CircleShape,
                    color = if (cantidad > 0) colorAcento else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "$cantidad $etiquetaCantidad",
                        color = if (cantidad > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Abrir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BarraVolverInicioLiquidador(
    subtitulo: String,
    onVolver: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onVolver) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver a inicio",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Volver a Inicio", fontWeight = FontWeight.SemiBold)
        }
        Text(
            text = subtitulo,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FilaSiniestro(
    s: SiniestroResumen,
    estadoSolicitado: String?,
    seleccionada: Boolean,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(s.numero) },
        shape = RoundedCornerShape(12.dp),
        border = if (seleccionada) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(s.numero, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(s.cliente, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("${s.tipo} · ${s.fecha}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                EstadoChip(s.estado)
                if (!estadoSolicitado.isNullOrBlank()) {
                    Text(
                        text = "Solicitado: $estadoSolicitado",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFB45309),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoChip(estado: String) {
    val (fondo, texto) = when (estado) {
        "En evaluación" -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        "En liquidación" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        "Cerrado" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        else -> Color(0xFFF3F4F6) to Color(0xFF4B5563)
    }
    Text(
        estado,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = texto,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(fondo)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
package cl.duocuc.miregistro.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import cl.duocuc.miregistro.data.SiniestroAdmin
import cl.duocuc.miregistro.data.SolicitudCambioEstado
import cl.duocuc.miregistro.data.SolicitudRegistro
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.components.ContenedorConMenuLateral
import cl.duocuc.miregistro.ui.theme.Exito
import cl.duocuc.miregistro.ui.theme.Primario

private enum class SeccionAnalista(val ruta: String, val titulo: String) {
    INICIO("analista_inicio", "Panel Analista"),
    SINIESTROS("analista_siniestros", "Gestión de siniestros"),
    ESTADOS("analista_estados", "Cambios de estado"),
    CUENTAS("analista_cuentas", "Solicitudes de cuenta")
}

private enum class FiltroSiniestroAnalista(val etiqueta: String) {
    TODOS("Todos"),
    SIN_ASIGNAR("Sin asignar"),
    ASIGNADOS("Asignados")
}

@Composable
fun PanelAnalistaScreen(
    idAnalista: Long,
    onEditarPerfil: (Long) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { UsuarioDbHelper(context) }

    var recargar by remember { mutableIntStateOf(0) }
    var seccion by rememberSaveable { mutableStateOf(SeccionAnalista.INICIO) }
    var filtroSiniestro by remember { mutableStateOf(FiltroSiniestroAnalista.TODOS) }
    var siniestroAAsignar by remember { mutableStateOf<SiniestroAdmin?>(null) }
    var mostrarAvisos by remember { mutableStateOf(false) }

    BackHandler(enabled = seccion != SeccionAnalista.INICIO) {
        seccion = SeccionAnalista.INICIO
    }

    val todosSiniestros = remember(recargar) { db.todosLosSiniestrosAdmin() }
    val liquidadores = remember(recargar) { db.listarLiquidadores() }
    val solicitudesPendientes = remember(recargar) { db.listarSolicitudesPendientes() }
    val solicitudesEstado = remember(recargar) { db.listarSolicitudesCambioEstado() }

    val sinAsignarCount = todosSiniestros.count { it.liquidadorId == null }
    val activosCount = todosSiniestros.count { it.liquidadorId != null && it.estado != "Cerrado" }
    val totalAvisos = solicitudesPendientes.size + solicitudesEstado.size

    val listaFiltrada = remember(todosSiniestros, filtroSiniestro) {
        when (filtroSiniestro) {
            FiltroSiniestroAnalista.TODOS -> todosSiniestros
            FiltroSiniestroAnalista.SIN_ASIGNAR -> todosSiniestros.filter { it.liquidadorId == null }
            FiltroSiniestroAnalista.ASIGNADOS -> todosSiniestros.filter { it.liquidadorId != null }
        }
    }

    ContenedorConMenuLateral(
        idUsuario = idAnalista,
        titulo = seccion.titulo,
        rutaActual = seccion.ruta,
        onIrPanelPrincipal = { seccion = SeccionAnalista.INICIO },
        onIrSiniestrosAnalista = { seccion = SeccionAnalista.SINIESTROS },
        onIrCambiosEstadoAnalista = { seccion = SeccionAnalista.ESTADOS },
        onIrSolicitudesCuentaAnalista = { seccion = SeccionAnalista.CUENTAS },
        badgeSinAsignar = sinAsignarCount,
        badgeCambiosEstado = solicitudesEstado.size,
        badgeSolicitudesCuenta = solicitudesPendientes.size,
        onEditarPerfil = { onEditarPerfil(idAnalista) },
        onCerrarSesion = onCerrarSesion,
        accionesExtra = {
            IconButton(onClick = { mostrarAvisos = true }) {
                BadgedBox(
                    badge = {
                        if (totalAvisos > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = totalAvisos.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (totalAvisos > 0) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                        contentDescription = "Notificaciones",
                        tint = if (totalAvisos > 0) Primario else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { innerPadding ->
        when (seccion) {
            SeccionAnalista.INICIO -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        CabeceraResumenAnalista(
                            totalSiniestros = todosSiniestros.size,
                            sinAsignar = sinAsignarCount,
                            pendientesRevision = totalAvisos
                        )
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
                        TarjetaApartadoAnalista(
                            titulo = "Gestión de siniestros",
                            descripcion = "Asigna o reasigna liquidadores a los casos ingresados",
                            icono = Icons.AutoMirrored.Filled.Assignment,
                            colorAcento = Primario,
                            colorFondoSuave = Color(0xFFDBEAFE),
                            etiquetaEstado = if (sinAsignarCount > 0) "$sinAsignarCount sin asignar" else "Todos asignados",
                            colorEtiquetaTexto = if (sinAsignarCount > 0) Color(0xFFB45309) else Color(0xFF15803D),
                            colorEtiquetaFondo = if (sinAsignarCount > 0) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                            onClick = { seccion = SeccionAnalista.SINIESTROS }
                        )
                    }

                    item {
                        TarjetaApartadoAnalista(
                            titulo = "Cambios de estado",
                            descripcion = "Evalúa los avances de etapa solicitados por liquidadores",
                            icono = Icons.Default.SwapHoriz,
                            colorAcento = Color(0xFFD97706),
                            colorFondoSuave = Color(0xFFFEF3C7),
                            etiquetaEstado = if (solicitudesEstado.isNotEmpty()) "${solicitudesEstado.size} pendiente(s)" else "Al día",
                            colorEtiquetaTexto = if (solicitudesEstado.isNotEmpty()) Color(0xFFB45309) else Color(0xFF15803D),
                            colorEtiquetaFondo = if (solicitudesEstado.isNotEmpty()) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
                            onClick = { seccion = SeccionAnalista.ESTADOS }
                        )
                    }

                    item {
                        TarjetaApartadoAnalista(
                            titulo = "Solicitudes de cuenta",
                            descripcion = "Aprueba o desaprueba el acceso de nuevos asegurados",
                            icono = Icons.Default.HowToReg,
                            colorAcento = Color(0xFF15803D),
                            colorFondoSuave = Color(0xFFDCFCE7),
                            etiquetaEstado = if (solicitudesPendientes.isNotEmpty()) "${solicitudesPendientes.size} por revisar" else "Al día",
                            colorEtiquetaTexto = if (solicitudesPendientes.isNotEmpty()) Color(0xFF1D4ED8) else Color(0xFF15803D),
                            colorEtiquetaFondo = if (solicitudesPendientes.isNotEmpty()) Color(0xFFDBEAFE) else Color(0xFFDCFCE7),
                            onClick = { seccion = SeccionAnalista.CUENTAS }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            SeccionAnalista.SINIESTROS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        BarraVolverInicioAnalista(onVolver = { seccion = SeccionAnalista.INICIO })
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TarjetaResumenAnalista(
                                titulo = "Sin asignar",
                                valor = sinAsignarCount,
                                color = if (sinAsignarCount > 0) Color(0xFFD97706) else Exito,
                                colorFondo = if (sinAsignarCount > 0) Color(0xFFFEF3C7).copy(alpha = 0.45f) else Color(0xFFDCFCE7).copy(alpha = 0.45f),
                                modifier = Modifier.weight(1f)
                            )
                            TarjetaResumenAnalista(
                                titulo = "En gestión",
                                valor = activosCount,
                                color = Primario,
                                colorFondo = Color(0xFFDBEAFE).copy(alpha = 0.55f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FiltroSiniestroAnalista.entries.forEach { opcion ->
                                FilterChip(
                                    selected = filtroSiniestro == opcion,
                                    onClick = { filtroSiniestro = opcion },
                                    label = { Text(opcion.etiqueta) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }

                    if (listaFiltrada.isEmpty()) {
                        item {
                            TarjetaEstadoVacioAnalista(
                                titulo = "Sin siniestros en este filtro",
                                subtitulo = "No hay registros que coincidan con la selección actual."
                            )
                        }
                    } else {
                        items(listaFiltrada, key = { it.numero }) { siniestro ->
                            TarjetaSiniestroAnalista(
                                siniestro = siniestro,
                                onAsignarClick = { siniestroAAsignar = siniestro }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            SeccionAnalista.ESTADOS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        BarraVolverInicioAnalista(onVolver = { seccion = SeccionAnalista.INICIO })
                    }

                    if (solicitudesEstado.isEmpty()) {
                        item {
                            TarjetaEstadoVacioAnalista(
                                titulo = "Sin cambios de estado pendientes",
                                subtitulo = "Cuando un liquidador solicite avanzar un caso de etapa, aparecerá aquí."
                            )
                        }
                    } else {
                        items(solicitudesEstado, key = { "estado_${it.numeroSiniestro}" }) { sol ->
                            TarjetaSolicitudCambioEstado(
                                solicitud = sol,
                                onAprobar = {
                                    val error = db.aprobarCambioEstado(idAnalista, sol.numeroSiniestro)
                                    if (error == null) {
                                        recargar++
                                        Toast.makeText(
                                            context,
                                            "Cambio aprobado: ${sol.numeroSiniestro} pasó a '${sol.estadoSolicitado}'",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                },
                                onRechazar = {
                                    val error = db.rechazarCambioEstado(idAnalista, sol.numeroSiniestro)
                                    if (error == null) {
                                        recargar++
                                        Toast.makeText(
                                            context,
                                            "Solicitud rechazada para ${sol.numeroSiniestro}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            SeccionAnalista.CUENTAS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        BarraVolverInicioAnalista(onVolver = { seccion = SeccionAnalista.INICIO })
                    }

                    if (solicitudesPendientes.isEmpty()) {
                        item {
                            TarjetaEstadoVacioAnalista(
                                titulo = "Sin solicitudes de cuenta",
                                subtitulo = "No hay registros nuevos esperando aprobación en este momento."
                            )
                        }
                    } else {
                        items(solicitudesPendientes, key = { "usuario_${it.idUsuario}" }) { solicitud ->
                            TarjetaSolicitudCuenta(
                                solicitud = solicitud,
                                onAprobar = {
                                    val error = db.aprobarSolicitudRegistro(idAnalista, solicitud.idUsuario)
                                    if (error == null) {
                                        recargar++
                                        Toast.makeText(
                                            context,
                                            "Cuenta de ${solicitud.nombre} aprobada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                },
                                onDesaprobar = {
                                    val error = db.desaprobarSolicitudRegistro(idAnalista, solicitud.idUsuario)
                                    if (error == null) {
                                        recargar++
                                        Toast.makeText(
                                            context,
                                            "Solicitud de ${solicitud.nombre} desaprobada",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    if (mostrarAvisos) {
        DialogoAvisosNotificacion(
            cantidadCuentas = solicitudesPendientes.size,
            cantidadEstados = solicitudesEstado.size,
            onDismiss = { mostrarAvisos = false },
            onIrCuentas = {
                mostrarAvisos = false
                seccion = SeccionAnalista.CUENTAS
            },
            onIrEstados = {
                mostrarAvisos = false
                seccion = SeccionAnalista.ESTADOS
            }
        )
    }

    siniestroAAsignar?.let { target ->
        DialogoAsignarLiquidadorAnalista(
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
private fun CabeceraResumenAnalista(
    totalSiniestros: Int,
    sinAsignar: Int,
    pendientesRevision: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                )
            )
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Centro de control",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Selecciona un apartado para gestionar casos o revisar solicitudes",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatoCabeceraAnalista("Siniestros", totalSiniestros, Modifier.weight(1f))
                DatoCabeceraAnalista("Sin asignar", sinAsignar, Modifier.weight(1f))
                DatoCabeceraAnalista("Por revisar", pendientesRevision, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DatoCabeceraAnalista(etiqueta: String, valor: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor.toString(),
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = etiqueta,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun TarjetaApartadoAnalista(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    colorAcento: Color,
    colorFondoSuave: Color,
    etiquetaEstado: String,
    colorEtiquetaTexto: Color,
    colorEtiquetaFondo: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, colorAcento.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorFondoSuave),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorAcento,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = colorEtiquetaFondo
                ) {
                    Text(
                        text = etiquetaEstado,
                        color = colorEtiquetaTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colorFondoSuave.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colorAcento
                )
            }
        }
    }
}

@Composable
private fun DialogoAvisosNotificacion(
    cantidadCuentas: Int,
    cantidadEstados: Int,
    onDismiss: () -> Unit,
    onIrCuentas: () -> Unit,
    onIrEstados: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Primario
                )
                Text(
                    text = "Avisos recientes",
                    fontWeight = FontWeight.Bold,
                    color = Primario
                )
            }
        },
        text = {
            if (cantidadCuentas == 0 && cantidadEstados == 0) {
                Text(
                    text = "No tienes notificaciones nuevas por el momento.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Toca un aviso para ir a su apartado:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (cantidadCuentas > 0) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onIrCuentas),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HowToReg,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Nuevos registros recibidos",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF14532D)
                                    )
                                    Text(
                                        text = "$cantidadCuentas solicitud(es) esperando revisión",
                                        fontSize = 12.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D)
                                )
                            }
                        }
                    }

                    if (cantidadEstados > 0) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onIrEstados),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Cambios de etapa solicitados",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF78350F)
                                    )
                                    Text(
                                        text = "$cantidadEstados caso(s) requieren tu aprobación",
                                        fontSize = 12.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = Primario, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun BarraVolverInicioAnalista(onVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onVolver,
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Primario,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Volver a Inicio",
                color = Primario,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun TarjetaEstadoVacioAnalista(titulo: String, subtitulo: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Exito,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitulo,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaResumenAnalista(
    titulo: String,
    valor: Int,
    color: Color,
    colorFondo: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(titulo, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun TarjetaSiniestroAnalista(
    siniestro: SiniestroAdmin,
    onAsignarClick: () -> Unit
) {
    val sinAsignar = siniestro.liquidadorId == null
    val estaCerrado = siniestro.estado.equals("Cerrado", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = siniestro.numero,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = siniestro.aseguradoNombre,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                ChipEstadoAnalista(siniestro.estado)
            }

            Text(
                text = "${siniestro.tipo} · Placa ${siniestro.placa.ifBlank { "N/A" }}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                        imageVector = if (sinAsignar) Icons.Default.Schedule else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (sinAsignar) Color(0xFFD97706) else Exito,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (sinAsignar) "Sin liquidador" else siniestro.liquidadorNombre ?: "",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (sinAsignar) Color(0xFFD97706) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!estaCerrado) {
                    if (sinAsignar) {
                        Button(
                            onClick = onAsignarClick,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentInd,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Asignar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        TextButton(
                            onClick = onAsignarClick,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Reasignar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipEstadoAnalista(estado: String) {
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
private fun DialogoAsignarLiquidadorAnalista(
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
                color = MaterialTheme.colorScheme.primary
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
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(liq.nombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(liq.correo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text("Confirmar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun TarjetaSolicitudCambioEstado(
    solicitud: SolicitudCambioEstado,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFD97706).copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Text(
                    text = "${solicitud.numeroSiniestro} · ${solicitud.aseguradoNombre}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${solicitud.tipoSiniestro} · Por ${solicitud.liquidadorNombre}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = solicitud.estadoActual,
                        color = Color(0xFF374151),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFDBEAFE)
                ) {
                    Text(
                        text = solicitud.estadoSolicitado,
                        color = Color(0xFF1D4ED8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRechazar,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rechazar")
                }

                Button(
                    onClick = onAprobar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aprobar")
                }
            }
        }
    }
}

@Composable
private fun TarjetaSolicitudCuenta(
    solicitud: SolicitudRegistro,
    onAprobar: () -> Unit,
    onDesaprobar: () -> Unit
) {
    val tieneSiniestros = solicitud.cantidadSiniestros > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFF15803D).copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Text(
                    text = solicitud.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "RUT: ${solicitud.rut} · ${solicitud.correo}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = if (tieneSiniestros) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
            ) {
                Text(
                    text = if (tieneSiniestros)
                        "Verificado: ${solicitud.cantidadSiniestros} siniestro(s) asociado(s)"
                    else
                        "Cuenta nueva sin siniestros previos",
                    color = if (tieneSiniestros) Color(0xFF15803D) else Color(0xFFB45309),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDesaprobar,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Desaprobar")
                }

                Button(
                    onClick = onAprobar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Exito)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aprobar")
                }
            }
        }
    }
}
package cl.duocuc.miregistro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.data.UsuarioDbHelper.SolicitudRegistro
import cl.duocuc.miregistro.data.SiniestroAdmin
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.components.AzulFondoSuave
import cl.duocuc.miregistro.ui.components.AzulPrincipal

private enum class SeccionAnalista { REGISTROS, SINIESTROS }
private enum class FiltroAdmin(val etiqueta: String) {
    TODOS("Todos"),
    SIN_ASIGNAR("Sin asignar"),
    ASIGNADOS("Asignados")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    idAdmin: Long,
    onEditarPerfil: (Long) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { UsuarioDbHelper(context) }

    var recargar by remember { mutableIntStateOf(0) }
    var seccionActual by remember { mutableStateOf(SeccionAnalista.REGISTROS) }
    var filtro by remember { mutableStateOf(FiltroAdmin.TODOS) }
    var siniestroAAsignar by remember { mutableStateOf<SiniestroAdmin?>(null) }
    var menuPerfilAbierto by remember { mutableStateOf(false) }

    val analista = remember(idAdmin, recargar) {
        if (db.esAdmin(idAdmin)) db.buscarPorId(idAdmin) else null
    }

    if (analista == null) {
        onCerrarSesion()
        return
    }

    val todosSiniestros = remember(recargar) { db.todosLosSiniestrosAdmin() }
    val liquidadores = remember(recargar) { db.listarLiquidadores() }
    val solicitudesPendientes = remember(recargar) { db.listarSolicitudesPendientes() }

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
        containerColor = AzulFondoSuave,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Panel Analista · Seguros BPO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = analista.nombre,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuPerfilAbierto = true }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Menú de perfil",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = menuPerfilAbierto,
                            onDismissRequest = { menuPerfilAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar perfil") },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = AzulPrincipal)
                                },
                                onClick = {
                                    menuPerfilAbierto = false
                                    onEditarPerfil(idAdmin)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    menuPerfilAbierto = false
                                    onCerrarSesion()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = seccionActual == SeccionAnalista.REGISTROS,
                    onClick = { seccionActual = SeccionAnalista.REGISTROS },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (solicitudesPendientes.isNotEmpty()) {
                                    Badge { Text("${solicitudesPendientes.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.HowToReg, contentDescription = "Solicitudes de registro")
                        }
                    },
                    label = { Text("Registros") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulPrincipal.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = seccionActual == SeccionAnalista.SINIESTROS,
                    onClick = { seccionActual = SeccionAnalista.SINIESTROS },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Siniestros")
                    },
                    label = { Text("Siniestros") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AzulPrincipal,
                        selectedTextColor = AzulPrincipal,
                        indicatorColor = AzulPrincipal.copy(alpha = 0.12f)
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            if (seccionActual == SeccionAnalista.REGISTROS) {
                item {
                    Text(
                        text = "Solicitudes de registro (${solicitudesPendientes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )
                }

                if (solicitudesPendientes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "No hay usuarios pendientes de aprobación.",
                                modifier = Modifier.padding(20.dp),
                                color = Color(0xFF5F6368)
                            )
                        }
                    }
                } else {
                    items(solicitudesPendientes, key = { it.idUsuario }) { solicitud ->
                        TarjetaSolicitudRegistro(
                            solicitud = solicitud,
                            onAprobar = {
                                val error = db.aprobarSolicitudRegistro(idAdmin, solicitud.idUsuario)
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
                                val error = db.desaprobarSolicitudRegistro(idAdmin, solicitud.idUsuario)
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
            } else {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TarjetaMetricaAdmin("Total", todosSiniestros.size, AzulPrincipal, Modifier.weight(1f))
                        TarjetaMetricaAdmin("Sin asignar", sinAsignarCount, Color(0xFFD97706), Modifier.weight(1f))
                        TarjetaMetricaAdmin("Activos", asignadosCount, Color(0xFF0288D1), Modifier.weight(1f))
                        TarjetaMetricaAdmin("Cerrados", cerradosCount, Color(0xFF2E7D32), Modifier.weight(1f))
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
                                    selectedContainerColor = AzulPrincipal.copy(alpha = 0.15f),
                                    selectedLabelColor = AzulPrincipal
                                )
                            )
                        }
                    }
                }

                if (listaFiltrada.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = "No hay siniestros en esta categoría.",
                                modifier = Modifier.padding(20.dp),
                                color = Color(0xFF5F6368)
                            )
                        }
                    }
                } else {
                    items(listaFiltrada, key = { it.numero }) { siniestro ->
                        TarjetaSiniestroAdmin(
                            siniestro = siniestro,
                            onAsignarClick = { siniestroAAsignar = siniestro }
                        )
                    }
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
                val error = db.asignarLiquidador(idAdmin, target.numero, idLiquidadorSeleccionado)
                if (error == null) {
                    recargar++
                    Toast.makeText(context, "Siniestro ${target.numero} asignado", Toast.LENGTH_SHORT).show()
                    siniestroAAsignar = null
                } else {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}

@Composable
private fun TarjetaSolicitudRegistro(
    solicitud: SolicitudRegistro,
    onAprobar: () -> Unit,
    onDesaprobar: () -> Unit
) {
    val tieneSiniestros = solicitud.cantidadSiniestros > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "RUT: ${solicitud.rut} · ${solicitud.correo}",
                    fontSize = 13.sp,
                    color = Color(0xFF5F6368)
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
                        "Sin siniestros registrados",
                    color = if (tieneSiniestros) Color(0xFF15803D) else Color(0xFFB45309),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
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
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Desaprobar")
                }

                Button(
                    onClick = onAprobar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aprobar")
                }
            }
        }
    }
}

@Composable
private fun TarjetaMetricaAdmin(
    titulo: String,
    valor: Int,
    colorAcento: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorAcento
            )
            Text(
                text = titulo,
                fontSize = 11.sp,
                color = Color(0xFF5F6368)
            )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
                    color = AzulPrincipal
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (estaCerrado) Color(0xFFDCFCE7) else AzulPrincipal.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = siniestro.estado,
                        color = if (estaCerrado) Color(0xFF15803D) else AzulPrincipal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "Asegurado: ${siniestro.aseguradoNombre}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1F2937)
            )
            Text(
                text = "${siniestro.tipo} · Patente ${siniestro.placa.ifBlank { "S/P" }}",
                fontSize = 13.sp,
                color = Color(0xFF5F6368)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (sinAsignar) "Liquidador: Sin asignar"
                    else "Liquidador: ${siniestro.liquidadorNombre ?: "Asignado"}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (sinAsignar) Color(0xFFD97706) else Color(0xFF1F2937)
                )

                if (!estaCerrado) {
                    Button(
                        onClick = onAsignarClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (sinAsignar) "Asignar" else "Reasignar",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoAsignarLiquidador(
    siniestro: SiniestroAdmin,
    liquidadores: List<Usuario>,
    onDismiss: () -> Unit,
    onConfirmar: (Long) -> Unit
) {
    var seleccionadoId by remember {
        mutableStateOf(siniestro.liquidadorId ?: liquidadores.firstOrNull()?.id)
    }

    val cambioValido = seleccionadoId != null && seleccionadoId != siniestro.liquidadorId

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Asignar ${siniestro.numero}",
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
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
                                colors = RadioButtonDefaults.colors(selectedColor = AzulPrincipal)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(liq.nombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(liq.correo, fontSize = 12.sp, color = Color(0xFF5F6368))
                            }
                        }
                    }
                    if (siniestro.liquidadorId != null && seleccionadoId == siniestro.liquidadorId) {
                        Text(
                            text = "Selecciona un liquidador diferente al actual para reasignar.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = cambioValido,
                onClick = { seleccionadoId?.let(onConfirmar) }
            ) {
                Text(
                    text = "Confirmar asignación",
                    color = if (cambioValido) AzulPrincipal else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
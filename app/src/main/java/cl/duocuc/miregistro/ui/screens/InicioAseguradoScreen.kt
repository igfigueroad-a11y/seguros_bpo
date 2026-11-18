
package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cl.duocuc.miregistro.data.AjustesTema
import cl.duocuc.miregistro.data.AvatarStore
import cl.duocuc.miregistro.data.SesionPreferencias
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.FiltroSiniestro
import cl.duocuc.miregistro.ui.components.AvatarUsuario
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.DialogoEditarAvatar
import cl.duocuc.miregistro.ui.components.HojaAlertasLiquidador
import cl.duocuc.miregistro.ui.components.TarjetaResumen
import cl.duocuc.miregistro.ui.components.coloresAvatar
import cl.duocuc.miregistro.ui.theme.Acento
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.ui.theme.Exito
import cl.duocuc.miregistro.ui.theme.Primario
import cl.duocuc.miregistro.ui.theme.PrimarioClaro
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioAseguradoScreen(
    idUsuario: Long,
    onConsultarSiniestro: () -> Unit,
    onAdjuntarDocumento: (String) -> Unit,
    onVerDetalle: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    onEditarPerfil: () -> Unit = {}
) {
    val contexto = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val db = remember { UsuarioDbHelper(contexto) }
    val avatarStore = remember { AvatarStore(contexto) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var version by remember { mutableIntStateOf(0) }
    var filtro by remember { mutableStateOf(FiltroSiniestro.COMPLETADOS) }
    var mostrarAlertas by remember { mutableStateOf(false) }
    var mostrarEditarAvatar by remember { mutableStateOf(false) }
    var mostrarConfirmarSalida by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) version++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val usuario = remember(version) { db.buscarPorId(idUsuario) }
    val resumen = remember(version) { db.resumenAsegurado(idUsuario) }
    val lista = remember(version, filtro) { db.siniestrosDeAsegurado(idUsuario, filtro) }
    val alertas = remember(version) { db.alertasDocumentos(idUsuario) }
    val foto = remember(version) { avatarStore.leerFoto(idUsuario) }
    val indiceColor = remember(version) { avatarStore.leerColor(idUsuario) }
    val colorAvatar = coloresAvatar.getOrElse(indiceColor) { Primario }

    val nombre = usuario?.nombre ?: "Usuario"
    val correo = usuario?.correo ?: ""
    val primerNombre = nombre.trim().split(" ").firstOrNull() ?: nombre

    val coloresItemMenu = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = PrimarioClaro.copy(alpha = 0.45f),
        selectedIconColor = Primario,
        selectedTextColor = Primario
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(Dimens.espacioGrande)) {
                    Box {
                        AvatarUsuario(
                            nombre = nombre,
                            colorFondo = colorAvatar,
                            foto = foto,
                            tamano = 72.dp,
                            modifier = Modifier.clickable { mostrarEditarAvatar = true }
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Primario)
                                .clickable { mostrarEditarAvatar = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Editar foto",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Inicio", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = true,
                    colors = coloresItemMenu,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Consultar siniestro") },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    selected = false,
                    colors = coloresItemMenu,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onConsultarSiniestro()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Editar perfil") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    selected = false,
                    colors = coloresItemMenu,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onEditarPerfil()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DarkMode, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Modo oscuro", modifier = Modifier.weight(1f))
                    Switch(
                        checked = AjustesTema.oscuro,
                        onCheckedChange = { AjustesTema.cambiar(contexto, it) }
                    )
                }

                NavigationDrawerItem(
                    label = { Text("Cambiar contraseña") },
                    icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    selected = false,
                    colors = coloresItemMenu,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onEditarPerfil()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        mostrarConfirmarSalida = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(),
            topBar = {
                TopAppBar(
                    windowInsets = WindowInsets(),
                    title = { Text("Mi panel") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { mostrarAlertas = true }) {
                            BadgedBox(
                                badge = { if (alertas.isNotEmpty()) Badge { Text(alertas.size.toString()) } }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alertas")
                            }
                        }
                        AvatarUsuario(
                            nombre = nombre,
                            colorFondo = colorAvatar,
                            foto = foto,
                            tamano = 36.dp,
                            modifier = Modifier.clickable { scope.launch { drawerState.open() } }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                )
            },
            bottomBar = {
                Box(modifier = Modifier.fillMaxWidth().padding(Dimens.espacioPantalla)) {
                    BotonPrincipal(
                        texto = "Consultar estado de un siniestro",
                        onClick = onConsultarSiniestro,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.espacioPantalla),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
                Text("Hola, $primerNombre", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text("Este es el resumen de tus siniestros", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (alertas.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (alertas.size == 1) "Tienes 1 documento pendiente" else "Tienes ${alertas.size} documentos pendientes",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { mostrarAlertas = true }) { Text("Ver") }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TarjetaResumen(
                        numero = resumen.total,
                        etiqueta = "Total",
                        color = MaterialTheme.colorScheme.onSurface,
                        seleccionada = filtro == FiltroSiniestro.TODOS,
                        onClick = { filtro = FiltroSiniestro.TODOS },
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaResumen(
                        numero = resumen.enProceso,
                        etiqueta = "En proceso",
                        color = Acento,
                        seleccionada = filtro == FiltroSiniestro.EN_PROCESO,
                        onClick = { filtro = FiltroSiniestro.EN_PROCESO },
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaResumen(
                        numero = resumen.completados,
                        etiqueta = "Completados",
                        color = Exito,
                        seleccionada = filtro == FiltroSiniestro.COMPLETADOS,
                        onClick = { filtro = FiltroSiniestro.COMPLETADOS },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = when (filtro) {
                        FiltroSiniestro.TODOS -> "Todos mis siniestros"
                        FiltroSiniestro.EN_PROCESO -> "Siniestros en proceso"
                        FiltroSiniestro.COMPLETADOS -> "Siniestros completados"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                if (lista.isEmpty()) {
                    Text(
                        text = when (filtro) {
                            FiltroSiniestro.COMPLETADOS -> "Aún no tienes siniestros completados"
                            FiltroSiniestro.EN_PROCESO -> "No tienes siniestros en proceso"
                            FiltroSiniestro.TODOS -> "Aún no tienes siniestros registrados"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    lista.forEach { s ->
                        Card(
                            onClick = { onVerDetalle(s.numero) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                val cerrado = s.estado == "Cerrado"
                                Icon(
                                    imageVector = if (cerrado) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (cerrado) Exito else Acento
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(s.numero, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(
                                        "${s.descripcion.ifBlank { s.tipo }} · ${s.fecha}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    s.estado,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (cerrado) Exito else Acento
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.espacioChico))
            }
        }
    }

    if (mostrarAlertas) {
        HojaAlertasLiquidador(
            alertas = alertas,
            onDismiss = { mostrarAlertas = false },
            onAdjuntarDocumento = onAdjuntarDocumento
        )
    }

    if (mostrarEditarAvatar) {
        DialogoEditarAvatar(
            idUsuario = idUsuario,
            indiceColor = indiceColor,
            tieneFoto = foto != null,
            avatarStore = avatarStore,
            onCambioGuardado = { version++ },
            onDismiss = { mostrarEditarAvatar = false }
        )
    }

    if (mostrarConfirmarSalida) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que quieres salir?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmarSalida = false
                    SesionPreferencias(contexto).borrar()
                    onCerrarSesion()
                }) { Text("Salir") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}
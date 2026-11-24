package cl.duocuc.miregistro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.ui.theme.Primario
import kotlinx.coroutines.launch

private data class OpcionMenuLateral(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector,
    val badge: Int = 0,
    val colorBadge: Color = Color(0xFFD32F2F),
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenedorConMenuLateral(
    idUsuario: Long,
    titulo: String,
    rutaActual: String,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    onIrInicioAsegurado: () -> Unit = {},
    onConsultarSiniestro: () -> Unit = {},
    onIrPanelPrincipal: () -> Unit = {},
    onIrSiniestrosAnalista: () -> Unit = {},
    onIrCambiosEstadoAnalista: () -> Unit = {},
    onIrSolicitudesCuentaAnalista: () -> Unit = {},
    badgeSinAsignar: Int? = null,
    badgeCambiosEstado: Int? = null,
    badgeSolicitudesCuenta: Int? = null,
    onIrTodosCasosLiquidador: (() -> Unit)? = null,
    onIrActivosLiquidador: (() -> Unit)? = null,
    onIrPendientesLiquidador: (() -> Unit)? = null,
    onIrCerradosLiquidador: (() -> Unit)? = null,
    badgeCasosLiquidador: Int? = null,
    badgeActivosLiquidador: Int? = null,
    badgePendientesLiquidador: Int? = null,
    onEditarPerfil: () -> Unit = {},
    accionesExtra: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    contenido: @Composable (PaddingValues) -> Unit
) {
    val contexto = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val db = remember { UsuarioDbHelper(contexto) }
    val avatarStore = remember { AvatarStore(contexto) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    var version by remember { mutableIntStateOf(0) }
    var mostrarEditarAvatar by remember { mutableStateOf(false) }
    var mostrarConfirmarSalida by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) version++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val usuario = remember(idUsuario, version) { db.buscarPorId(idUsuario) }
    val foto = remember(idUsuario, version) { avatarStore.leerFoto(idUsuario) }
    val indiceColor = remember(idUsuario, version) { avatarStore.leerColor(idUsuario) }
    val colorAvatar = coloresAvatar.getOrElse(indiceColor) { Primario }

    val nombre = usuario?.nombre ?: "Usuario"
    val correo = usuario?.correo ?: ""
    val rol = usuario?.rolNombre ?: "ASEGURADO"

    // Contadores para Analista
    val countSinAsignar = remember(idUsuario, version, rol, badgeSinAsignar) {
        if (rol != "ANALISTA") 0
        else badgeSinAsignar ?: db.todosLosSiniestrosAdmin().count { it.liquidadorId == null }
    }
    val countEstados = remember(idUsuario, version, rol, badgeCambiosEstado) {
        if (rol != "ANALISTA") 0
        else badgeCambiosEstado ?: db.listarSolicitudesCambioEstado().size
    }
    val countCuentas = remember(idUsuario, version, rol, badgeSolicitudesCuenta) {
        if (rol != "ANALISTA") 0
        else badgeSolicitudesCuenta ?: db.listarSolicitudesPendientes().size
    }

    // Contadores para Liquidador
    val resumenLiquidador = remember(idUsuario, version, rol) {
        if (rol != "LIQUIDADOR") null
        else db.resumenLiquidador(idUsuario)
    }
    val countTodosLiq = remember(idUsuario, version, rol, badgeCasosLiquidador, resumenLiquidador) {
        if (rol != "LIQUIDADOR") 0
        else badgeCasosLiquidador ?: (resumenLiquidador?.asignados ?: 0)
    }
    val countActivosLiq = remember(idUsuario, version, rol, badgeActivosLiquidador) {
        if (rol != "LIQUIDADOR") 0
        else badgeActivosLiquidador ?: db.siniestrosDeLiquidador(idUsuario)
            .count { !it.estado.equals("Cerrado", ignoreCase = true) }
    }
    val countPendientesLiq = remember(idUsuario, version, rol, badgePendientesLiquidador, resumenLiquidador) {
        if (rol != "LIQUIDADOR") 0
        else badgePendientesLiquidador ?: (resumenLiquidador?.pendientesDocs ?: 0)
    }

    val opcionesNavegacion = when (rol) {
        "ASEGURADO" -> listOf(
            OpcionMenuLateral("inicio", "Historial de siniestros", Icons.Default.History, onClick = onIrInicioAsegurado),
            OpcionMenuLateral("consulta", "Consultar siniestro", Icons.Default.Search, onClick = onConsultarSiniestro),
            OpcionMenuLateral("editar_perfil", "Editar perfil", Icons.Default.Edit, onClick = onEditarPerfil)
        )
        "LIQUIDADOR" -> listOf(
            OpcionMenuLateral(
                ruta = "liquidador_inicio",
                etiqueta = "Inicio",
                icono = Icons.Default.Home,
                onClick = onIrPanelPrincipal
            ),
            OpcionMenuLateral(
                ruta = "liquidador_todos",
                etiqueta = "Todos mis casos",
                icono = Icons.AutoMirrored.Filled.Assignment,
                badge = countTodosLiq,
                colorBadge = Primario,
                onClick = onIrTodosCasosLiquidador ?: onIrPanelPrincipal
            ),
            OpcionMenuLateral(
                ruta = "liquidador_activos",
                etiqueta = "En evaluación y activos",
                icono = Icons.Default.Schedule,
                badge = countActivosLiq,
                colorBadge = Color(0xFFD97706),
                onClick = onIrActivosLiquidador ?: onIrPanelPrincipal
            ),
            OpcionMenuLateral(
                ruta = "liquidador_pendientes",
                etiqueta = "Pendientes de docs.",
                icono = Icons.Default.Warning,
                badge = countPendientesLiq,
                colorBadge = Color(0xFFDC2626),
                onClick = onIrPendientesLiquidador ?: onIrPanelPrincipal
            ),
            OpcionMenuLateral(
                ruta = "liquidador_cerrados",
                etiqueta = "Casos cerrados",
                icono = Icons.Default.CheckCircle,
                onClick = onIrCerradosLiquidador ?: onIrPanelPrincipal
            ),
            OpcionMenuLateral(
                ruta = "editar_perfil_staff",
                etiqueta = "Editar perfil",
                icono = Icons.Default.Edit,
                onClick = onEditarPerfil
            )
        )
        "ANALISTA" -> listOf(
            OpcionMenuLateral(
                ruta = "analista_inicio",
                etiqueta = "Inicio",
                icono = Icons.Default.Home,
                onClick = onIrPanelPrincipal
            ),
            OpcionMenuLateral(
                ruta = "analista_siniestros",
                etiqueta = "Gestión de siniestros",
                icono = Icons.AutoMirrored.Filled.Assignment,
                badge = countSinAsignar,
                colorBadge = Color(0xFFD97706),
                onClick = onIrSiniestrosAnalista
            ),
            OpcionMenuLateral(
                ruta = "analista_estados",
                etiqueta = "Cambios de estado",
                icono = Icons.Default.SwapHoriz,
                badge = countEstados,
                colorBadge = Primario,
                onClick = onIrCambiosEstadoAnalista
            ),
            OpcionMenuLateral(
                ruta = "analista_cuentas",
                etiqueta = "Solicitudes de cuenta",
                icono = Icons.Default.HowToReg,
                badge = countCuentas,
                colorBadge = Color(0xFF15803D),
                onClick = onIrSolicitudesCuentaAnalista
            ),
            OpcionMenuLateral(
                ruta = "editar_perfil_staff",
                etiqueta = "Editar perfil",
                icono = Icons.Default.Edit,
                onClick = onEditarPerfil
            )
        )
        else -> emptyList()
    }

    val coloresItemMenu = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
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
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = correo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    ) {
                        Text(
                            text = rol,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                opcionesNavegacion.forEach { opcion ->
                    val seleccionada = rutaActual == opcion.ruta
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = opcion.etiqueta,
                                fontWeight = if (seleccionada) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(opcion.icono, contentDescription = null) },
                        badge = if (opcion.badge > 0) {
                            {
                                Badge(
                                    containerColor = opcion.colorBadge,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = opcion.badge.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else null,
                        selected = seleccionada,
                        colors = coloresItemMenu,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (!seleccionada) {
                                opcion.onClick()
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Modo oscuro",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = AjustesTema.oscuro,
                        onCheckedChange = { AjustesTema.cambiar(contexto, it) }
                    )
                }

                val enEditarPerfil = rutaActual == "editar_perfil" || rutaActual == "editar_perfil_staff"
                NavigationDrawerItem(
                    label = { Text("Cambiar contraseña") },
                    icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    selected = false,
                    colors = coloresItemMenu,
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (!enEditarPerfil) {
                            onEditarPerfil()
                        }
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
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    windowInsets = WindowInsets(),
                    title = { Text(titulo, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    },
                    actions = {
                        accionesExtra()
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
            bottomBar = bottomBar,
            content = contenido
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
package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.components.ContenedorConMenuLateral
import cl.duocuc.miregistro.ui.theme.Exito

@Composable
fun EditarPerfilScreen(
    idUsuario: Long,
    onVolver: () -> Unit,
    onVerMisSiniestros: () -> Unit,
    onConsultarSiniestro: () -> Unit = {},
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { UsuarioDbHelper(context) }
    var usuario by remember(idUsuario) { mutableStateOf(dbHelper.buscarPorId(idUsuario)) }

    var nombre by remember(usuario) { mutableStateOf(usuario?.nombre ?: "") }
    var correo by remember(usuario) { mutableStateOf(usuario?.correo ?: "") }

    var claveActual by remember { mutableStateOf("") }
    var claveNueva by remember { mutableStateOf("") }
    var claveConfirmar by remember { mutableStateOf("") }
    var visibleActual by remember { mutableStateOf(false) }
    var visibleNueva by remember { mutableStateOf(false) }
    var visibleConfirmar by remember { mutableStateOf(false) }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    ContenedorConMenuLateral(
        idUsuario = idUsuario,
        titulo = "Editar perfil",
        rutaActual = "editar_perfil",
        onIrInicioAsegurado = onVerMisSiniestros,
        onConsultarSiniestro = onConsultarSiniestro,
        onCerrarSesion = onCerrarSesion
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Datos personales",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    CampoTexto(
                        valor = nombre,
                        onValorCambia = {
                            nombre = it
                            mensajeError = null
                            mensajeExito = null
                        },
                        etiqueta = "Nombre completo",
                        icono = R.drawable.ic_persona
                    )

                    CampoTexto(
                        valor = correo,
                        onValorCambia = {
                            correo = it
                            mensajeError = null
                            mensajeExito = null
                        },
                        etiqueta = "Correo electrónico",
                        icono = R.drawable.ic_correo,
                        tipoTeclado = KeyboardType.Email
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "Cambiar contraseña (opcional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    CampoContrasena(
                        valor = claveActual,
                        onValorCambia = {
                            claveActual = it
                            mensajeError = null
                            mensajeExito = null
                        },
                        etiqueta = "Contraseña actual",
                        visible = visibleActual,
                        onAlternarVisible = { visibleActual = !visibleActual }
                    )

                    CampoContrasena(
                        valor = claveNueva,
                        onValorCambia = {
                            claveNueva = it
                            mensajeError = null
                            mensajeExito = null
                        },
                        etiqueta = "Nueva contraseña",
                        visible = visibleNueva,
                        onAlternarVisible = { visibleNueva = !visibleNueva }
                    )

                    CampoContrasena(
                        valor = claveConfirmar,
                        onValorCambia = {
                            claveConfirmar = it
                            mensajeError = null
                            mensajeExito = null
                        },
                        etiqueta = "Confirmar nueva contraseña",
                        visible = visibleConfirmar,
                        onAlternarVisible = { visibleConfirmar = !visibleConfirmar }
                    )

                    mensajeError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    mensajeExito?.let {
                        Text(
                            text = it,
                            color = Exito,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    BotonPrincipal(
                        texto = "Guardar cambios",
                        onClick = {
                            if (nombre.isBlank() || correo.isBlank()) {
                                mensajeError = "El nombre y el correo no pueden quedar vacíos"
                                return@BotonPrincipal
                            }

                            val perfilActualizado = dbHelper.actualizarPerfil(idUsuario, nombre, correo)
                            if (!perfilActualizado) {
                                mensajeError = "Ese correo ya está registrado por otro usuario"
                                return@BotonPrincipal
                            }

                            if (claveActual.isNotBlank() || claveNueva.isNotBlank() || claveConfirmar.isNotBlank()) {
                                if (claveActual.isBlank()) {
                                    mensajeError = "Ingresa tu contraseña actual"
                                    return@BotonPrincipal
                                }

                                if (claveNueva != claveConfirmar) {
                                    mensajeError = "Las contraseñas nuevas no coinciden"
                                    return@BotonPrincipal
                                }

                                val esValida = claveNueva.length >= 8 &&
                                        claveNueva.any { it.isUpperCase() } &&
                                        claveNueva.any { it.isDigit() } &&
                                        claveNueva.any { !it.isLetterOrDigit() }

                                if (!esValida) {
                                    mensajeError = "La nueva contraseña debe tener 8 caracteres, 1 mayúscula, 1 número y 1 símbolo"
                                    return@BotonPrincipal
                                }

                                val claveCambiada = dbHelper.cambiarClave(idUsuario, claveActual, claveNueva)
                                if (!claveCambiada) {
                                    mensajeError = "La contraseña actual es incorrecta"
                                    return@BotonPrincipal
                                }
                                claveActual = ""
                                claveNueva = ""
                                claveConfirmar = ""
                            }

                            usuario = dbHelper.buscarPorId(idUsuario)
                            mensajeError = null
                            mensajeExito = "Perfil actualizado correctamente"
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
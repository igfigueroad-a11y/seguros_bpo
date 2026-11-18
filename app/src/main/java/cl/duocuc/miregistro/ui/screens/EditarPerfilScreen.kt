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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.components.MiRegistroTopBar
import cl.duocuc.miregistro.ui.theme.Exito
import cl.duocuc.miregistro.ui.theme.FondoClaro
import cl.duocuc.miregistro.ui.theme.Primario

@Composable
fun EditarPerfilScreen(
    idUsuario: Long,
    onVolver: () -> Unit,
    onVerMisSiniestros: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { UsuarioDbHelper(context) }
    var usuario by remember(idUsuario) { mutableStateOf(dbHelper.buscarPorId(idUsuario)) }

    var nombre by remember(usuario) { mutableStateOf(usuario?.nombre ?: "") }
    var correo by remember(usuario) { mutableStateOf(usuario?.correo ?: "") }

    var claveActual by remember { mutableStateOf("") }
    var claveNueva by remember { mutableStateOf("") }
    var claveVisible by remember { mutableStateOf(false) }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            MiRegistroTopBar(
                titulo = "Editar perfil",
                usuario = usuario,
                mostrarVolver = true,
                onVolver = onVolver,
                onVerMisSiniestros = onVerMisSiniestros,
                onEditarPerfil = {},
                onCerrarSesion = onCerrarSesion
            )
        }
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = Primario
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
                        color = Primario
                    )

                    CampoContrasena(
                        valor = claveActual,
                        onValorCambia = {
                            claveActual = it
                            mensajeError = null
                            mensajeExito = null
                        },
                        etiqueta = "Contraseña actual",
                        visible = claveVisible,
                        onAlternarVisible = { claveVisible = !claveVisible }
                    )

                    CampoContrasena(
                        valor = claveNueva,
                        onValorCambia = {
                            claveNueva = it
                            mensajeError = null
                            mensajeExito = null
                        },
                        etiqueta = "Nueva contraseña",
                        visible = claveVisible,
                        onAlternarVisible = { claveVisible = !claveVisible }
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

                            if (claveActual.isNotBlank() || claveNueva.isNotBlank()) {
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
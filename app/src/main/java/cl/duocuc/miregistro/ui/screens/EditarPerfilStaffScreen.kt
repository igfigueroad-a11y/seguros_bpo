package cl.duocuc.miregistro.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.components.ContenedorConMenuLateral
import cl.duocuc.miregistro.ui.theme.Dimens

private fun esClaveStaffValida(password: String): Boolean =
    password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() }

@Composable
fun EditarPerfilStaffScreen(
    idUsuario: Long,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { UsuarioDbHelper(context) }
    val usuario = remember(idUsuario) { db.buscarPorId(idUsuario) }

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var errorDatos by remember { mutableStateOf<String?>(null) }

    var claveActual by remember { mutableStateOf("") }
    var claveNueva by remember { mutableStateOf("") }
    var claveConfirmar by remember { mutableStateOf("") }
    var visibleActual by remember { mutableStateOf(false) }
    var visibleNueva by remember { mutableStateOf(false) }
    var visibleConfirmar by remember { mutableStateOf(false) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuario) {
        usuario?.let {
            nombre = it.nombre
            correo = it.correo
        }
    }

    ContenedorConMenuLateral(
        idUsuario = idUsuario,
        titulo = "Editar perfil",
        rutaActual = "editar_perfil_staff",
        onIrPanelPrincipal = onVolver,
        onCerrarSesion = onCerrarSesion
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.espacioGrande),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Datos personales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        ) {
                            Text(
                                text = usuario?.rolNombre ?: "PERSONAL",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "RUT: ${usuario?.rut ?: "-"}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    CampoTexto(
                        valor = nombre,
                        onValorCambia = {
                            nombre = it
                            errorDatos = null
                        },
                        etiqueta = "Nombre completo",
                        icono = R.drawable.ic_persona
                    )

                    CampoTexto(
                        valor = correo,
                        onValorCambia = {
                            correo = it
                            errorDatos = null
                        },
                        etiqueta = "Correo electrónico",
                        icono = R.drawable.ic_correo,
                        tipoTeclado = KeyboardType.Email
                    )

                    errorDatos?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    BotonPrincipal(
                        texto = "Guardar datos",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            when {
                                nombre.isBlank() || correo.isBlank() ->
                                    errorDatos = "El nombre y el correo son obligatorios"
                                !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() ->
                                    errorDatos = "Ingresa un correo electrónico válido"
                                else -> {
                                    val ok = db.actualizarPerfil(idUsuario, nombre, correo)
                                    if (ok) {
                                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                    } else {
                                        errorDatos = "Ese correo ya está en uso por otra cuenta"
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.espacioGrande),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
                ) {
                    Text(
                        text = "Cambiar contraseña",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    CampoContrasena(
                        valor = claveActual,
                        onValorCambia = {
                            claveActual = it
                            errorClave = null
                        },
                        etiqueta = "Contraseña actual",
                        visible = visibleActual,
                        onAlternarVisible = { visibleActual = !visibleActual }
                    )

                    CampoContrasena(
                        valor = claveNueva,
                        onValorCambia = {
                            claveNueva = it
                            errorClave = null
                        },
                        etiqueta = "Nueva contraseña",
                        visible = visibleNueva,
                        onAlternarVisible = { visibleNueva = !visibleNueva }
                    )

                    CampoContrasena(
                        valor = claveConfirmar,
                        onValorCambia = {
                            claveConfirmar = it
                            errorClave = null
                        },
                        etiqueta = "Confirmar nueva contraseña",
                        visible = visibleConfirmar,
                        onAlternarVisible = { visibleConfirmar = !visibleConfirmar }
                    )

                    errorClave?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    BotonPrincipal(
                        texto = "Actualizar contraseña",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            when {
                                claveActual.isBlank() || claveNueva.isBlank() || claveConfirmar.isBlank() ->
                                    errorClave = "Completa todos los campos de contraseña"
                                claveNueva != claveConfirmar ->
                                    errorClave = "Las contraseñas nuevas no coinciden"
                                !esClaveStaffValida(claveNueva) ->
                                    errorClave = "Debe tener al menos 8 caracteres, 1 mayúscula, 1 número y 1 carácter especial"
                                else -> {
                                    val ok = db.cambiarClave(idUsuario, claveActual, claveNueva)
                                    if (ok) {
                                        claveActual = ""
                                        claveNueva = ""
                                        claveConfirmar = ""
                                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                    } else {
                                        errorClave = "La contraseña actual es incorrecta"
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
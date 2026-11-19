package cl.duocuc.miregistro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.theme.Dimens


private fun esContrasenaValida(password: String): Boolean {
    val longitudValida = password.length >= 8
    val tieneMayuscula = password.any { it.isUpperCase() }
    val tieneNumero = password.any { it.isDigit() }
    val tieneEspecial = password.any { !it.isLetterOrDigit() }
    return longitudValida && tieneMayuscula && tieneNumero && tieneEspecial
}

@Composable
fun NuevaContrasenaScreen(
    correo: String,
    onContrasenaActualizada: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dbHelper = remember { UsuarioDbHelper(context) }

    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(Dimens.espacioPantalla),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.espacioGrande),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Identidad validada",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Crea tu nueva contraseña para $correo",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                CampoContrasena(
                    valor = nuevaContrasena,
                    onValorCambia = { nuevaContrasena = it },
                    etiqueta = "Nueva contraseña",
                    visible = contrasenaVisible,
                    onAlternarVisible = { contrasenaVisible = !contrasenaVisible }
                )

                CampoContrasena(
                    valor = confirmarContrasena,
                    onValorCambia = { confirmarContrasena = it },
                    etiqueta = "Confirmar contraseña",
                    visible = confirmarVisible,
                    onAlternarVisible = { confirmarVisible = !confirmarVisible }
                )

                mensajeError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }

                BotonPrincipal(
                    texto = "Actualizar contraseña",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (nuevaContrasena.isBlank() || confirmarContrasena.isBlank()) {
                            mensajeError = "Ambos campos son obligatorios"
                        } else if (nuevaContrasena != confirmarContrasena) {
                            mensajeError = "Las contraseñas no coinciden"
                        } else if (!esContrasenaValida(nuevaContrasena)) {

                            mensajeError = "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 número y 1 carácter especial"
                        } else {
                            val exito = dbHelper.actualizarContrasena(correo, nuevaContrasena)
                            if (exito) {
                                Toast.makeText(context, "Contraseña actualizada con éxito", Toast.LENGTH_LONG).show()
                                onContrasenaActualizada()
                            } else {
                                mensajeError = "Error al actualizar la contraseña"
                            }
                        }
                    }
                )
            }
        }
    }
}
package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Importación agregada
import androidx.compose.ui.text.style.TextAlign
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.theme.Dimens

@Composable
fun RecuperarClaveScreen(
    onVolver: () -> Unit,
    onRecuperarExitoso: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val dbHelper = remember { UsuarioDbHelper(context) }

    var correo by remember { mutableStateOf("") }
    var respuesta1 by remember { mutableStateOf("") }
    var respuesta2 by remember { mutableStateOf("") }
    var respuesta3 by remember { mutableStateOf("") }

    // Estado para mostrar un error si se equivoca
    var mensajeError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = Dimens.elevacionTarjeta
                )
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.espacioGrande),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Recuperar contraseña",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Responde las preguntas de seguridad para verificar tu identidad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    CampoTexto(
                        valor = correo,
                        onValorCambia = { correo = it },
                        etiqueta = "Correo electrónico asociado",
                        icono = R.drawable.ic_correo
                    )

                    Text(
                        text = "¿Cuál es el nombre de tu primera mascota?",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                    CampoTexto(
                        valor = respuesta1,
                        onValorCambia = { respuesta1 = it },
                        etiqueta = "Respuesta 1",
                        icono = R.drawable.ic_candado
                    )

                    Text(
                        text = "¿En qué ciudad naciste?",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                    CampoTexto(
                        valor = respuesta2,
                        onValorCambia = { respuesta2 = it },
                        etiqueta = "Respuesta 2",
                        icono = R.drawable.ic_candado
                    )

                    Text(
                        text = "¿Cuál es tu película favorita?",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                    CampoTexto(
                        valor = respuesta3,
                        onValorCambia = { respuesta3 = it },
                        etiqueta = "Respuesta 3",
                        icono = R.drawable.ic_candado
                    )


                    mensajeError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    BotonPrincipal(
                        texto = "Validar respuestas",
                        onClick = {
                            // 2. Usamos dbHelper (instancia) en vez de UsuarioDbHelper (clase)
                            val sonCorrectas = dbHelper.validarRespuestasSeguridad(correo, respuesta1, respuesta2, respuesta3)

                            if (sonCorrectas) {
                                mensajeError = null
                                onRecuperarExitoso(correo)
                            } else {
                                mensajeError = "Datos incorrectos. Verifica tu correo y las respuestas ingresadas."
                            }
                        }
                    )

                    TextButton(onClick = onVolver) {
                        Text(
                            text = "Volver a iniciar sesión",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
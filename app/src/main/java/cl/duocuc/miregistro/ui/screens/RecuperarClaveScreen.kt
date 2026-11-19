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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.components.AzulFondoSuave
import cl.duocuc.miregistro.ui.components.AzulPrincipal
import cl.duocuc.miregistro.ui.components.BloquePreguntaSeguridad
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

    var pregunta1 by remember { mutableStateOf("") }
    var pregunta2 by remember { mutableStateOf("") }
    var pregunta3 by remember { mutableStateOf("") }

    var respuesta1 by remember { mutableStateOf("") }
    var respuesta2 by remember { mutableStateOf("") }
    var respuesta3 by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AzulFondoSuave),
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
                        text = "Selecciona tus 3 preguntas de seguridad y respóndelas para verificar tu identidad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    CampoTexto(
                        valor = correo,
                        onValorCambia = {
                            correo = it
                            mensajeError = null
                        },
                        etiqueta = "Correo electrónico asociado",
                        icono = R.drawable.ic_correo
                    )

                    BloquePreguntaSeguridad(
                        numeroPregunta = 1,
                        preguntaSeleccionada = pregunta1,
                        preguntasExcluidas = listOf(pregunta2, pregunta3).filter { it.isNotBlank() },
                        onPreguntaSelected = {
                            pregunta1 = it
                            mensajeError = null
                        },
                        respuesta = respuesta1,
                        onRespuestaChange = {
                            respuesta1 = it
                            mensajeError = null
                        }
                    )

                    BloquePreguntaSeguridad(
                        numeroPregunta = 2,
                        preguntaSeleccionada = pregunta2,
                        preguntasExcluidas = listOf(pregunta1, pregunta3).filter { it.isNotBlank() },
                        onPreguntaSelected = {
                            pregunta2 = it
                            mensajeError = null
                        },
                        respuesta = respuesta2,
                        onRespuestaChange = {
                            respuesta2 = it
                            mensajeError = null
                        }
                    )

                    BloquePreguntaSeguridad(
                        numeroPregunta = 3,
                        preguntaSeleccionada = pregunta3,
                        preguntasExcluidas = listOf(pregunta1, pregunta2).filter { it.isNotBlank() },
                        onPreguntaSelected = {
                            pregunta3 = it
                            mensajeError = null
                        },
                        respuesta = respuesta3,
                        onRespuestaChange = {
                            respuesta3 = it
                            mensajeError = null
                        }
                    )

                    mensajeError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    BotonPrincipal(
                        texto = "Validar respuestas",
                        onClick = {
                            when {
                                correo.isBlank() ->
                                    mensajeError = "Ingresa tu correo electrónico asociado"
                                pregunta1.isBlank() || pregunta2.isBlank() || pregunta3.isBlank() ->
                                    mensajeError = "Debes seleccionar las 3 preguntas de seguridad"
                                respuesta1.isBlank() || respuesta2.isBlank() || respuesta3.isBlank() ->
                                    mensajeError = "Responde las 3 preguntas seleccionadas"
                                else -> {
                                    val sonCorrectas = dbHelper.validarRespuestasSeguridad(
                                        correo = correo,
                                        preg1 = pregunta1,
                                        resp1 = respuesta1,
                                        preg2 = pregunta2,
                                        resp2 = respuesta2,
                                        preg3 = pregunta3,
                                        resp3 = respuesta3
                                    )
                                    if (sonCorrectas) {
                                        mensajeError = null
                                        onRecuperarExitoso(correo.trim())
                                    } else {
                                        mensajeError = "Datos incorrectos. Verifica tu correo, las preguntas elegidas y tus respuestas."
                                    }
                                }
                            }
                        }
                    )

                    TextButton(onClick = onVolver) {
                        Text(
                            text = "Volver a iniciar sesión",
                            color = AzulPrincipal,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
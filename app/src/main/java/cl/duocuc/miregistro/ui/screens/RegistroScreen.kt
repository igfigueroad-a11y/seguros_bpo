package cl.duocuc.miregistro.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.ui.components.AzulFondoSuave
import cl.duocuc.miregistro.ui.components.AzulPrincipal
import cl.duocuc.miregistro.ui.components.BloquePreguntaSeguridad
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.components.EncabezadoEmpresa
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onNavegarLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel()
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }

    var mostrarPreguntas by remember { mutableStateOf(false) }
    var pregunta1 by remember { mutableStateOf("") }
    var pregunta2 by remember { mutableStateOf("") }
    var pregunta3 by remember { mutableStateOf("") }
    var respuesta1 by remember { mutableStateOf("") }
    var respuesta2 by remember { mutableStateOf("") }
    var respuesta3 by remember { mutableStateOf("") }

    val registroExitoso by viewModel.registroExitoso.collectAsStateWithLifecycle()
    val mensajeError by viewModel.mensajeError.collectAsStateWithLifecycle()

    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            Toast.makeText(
                context,
                "Solicitud enviada. Un analista revisará tus datos para aprobar tu cuenta.",
                Toast.LENGTH_LONG
            ).show()
            viewModel.limpiarEstado()
            onRegistroExitoso()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AzulFondoSuave)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoEmpresa(onVolver = onNavegarLogin)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.espacioPantalla)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        text = "Crea tu cuenta",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Tu cuenta quedará sujeta a aprobación de un analista antes de iniciar sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    CampoTexto(
                        valor = nombre,
                        onValorCambia = { nombre = it },
                        etiqueta = "Nombre completo",
                        icono = R.drawable.ic_persona
                    )

                    CampoTexto(
                        valor = rut,
                        onValorCambia = { rut = it },
                        etiqueta = "RUT",
                        icono = R.drawable.ic_persona
                    )

                    CampoTexto(
                        valor = correo,
                        onValorCambia = { correo = it },
                        etiqueta = "Correo electrónico",
                        icono = R.drawable.ic_correo,
                        tipoTeclado = KeyboardType.Email
                    )

                    CampoContrasena(
                        valor = contrasena,
                        onValorCambia = { contrasena = it },
                        etiqueta = "Contraseña",
                        visible = contrasenaVisible,
                        onAlternarVisible = { contrasenaVisible = !contrasenaVisible }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { mostrarPreguntas = !mostrarPreguntas },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, AzulPrincipal),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrincipal)
                    ) {
                        Text(
                            if (mostrarPreguntas) "Ocultar preguntas de seguridad"
                            else "Configurar preguntas de seguridad"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (mostrarPreguntas) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = AzulPrincipal
                        )
                    }

                    AnimatedVisibility(visible = mostrarPreguntas) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "Elige 3 preguntas de la lista y respóndelas. Son obligatorias para recuperar tu acceso si olvidas tu contraseña.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AzulPrincipal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            BloquePreguntaSeguridad(
                                numeroPregunta = 1,
                                preguntaSeleccionada = pregunta1,
                                preguntasExcluidas = listOf(pregunta2, pregunta3).filter { it.isNotBlank() },
                                onPreguntaSelected = { pregunta1 = it },
                                respuesta = respuesta1,
                                onRespuestaChange = { respuesta1 = it }
                            )

                            BloquePreguntaSeguridad(
                                numeroPregunta = 2,
                                preguntaSeleccionada = pregunta2,
                                preguntasExcluidas = listOf(pregunta1, pregunta3).filter { it.isNotBlank() },
                                onPreguntaSelected = { pregunta2 = it },
                                respuesta = respuesta2,
                                onRespuestaChange = { respuesta2 = it }
                            )

                            BloquePreguntaSeguridad(
                                numeroPregunta = 3,
                                preguntaSeleccionada = pregunta3,
                                preguntasExcluidas = listOf(pregunta1, pregunta2).filter { it.isNotBlank() },
                                onPreguntaSelected = { pregunta3 = it },
                                respuesta = respuesta3,
                                onRespuestaChange = { respuesta3 = it }
                            )
                        }
                    }

                    mensajeError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    BotonPrincipal(
                        texto = "Solicitar registro",
                        onClick = {
                            viewModel.registrar(
                                nombre = nombre,
                                rut = rut,
                                correo = correo,
                                contrasena = contrasena,
                                preg1 = pregunta1,
                                resp1 = respuesta1,
                                preg2 = pregunta2,
                                resp2 = respuesta2,
                                preg3 = pregunta3,
                                resp3 = respuesta3
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = onNavegarLogin) {
                        Text(
                            text = "Ya tengo una cuenta · Iniciar sesión",
                            color = AzulPrincipal,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onNavegarLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }

    // Observamos el estado del ViewModel
    val registroExitoso by viewModel.registroExitoso.collectAsStateWithLifecycle()
    val mensajeError by viewModel.mensajeError.collectAsStateWithLifecycle()

    // Si el registro fue exitoso, navegamos
    LaunchedEffect(registroExitoso) {
        if (registroExitoso) {
            viewModel.limpiarEstado()
            onRegistroExitoso()
        }
    }

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
                        text = "Regístrate para gestionar tus siniestros",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    CampoTexto(
                        valor = nombre,
                        onValorCambia = { nombre = it },
                        etiqueta = "Nombre completo",
                        icono = R.drawable.ic_correo
                    )

                    CampoTexto(
                        valor = rut,
                        onValorCambia = { rut = it },
                        etiqueta = "RUT",
                        icono = R.drawable.ic_correo
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

                    // Mostrar error si existe
                    mensajeError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    BotonPrincipal(
                        texto = "Registrarme",
                        onClick = {

                            viewModel.registrar(nombre, rut, correo, contrasena)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = onNavegarLogin) {
                        Text(
                            text = "Ya tengo una cuenta · Iniciar sesión",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
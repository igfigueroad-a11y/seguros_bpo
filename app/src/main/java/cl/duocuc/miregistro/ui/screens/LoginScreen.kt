package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.SesionPreferencias
import cl.duocuc.miregistro.model.LoginUiState
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.components.EncabezadoEmpresa
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    onNavegarRecuperar: () -> Unit,
    onNavegarRegistro: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel? = null
) {
    val context = LocalContext.current
    val sesion = remember { SesionPreferencias(context) }
    val vm = viewModel ?: viewModel {
        LoginViewModel(
            onGuardarSesion = { id, rolId, token ->
                sesion.guardar(id, rolId, token)
            }
        )
    }

    val estado by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.usuario) {
        estado.usuario?.let { usuario ->
            onLoginExitoso(usuario)
            vm.onLoginConsumido()
        }
    }

    LoginContenido(
        estado = estado,
        onCorreoCambia = vm::onCorreoCambia,
        onContrasenaCambia = vm::onContrasenaCambia,
        onAlternarVisibilidad = vm::onAlternarVisibilidad,
        onIngresar = vm::onIngresar,
        onNavegarRecuperar = onNavegarRecuperar,
        onNavegarRegistro = onNavegarRegistro,
        modifier = modifier
    )
}

@Composable
private fun LoginContenido(
    estado: LoginUiState,
    onCorreoCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onIngresar: () -> Unit,
    onNavegarRecuperar: () -> Unit,
    onNavegarRegistro: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoEmpresa()

        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                        text = stringResource(R.string.login_titulo),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.login_subtitulo),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(Dimens.espacioChico))

                    CampoTexto(
                        valor = estado.correo,
                        onValorCambia = onCorreoCambia,
                        etiqueta = stringResource(R.string.login_correo),
                        icono = R.drawable.ic_correo,
                        error = estado.errorCorreo?.let { stringResource(it) },
                        tipoTeclado = KeyboardType.Email
                    )

                    CampoContrasena(
                        valor = estado.contrasena,
                        onValorCambia = onContrasenaCambia,
                        etiqueta = stringResource(R.string.login_contrasena),
                        visible = estado.contrasenaVisible,
                        onAlternarVisible = onAlternarVisibilidad,
                        error = estado.errorContrasena?.let { stringResource(it) }
                    )

                    TextButton(
                        onClick = onNavegarRecuperar,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    estado.mensajeBloqueo?.let { aviso ->
                        Text(
                            text = aviso,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    estado.errorGeneral?.let { idMensaje ->
                        Text(
                            text = stringResource(idMensaje),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    BotonPrincipal(
                        texto = stringResource(R.string.login_boton),
                        onClick = onIngresar,
                        habilitado = !estado.cargando,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(
                        onClick = onNavegarRegistro,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "¿No tienes una cuenta? Regístrate",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    if (estado.cargando) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // Añadido para el botón de texto
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.model.LoginUiState
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.CampoContrasena
import cl.duocuc.miregistro.ui.components.CampoTexto
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.ui.theme.MiRegistroTheme
import cl.duocuc.miregistro.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: (Usuario) -> Unit,
    onNavegarRecuperar: () -> Unit, // NUEVO: Parámetro de navegación
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.usuario) {
        estado.usuario?.let { usuario ->
            onLoginExitoso(usuario)
            viewModel.onLoginConsumido()
        }
    }

    LoginContenido(
        estado = estado,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onIngresar = viewModel::onIngresar,
        onNavegarRecuperar = onNavegarRecuperar, // Pasamos la función al contenido
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
    onNavegarRecuperar: () -> Unit, // NUEVO: Recibimos la acción
    modifier: Modifier = Modifier
) {
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
                    verticalArrangement = Arrangement.spacedBy(
                        Dimens.espacioMedio
                    ),
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

                    Spacer(
                        modifier = Modifier.height(Dimens.espacioChico)
                    )

                    CampoTexto(
                        valor = estado.correo,
                        onValorCambia = onCorreoCambia,
                        etiqueta = stringResource(R.string.login_correo),
                        icono = R.drawable.ic_correo,
                        error = estado.errorCorreo?.let {
                            stringResource(it)
                        },
                        tipoTeclado = KeyboardType.Email
                    )

                    CampoContrasena(
                        valor = estado.contrasena,
                        onValorCambia = onContrasenaCambia,
                        etiqueta = stringResource(
                            R.string.login_contrasena
                        ),
                        visible = estado.contrasenaVisible,
                        onAlternarVisible = onAlternarVisibilidad,
                        error = estado.errorContrasena?.let {
                            stringResource(it)
                        }
                    )

                    // NUEVO: Botón de "Olvidaste tu contraseña" alineado a la derecha
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

                    if (estado.cargando) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    MiRegistroTheme {
        LoginContenido(
            estado = LoginUiState(),
            onCorreoCambia = {},
            onContrasenaCambia = {},
            onAlternarVisibilidad = {},
            onIngresar = {},
            onNavegarRecuperar = {} // Añadido para el Preview
        )
    }
}
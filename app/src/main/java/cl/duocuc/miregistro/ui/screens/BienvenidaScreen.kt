package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.ui.components.BotonPrincipal
import cl.duocuc.miregistro.ui.components.BotonSecundario
import cl.duocuc.miregistro.ui.components.ImagenEncabezado
import cl.duocuc.miregistro.ui.theme.Dimens

@Composable
fun BienvenidaScreen(
    onNavegarLogin: () -> Unit,
    onNavegarRegistro: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImagenEncabezado(
            imagen = R.drawable.ic_launcher_background, // Cambia por tu logo/banner
            descripcion = "Bienvenida",
            titulo = "Servicios Corporativos Andes",
            subtitulo = "Gestión de siniestros"
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            BotonPrincipal(
                texto = "Iniciar sesión",
                onClick = onNavegarLogin,
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = "Registrarme",
                onClick = onNavegarRegistro,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
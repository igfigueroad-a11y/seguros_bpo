package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.ui.theme.GrisCampo
import cl.duocuc.miregistro.viewmodel.ConsultaSiniestroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultaSiniestroScreen(
    onSiniestroEncontrado: (String) -> Unit,
    onCerrarSesion: () -> Unit = {},
    viewModel: ConsultaSiniestroViewModel = viewModel()
) {
    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(ui.siniestroEncontrado) {
        ui.siniestroEncontrado?.let {
            onSiniestroEncontrado(it)
            viewModel.navegacionConsumida()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text(
                            text = "Salir",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Consulta tu Siniestro",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = ui.numero,
                onValueChange = viewModel::onNumeroChange,
                placeholder = {
                    Text(
                        "Ingresa el N° de siniestro\n(ej: SIN-26-008942)",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                isError = ui.error != null,
                supportingText = { ui.error?.let { Text(it) } },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GrisCampo,
                    unfocusedContainerColor = GrisCampo,
                    unfocusedBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = { viewModel.buscar() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = viewModel::buscar,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth(0.75f).height(56.dp)
            ) {
                Icon(Icons.Filled.Search, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Buscar", fontWeight = FontWeight.Bold)
            }
        }
    }
}
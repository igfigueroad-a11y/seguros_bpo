package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.FiltroSiniestro
import cl.duocuc.miregistro.ui.components.ContenedorConMenuLateral
import cl.duocuc.miregistro.viewmodel.ConsultaSiniestroViewModel

private val ColorEnProceso = Color(0xFFFFB74D)
private val ColorEnProcesoTexto = Color(0xFFE65100)
private val ColorCerrado = Color(0xFF81C784)
private val ColorCerradoTexto = Color(0xFF1B5E20)
private val ColorIndigoSuave = Color(0xFF3949AB)

@Composable
fun ConsultaSiniestroScreen(
    idUsuario: Long,
    mostrarBienvenida: Boolean = false,
    onBienvenidaMostrada: () -> Unit = {},
    onSiniestroEncontrado: (String) -> Unit,
    onVerMisSiniestros: () -> Unit = {},
    onEditarPerfil: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: ConsultaSiniestroViewModel = viewModel()
) {
    val context = LocalContext.current
    val dbHelper = remember { UsuarioDbHelper(context) }
    val usuario = remember(idUsuario) { dbHelper.buscarPorId(idUsuario) }
    val resumen = remember(idUsuario) { dbHelper.resumenAsegurado(idUsuario) }
    val misSiniestros = remember(idUsuario) {
        dbHelper.siniestrosDeAsegurado(idUsuario, FiltroSiniestro.TODOS).take(3)
    }

    val ui by viewModel.ui.collectAsState()

    LaunchedEffect(ui.siniestroEncontrado) {
        ui.siniestroEncontrado?.let {
            onSiniestroEncontrado(it)
            viewModel.navegacionConsumida()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (mostrarBienvenida) {
                onBienvenidaMostrada()
            }
        }
    }

    val cerrarSesionLimpio = {
        if (mostrarBienvenida) {
            onBienvenidaMostrada()
        }
        onCerrarSesion()
    }

    ContenedorConMenuLateral(
        idUsuario = idUsuario,
        titulo = "Consultar siniestro",
        rutaActual = "consulta",
        onIrInicioAsegurado = onVerMisSiniestros,
        onEditarPerfil = onEditarPerfil,
        onCerrarSesion = cerrarSesionLimpio
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                        ),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Consulta tu Siniestro",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Ingresa tu código de seguimiento para revisar el estado en tiempo real.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PildoraResumenConsulta(
                            etiqueta = "Total",
                            valor = resumen.total,
                            colorAcento = Color.White.copy(alpha = 0.22f),
                            modifier = Modifier.weight(1f)
                        )
                        PildoraResumenConsulta(
                            etiqueta = "En proceso",
                            valor = resumen.enProceso,
                            colorAcento = ColorEnProceso.copy(alpha = 0.30f),
                            modifier = Modifier.weight(1f)
                        )
                        PildoraResumenConsulta(
                            etiqueta = "Cerrados",
                            valor = resumen.completados,
                            colorAcento = ColorCerrado.copy(alpha = 0.30f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Código de seguimiento",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = ui.numero,
                            onValueChange = viewModel::onNumeroChange,
                            placeholder = {
                                Text(
                                    text = "Ej: SIN-26-008942",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            isError = ui.error != null,
                            supportingText = {
                                ui.error?.let {
                                    Text(text = it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(onSearch = { viewModel.buscar() }),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = viewModel::buscar,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Text("Buscar siniestro", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ColorIndigoSuave,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Acceso rápido a tus casos",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (misSiniestros.isEmpty()) {
                            Text(
                                text = "Aún no tienes siniestros asociados a tu cuenta.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            misSiniestros.forEachIndexed { index, item ->
                                if (index > 0) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }
                                val esCerrado = item.estado.equals("Cerrado", ignoreCase = true)
                                val fondoEstado = if (esCerrado) ColorCerrado.copy(alpha = 0.22f) else ColorEnProceso.copy(alpha = 0.22f)
                                val textoEstado = if (esCerrado) ColorCerradoTexto else ColorEnProcesoTexto

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onSiniestroEncontrado(item.numero) }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.numero,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = item.tipo,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = fondoEstado
                                            ) {
                                                Text(
                                                    text = item.estado,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = textoEstado,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Ver detalle",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = onVerMisSiniestros,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, tint = ColorIndigoSuave)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ver todos mis siniestros", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    if (mostrarBienvenida && usuario != null) {
        AlertDialog(
            onDismissRequest = onBienvenidaMostrada,
            title = {
                Text(
                    text = "Bienvenido, ${usuario.nombre}",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Puedes buscar un siniestro por su código o gestionar tu cuenta desde el menú superior.")
            },
            confirmButton = {
                TextButton(onClick = onBienvenidaMostrada) {
                    Text("Continuar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun PildoraResumenConsulta(
    etiqueta: String,
    valor: Int,
    colorAcento: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = colorAcento,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = etiqueta,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.92f)
            )
        }
    }
}
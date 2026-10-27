package cl.duocuc.miregistro.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.model.Siniestro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSiniestroScreen(
    siniestro: Siniestro,
    onVolver: () -> Unit,
    onAdjuntarEvidencia: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorFondo = colorResource(id = R.color.fondo)
    val colorTextPrincipal = colorResource(id = R.color.texto_primario)
    val colorTextSecundario = colorResource(id = R.color.texto_secundario)

    val colorActual = colorResource(id = R.color.estado_actual)
    val colorCompletado = colorResource(id = R.color.estado_completado)
    val colorPendiente = colorResource(id = R.color.estado_pendiente)
    val colorLinea = colorResource(id = R.color.linea_tiempo)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del siniestro",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colorTextPrincipal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = colorTextPrincipal, modifier = Modifier.size(20.dp))
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.contenedor_primario)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = colorResource(id = R.color.primario), modifier = Modifier.size(16.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo)
            )
        },
        containerColor = colorFondo
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // 1. Tarjeta principal superior
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.superficie)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = siniestro.numero,
                            fontSize = 20.sp,
                            color = colorTextPrincipal,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorResource(id = R.color.insignia_evaluacion_fondo))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = siniestro.etapaNombre.uppercase(),
                                color = colorResource(id = R.color.insignia_evaluacion_texto),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Cliente / Asegurado", fontSize = 11.sp, color = colorTextSecundario)
                            Text(siniestro.aseguradoNombre, fontSize = 14.sp, color = colorTextPrincipal, fontWeight = FontWeight.Medium)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Vehículo / Tipo", fontSize = 11.sp, color = colorTextSecundario)
                            Text(siniestro.tipoSiniestro, fontSize = 14.sp, color = colorTextPrincipal, fontWeight = FontWeight.Medium)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = colorFondo)

                    // Fecha de última actualización
                    Text(
                        text = "Última actualización · ${siniestro.fechaUltimaActualizacion}",
                        fontSize = 11.sp,
                        color = colorTextSecundario
                    )

                    // TARJETA TIPO ALERTA DESTACADA DEBAJO DE ÚLTIMA ACTUALIZACIÓN
                    if (siniestro.comentario.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFEFF6FF)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Observación del liquidador:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E40AF)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = siniestro.comentario,
                                        fontSize = 13.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Sección de Seguimiento del caso (Línea de tiempo)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Seguimiento del caso", fontSize = 18.sp, color = colorTextPrincipal, fontWeight = FontWeight.Bold)
                Text("Consulta el avance de tu siniestro en tiempo real", fontSize = 12.sp, color = colorTextSecundario)

                Spacer(modifier = Modifier.height(16.dp))

                val etapas = listOf(
                    Pair("Recibido", "Reporte registrado correctamente"),
                    Pair("En evaluación", "Nuestro equipo está analizando tu caso"),
                    Pair("En liquidación", "Pendiente de revisión y aprobación"),
                    Pair("Cerrado", "Te notificaremos cuando el caso finalice")
                )

                etapas.forEachIndexed { index, etapa ->
                    val idEtapaFila = index + 1L
                    val estaCompletado = idEtapaFila < siniestro.idEtapa || (idEtapaFila == 4L && siniestro.idEtapa == 4L)
                    val esActual = idEtapaFila == siniestro.idEtapa && idEtapaFila != 4L

                    val colorPunto = when {
                        estaCompletado -> colorCompletado
                        esActual -> colorActual
                        else -> colorPendiente
                    }

                    val textoEstado = when {
                        estaCompletado -> siniestro.fechaUltimaActualizacion
                        esActual -> "Estado actual"
                        idEtapaFila == siniestro.idEtapa + 1 -> "Próximo hito"
                        else -> "Pendiente"
                    }

                    val textoDescripcion = if (idEtapaFila == 4L && siniestro.idEtapa == 4L) {
                        "El caso ha sido cerrado y resuelto exitosamente"
                    } else {
                        etapa.second
                    }

                    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(colorPunto),
                                contentAlignment = Alignment.Center
                            ) {
                                if (estaCompletado) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else if (esActual) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                                }
                            }
                            if (index < etapas.size - 1) {
                                Box(modifier = Modifier.width(2.dp).weight(1f).background(colorLinea))
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f).padding(bottom = 28.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = etapa.first,
                                    fontSize = 14.sp,
                                    fontWeight = if (esActual || estaCompletado) FontWeight.Bold else FontWeight.Normal,
                                    color = if (esActual || estaCompletado) colorTextPrincipal else colorTextSecundario
                                )
                                Text(
                                    text = textoEstado,
                                    fontSize = 10.sp,
                                    color = colorPunto,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(text = textoDescripcion, fontSize = 12.sp, color = colorTextSecundario)
                        }
                    }
                }
            }

            // 3. Tarjeta Inferior de Adjuntar Evidencia
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.contenedor_primario)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¿Tienes documentación adicional?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorTextPrincipal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Adjunta fotos o archivos para agilizar la evaluación.",
                            fontSize = 11.sp,
                            color = colorTextSecundario,
                            lineHeight = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onAdjuntarEvidencia,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.primario)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adjuntar\nevidencia", fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
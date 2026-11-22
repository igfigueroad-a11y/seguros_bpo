package cl.duocuc.miregistro.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.data.SolicitudRegistro
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.ui.components.ContenedorConMenuLateral

@Composable
fun PanelAnalistaScreen(
    idAnalista: Long,
    onEditarPerfil: (Long) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { UsuarioDbHelper(context) }

    var recargar by remember { mutableIntStateOf(0) }
    val solicitudesPendientes = remember(recargar) { db.listarSolicitudesPendientes() }
    val conSiniestrosCount = solicitudesPendientes.count { it.cantidadSiniestros > 0 }

    ContenedorConMenuLateral(
        idUsuario = idAnalista,
        titulo = "Panel Analista",
        rutaActual = "analista",
        onEditarPerfil = { onEditarPerfil(idAnalista) },
        onCerrarSesion = onCerrarSesion
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaMetricaAnalista(
                        titulo = "Pendientes de revisión",
                        valor = solicitudesPendientes.size,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaMetricaAnalista(
                        titulo = "Con siniestro previo",
                        valor = conSiniestrosCount,
                        color = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Solicitudes de creación de cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (solicitudesPendientes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = "No hay solicitudes de registro pendientes por evaluar.",
                            modifier = Modifier.padding(20.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(solicitudesPendientes, key = { it.idUsuario }) { solicitud ->
                    TarjetaSolicitudAnalista(
                        solicitud = solicitud,
                        onAprobar = {
                            val error = db.aprobarSolicitudRegistro(idAnalista, solicitud.idUsuario)
                            if (error == null) {
                                recargar++
                                Toast.makeText(
                                    context,
                                    "Cuenta de ${solicitud.nombre} aprobada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        },
                        onDesaprobar = {
                            val error = db.desaprobarSolicitudRegistro(idAnalista, solicitud.idUsuario)
                            if (error == null) {
                                recargar++
                                Toast.makeText(
                                    context,
                                    "Solicitud de ${solicitud.nombre} desaprobada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun TarjetaMetricaAnalista(
    titulo: String,
    valor: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor.toString(), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun TarjetaSolicitudAnalista(
    solicitud: SolicitudRegistro,
    onAprobar: () -> Unit,
    onDesaprobar: () -> Unit
) {
    val tieneSiniestros = solicitud.cantidadSiniestros > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Text(
                    text = solicitud.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "RUT: ${solicitud.rut} · ${solicitud.correo}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = if (tieneSiniestros) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
            ) {
                Text(
                    text = if (tieneSiniestros)
                        "Verificado: ${solicitud.cantidadSiniestros} siniestro(s) asociado(s)"
                    else
                        "Cuenta nueva sin siniestros previos",
                    color = if (tieneSiniestros) Color(0xFF15803D) else Color(0xFFB45309),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDesaprobar,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Desaprobar")
                }

                Button(
                    onClick = onAprobar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aprobar")
                }
            }
        }
    }
}
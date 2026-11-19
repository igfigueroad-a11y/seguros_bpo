package cl.duocuc.miregistro.ui.components


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import cl.duocuc.miregistro.data.AvatarStore
import cl.duocuc.miregistro.model.AlertaDocumento
import cl.duocuc.miregistro.ui.theme.Acento
import cl.duocuc.miregistro.ui.theme.AcentoOscuro
import cl.duocuc.miregistro.ui.theme.Dimens
import cl.duocuc.miregistro.ui.theme.Exito
import cl.duocuc.miregistro.ui.theme.Primario
import cl.duocuc.miregistro.ui.theme.PrimarioOscuro
import cl.duocuc.miregistro.ui.theme.Secundario

val coloresAvatar = listOf(Primario, Secundario, Acento, Exito, PrimarioOscuro, AcentoOscuro)

@Composable
fun AvatarUsuario(
    nombre: String,
    colorFondo: Color,
    foto: Bitmap?,
    tamano: Dp,
    modifier: Modifier = Modifier
) {
    val iniciales = nombre.trim().split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        if (foto != null) {
            Image(
                bitmap = foto.asImageBitmap(),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = iniciales,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = (tamano.value * 0.36f).sp
            )
        }
    }
}

@Composable
fun TarjetaResumen(
    numero: Int,
    etiqueta: String,
    color: Color,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (seleccionada) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = numero.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HojaAlertasLiquidador(
    alertas: List<AlertaDocumento>,
    onDismiss: () -> Unit,
    onAdjuntarDocumento: (String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.espacioPantalla)
                .padding(bottom = Dimens.espacioGrande),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text(
                text = "Mensajes de tu liquidador",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (alertas.isEmpty()) {
                Text(
                    text = "No tienes alertas pendientes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            alertas.forEach { alerta ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Acento)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Falta adjuntar: ${alerta.nombreDocumento}",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Siniestro ${alerta.numeroSiniestro}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                        )
                        Button(
                            onClick = {
                                onDismiss()
                                onAdjuntarDocumento(alerta.numeroSiniestro)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Adjuntar documento")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoEditarAvatar(
    idUsuario: Long,
    indiceColor: Int,
    tieneFoto: Boolean,
    avatarStore: AvatarStore,
    onCambioGuardado: () -> Unit,
    onDismiss: () -> Unit
) {
    val contexto = LocalContext.current

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            if (avatarStore.guardarFotoDesdeUri(idUsuario, uri)) onCambioGuardado()
            else Toast.makeText(contexto, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            avatarStore.guardarFoto(idUsuario, bitmap)
            onCambioGuardado()
        }
    }

    val permisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) camara.launch(null)
        else Toast.makeText(contexto, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Foto de perfil") },
        text = {
            Column {
                Text("Color de las iniciales", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    coloresAvatar.forEachIndexed { i, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (i == indiceColor)
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                                .clickable {
                                    avatarStore.guardarColor(idUsuario, i)
                                    onCambioGuardado()
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = {
                    onDismiss()
                    galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Elegir de la galería")
                }
                TextButton(onClick = {
                    onDismiss()
                    val tienePermiso = ContextCompat.checkSelfPermission(
                        contexto, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (tienePermiso) camara.launch(null)
                    else permisoCamara.launch(Manifest.permission.CAMERA)
                }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar una foto")
                }
                if (tieneFoto) {
                    TextButton(onClick = {
                        avatarStore.quitarFoto(idUsuario)
                        onCambioGuardado()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Quitar foto")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Listo") }
        }
    )
}
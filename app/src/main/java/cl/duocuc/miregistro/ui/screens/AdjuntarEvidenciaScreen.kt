package cl.duocuc.miregistro.ui.screens

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import cl.duocuc.miregistro.data.UsuarioDbHelper

data class ArchivoSeleccionado(
    val uri: Uri? = null,
    val bitmap: Bitmap? = null,
    val nombre: String,
    val esImagen: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjuntarEvidenciaScreen(
    numeroSiniestro: String,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val db = remember { UsuarioDbHelper(context) }

    val archivosSeleccionados = remember { mutableStateListOf<ArchivoSeleccionado>() }
    var guardadoExitoso by remember { mutableStateOf(false) }

    val declaraPermisoCamara = remember {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
            info.requestedPermissions?.contains(Manifest.permission.CAMERA) == true
        } catch (_: Exception) {
            false
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val nombre = "foto_camara_${System.currentTimeMillis()}.jpg"
            archivosSeleccionados.add(ArchivoSeleccionado(bitmap = bitmap, nombre = nombre, esImagen = true))
        }
    }

    fun abrirCamaraSegura() {
        try {
            camaraLauncher.launch(null)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No se encontró una app de cámara disponible en el dispositivo",
                Toast.LENGTH_LONG
            ).show()
        } catch (_: SecurityException) {
            Toast.makeText(
                context,
                "Permiso de cámara denegado por el sistema",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamaraSegura()
        } else {
            Toast.makeText(
                context,
                "Debes conceder permiso de cámara para tomar fotografías",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val galeriaMultiLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris.forEach { uri ->
            var nombre = "foto_galeria_${System.currentTimeMillis()}.jpg"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx != -1) nombre = cursor.getString(idx)
                }
            }
            archivosSeleccionados.add(ArchivoSeleccionado(uri = uri, nombre = nombre, esImagen = true))
        }
    }

    val docMultiLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris.forEach { uri ->
            var nombre = "documento_${System.currentTimeMillis()}"
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx != -1) nombre = cursor.getString(idx)
                }
            }
            val esImg = nombre.endsWith(".jpg", true) || nombre.endsWith(".png", true) || nombre.endsWith(".jpeg", true)
            archivosSeleccionados.add(ArchivoSeleccionado(uri = uri, nombre = nombre, esImagen = esImg))
        }
    }

    val colorPrincipal = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Adjuntar Evidencia", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Siniestro: $numeroSiniestro",
                color = colorPrincipal,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Sube fotografías (Cámara/Galería) o documentos (PDF, Word) para agilizar tu caso.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (archivosSeleccionados.isEmpty()) {
                    Text("No hay archivos seleccionados", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        items(archivosSeleccionados) { archivo ->
                            Card(
                                modifier = Modifier.size(120.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (archivo.bitmap != null) {
                                        Image(
                                            bitmap = archivo.bitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else if (archivo.uri != null && archivo.esImagen) {
                                        val bitmapUri = remember(archivo.uri) {
                                            try {
                                                contentResolver.openInputStream(archivo.uri)?.use { stream ->
                                                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                                                }
                                            } catch (_: Exception) {
                                                null
                                            }
                                        }
                                        if (bitmapUri != null) {
                                            Image(
                                                bitmap = bitmapUri,
                                                contentDescription = null,
                                                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(6.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(Icons.Filled.Image, contentDescription = null, tint = colorPrincipal, modifier = Modifier.size(36.dp))
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Description,
                                            contentDescription = null,
                                            tint = colorPrincipal,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = archivo.nombre,
                                        fontSize = 9.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        if (!declaraPermisoCamara) {
                            abrirCamaraSegura()
                        } else {
                            val tienePermiso = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (tienePermiso) {
                                abrirCamaraSegura()
                            } else {
                                permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) { Text("Cámara", fontSize = 11.sp) }

                OutlinedButton(
                    onClick = { galeriaMultiLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) { Text("Galería", fontSize = 11.sp) }

                OutlinedButton(
                    onClick = { docMultiLauncher.launch("*/*") },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) { Text("Archivo", fontSize = 11.sp) }
            }

            Spacer(Modifier.weight(1f))

            if (guardadoExitoso) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D))
                        Spacer(Modifier.width(8.dp))
                        Text("Evidencias enviadas correctamente", color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        archivosSeleccionados.forEach { archivo ->
                            when {
                                archivo.bitmap != null -> db.guardarEvidenciaDesdeBitmap(
                                    numeroSiniestro = numeroSiniestro,
                                    nombre = archivo.nombre,
                                    bitmap = archivo.bitmap
                                )
                                archivo.uri != null -> db.guardarEvidenciaDesdeUri(
                                    numeroSiniestro = numeroSiniestro,
                                    nombre = archivo.nombre,
                                    esImagen = archivo.esImagen,
                                    uriOrigen = archivo.uri
                                )
                            }
                        }
                        guardadoExitoso = true
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = archivosSeleccionados.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                ) {
                    Text("Enviar evidencias (${archivosSeleccionados.size})", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
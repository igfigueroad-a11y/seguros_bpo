package cl.duocuc.miregistro.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.utils.ConvertidorImagen
import cl.duocuc.miregistro.utils.permisos.GestorPermisos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjuntarEvidenciaScreen(
    numeroSiniestro: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    var imagenBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Lanzador para la Cámara
    val launcherCamara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) imagenBitmap = bitmap
    }

    // Lanzador para los Permisos de la Cámara
    val launcherPermisos = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) {
            launcherCamara.launch()
        } else {
            Toast.makeText(contexto, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para la Galería
    val launcherGaleria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Convertir la URI de la galería a Bitmap
            imagenBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contexto.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(contexto.contentResolver, it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adjuntar Evidencia", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colorResource(id = R.color.texto_primario)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = colorResource(id = R.color.texto_primario))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.fondo))
            )
        },
        containerColor = colorResource(id = R.color.fondo)
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Siniestro: $numeroSiniestro",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.primario_oscuro),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Sube una fotografía de los daños para agilizar la evaluación de tu caso.",
                fontSize = 14.sp,
                color = colorResource(id = R.color.texto_secundario),
                modifier = Modifier.fillMaxWidth()
            )

            // Caja de vista previa de la imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(id = R.color.superficie_variante)),
                contentAlignment = Alignment.Center
            ) {
                if (imagenBitmap != null) {
                    Image(
                        bitmap = imagenBitmap!!.asImageBitmap(),
                        contentDescription = "Vista previa",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Sin imagen seleccionada", color = colorResource(id = R.color.texto_sugerencia))
                }
            }


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        GestorPermisos.solicitarPermisoCamara(contexto, launcherPermisos) {
                            launcherCamara.launch()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(id = R.color.primario))
                ) {
                    Text("Tomar Foto")
                }

                OutlinedButton(
                    onClick = { launcherGaleria.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(id = R.color.primario))
                ) {
                    Text("Galería")
                }
            }

            Spacer(modifier = Modifier.weight(1f))


            Button(
                onClick = {
                    if (imagenBitmap != null) {
                        val db = UsuarioDbHelper(contexto)
                        val bytesBlob = ConvertidorImagen.bitmapABlob(imagenBitmap!!)

                        val exito = db.guardarEvidencia(numeroSiniestro, bytesBlob)
                        if (exito) {
                            Toast.makeText(contexto, "Evidencia guardada exitosamente", Toast.LENGTH_SHORT).show()
                            onVolver()
                        } else {
                            Toast.makeText(contexto, "Error al guardar en base de datos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(contexto, "Debes seleccionar una imagen primero", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.primario)),
                enabled = imagenBitmap != null
            ) {
                Text("Enviar evidencia", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
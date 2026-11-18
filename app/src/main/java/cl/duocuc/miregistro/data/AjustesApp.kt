// Archivo NUEVO: app/src/main/java/cl/duocuc/miregistro/data/AjustesApp.kt
package cl.duocuc.miregistro.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import java.io.File
import java.io.FileOutputStream

// Modo oscuro: el estado vive aquí y MainActivity lo observa
object AjustesTema {
    private const val PREFS = "ajustes"
    private const val CLAVE_OSCURO = "modo_oscuro"

    var oscuro by mutableStateOf(false)
        private set

    fun cargar(context: Context) {
        oscuro = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(CLAVE_OSCURO, false)
    }

    fun cambiar(context: Context, valor: Boolean) {
        oscuro = valor
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putBoolean(CLAVE_OSCURO, valor)
        }
    }
}

// Avatar: color de las iniciales y foto opcional (galería o cámara), por usuario
class AvatarStore(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("avatar", Context.MODE_PRIVATE)

    private fun archivo(idUsuario: Long) = File(appContext.filesDir, "avatar_$idUsuario.jpg")

    fun leerColor(idUsuario: Long): Int = prefs.getInt("color_$idUsuario", 0)

    fun guardarColor(idUsuario: Long, indice: Int) {
        prefs.edit { putInt("color_$idUsuario", indice) }
    }

    fun leerFoto(idUsuario: Long): Bitmap? {
        val f = archivo(idUsuario)
        return if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
    }

    fun quitarFoto(idUsuario: Long) {
        archivo(idUsuario).delete()
    }

    // Foto sacada con la cámara (miniatura en Bitmap)
    fun guardarFoto(idUsuario: Long, bitmap: Bitmap) {
        val lado = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - lado) / 2
        val y = (bitmap.height - lado) / 2
        val cuadrado = Bitmap.createBitmap(bitmap, x, y, lado, lado)
        val final = Bitmap.createScaledBitmap(cuadrado, 400, 400, true)
        FileOutputStream(archivo(idUsuario)).use {
            final.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
    }

    // Foto elegida de la galería
    fun guardarFotoDesdeUri(idUsuario: Long, uri: Uri): Boolean {
        return try {
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            appContext.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, limites)
            }
            var muestreo = 1
            while (limites.outWidth / muestreo > 1200) muestreo *= 2
            val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
            val bitmap = appContext.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opciones)
            } ?: return false
            guardarFoto(idUsuario, bitmap)
            true
        } catch (e: Exception) {
            false
        }
    }
}

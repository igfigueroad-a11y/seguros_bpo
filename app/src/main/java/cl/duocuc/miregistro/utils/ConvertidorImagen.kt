package cl.duocuc.miregistro.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ConvertidorImagen {

    // Convierte la foto de la cámara (Bitmap) a un formato que SQLite puede guardar (BLOB)
    fun bitmapABlob(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        // Comprimimos en JPEG al 80% para evitar que la base de datos colapse por peso
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }

    // Convierte lo que sacamos de SQLite (BLOB) de vuelta a una foto visible (Bitmap)
    fun blobABitmap(blob: ByteArray?): Bitmap? {
        if (blob == null || blob.isEmpty()) return null
        return BitmapFactory.decodeByteArray(blob, 0, blob.size)
    }
}
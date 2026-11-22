package cl.duocuc.miregistro.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ConvertidorImagen {

    fun bitmapABlob(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
    }

    fun blobABitmap(blob: ByteArray?): Bitmap? {
        if (blob == null || blob.isEmpty()) return null
        return BitmapFactory.decodeByteArray(blob, 0, blob.size)
    }
}
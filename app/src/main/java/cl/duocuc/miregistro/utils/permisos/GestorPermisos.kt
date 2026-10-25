package cl.duocuc.miregistro.utils.permisos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

object GestorPermisos {

    fun tienePermisoCamara(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun solicitarPermisoCamara(
        context: Context,
        launcher: ManagedActivityResultLauncher<String, Boolean>,
        onPermisoYaConcedido: () -> Unit
    ) {
        if (tienePermisoCamara(context)) {
            onPermisoYaConcedido()
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
}
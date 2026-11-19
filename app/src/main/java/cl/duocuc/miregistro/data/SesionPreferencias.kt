package cl.duocuc.miregistro.data

import android.content.Context
import androidx.core.content.edit

class SesionPreferencias(context: Context) {

    private val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)

    companion object {
        private const val CLAVE_ID_USUARIO = "id_usuario"
        private const val CLAVE_ID_ROL = "id_rol"
        private const val CLAVE_TOKEN = "jwt_token"
        private const val SIN_SESION = -1L
    }

    fun guardar(idUsuario: Long, idRol: Long, token: String = "") {
        prefs.edit {
            putLong(CLAVE_ID_USUARIO, idUsuario)
            putLong(CLAVE_ID_ROL, idRol)
            putString(CLAVE_TOKEN, token)
        }
    }

    fun leer(): Long = prefs.getLong(CLAVE_ID_USUARIO, SIN_SESION)

    fun leerRol(): Long = prefs.getLong(CLAVE_ID_ROL, SIN_SESION)

    fun leerToken(): String = prefs.getString(CLAVE_TOKEN, "") ?: ""

    fun haySesion(): Boolean = leer() != SIN_SESION && leerToken().isNotBlank()

    fun borrar() {
        prefs.edit {
            remove(CLAVE_ID_USUARIO)
            remove(CLAVE_ID_ROL)
            remove(CLAVE_TOKEN)
        }
    }
}

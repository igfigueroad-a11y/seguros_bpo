package cl.duocuc.miregistro.data

import cl.duocuc.miregistro.model.Usuario

object SesionUsuario {
    // Variable global en memoria para almacenar los datos del usuario activo
    var usuarioActual: Usuario? = null

    // Función para borrar los datos cuando se cierre sesión
    fun limpiarSesion() {
        usuarioActual = null
    }
}
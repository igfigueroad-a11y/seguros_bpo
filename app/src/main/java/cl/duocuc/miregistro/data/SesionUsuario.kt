package cl.duocuc.miregistro.data

import cl.duocuc.miregistro.model.Usuario

object SesionUsuario {
    var usuarioActual: Usuario? = null

    fun limpiarSesion() {
        usuarioActual = null
    }
}
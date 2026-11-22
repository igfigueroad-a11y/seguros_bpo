package cl.duocuc.miregistro.model

import androidx.annotation.StringRes

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    @StringRes val errorCorreo: Int? = null,
    @StringRes val errorContrasena: Int? = null,
    @StringRes val errorGeneral: Int? = null,
    val mensajeBloqueo: String? = null,
    val cargando: Boolean = false,
    val usuario: Usuario? = null
)
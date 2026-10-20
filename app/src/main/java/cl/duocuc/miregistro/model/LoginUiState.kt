package cl.duocuc.miregistro.model

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val errorCorreo: Int? = null,
    val errorContrasena: Int? = null,
    val errorGeneral: Int? = null,
    val cargando: Boolean = false,
    val usuario: Usuario? = null
)
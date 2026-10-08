package cl.duocuc.miregistro.model

enum class NivelExperiencia(val etiqueta: String){
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado")
}

//variables o datos de la pantalla
data class FormularioUiState(
    //valores de los campos del formulario
    val nombre: String = "",
    val correo : String = "",
    val contrasena: String = "",
    val nivel: NivelExperiencia = NivelExperiencia.PRINCIPIANTE,
    val recibirNoticias: Boolean = false,
    val aceptaterminos: Boolean = false,
    //estado visual de la contraseña
    val contrasenaVisible: Boolean = false,
    //errores de validacion de los campos
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorTerminos: String? = null,
    //resultado exitoso
    val registroExitoso: Boolean = false

)
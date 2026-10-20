package cl.duocuc.miregistro.model

data class Usuario(
    val id: Long = 0,
    val nombre: String,
    val rut: String,
    val correo: String,
    val rolId: Long,
    val rolNombre: String = ""
)
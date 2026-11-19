package cl.duocuc.miregistro.data.remote

import cl.duocuc.miregistro.model.DetalleSiniestro
import cl.duocuc.miregistro.model.Documento
import cl.duocuc.miregistro.model.SiniestroResumen
import cl.duocuc.miregistro.model.Usuario

// =====================================================================
//  DTOs DE AUTH-SERVICE (Puerto 8081)
// =====================================================================

data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class RegistroRequest(
    val nombre: String,
    val rut: String,
    val correo: String,
    val contrasena: String,
    val respuesta1: String,
    val respuesta2: String,
    val respuesta3: String
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val rut: String,
    val correo: String,
    val rol: String
)

data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)

data class ValidarRespuestasRequest(
    val correo: String,
    val respuesta1: String,
    val respuesta2: String,
    val respuesta3: String
)

data class ResetTokenResponse(
    val resetToken: String
)

data class NuevaContrasenaRequest(
    val resetToken: String,
    val nuevaContrasena: String
)

data class CambiarContrasenaRequest(
    val contrasenaActual: String,
    val nuevaContrasena: String
)

data class MensajeResponse(
    val mensaje: String
)

fun UsuarioResponse.aUsuario(): Usuario {
    val idRol = when (rol.uppercase()) {
        "ASEGURADO" -> 1L
        "LIQUIDADOR" -> 2L
        "ANALISTA" -> 3L
        "ADMIN" -> 4L
        else -> 1L
    }
    return Usuario(
        id = id,
        nombre = nombre,
        rut = rut,
        correo = correo,
        rolId = idRol,
        rolNombre = rol.uppercase()
    )
}

// =====================================================================
//  DTOs DE LIQUIDADOR-SERVICE (Puerto 8084)
// =====================================================================

data class SiniestroResumenResponse(
    val numero: String,
    val nombreAsegurado: String,
    val tipo: String,
    val estado: String,
    val fechaCreacion: String
)

data class DocumentoResponse(
    val id: Long,
    val nombre: String,
    val mensaje: String?,
    val recibido: Boolean,
    val fechaSolicitud: String
)

data class SiniestroDetalleResponse(
    val numero: String,
    val idAsegurado: Long,
    val nombreAsegurado: String,
    val tipo: String,
    val descripcion: String?,
    val estado: String,
    val fechaCreacion: String,
    val documentos: List<DocumentoResponse>
)

data class CrearSiniestroRequest(
    val idAsegurado: Long,
    val tipo: String,
    val descripcion: String?
)

// Estados válidos en backend:
// "EN_REVISION", "DOCUMENTACION_PENDIENTE", "EN_LIQUIDACION", "LIQUIDADO", "RECHAZADO"
data class CambiarEstadoRequest(
    val estado: String
)

data class SolicitarDocumentoRequest(
    val nombre: String,
    val mensaje: String? = null
)

fun SiniestroResumenResponse.aSiniestroResumen() = SiniestroResumen(
    numero = numero,
    cliente = nombreAsegurado,
    tipo = tipo,
    estado = estado,
    fecha = fechaCreacion
)

fun SiniestroDetalleResponse.aDetalleSiniestro() = DetalleSiniestro(
    numero = numero,
    cliente = nombreAsegurado,
    placa = "",
    prioridad = "Media",
    estado = estado,
    comentario = descripcion ?: "",
    documentos = documentos.map { Documento(nombre = it.nombre, recibido = it.recibido) }
)

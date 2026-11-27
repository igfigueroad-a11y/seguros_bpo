package cl.duocuc.miregistro.data.repository

import android.content.Context
import cl.duocuc.miregistro.data.SiniestroAdmin
import cl.duocuc.miregistro.data.SolicitudCambioEstado
import cl.duocuc.miregistro.data.SolicitudRegistro
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.data.remote.AnalistaApi
import cl.duocuc.miregistro.data.remote.RetrofitClient
import cl.duocuc.miregistro.data.remote.dto.AsignarLiquidadorRequestDto
import cl.duocuc.miregistro.data.remote.dto.LiquidadorResumenDto
import cl.duocuc.miregistro.data.remote.dto.SiniestroAnalistaDto
import cl.duocuc.miregistro.model.Usuario

 class AnalistaRepository(
    context: Context,
    private val api: AnalistaApi = RetrofitClient.analistaApi,
    private val usarBackend: Boolean = false,
    private val tokenProvider: () -> String? = { null }
) {
    private val db = UsuarioDbHelper(context)

    suspend fun obtenerSiniestros(estadoFiltro: String = "TODOS"): List<SiniestroAdmin> {
        if (usarBackend) {
            val token = tokenProvider()
            if (!token.isNullOrBlank()) {
                val res = api.listarSiniestros(RetrofitClient.bearer(token), estadoFiltro)
                if (res.isSuccessful) {
                    return res.body().orEmpty().map { it.aSiniestroAdmin() }
                }
            }
        }
        return db.todosLosSiniestrosAdmin()
    }

    suspend fun obtenerLiquidadores(): List<Usuario> {
        if (usarBackend) {
            val token = tokenProvider()
            if (!token.isNullOrBlank()) {
                val res = api.listarLiquidadores(RetrofitClient.bearer(token))
                if (res.isSuccessful) {
                    return res.body().orEmpty().map { it.aUsuarioLiquidador() }
                }
            }
        }
        return db.listarLiquidadores()
    }

    suspend fun obtenerSolicitudesCambioEstado(): List<SolicitudCambioEstado> {
        return db.listarSolicitudesCambioEstado()
    }

    suspend fun obtenerSolicitudesCuenta(): List<SolicitudRegistro> {
        return db.listarSolicitudesPendientes()
    }

    suspend fun asignarLiquidador(numeroSiniestro: String, idLiquidador: Long): Boolean {
        if (usarBackend) {
            val token = tokenProvider()
            if (!token.isNullOrBlank()) {
                val res = api.asignarLiquidador(
                    authorization = RetrofitClient.bearer(token),
                    numero = numeroSiniestro,
                    request = AsignarLiquidadorRequestDto(idLiquidador = idLiquidador)
                )
                return res.isSuccessful
            }
            return false
        }
        return db.asignarLiquidador(numeroSiniestro, idLiquidador)
    }

    suspend fun aprobarCambioEstado(idAnalista: Long, numeroSiniestro: String): String? {
        return db.aprobarCambioEstado(idAnalista, numeroSiniestro)
    }

    suspend fun rechazarCambioEstado(idAnalista: Long, numeroSiniestro: String): String? {
        return db.rechazarCambioEstado(idAnalista, numeroSiniestro)
    }

    suspend fun aprobarSolicitudCuenta(idAnalista: Long, idUsuarioSolicitud: Long): String? {
        return db.aprobarSolicitudRegistro(idAnalista, idUsuarioSolicitud)
    }

    suspend fun desaprobarSolicitudCuenta(idAnalista: Long, idUsuarioSolicitud: Long): String? {
        return db.desaprobarSolicitudRegistro(idAnalista, idUsuarioSolicitud)
    }

    private fun SiniestroAnalistaDto.aSiniestroAdmin(): SiniestroAdmin {
        val estadoVisible = when (estado?.uppercase()) {
            "EN_REVISION" -> "En evaluación"
            "DOCUMENTACION_PENDIENTE" -> "Documentación pendiente"
            "EN_LIQUIDACION" -> "En liquidación"
            "LIQUIDADO" -> "Cerrado"
            "RECHAZADO" -> "Rechazado"
            else -> estado ?: "En evaluación"
        }
        return SiniestroAdmin(
            numero = numero,
            tipo = tipo ?: "Siniestro",
            descripcion = "",
            fecha = fechaCreacion ?: "",
            estado = estadoVisible,
            placa = "",
            prioridad = "Media",
            aseguradoNombre = nombreAsegurado ?: "Asegurado #${idAsegurado ?: 0}",
            liquidadorId = idLiquidador,
            liquidadorNombre = nombreLiquidador
        )
    }

    private fun LiquidadorResumenDto.aUsuarioLiquidador(): Usuario {
        return Usuario(
            id = id,
            nombre = nombre,
            rut = "",
            correo = correo,
            rolId = 2,
            rolNombre = "LIQUIDADOR"
        )
    }
}
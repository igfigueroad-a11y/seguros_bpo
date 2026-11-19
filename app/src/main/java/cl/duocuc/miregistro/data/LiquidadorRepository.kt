package cl.duocuc.miregistro.data

import cl.duocuc.miregistro.data.remote.*
import cl.duocuc.miregistro.model.DetalleSiniestro
import cl.duocuc.miregistro.model.SiniestroResumen

class LiquidadorRepository(
    private val api: LiquidadorApi = RetrofitClient.liquidadorApi
) {
    suspend fun listarSiniestros(token: String, estado: String? = null): List<SiniestroResumen> =
        llamarApi { api.listar(RetrofitClient.bearer(token), estado) }
            .map { it.aSiniestroResumen() }

    suspend fun detalleSiniestro(token: String, numero: String): DetalleSiniestro =
        llamarApi { api.detalle(RetrofitClient.bearer(token), numero) }
            .aDetalleSiniestro()

    suspend fun crearSiniestro(
        token: String,
        idAsegurado: Long,
        tipo: String,
        descripcion: String?
    ): DetalleSiniestro =
        llamarApi {
            api.crear(
                RetrofitClient.bearer(token),
                CrearSiniestroRequest(idAsegurado, tipo, descripcion)
            )
        }.aDetalleSiniestro()

    suspend fun cambiarEstado(token: String, numero: String, estado: String): DetalleSiniestro =
        llamarApi {
            api.cambiarEstado(
                RetrofitClient.bearer(token),
                numero,
                CambiarEstadoRequest(estado)
            )
        }.aDetalleSiniestro()

    suspend fun solicitarDocumento(
        token: String,
        numero: String,
        nombre: String,
        mensaje: String? = null
    ) {
        llamarApi {
            api.solicitarDocumento(
                RetrofitClient.bearer(token),
                numero,
                SolicitarDocumentoRequest(nombre, mensaje)
            )
        }
    }
}

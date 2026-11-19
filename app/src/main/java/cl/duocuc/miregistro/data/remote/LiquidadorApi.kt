package cl.duocuc.miregistro.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface LiquidadorApi {

    @GET("liquidador/siniestros")
    suspend fun listar(
        @Header("Authorization") authorization: String,
        @Query("estado") estado: String? = null
    ): List<SiniestroResumenResponse>

    @GET("liquidador/siniestros/{numero}")
    suspend fun detalle(
        @Header("Authorization") authorization: String,
        @Path("numero") numero: String
    ): SiniestroDetalleResponse

    @POST("liquidador/siniestros")
    suspend fun crear(
        @Header("Authorization") authorization: String,
        @Body datos: CrearSiniestroRequest
    ): SiniestroDetalleResponse

    @PUT("liquidador/siniestros/{numero}/estado")
    suspend fun cambiarEstado(
        @Header("Authorization") authorization: String,
        @Path("numero") numero: String,
        @Body datos: CambiarEstadoRequest
    ): SiniestroDetalleResponse

    @POST("liquidador/siniestros/{numero}/documentos")
    suspend fun solicitarDocumento(
        @Header("Authorization") authorization: String,
        @Path("numero") numero: String,
        @Body datos: SolicitarDocumentoRequest
    ): DocumentoResponse

    @PUT("liquidador/siniestros/{numero}/documentos/{idDocumento}/recibido")
    suspend fun marcarRecibido(
        @Header("Authorization") authorization: String,
        @Path("numero") numero: String,
        @Path("idDocumento") idDocumento: Long
    ): SiniestroDetalleResponse

    @DELETE("liquidador/siniestros/{numero}/documentos/{idDocumento}")
    suspend fun eliminarDocumento(
        @Header("Authorization") authorization: String,
        @Path("numero") numero: String,
        @Path("idDocumento") idDocumento: Long
    ): SiniestroDetalleResponse
}

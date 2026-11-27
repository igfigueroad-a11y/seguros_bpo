package cl.duocuc.miregistro.data.remote

import cl.duocuc.miregistro.data.remote.dto.AsignarLiquidadorRequestDto
import cl.duocuc.miregistro.data.remote.dto.LiquidadorResumenDto
import cl.duocuc.miregistro.data.remote.dto.PanelAnalistaResponseDto
import cl.duocuc.miregistro.data.remote.dto.SiniestroAnalistaDto
import cl.duocuc.miregistro.data.remote.dto.SiniestroDetalleRemotoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AnalistaApi {

    @GET("analista/panel")
    suspend fun obtenerPanel(
        @Header("Authorization") authorization: String,
        @Query("estado") estado: String = "TODOS"
    ): Response<PanelAnalistaResponseDto>

    @GET("analista/siniestros")
    suspend fun listarSiniestros(
        @Header("Authorization") authorization: String,
        @Query("estado") estado: String = "TODOS"
    ): Response<List<SiniestroAnalistaDto>>

    @GET("analista/siniestros/{numero}")
    suspend fun obtenerDetalleSiniestro(
        @Header("Authorization") authorization: String,
        @Path("numero") numero: String
    ): Response<SiniestroDetalleRemotoDto>

    @GET("analista/liquidadores")
    suspend fun listarLiquidadores(
        @Header("Authorization") authorization: String
    ): Response<List<LiquidadorResumenDto>>

    @PUT("analista/siniestros/{numero}/asignar")
    suspend fun asignarLiquidador(
        @Header("Authorization") authorization: String,
        @Path("numero") numero: String,
        @Body request: AsignarLiquidadorRequestDto
    ): Response<SiniestroAnalistaDto>
}
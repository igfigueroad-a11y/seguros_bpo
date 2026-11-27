package cl.duocuc.miregistro.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AsignarLiquidadorRequestDto(
    @SerializedName("idLiquidador") val idLiquidador: Long
)

data class SiniestroAnalistaDto(
    @SerializedName("numero") val numero: String,
    @SerializedName("idAsegurado") val idAsegurado: Long?,
    @SerializedName("nombreAsegurado") val nombreAsegurado: String?,
    @SerializedName("idLiquidador") val idLiquidador: Long?,
    @SerializedName("nombreLiquidador") val nombreLiquidador: String?,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?,
    @SerializedName("documentosPendientes") val documentosPendientes: Int?
)

data class LiquidadorResumenDto(
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("siniestrosActivos") val siniestrosActivos: Long,
    @SerializedName("siniestrosTotales") val siniestrosTotales: Long
)

data class MetricasPanelDto(
    @SerializedName("total") val total: Long,
    @SerializedName("activos") val activos: Long,
    @SerializedName("cerrados") val cerrados: Long,
    @SerializedName("porEstado") val porEstado: Map<String, Long>?
)

data class PanelAnalistaResponseDto(
    @SerializedName("metricas") val metricas: MetricasPanelDto?,
    @SerializedName("siniestros") val siniestros: List<SiniestroAnalistaDto>?,
    @SerializedName("liquidadores") val liquidadores: List<LiquidadorResumenDto>?
)

data class DocumentoRemotoDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("recibido") val recibido: Boolean?,
    @SerializedName("fechaSolicitud") val fechaSolicitud: String?
)

data class SiniestroDetalleRemotoDto(
    @SerializedName("numero") val numero: String,
    @SerializedName("idAsegurado") val idAsegurado: Long?,
    @SerializedName("nombreAsegurado") val nombreAsegurado: String?,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("estado") val estado: String?,
    @SerializedName("fechaCreacion") val fechaCreacion: String?,
    @SerializedName("documentos") val documentos: List<DocumentoRemotoDto>?
)
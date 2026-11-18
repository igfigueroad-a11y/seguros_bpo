
package cl.duocuc.miregistro.model

data class ResumenAsegurado(
    val total: Int,
    val enProceso: Int,
    val completados: Int
)

data class SiniestroAsegurado(
    val numero: String,
    val tipo: String,
    val descripcion: String,
    val estado: String,
    val fecha: String
)


data class AlertaDocumento(
    val numeroSiniestro: String,
    val nombreDocumento: String
)

enum class FiltroSiniestro { TODOS, EN_PROCESO, COMPLETADOS }

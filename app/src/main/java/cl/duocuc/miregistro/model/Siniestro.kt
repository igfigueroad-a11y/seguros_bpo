package cl.duocuc.miregistro.model

data class Siniestro(
    val idSiniestro: Long = 0,
    val numero: String,
    val aseguradoNombre: String,
    val companiaNombre: String,
    val tipoSiniestro: String,
    val fechaOcurrencia: String,
    val idEtapa: Long,
    val etapaNombre: String,
    val fechaUltimaActualizacion: String
)
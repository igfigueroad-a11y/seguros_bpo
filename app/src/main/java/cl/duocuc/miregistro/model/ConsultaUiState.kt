package cl.duocuc.miregistro.model


data class ConsultaUiState(
    val numero: String = "",
    val error: String? = null,
    val siniestroEncontrado: String? = null
)
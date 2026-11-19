package cl.duocuc.miregistro.model


val ESTADOS = listOf("Recibido", "En evaluación", "En liquidación", "Cerrado")

data class SiniestroResumen(
    val numero: String,
    val cliente: String,
    val tipo: String,
    val estado: String,
    val fecha: String
)

data class ResumenPanel(
    val asignados: Int = 0,
    val enEvaluacion: Int = 0,
    val pendientesDocs: Int = 0,
    val cerradosMes: Int = 0
)

data class Documento(
    val nombre: String,
    val recibido: Boolean
)

// NUEVA CLASE PARA SOPORTAR FOTOS Y DOCUMENTOS CON SUS NOMBRES
data class EvidenciaArchivo(
    val nombre: String,
    val esImagen: Boolean,
    val bytes: ByteArray
)

data class DetalleSiniestro(
    val numero: String,
    val cliente: String,
    val placa: String,
    val prioridad: String,
    val estado: String,
    val comentario: String,
    val documentos: List<Documento>,
    val evidencias: List<EvidenciaArchivo> = emptyList(), // <-- ACTUALIZADO AQUÍ
)

data class PanelUiState(
    val cargando: Boolean = true,
    val resumen: ResumenPanel = ResumenPanel(),
    val siniestros: List<SiniestroResumen> = emptyList(),
    val seleccionado: DetalleSiniestro? = null,
    val mensaje: String? = null
)
package cl.duocuc.miregistro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.SiniestroAdmin
import cl.duocuc.miregistro.data.SolicitudCambioEstado
import cl.duocuc.miregistro.data.SolicitudRegistro
import cl.duocuc.miregistro.data.repository.AnalistaRepository
import cl.duocuc.miregistro.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SeccionAnalista(val ruta: String, val titulo: String) {
    INICIO("analista_inicio", "Panel Analista"),
    SINIESTROS("analista_siniestros", "Gestión de siniestros"),
    ESTADOS("analista_estados", "Cambios de estado"),
    CUENTAS("analista_cuentas", "Solicitudes de cuenta")
}

enum class FiltroSiniestroAnalista(val etiqueta: String) {
    TODOS("Todos"),
    SIN_ASIGNAR("Sin asignar"),
    ASIGNADOS("Asignados")
}

data class PanelAnalistaUiState(
    val cargando: Boolean = true,
    val seccion: SeccionAnalista = SeccionAnalista.INICIO,
    val filtroSiniestro: FiltroSiniestroAnalista = FiltroSiniestroAnalista.TODOS,
    val todosSiniestros: List<SiniestroAdmin> = emptyList(),
    val liquidadores: List<Usuario> = emptyList(),
    val solicitudesPendientes: List<SolicitudRegistro> = emptyList(),
    val solicitudesEstado: List<SolicitudCambioEstado> = emptyList(),
    val eventoToast: String? = null
) {
    val sinAsignarCount: Int
        get() = todosSiniestros.count { it.liquidadorId == null }

    val activosCount: Int
        get() = todosSiniestros.count {
            it.liquidadorId != null && !it.estado.equals("Cerrado", ignoreCase = true)
        }

    val totalAvisos: Int
        get() = solicitudesPendientes.size + solicitudesEstado.size

    val siniestrosFiltrados: List<SiniestroAdmin>
        get() = when (filtroSiniestro) {
            FiltroSiniestroAnalista.TODOS -> todosSiniestros
            FiltroSiniestroAnalista.SIN_ASIGNAR -> todosSiniestros.filter { it.liquidadorId == null }
            FiltroSiniestroAnalista.ASIGNADOS -> todosSiniestros.filter { it.liquidadorId != null }
        }
}

class PanelAnalistaViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = AnalistaRepository(app)
    private var idAnalista = 0L

    private val _ui = MutableStateFlow(PanelAnalistaUiState())
    val ui: StateFlow<PanelAnalistaUiState> = _ui.asStateFlow()

    fun cargar(id: Long) {
        idAnalista = id
        refrescar()
    }

    private fun refrescar(toastMensaje: String? = null) {
        viewModelScope.launch {
            val siniestros: List<SiniestroAdmin> = withContext(Dispatchers.IO) {
                repository.obtenerSiniestros()
            }
            val liquidadores: List<Usuario> = withContext(Dispatchers.IO) {
                repository.obtenerLiquidadores()
            }
            val pendientes: List<SolicitudRegistro> = withContext(Dispatchers.IO) {
                repository.obtenerSolicitudesCuenta()
            }
            val estados: List<SolicitudCambioEstado> = withContext(Dispatchers.IO) {
                repository.obtenerSolicitudesCambioEstado()
            }

            _ui.update { actual ->
                actual.copy(
                    cargando = false,
                    todosSiniestros = siniestros,
                    liquidadores = liquidadores,
                    solicitudesPendientes = pendientes,
                    solicitudesEstado = estados,
                    eventoToast = toastMensaje ?: actual.eventoToast
                )
            }
        }
    }

    fun cambiarSeccion(nuevaSeccion: SeccionAnalista) {
        _ui.update { it.copy(seccion = nuevaSeccion) }
    }

    fun cambiarFiltro(nuevoFiltro: FiltroSiniestroAnalista) {
        _ui.update { it.copy(filtroSiniestro = nuevoFiltro) }
    }

    fun toastConsumido() {
        _ui.update { it.copy(eventoToast = null) }
    }

    fun asignarLiquidador(numeroSiniestro: String, idLiquidador: Long) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                repository.asignarLiquidador(numeroSiniestro, idLiquidador)
            }
            if (ok) {
                refrescar("Siniestro $numeroSiniestro asignado")
            } else {
                _ui.update { it.copy(eventoToast = "No se pudo asignar el liquidador") }
            }
        }
    }

    fun aprobarCambioEstado(numeroSiniestro: String, estadoSolicitado: String) {
        viewModelScope.launch {
            val error = withContext(Dispatchers.IO) {
                repository.aprobarCambioEstado(idAnalista, numeroSiniestro)
            }
            if (error == null) {
                refrescar("Cambio aprobado: $numeroSiniestro pasó a '$estadoSolicitado'")
            } else {
                _ui.update { it.copy(eventoToast = error) }
            }
        }
    }

    fun rechazarCambioEstado(numeroSiniestro: String) {
        viewModelScope.launch {
            val error = withContext(Dispatchers.IO) {
                repository.rechazarCambioEstado(idAnalista, numeroSiniestro)
            }
            if (error == null) {
                refrescar("Solicitud rechazada para $numeroSiniestro")
            } else {
                _ui.update { it.copy(eventoToast = error) }
            }
        }
    }

    fun aprobarSolicitudCuenta(idUsuarioSolicitud: Long, nombreUsuario: String) {
        viewModelScope.launch {
            val error = withContext(Dispatchers.IO) {
                repository.aprobarSolicitudCuenta(idAnalista, idUsuarioSolicitud)
            }
            if (error == null) {
                refrescar("Cuenta de $nombreUsuario aprobada")
            } else {
                _ui.update { it.copy(eventoToast = error) }
            }
        }
    }

    fun desaprobarSolicitudCuenta(idUsuarioSolicitud: Long, nombreUsuario: String) {
        viewModelScope.launch {
            val error = withContext(Dispatchers.IO) {
                repository.desaprobarSolicitudCuenta(idAnalista, idUsuarioSolicitud)
            }
            if (error == null) {
                refrescar("Solicitud de $nombreUsuario desaprobada")
            } else {
                _ui.update { it.copy(eventoToast = error) }
            }
        }
    }
}
package cl.duocuc.miregistro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.LiquidadorRepository
import cl.duocuc.miregistro.model.PanelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private typealias MapaPendientes = Map<String, String>

class PanelLiquidadorViewModel(
    private val repository: LiquidadorRepository = LiquidadorRepository()
) : ViewModel() {

    private var tokenActual = ""
    private val _ui = MutableStateFlow(PanelUiState())
    val ui: StateFlow<PanelUiState> = _ui.asStateFlow()

    private val _solicitudesPendientes = MutableStateFlow<MapaPendientes>(emptyMap())
    val solicitudesPendientes: StateFlow<MapaPendientes> = _solicitudesPendientes.asStateFlow()

    fun cargar(token: String) {
        tokenActual = token
        refrescar(null, null)
    }

    private fun refrescar(numeroSel: String?, mensaje: String?) {
        viewModelScope.launch {
            runCatching {
                val lista = repository.listarSiniestros(tokenActual)
                val detalle = numeroSel?.let { repository.detalleSiniestro(tokenActual, it) }
                lista to detalle
            }.onSuccess { (lista, detalle) ->
                _ui.value = _ui.value.copy(
                    cargando = false,
                    siniestros = lista,
                    seleccionado = detalle,
                    mensaje = mensaje
                )
            }.onFailure { error ->
                _ui.value = _ui.value.copy(
                    cargando = false,
                    mensaje = error.message ?: "Error al cargar siniestros"
                )
            }
        }
    }

    fun seleccionar(numero: String) {
        refrescar(numero, null)
    }

    fun guardarEstado(estado: String) {
        val numero = _ui.value.seleccionado?.numero ?: return
        viewModelScope.launch {
            runCatching {
                repository.cambiarEstado(tokenActual, numero, estado)
            }.onSuccess {
                refrescar(numero, "Estado actualizado a '$estado'")
            }.onFailure { error ->
                refrescar(numero, error.message ?: "No se pudo actualizar el estado")
            }
        }
    }

    fun solicitarDocumento(nombre: String, mensaje: String? = null) {
        val numero = _ui.value.seleccionado?.numero ?: return
        viewModelScope.launch {
            runCatching {
                repository.solicitarDocumento(tokenActual, numero, nombre, mensaje)
            }.onSuccess {
                refrescar(numero, "Documento '$nombre' solicitado al asegurado")
            }.onFailure { error ->
                refrescar(numero, error.message ?: "No se pudo solicitar el documento")
            }
        }
    }
}
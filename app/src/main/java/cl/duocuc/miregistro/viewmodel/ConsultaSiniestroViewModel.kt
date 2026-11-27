package cl.duocuc.miregistro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.LiquidadorRepository
import cl.duocuc.miregistro.model.ConsultaUiState
import cl.duocuc.miregistro.utils.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConsultaSiniestroViewModel(
    private val repository: LiquidadorRepository = LiquidadorRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(ConsultaUiState())
    val ui: StateFlow<ConsultaUiState> = _ui.asStateFlow()

    fun onNumeroChange(texto: String) {
        _ui.update { it.copy(numero = texto, error = null) }
    }

    fun buscar(token: String = "") {
        val numero = _ui.value.numero.trim().uppercase()
        val error = Validaciones.validarNumeroSiniestro(numero)
        if (error != null) {
            _ui.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            runCatching {
                repository.detalleSiniestro(token, numero)
            }.onSuccess {
                _ui.update { it.copy(siniestroEncontrado = numero) }
            }.onFailure {
                _ui.update { it.copy(error = "No se encontró el siniestro $numero") }
            }
        }
    }

    fun navegacionConsumida() {
        _ui.update { it.copy(siniestroEncontrado = null) }
    }
}
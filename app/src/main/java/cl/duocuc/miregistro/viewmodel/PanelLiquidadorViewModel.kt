package cl.duocuc.miregistro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.PanelUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PanelLiquidadorViewModel(app: Application) : AndroidViewModel(app) {

    private val dbHelper = UsuarioDbHelper(app)
    private var idLiquidador = 0L
    private val _ui = MutableStateFlow(PanelUiState())
    val ui: StateFlow<PanelUiState> = _ui.asStateFlow()

    fun cargar(id: Long) {
        idLiquidador = id
        refrescar(null, null)
    }

    private fun refrescar(numeroSel: String?, mensaje: String?) {
        viewModelScope.launch {
            val estado = withContext(Dispatchers.IO) {
                PanelUiState(
                    cargando = false,
                    resumen = dbHelper.resumenLiquidador(idLiquidador),
                    siniestros = dbHelper.siniestrosDeLiquidador(idLiquidador),
                    seleccionado = numeroSel?.let { dbHelper.detalleSiniestro(it) },
                    mensaje = mensaje
                )
            }
            _ui.value = estado
        }
    }

    fun seleccionar(numero: String) {
        refrescar(numero, null)
    }

    fun guardar(estado: String, comentario: String) {
        val numero = _ui.value.seleccionado?.numero ?: return
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                dbHelper.actualizarSiniestro(numero, estado, comentario)
            }
            refrescar(numero, if (ok) "Cambios guardados" else "No se pudo guardar")
        }
    }

    fun solicitarDocumento(nombre: String) {
        val numero = _ui.value.seleccionado?.numero ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) { dbHelper.solicitarDocumento(numero, nombre) }
            refrescar(numero, "Documento solicitado")
        }
    }
}
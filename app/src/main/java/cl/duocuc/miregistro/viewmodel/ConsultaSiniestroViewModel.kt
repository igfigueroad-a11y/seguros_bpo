package cl.duocuc.miregistro.viewmodel



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.ConsultaUiState
import cl.duocuc.miregistro.utils.Validaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConsultaSiniestroViewModel(app: Application) : AndroidViewModel(app) {

    private val dbHelper = UsuarioDbHelper(app)

    private val _ui = MutableStateFlow(ConsultaUiState())
    val ui: StateFlow<ConsultaUiState> = _ui.asStateFlow()

    fun onNumeroChange(texto: String) {
        _ui.update { it.copy(numero = texto, error = null) }
    }

    fun buscar() {
        val numero = _ui.value.numero.trim().uppercase()
        // La regla ahora vive en Validaciones (así se puede probar sola)
        val error = Validaciones.validarNumeroSiniestro(numero)
        if (error != null) {
            _ui.update { it.copy(error = error) }
            return
        }
        viewModelScope.launch {
            val existe = withContext(Dispatchers.IO) { dbHelper.existeSiniestro(numero) }
            _ui.update {
                if (existe) it.copy(siniestroEncontrado = numero)
                else it.copy(error = "No se encontró el siniestro $numero")
            }
        }
    }

    fun navegacionConsumida() {
        _ui.update { it.copy(siniestroEncontrado = null) }
    }
}
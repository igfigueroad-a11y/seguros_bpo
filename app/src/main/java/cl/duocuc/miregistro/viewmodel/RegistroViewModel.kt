package cl.duocuc.miregistro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.UsuarioDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val db = UsuarioDbHelper(application)

    // Variables de estado para la UI
    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    fun registrar(nombre: String, rut: String, correo: String, contrasena: String) {
        // Validaciones básicas
        if (nombre.isBlank() || rut.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Todos los campos son obligatorios"
            return
        }

        viewModelScope.launch {
            val exito = withContext(Dispatchers.IO) {
                db.registrarUsuario(nombre, rut, correo, contrasena)
            }

            if (exito) {
                _registroExitoso.value = true
                _mensajeError.value = null
            } else {
                _mensajeError.value = "Error al registrar. El correo o RUT ya existen."
            }
        }
    }

    fun limpiarEstado() {
        _registroExitoso.value = false
        _mensajeError.value = null
    }
}
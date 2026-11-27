package cl.duocuc.miregistro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private fun esContrasenaValida(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { !it.isLetterOrDigit() }) return false
        return true
    }

    fun registrar(
        nombre: String,
        rut: String,
        correo: String,
        contrasena: String,
        preg1: String,
        resp1: String,
        preg2: String,
        resp2: String,
        preg3: String,
        resp3: String
    ) {
        if (nombre.isBlank() || rut.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Completa todos los datos personales"
            return
        }
        if (preg1.isBlank() || preg2.isBlank() || preg3.isBlank()) {
            _mensajeError.value = "Debes seleccionar las 3 preguntas de seguridad"
            return
        }
        if (resp1.isBlank() || resp2.isBlank() || resp3.isBlank()) {
            _mensajeError.value = "Debes responder las 3 preguntas de seguridad"
            return
        }
        if (!esContrasenaValida(contrasena)) {
            _mensajeError.value = "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 número y 1 carácter especial"
            return
        }

        viewModelScope.launch {
            runCatching {
                repository.registrar(nombre, rut, correo, contrasena, resp1, resp2, resp3)
            }.onSuccess {
                _registroExitoso.value = true
                _mensajeError.value = null
            }.onFailure { error ->
                _mensajeError.value = error.message ?: "No se pudo completar el registro"
            }
        }
    }

    fun limpiarEstado() {
        _registroExitoso.value = false
        _mensajeError.value = null
    }
}
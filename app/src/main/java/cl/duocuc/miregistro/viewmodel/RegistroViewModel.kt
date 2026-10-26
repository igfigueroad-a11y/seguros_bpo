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

    private val _registroExitoso = MutableStateFlow(false)
    val registroExitoso: StateFlow<Boolean> = _registroExitoso.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // 1. Función para validar la complejidad de la contraseña
    private fun esContrasenaValida(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { !it.isLetterOrDigit() }) return false
        return true
    }

    fun registrar(nombre: String, rut: String, correo: String, contrasena: String, resp1: String, resp2: String, resp3: String) {

        if (nombre.isBlank() || rut.isBlank() || correo.isBlank() || contrasena.isBlank() ||
            resp1.isBlank() || resp2.isBlank() || resp3.isBlank()) {
            _mensajeError.value = "Todos los campos y preguntas son obligatorios"
            return
        }

        // 2. Aplicamos la validación antes de guardar
        if (!esContrasenaValida(contrasena)) {
            _mensajeError.value = "La contraseña debe tener al menos 8 caracteres, 1 mayúscula, 1 número y 1 carácter especial"
            return
        }

        viewModelScope.launch {
            val exito = withContext(Dispatchers.IO) {
                db.registrarUsuario(nombre, rut, correo, contrasena, resp1, resp2, resp3)
            }

            if (exito) {
                _registroExitoso.value = true
                _mensajeError.value = null
            } else {
                _mensajeError.value = "El correo o RUT ingresado ya está registrado"
            }
        }
    }

    fun limpiarEstado() {
        _registroExitoso.value = false
        _mensajeError.value = null
    }
}
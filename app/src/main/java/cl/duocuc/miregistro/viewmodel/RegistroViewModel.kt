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
            val exito = withContext(Dispatchers.IO) {
                db.registrarUsuario(
                    nombre = nombre,
                    rut = rut,
                    correo = correo,
                    claveTextoPlano = contrasena,
                    preg1 = preg1,
                    resp1 = resp1,
                    preg2 = preg2,
                    resp2 = resp2,
                    preg3 = preg3,
                    resp3 = resp3
                )
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
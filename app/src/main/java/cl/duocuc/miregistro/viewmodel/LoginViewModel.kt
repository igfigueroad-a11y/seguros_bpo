package cl.duocuc.miregistro.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = UsuarioDbHelper(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(nuevoCorreo: String) {
        _uiState.update {
            it.copy(
                correo = nuevoCorreo,
                errorCorreo = null,
                errorGeneral = null,
                mensajeBloqueo = null
            )
        }
    }

    fun onContrasenaCambia(nuevaContrasena: String) {
        _uiState.update {
            it.copy(
                contrasena = nuevaContrasena,
                errorContrasena = null,
                errorGeneral = null,
                mensajeBloqueo = null
            )
        }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onIngresar() {
        val estadoActual = _uiState.value
        val correoLimpio = estadoActual.correo.trim()
        val clave = estadoActual.contrasena

        if (correoLimpio.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            _uiState.update { it.copy(errorCorreo = R.string.error_correo_invalido) }
            return
        }

        if (clave.isBlank()) {
            _uiState.update { it.copy(errorContrasena = R.string.error_contrasena_vacia) }
            return
        }

        _uiState.update { it.copy(cargando = true, errorGeneral = null, mensajeBloqueo = null) }

        val bloqueo = dbHelper.verificarEstadoRegistro(correoLimpio)
        if (bloqueo != null) {
            _uiState.update {
                it.copy(
                    cargando = false,
                    mensajeBloqueo = bloqueo
                )
            }
            return
        }

        val usuarioAutenticado = dbHelper.iniciarSesion(correoLimpio, clave)
        if (usuarioAutenticado != null) {
            _uiState.update {
                it.copy(
                    cargando = false,
                    usuario = usuarioAutenticado
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    cargando = false,
                    errorGeneral = R.string.error_credenciales
                )
            }
        }
    }

    fun onLoginConsumido() {
        _uiState.update { it.copy(usuario = null) }
    }
}
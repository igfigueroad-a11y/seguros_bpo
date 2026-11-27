package cl.duocuc.miregistro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.AuthRepository
import cl.duocuc.miregistro.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val onGuardarSesion: (id: Long, rolId: Long, token: String) -> Unit = { _, _, _ -> }
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val regexCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(correo = valor, errorCorreo = null, errorGeneral = null, mensajeBloqueo = null)
        }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null, mensajeBloqueo = null)
        }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onIngresar() {
        val estado = _uiState.value

        val errorCorreo = when {
            estado.correo.isBlank() -> R.string.login_error_correo_vacio
            !regexCorreo.matches(estado.correo.trim()) -> R.string.login_error_correo_invalido
            else -> null
        }
        val errorContrasena =
            if (estado.contrasena.isEmpty()) R.string.login_error_contrasena_vacia else null

        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena)
            }
            return
        }

        _uiState.update { it.copy(cargando = true, errorGeneral = null, mensajeBloqueo = null) }

        viewModelScope.launch {
            runCatching {
                repository.login(estado.correo, estado.contrasena)
            }.onSuccess { (token, usuario) ->
                onGuardarSesion(usuario.id, usuario.rolId, token)
                _uiState.update { it.copy(cargando = false, usuario = usuario) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeBloqueo = error.message ?: "Error al iniciar sesión"
                    )
                }
            }
        }
    }

    fun onLoginConsumido() {
        _uiState.update { it.copy(usuario = null) }
    }
}
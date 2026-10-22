package cl.duocuc.miregistro.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.data.SesionPreferencias
import cl.duocuc.miregistro.data.UsuarioDbHelper
import cl.duocuc.miregistro.model.LoginUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = UsuarioDbHelper(application)
    private val sesion = SesionPreferencias(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onIngresar() {
        val estado = _uiState.value

        val errorCorreo = when {
            estado.correo.isBlank() -> R.string.login_error_correo_vacio
            !Patterns.EMAIL_ADDRESS.matcher(estado.correo.trim()).matches() ->
                R.string.login_error_correo_invalido
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

        _uiState.update { it.copy(cargando = true, errorGeneral = null) }

        viewModelScope.launch {
            val resultado = runCatching {
                withContext(Dispatchers.IO) {
                    db.validarLogin(estado.correo, estado.contrasena)
                }
            }
            resultado
                .onSuccess { usuario ->
                    if (usuario != null) {
                        sesion.guardar(usuario.id, usuario.rolId)
                        _uiState.update { it.copy(cargando = false, usuario = usuario) }
                    } else {
                        _uiState.update {
                            it.copy(cargando = false, errorGeneral = R.string.login_error_credenciales)
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(cargando = false, errorGeneral = R.string.login_error_generico)
                    }
                }
        }
    }

    // La pantalla lo llama después de navegar, para no repetir el ingreso
    fun onLoginConsumido() {
        _uiState.update { it.copy(usuario = null) }
    }
}
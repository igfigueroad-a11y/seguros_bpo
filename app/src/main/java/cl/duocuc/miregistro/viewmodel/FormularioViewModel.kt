package cl.duocuc.miregistro.viewmodel

import androidx.lifecycle.ViewModel
import cl.duocuc.miregistro.model.FormularioUiState
import cl.duocuc.miregistro.model.NivelExperiencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormularioViewModel : ViewModel(){
    //privada y mutable
    private val _uiState = MutableStateFlow(FormularioUiState())
    //publica y solo de lectura
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()
    //eventos de los campos de la pantalla
    fun onNombreCambia(valor: String){
        _uiState.update { it.copy(nombre = valor, errorNombre = null, registroExitoso = false) }
    }
    fun onCorreoCambia(valor: String){
        _uiState.update { it.copy(correo = valor, errorCorreo = null, registroExitoso = false) }
    }
    fun onContrasenaCambia(valor: String){
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null, registroExitoso = false) }
    }
    fun onAlternarVisibilidad(){
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }
    //eventos de los controles de seleccion
    fun onNivelSeleccionado(nivel: NivelExperiencia){
        _uiState.update { it.copy(nivel = nivel) }
    }
    fun onNoticiasCambia(valor: Boolean){
        _uiState.update { it.copy(recibirNoticias = valor) }
    }
    fun onAceptaTerminos(valor: Boolean){
        _uiState.update { it.copy(aceptaterminos = valor, errorTerminos = null) }
    }
    //eventos de botones
    //limpiar
    fun onLimpiar(){
        _uiState.value = FormularioUiState()
    }
    //registrarse
    fun onRegistrar(){
        val estado = _uiState.value
        //validar los campos
        val errorNombre = if(estado.nombre.isBlank()) "El nombre es obligatorio" else null
        val errorCorreo = if(!estado.correo.contains("@") || !estado.correo.contains("."))
            "Ingrese un correo válido" else null
        val errorContrasena = if(estado.contrasena.length < 6) "Mínimo 6 caracteres" else null
        val errorTerminos = if(!estado.aceptaterminos) "Debe aceptar términos" else null
        
    }


}
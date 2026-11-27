package cl.duocuc.miregistro.utils
import cl.duocuc.miregistro.R
import cl.duocuc.miregistro.model.DetalleSiniestro
import cl.duocuc.miregistro.model.Documento
import cl.duocuc.miregistro.model.FiltroSiniestro
import cl.duocuc.miregistro.model.LoginUiState
import cl.duocuc.miregistro.model.PanelUiState
import cl.duocuc.miregistro.model.SiniestroResumen
import cl.duocuc.miregistro.model.Usuario
import cl.duocuc.miregistro.viewmodel.PanelLiquidadorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test



class ValidacionesTest {


    @Test
    fun validarNumeroSiniestro_conNumeroVacio_devuelveError() {

        val numero = ""

        val error = Validaciones.validarNumeroSiniestro(numero)


        assertEquals("Ingresa el número de siniestro", error)
    }

    @Test
    fun validarNumeroSiniestro_conFormatoValido (){
        val numero = "SIN-26-123456"

        val error = Validaciones.validarNumeroSiniestro(numero)

        assertNull(error)
    }

    @Test
    fun loginUiState_alActualizarCorreo_guardaCorreoYLimpiaError(){
        val estadoInicial = LoginUiState(errorCorreo = 123)

        val estadoActualizado = estadoInicial.copy(correo = "ana@duoc.cl",
            errorCorreo = null)

        assertEquals("ana@duoc.cl",estadoActualizado.correo)
        assertNull(estadoActualizado.errorCorreo)
    }

    @Test
    fun validarCorreo_esta_EnBlanco() {
        val estado = LoginUiState(correo = "")

        val errorCorreo = if (estado.correo.isBlank()) R.string.login_error_correo_vacio else null

        assertEquals(R.string.login_error_correo_vacio, errorCorreo)
    }

    @Test
    fun validarNumeroSiniestro_conFormatoInvalido_devuelveError() {
        val numero = "ABC123"

        val error = Validaciones.validarNumeroSiniestro(numero)

        assertEquals("Formato inválido. Ejemplo: SIN-26-008942", error)
    }


    @Test
    fun esContrasenaValida_sinMayuscula_devuelveFalse() {
        val contrasena = "clave123!"

        val resultado = Validaciones.esContrasenaValida(contrasena)

        assertFalse(resultado)
    }

    @Test
    fun contrasena_esta_en_Blanco(){

        val estado = LoginUiState(contrasena="")

        val errorContrasena = if(estado.contrasena.isEmpty()) R.string.login_error_contrasena_vacia

        else null

        assertEquals(R.string.login_error_contrasena_vacia,errorContrasena)
    }

    @Test
    fun esContrasenaValida_conTodasLasReglas_devuelveTrue() {
        val contrasena = "Clave123!"

        val resultado = Validaciones.esContrasenaValida(contrasena)

        assertTrue(resultado)
    }
    @Test
    fun guardarComentario_devuelve_mensajeOk(){
         val estadoInicial= PanelUiState()
        val ok = true

        val mensajeResultado = if(ok){
            "Comentario enviado y guardado correctamente"

        }else{
            "No se pudo guardar el comentario"
        }
        val estadoFinal = estadoInicial.copy(mensaje = mensajeResultado)

        assertEquals("Comentario enviado y guardado correctamente", estadoFinal.mensaje)



    }


    @Test
    fun numeroDeSiniestroEnBlanco (){    fun esContrasenaValida_conTodasLasReglas_devuelveTrue() {
        val contrasena = "Clave123!"

        val resultado = Validaciones.esContrasenaValida(contrasena)

        assertTrue(resultado)
    }
        val siniestro = ""

        val error = Validaciones.validarNumeroSiniestro(siniestro)

        assertEquals("Ingresa el número de siniestro",error)
    }
@Test
    fun usuarioAlCrearCuentaQueSeaValidaYSeaElRolCorrecto (){
        val usuario = Usuario(
            id = 1L,
            nombre = "Camila Rojas",
            rut = "15842371-5",
            correo = "camila@correo.cl",
            rolId = 1L,
            rolNombre = "ASEGURADO"
        )
        assertEquals("Camila Rojas",usuario.nombre)

    }
    @Test
    fun contrasenaSinNumero_devuelveFalse(){
        val contrasena = "Cla1!"

        val resultado = Validaciones.esContrasenaValida(contrasena)

        assertFalse(resultado)
    }

@Test
fun DocumentoRecibidoDevuelveTrue(){
    val documento = Documento(nombre = "Reporte Inicial", recibido = true)

    assertTrue(documento.recibido)
}
    @Test
    fun siniestroResumen_guardaEstadoCorrecto() {
        val siniestro = SiniestroResumen(
            numero = "SIN-26-008942",
            cliente = "Camila Rojas",
            tipo = "Daño vehicular",
            estado = "En evaluación",
            fecha = "2026-10-23"
        )

        assertEquals("En evaluación", siniestro.estado)
    }
}

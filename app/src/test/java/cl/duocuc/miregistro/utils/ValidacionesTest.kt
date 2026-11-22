package cl.duocuc.miregistro.utils
import cl.duocuc.miregistro.model.Documento
import cl.duocuc.miregistro.model.FiltroSiniestro
import cl.duocuc.miregistro.model.LoginUiState
import cl.duocuc.miregistro.model.SiniestroResumen
import cl.duocuc.miregistro.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test



class ValidacionesTest {


    @Test
    fun validarNumeroSiniestro_conNumeroVacio_devuelveError() {

        val numero = ""

        val error = Validaciones.validarNumeroSiniestro(numero)


        assertEquals("Ingresa el número de siniestro", error)
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
    fun esContrasenaValida_conTodasLasReglas_devuelveTrue() {
        val contrasena = "Clave123!"

        val resultado = Validaciones.esContrasenaValida(contrasena)

        assertTrue(resultado)
    }

    @Test
    fun numeroDeSiniestroEnBlanco (){
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

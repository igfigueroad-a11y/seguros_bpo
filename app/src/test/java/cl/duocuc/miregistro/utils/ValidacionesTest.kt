package cl.duocuc.miregistro.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// =====================================================================
//  NUEVO (Pruebas unitarias) — ValidacionesTest.kt
//  ---------------------------------------------------------------------
//  Probamos las reglas de DOS pantallas:
//   - Consulta de siniestro: número vacío y formato inválido
//   - Registro: reglas de la contraseña
//
//  No necesitamos @Before: Validaciones es un "object" (una sola instancia)
//  y no guarda estado, así que las pruebas no se mezclan.
//
//  Patrón AAA en cada prueba:
//   1. Preparar  -> el dato de entrada
//   2. Ejecutar  -> llamar a la función
//   3. Verificar -> comprobar el resultado con assert
// =====================================================================

class ValidacionesTest {

    // ---------------- Pantalla: Consulta de siniestro ----------------

    @Test
    fun validarNumeroSiniestro_conNumeroVacio_devuelveError() {
        // Preparar
        val numero = ""

        // Ejecutar
        val error = Validaciones.validarNumeroSiniestro(numero)

        // Verificar
        assertEquals("Ingresa el número de siniestro", error)
    }

    @Test
    fun validarNumeroSiniestro_conFormatoInvalido_devuelveError() {
        val numero = "ABC123"   // no sigue el formato SIN-26-008942

        val error = Validaciones.validarNumeroSiniestro(numero)

        assertEquals("Formato inválido. Ejemplo: SIN-26-008942", error)
    }

    // ---------------- Pantalla: Registro ----------------

    @Test
    fun esContrasenaValida_sinMayuscula_devuelveFalse() {
        val contrasena = "clave123!"   // tiene todo MENOS la mayúscula

        val resultado = Validaciones.esContrasenaValida(contrasena)

        assertFalse(resultado)
    }

    @Test
    fun esContrasenaValida_conTodasLasReglas_devuelveTrue() {
        val contrasena = "Clave123!"   // 9 caracteres, mayúscula, número y especial

        val resultado = Validaciones.esContrasenaValida(contrasena)

        assertTrue(resultado)
    }

    @Test
    fun numeroDeSiniestroEnBlanco (){
        val siniestro = ""

        val error = Validaciones.validarNumeroSiniestro(siniestro)

        assertEquals("Ingresa el número de siniestro",error)
    }

}

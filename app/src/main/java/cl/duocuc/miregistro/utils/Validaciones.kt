package cl.duocuc.miregistro.utils

// =====================================================================
//  NUEVO (Pruebas unitarias) — Validaciones.kt
//  ---------------------------------------------------------------------
//  Aquí viven las REGLAS de validación, en Kotlin puro (sin Android).
//
//  ¿Por qué las sacamos de los ViewModels?
//  Nuestros ViewModels son AndroidViewModel(application) y crean
//  UsuarioDbHelper por dentro. En una prueba unitaria (carpeta src/test)
//  no existe Android, así que no se pueden crear ahí.
//
//  En cambio, este "object" no depende de nada: se puede probar en
//  segundos, sin emulador y sin base de datos.
//  Los ViewModels y pantallas siguen funcionando igual: solo LLAMAN a estas funciones.
// =====================================================================

object Validaciones {

    // Formato esperado: SIN-26-008942  (SIN - 2 dígitos - 6 dígitos)
    private val patronSiniestro = Regex("^SIN-\\d{2}-\\d{6}$")

    // Devuelve el MENSAJE de error, o null si el número es válido.
    // Recibe el número ya limpio (trim + uppercase) desde el ViewModel.
    fun validarNumeroSiniestro(numero: String): String? = when {
        numero.isBlank() -> "Ingresa el número de siniestro"
        !patronSiniestro.matches(numero) -> "Formato inválido. Ejemplo: SIN-26-008942"
        else -> null
    }

    // true solo si cumple las 4 reglas:
    // mínimo 8 caracteres, 1 mayúscula, 1 número y 1 carácter especial.
    // La usan RegistroViewModel y NuevaContrasenaScreen (antes estaba repetida).
    fun esContrasenaValida(password: String): Boolean {
        val longitudValida = password.length >= 8
        val tieneMayuscula = password.any { it.isUpperCase() }
        val tieneNumero = password.any { it.isDigit() }
        val tieneEspecial = password.any { !it.isLetterOrDigit() }
        return longitudValida && tieneMayuscula && tieneNumero && tieneEspecial
    }
}

package cl.duocuc.miregistro.utils


object Validaciones {

    private val patronSiniestro = Regex("^SIN-\\d{2}-\\d{6}$")

    fun validarNumeroSiniestro(numero: String): String? = when {
        numero.isBlank() -> "Ingresa el número de siniestro"
        !patronSiniestro.matches(numero) -> "Formato inválido. Ejemplo: SIN-26-008942"
        else -> null
    }

    fun esContrasenaValida(password: String): Boolean {
        val longitudValida = password.length >= 8
        val tieneMayuscula = password.any { it.isUpperCase() }
        val tieneNumero = password.any { it.isDigit() }
        val tieneEspecial = password.any { !it.isLetterOrDigit() }
        return longitudValida && tieneMayuscula && tieneNumero && tieneEspecial
    }
}

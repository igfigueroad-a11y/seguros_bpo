package cl.duocuc.miregistro.data

import cl.duocuc.miregistro.data.remote.*
import cl.duocuc.miregistro.model.Usuario
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val api: AuthApi = RetrofitClient.authApi
) {
    suspend fun login(correo: String, contrasena: String): Pair<String, Usuario> {
        val resp = llamarApi { api.login(LoginRequest(correo.trim(), contrasena)) }
        return resp.token to resp.usuario.aUsuario()
    }

    suspend fun registrar(
        nombre: String,
        rut: String,
        correo: String,
        contrasena: String,
        resp1: String,
        resp2: String,
        resp3: String
    ): Usuario {
        val req = RegistroRequest(nombre, rut, correo, contrasena, resp1, resp2, resp3)
        return llamarApi { api.registrar(req) }.aUsuario()
    }

    suspend fun validarRespuestas(
        correo: String,
        resp1: String,
        resp2: String,
        resp3: String
    ): String {
        val req = ValidarRespuestasRequest(correo, resp1, resp2, resp3)
        return llamarApi { api.validarRespuestas(req) }.resetToken
    }

    suspend fun nuevaContrasena(resetToken: String, nuevaClave: String): String {
        return llamarApi { api.nuevaContrasena(NuevaContrasenaRequest(resetToken, nuevaClave)) }.mensaje
    }

    suspend fun cambiarContrasena(token: String, actual: String, nueva: String): String {
        return llamarApi {
            api.cambiarContrasena(
                RetrofitClient.bearer(token),
                CambiarContrasenaRequest(actual, nueva)
            )
        }.mensaje
    }
}

internal suspend fun <T> llamarApi(peticion: suspend () -> T): T = try {
    peticion()
} catch (e: HttpException) {
    val cuerpo = e.response()?.errorBody()?.string()
    val mensaje = try {
        JSONObject(cuerpo ?: "").optString("error")
    } catch (_: Exception) {
        ""
    }
    throw Exception(mensaje.ifBlank { "Error del servidor (${e.code()})" })
} catch (_: IOException) {
    throw Exception("No se pudo conectar con el servidor. Verifica que los microservicios estén encendidos.")
}

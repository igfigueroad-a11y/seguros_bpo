package cl.duocuc.miregistro.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {

    @POST("auth/registro")
    suspend fun registrar(@Body datos: RegistroRequest): UsuarioResponse

    @POST("auth/login")
    suspend fun login(@Body datos: LoginRequest): LoginResponse

    @POST("auth/recuperar/validar")
    suspend fun validarRespuestas(@Body datos: ValidarRespuestasRequest): ResetTokenResponse

    @POST("auth/recuperar/nueva-contrasena")
    suspend fun nuevaContrasena(@Body datos: NuevaContrasenaRequest): MensajeResponse

    @PUT("auth/cambiar-contrasena")
    suspend fun cambiarContrasena(
        @Header("Authorization") authorization: String,
        @Body datos: CambiarContrasenaRequest
    ): MensajeResponse

    @GET("auth/me")
    suspend fun me(
        @Header("Authorization") authorization: String
    ): UsuarioResponse

    @GET("auth/usuarios/{id}")
    suspend fun usuarioPorId(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): UsuarioResponse
}

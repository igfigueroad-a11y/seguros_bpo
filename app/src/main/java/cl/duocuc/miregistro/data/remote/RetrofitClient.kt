package cl.duocuc.miregistro.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Punto de entrada único a través de api-gateway (puerto 8090) desde el emulador Android:
    // - /auth/**       -> redirige a auth-service (8081)
    // - /liquidador/** -> redirige a liquidador-service (8084)
    private const val URL_GATEWAY = "http://10.0.2.2:8090/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_GATEWAY)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val liquidadorApi: LiquidadorApi by lazy {
        retrofit.create(LiquidadorApi::class.java)
    }

    fun bearer(token: String): String = "Bearer $token"
}

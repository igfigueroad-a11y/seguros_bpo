package cl.duocuc.miregistro.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Punto de entrada único a través de api-gateway (puerto 8090) desde el emulador Android:
    // - /auth/**       -> redirige a auth-service (8081)
    // - /liquidador/** -> redirige a liquidador-service (8084)
    // - /analista/**   -> redirige a analista-service (8085)
    private const val URL_GATEWAY = "http://localhost:8090/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_GATEWAY)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val liquidadorApi: LiquidadorApi by lazy {
        retrofit.create(LiquidadorApi::class.java)
    }

    val analistaApi: AnalistaApi by lazy {
        retrofit.create(AnalistaApi::class.java)
    }

    fun bearer(token: String): String = "Bearer $token"
}
package com.example.parcial2.data.remote

import com.example.parcial2.BuildConfig
import com.example.parcial2.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente HTTP único basado en OkHttp y Retrofit.
 * Adjunta el token Bearer solo cuando la petición va hacia nuestro backend (BuildConfig.BASE_URL).
 */
object RetrofitClient {

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestOriginal = chain.request()
                val token = SessionManager.token
                val esBackendPropio = requestOriginal.url.toString().startsWith(BuildConfig.BASE_URL)

                val request = if (!token.isNullOrBlank() && esBackendPropio) {
                    requestOriginal.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    requestOriginal
                }
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val backend: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun crearExterno(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

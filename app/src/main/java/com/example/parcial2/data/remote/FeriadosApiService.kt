package com.example.parcial2.data.remote

import com.example.parcial2.data.remote.dto.FeriadoDto
import retrofit2.http.GET
import retrofit2.http.Path

interface FeriadosApiService {

    // La API responde con una lista JSON, por eso devolvemos List<FeriadoDto>
    @GET("api/v3/PublicHolidays/{anio}/{pais}")
    suspend fun obtenerFeriados(
        @Path("anio") anio: Int,
        @Path("pais") pais: String
    ): List<FeriadoDto>

    companion object {
        private const val BASE_URL_EXTERNA = "https://date.nager.at/"

        // "by lazy": el cliente se crea solo la primera vez que se usa
        val instancia: FeriadosApiService by lazy {
            RetrofitClient.crearExterno(BASE_URL_EXTERNA)
                .create(FeriadosApiService::class.java)
        }
    }
}
package com.example.parcial2.data.remote

import com.example.parcial2.data.remote.dto.AuthResponseDto
import com.example.parcial2.data.remote.dto.CursoDetalleResponseDto
import com.example.parcial2.data.remote.dto.CursoDto
import com.example.parcial2.data.remote.dto.GuardarResultadoRequestDto
import com.example.parcial2.data.remote.dto.HistorialItemDto
import com.example.parcial2.data.remote.dto.LoginRequestDto
import com.example.parcial2.data.remote.dto.QuizPreguntasResponseDto
import com.example.parcial2.data.remote.dto.RegisterRequestDto
import com.example.parcial2.data.remote.dto.ResultadoQuizDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Contrato REST del microservicio Spring Boot.
 * Las rutas protegidas reciben el header Authorization automáticamente vía RetrofitClient.
 */
interface BackendApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @GET("api/cursos")
    suspend fun obtenerCursos(): List<CursoDto>

    @GET("api/cursos/{cursoId}")
    suspend fun obtenerCursoDetalle(@Path("cursoId") cursoId: Int): CursoDetalleResponseDto

    @GET("api/quizzes/{quizId}/preguntas")
    suspend fun obtenerQuizPreguntas(@Path("quizId") quizId: Int): QuizPreguntasResponseDto

    @POST("api/resultados")
    suspend fun guardarResultado(@Body request: GuardarResultadoRequestDto): ResultadoQuizDto

    @GET("api/resultados/historial")
    suspend fun obtenerHistorial(): List<HistorialItemDto>

    companion object {
        val instancia: BackendApiService by lazy {
            RetrofitClient.backend.create(BackendApiService::class.java)
        }
    }
}

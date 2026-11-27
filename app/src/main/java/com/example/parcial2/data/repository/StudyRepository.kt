package com.example.parcial2.data.repository

import com.example.parcial2.data.local.dao.StudyDao
import com.example.parcial2.data.remote.BackendApiService
import com.example.parcial2.data.remote.dto.GuardarResultadoRequestDto
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.HistorialItem
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.ResultadoQuiz
import com.example.parcial2.domain.model.UnidadConQuizzes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class StudyRepository(
    private val studyDao: StudyDao,
    private val api: BackendApiService = BackendApiService.instancia
) {

    suspend fun obtenerCursos(): Result<List<Curso>> = ejecutarConRespaldo(
        remoto = { api.obtenerCursos().map { it.toDomain() } },
        local = { studyDao.obtenerCursos().takeIf { it.isNotEmpty() } }
    )

    suspend fun obtenerDetalleCurso(cursoId: Int): Result<Pair<Curso, List<UnidadConQuizzes>>> =
        ejecutarConRespaldo(
            remoto = {
                val resp = api.obtenerCursoDetalle(cursoId)
                resp.curso.toDomain() to resp.unidades.map { it.toDomain() }
            },
            local = {
                val curso = studyDao.obtenerCurso(cursoId) ?: return@ejecutarConRespaldo null
                val unidades = studyDao.obtenerUnidades(cursoId).map { unidad ->
                    UnidadConQuizzes(unidad, studyDao.obtenerQuizzes(unidad.id))
                }
                curso to unidades
            }
        )

    suspend fun obtenerQuizConPreguntas(quizId: Int): Result<Pair<Quiz, List<Pregunta>>> =
        ejecutarConRespaldo(
            remoto = {
                val resp = api.obtenerQuizPreguntas(quizId)
                resp.quiz.toDomain() to resp.preguntas.map { it.toDomain() }
            },
            local = {
                val quiz = studyDao.obtenerQuiz(quizId) ?: return@ejecutarConRespaldo null
                quiz to studyDao.obtenerPreguntas(quizId)
            }
        )

    suspend fun guardarResultado(
        usuarioId: Int,
        quizId: Int,
        aciertos: Int,
        totalPreguntas: Int
    ): Result<ResultadoQuiz> = ejecutarConRespaldo(
        remoto = {
            api.guardarResultado(
                GuardarResultadoRequestDto(
                    quizId = quizId,
                    aciertos = aciertos,
                    totalPreguntas = totalPreguntas
                )
            ).toDomain()
        },
        local = {
            val fecha = System.currentTimeMillis()
            val id = studyDao.insertarResultado(usuarioId, quizId, aciertos, totalPreguntas, fecha)
            if (id == -1L) null
            else ResultadoQuiz(id.toInt(), usuarioId, quizId, aciertos, totalPreguntas, fecha)
        }
    )

    suspend fun obtenerHistorial(usuarioId: Int): Result<List<HistorialItem>> =
        ejecutarConRespaldo(
            remoto = { api.obtenerHistorial().map { it.toDomain() } },
            local = { studyDao.obtenerHistorial(usuarioId).takeIf { it.isNotEmpty() } }
        )

    private suspend fun <T> ejecutarConRespaldo(
        remoto: suspend () -> T,
        local: () -> T?
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(remoto())
        } catch (e: HttpException) {
            val mensaje = when (e.code()) {
                401 -> "Tu sesión expiró. Inicia sesión nuevamente."
                404 -> "Contenido no encontrado en el servidor."
                in 500..599 -> "Error interno en el servidor (${e.code()})."
                else -> "Error del servidor (${e.code()})."
            }
            Result.failure(Exception(mensaje))
        } catch (_: IOException) {
            val respaldo = try { local() } catch (_: Exception) { null }
            if (respaldo != null) {
                Result.success(respaldo)
            } else {
                Result.failure(Exception("Sin conexión con el servidor. Verifica tu red o que Spring Boot esté activo."))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al cargar los datos."))
        }
    }
}

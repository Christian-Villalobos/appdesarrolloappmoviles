package com.example.parcial2.data.repository

import com.example.parcial2.data.local.dao.StudyDao
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.Unidad
import com.example.parcial2.domain.model.UnidadConQuizzes
import com.example.parcial2.domain.model.ResultadoQuiz
import com.example.parcial2.domain.model.HistorialItem

class StudyRepository(private val studyDao: StudyDao) {

    fun obtenerCursos(): List<Curso> = studyDao.obtenerCursos()

    fun obtenerCurso(cursoId: Int): Curso? = studyDao.obtenerCurso(cursoId)

    fun obtenerUnidades(cursoId: Int): List<Unidad> = studyDao.obtenerUnidades(cursoId)

    fun obtenerQuizzes(unidadId: Int): List<Quiz> = studyDao.obtenerQuizzes(unidadId)

    fun obtenerPreguntas(quizId: Int): List<Pregunta> = studyDao.obtenerPreguntas(quizId)

    fun obtenerHistorial(usuarioId: Int): List<HistorialItem> = studyDao.obtenerHistorial(usuarioId)

    // Arma la jerarquía: cada unidad del curso, junto con sus quizzes
    fun obtenerUnidadesConQuizzes(cursoId: Int): List<UnidadConQuizzes> =
        studyDao.obtenerUnidades(cursoId).map { unidad ->
            UnidadConQuizzes(
                unidad = unidad,
                quizzes = studyDao.obtenerQuizzes(unidad.id)
            )
        }
    fun obtenerQuiz(quizId: Int): Quiz? = studyDao.obtenerQuiz(quizId)

    // Registra el intento con la fecha actual y devuelve el resultado completo
    fun guardarResultado(
        usuarioId: Int,
        quizId: Int,
        aciertos: Int,
        totalPreguntas: Int
    ): ResultadoQuiz {
        val fecha = System.currentTimeMillis()
        val id = studyDao.insertarResultado(usuarioId, quizId, aciertos, totalPreguntas, fecha)
        return ResultadoQuiz(
            id = id.toInt(),
            usuarioId = usuarioId,
            quizId = quizId,
            aciertos = aciertos,
            totalPreguntas = totalPreguntas,
            fecha = fecha
        )
    }
}
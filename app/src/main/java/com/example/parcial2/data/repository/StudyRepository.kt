package com.example.parcial2.data.repository

import com.example.parcial2.data.local.dao.StudyDao
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.Unidad
import com.example.parcial2.domain.model.UnidadConQuizzes

class StudyRepository(private val studyDao: StudyDao) {

    fun obtenerCursos(): List<Curso> = studyDao.obtenerCursos()

    fun obtenerCurso(cursoId: Int): Curso? = studyDao.obtenerCurso(cursoId)

    fun obtenerUnidades(cursoId: Int): List<Unidad> = studyDao.obtenerUnidades(cursoId)

    fun obtenerQuizzes(unidadId: Int): List<Quiz> = studyDao.obtenerQuizzes(unidadId)

    fun obtenerPreguntas(quizId: Int): List<Pregunta> = studyDao.obtenerPreguntas(quizId)

    // Arma la jerarquía: cada unidad del curso, junto con sus quizzes
    fun obtenerUnidadesConQuizzes(cursoId: Int): List<UnidadConQuizzes> =
        studyDao.obtenerUnidades(cursoId).map { unidad ->
            UnidadConQuizzes(
                unidad = unidad,
                quizzes = studyDao.obtenerQuizzes(unidad.id)
            )
        }
}
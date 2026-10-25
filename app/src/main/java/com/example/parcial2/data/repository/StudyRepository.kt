package com.example.parcial2.data.repository

import com.example.parcial2.data.local.dao.StudyDao
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.Unidad

class StudyRepository(private val studyDao: StudyDao) {

    fun obtenerCursos(): List<Curso> = studyDao.obtenerCursos()

    fun obtenerUnidades(cursoId: Int): List<Unidad> = studyDao.obtenerUnidades(cursoId)

    fun obtenerQuizzes(unidadId: Int): List<Quiz> = studyDao.obtenerQuizzes(unidadId)

    fun obtenerPreguntas(quizId: Int): List<Pregunta> = studyDao.obtenerPreguntas(quizId)
}
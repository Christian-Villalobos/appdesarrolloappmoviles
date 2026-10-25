package com.example.parcial2.data.local.dao

import android.database.Cursor
import com.example.parcial2.data.local.db.DbContract.Cursos
import com.example.parcial2.data.local.db.DbContract.Preguntas
import com.example.parcial2.data.local.db.DbContract.Quizzes
import com.example.parcial2.data.local.db.DbContract.Unidades
import com.example.parcial2.data.local.db.DbHelper
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.Unidad

class StudyDao(private val dbHelper: DbHelper) {

    fun obtenerCursos(): List<Curso> {
        val cursor = dbHelper.readableDatabase.query(
            Cursos.TABLA, null, null, null, null, null, Cursos.ID
        )
        return cursor.use {
            val lista = mutableListOf<Curso>()
            while (it.moveToNext()) {
                lista.add(
                    Curso(
                        id = it.leerInt(Cursos.ID),
                        nombre = it.leerTexto(Cursos.NOMBRE),
                        descripcion = it.leerTexto(Cursos.DESCRIPCION)
                    )
                )
            }
            lista
        }
    }

    fun obtenerUnidades(cursoId: Int): List<Unidad> {
        val cursor = dbHelper.readableDatabase.query(
            Unidades.TABLA, null,
            "${Unidades.CURSO_ID} = ?", arrayOf(cursoId.toString()),
            null, null, Unidades.ID
        )
        return cursor.use {
            val lista = mutableListOf<Unidad>()
            while (it.moveToNext()) {
                lista.add(
                    Unidad(
                        id = it.leerInt(Unidades.ID),
                        cursoId = it.leerInt(Unidades.CURSO_ID),
                        nombre = it.leerTexto(Unidades.NOMBRE)
                    )
                )
            }
            lista
        }
    }

    fun obtenerQuizzes(unidadId: Int): List<Quiz> {
        val cursor = dbHelper.readableDatabase.query(
            Quizzes.TABLA, null,
            "${Quizzes.UNIDAD_ID} = ?", arrayOf(unidadId.toString()),
            null, null, Quizzes.ID
        )
        return cursor.use {
            val lista = mutableListOf<Quiz>()
            while (it.moveToNext()) {
                lista.add(
                    Quiz(
                        id = it.leerInt(Quizzes.ID),
                        unidadId = it.leerInt(Quizzes.UNIDAD_ID),
                        titulo = it.leerTexto(Quizzes.TITULO)
                    )
                )
            }
            lista
        }
    }

    fun obtenerPreguntas(quizId: Int): List<Pregunta> {
        val cursor = dbHelper.readableDatabase.query(
            Preguntas.TABLA, null,
            "${Preguntas.QUIZ_ID} = ?", arrayOf(quizId.toString()),
            null, null, Preguntas.ID
        )
        return cursor.use {
            val lista = mutableListOf<Pregunta>()
            while (it.moveToNext()) {
                lista.add(
                    Pregunta(
                        id = it.leerInt(Preguntas.ID),
                        quizId = it.leerInt(Preguntas.QUIZ_ID),
                        enunciado = it.leerTexto(Preguntas.ENUNCIADO),
                        // Las 4 columnas de la base vuelven a ser una lista
                        opciones = listOf(
                            it.leerTexto(Preguntas.OPCION_A),
                            it.leerTexto(Preguntas.OPCION_B),
                            it.leerTexto(Preguntas.OPCION_C),
                            it.leerTexto(Preguntas.OPCION_D)
                        ),
                        indiceCorrecto = it.leerInt(Preguntas.INDICE_CORRECTO),
                        justificacionLegal = it.leerTexto(Preguntas.JUSTIFICACION)
                    )
                )
            }
            lista
        }
    }
}

// Atajos para no repetir "getColumnIndexOrThrow" en cada lectura
private fun Cursor.leerInt(columna: String): Int = getInt(getColumnIndexOrThrow(columna))

private fun Cursor.leerTexto(columna: String): String = getString(getColumnIndexOrThrow(columna))
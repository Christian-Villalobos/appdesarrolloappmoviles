package com.example.parcial2.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.parcial2.data.local.db.DbContract.Cursos
import com.example.parcial2.data.local.db.DbContract.Preguntas
import com.example.parcial2.data.local.db.DbContract.Quizzes
import com.example.parcial2.data.local.db.DbContract.Resultados
import com.example.parcial2.data.local.db.DbContract.Unidades
import com.example.parcial2.data.local.db.DbHelper
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.ResultadoConQuiz
import com.example.parcial2.domain.model.ResultadoQuiz
import com.example.parcial2.domain.model.Unidad

class StudyDao(private val dbHelper: DbHelper) {

    // ---------- Funciones auxiliares para leer columnas ----------
    // Evitan repetir "getColumnIndexOrThrow" cientos de veces.
    private fun Cursor.int(columna: String): Int = getInt(getColumnIndexOrThrow(columna))
    private fun Cursor.texto(columna: String): String = getString(getColumnIndexOrThrow(columna))

    // ---------- Cursos ----------
    fun obtenerCursos(): List<Curso> {
        val lista = mutableListOf<Curso>()
        val cursor = dbHelper.readableDatabase.query(
            Cursos.TABLA, null, null, null, null, null, "${Cursos.ID} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Curso(
                        id = it.int(Cursos.ID),
                        nombre = it.texto(Cursos.NOMBRE),
                        descripcion = it.texto(Cursos.DESCRIPCION)
                    )
                )
            }
        }
        return lista
    }

    // ---------- Unidades de un curso ----------
    fun obtenerUnidades(cursoId: Int): List<Unidad> {
        val lista = mutableListOf<Unidad>()
        val cursor = dbHelper.readableDatabase.query(
            Unidades.TABLA, null,
            "${Unidades.CURSO_ID} = ?", arrayOf(cursoId.toString()),
            null, null, "${Unidades.ID} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Unidad(
                        id = it.int(Unidades.ID),
                        cursoId = it.int(Unidades.CURSO_ID),
                        nombre = it.texto(Unidades.NOMBRE)
                    )
                )
            }
        }
        return lista
    }

    // ---------- Quizzes de una unidad ----------
    fun obtenerQuizzes(unidadId: Int): List<Quiz> {
        val lista = mutableListOf<Quiz>()
        val cursor = dbHelper.readableDatabase.query(
            Quizzes.TABLA, null,
            "${Quizzes.UNIDAD_ID} = ?", arrayOf(unidadId.toString()),
            null, null, "${Quizzes.ID} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Quiz(
                        id = it.int(Quizzes.ID),
                        unidadId = it.int(Quizzes.UNIDAD_ID),
                        titulo = it.texto(Quizzes.TITULO)
                    )
                )
            }
        }
        return lista
    }

    // ---------- Preguntas de un quiz ----------
    fun obtenerPreguntas(quizId: Int): List<Pregunta> {
        val lista = mutableListOf<Pregunta>()
        val cursor = dbHelper.readableDatabase.query(
            Preguntas.TABLA, null,
            "${Preguntas.QUIZ_ID} = ?", arrayOf(quizId.toString()),
            null, null, "${Preguntas.ID} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Pregunta(
                        id = it.int(Preguntas.ID),
                        quizId = it.int(Preguntas.QUIZ_ID),
                        enunciado = it.texto(Preguntas.ENUNCIADO),
                        // Las 4 columnas se juntan en una lista: posición 0 = A, 1 = B, 2 = C, 3 = D
                        opciones = listOf(
                            it.texto(Preguntas.OPCION_A),
                            it.texto(Preguntas.OPCION_B),
                            it.texto(Preguntas.OPCION_C),
                            it.texto(Preguntas.OPCION_D)
                        ),
                        indiceCorrecto = it.int(Preguntas.INDICE_CORRECTO),
                        justificacionLegal = it.texto(Preguntas.JUSTIFICACION)
                    )
                )
            }
        }
        return lista
    }

    // ---------- Guardar un intento de quiz ----------
    // Devuelve el id de la fila creada, o -1 si falló.
    fun insertarResultado(
        usuarioId: Int,
        quizId: Int,
        aciertos: Int,
        totalPreguntas: Int,
        fecha: Long = System.currentTimeMillis()
    ): Long {
        val valores = ContentValues().apply {
            put(Resultados.USUARIO_ID, usuarioId)
            put(Resultados.QUIZ_ID, quizId)
            put(Resultados.ACIERTOS, aciertos)
            put(Resultados.TOTAL_PREGUNTAS, totalPreguntas)
            put(Resultados.FECHA, fecha)
        }
        return dbHelper.writableDatabase.insert(Resultados.TABLA, null, valores)
    }

    // ---------- Historial de un usuario, con el título del quiz ----------
    fun obtenerResultados(usuarioId: Int): List<ResultadoConQuiz> {
        val lista = mutableListOf<ResultadoConQuiz>()

        // JOIN: une cada fila de "resultados" con la fila de "quizzes" cuyo id coincide con quiz_id.
        val sql = """
            SELECT r.${Resultados.ID}, r.${Resultados.USUARIO_ID}, r.${Resultados.QUIZ_ID},
                   r.${Resultados.ACIERTOS}, r.${Resultados.TOTAL_PREGUNTAS}, r.${Resultados.FECHA},
                   q.${Quizzes.TITULO}
            FROM ${Resultados.TABLA} r
            INNER JOIN ${Quizzes.TABLA} q ON r.${Resultados.QUIZ_ID} = q.${Quizzes.ID}
            WHERE r.${Resultados.USUARIO_ID} = ?
            ORDER BY r.${Resultados.FECHA} DESC
        """.trimIndent()

        val cursor = dbHelper.readableDatabase.rawQuery(sql, arrayOf(usuarioId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    ResultadoConQuiz(
                        resultado = ResultadoQuiz(
                            id = it.int(Resultados.ID),
                            usuarioId = it.int(Resultados.USUARIO_ID),
                            quizId = it.int(Resultados.QUIZ_ID),
                            aciertos = it.int(Resultados.ACIERTOS),
                            totalPreguntas = it.int(Resultados.TOTAL_PREGUNTAS),
                            fecha = it.getLong(it.getColumnIndexOrThrow(Resultados.FECHA))
                        ),
                        tituloQuiz = it.texto(Quizzes.TITULO)
                    )
                )
            }
        }
        return lista
    }
}
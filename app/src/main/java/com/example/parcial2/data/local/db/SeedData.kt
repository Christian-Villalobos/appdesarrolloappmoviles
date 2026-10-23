package com.example.parcial2.data.local.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.parcial2.data.local.db.DbContract.Cursos
import com.example.parcial2.data.local.db.DbContract.Preguntas
import com.example.parcial2.data.local.db.DbContract.Quizzes
import com.example.parcial2.data.local.db.DbContract.Unidades

object SeedData {

    fun insertar(db: SQLiteDatabase) {

        // ---------- CURSO ----------
        val cursoId = insertarCurso(
            db,
            nombre = "Fundamentos Jurídicos de la Función Policial",
            descripcion = "Marco constitucional de Carabineros y procedimiento de detención."
        )

        // ---------- UNIDAD 1 ----------
        val unidad1 = insertarUnidad(db, cursoId, "Marco constitucional e institucional")
        val quiz1 = insertarQuiz(db, unidad1, "Quiz: Constitución y Carabineros")

        insertarPregunta(
            db, quiz1,
            enunciado = "Según la Constitución, ¿qué instituciones integran las Fuerzas de Orden y Seguridad Pública?",
            opciones = listOf(
                "Carabineros e Investigaciones de Chile",
                "Carabineros y Gendarmería de Chile",
                "El Ejército y Carabineros de Chile",
                "Investigaciones y Gendarmería de Chile"
            ),
            indiceCorrecto = 0,
            justificacion = "El artículo 101 de la Constitución Política establece que las Fuerzas de Orden y Seguridad Pública están integradas solo por Carabineros e Investigaciones de Chile."
        )

        insertarPregunta(
            db, quiz1,
            enunciado = "Según el artículo 101 de la Constitución, las Fuerzas de Orden y Seguridad Pública son esencialmente:",
            opciones = listOf(
                "Deliberantes y autónomas",
                "Obedientes y no deliberantes",
                "Autónomas y representativas",
                "Políticas y deliberantes"
            ),
            indiceCorrecto = 1,
            justificacion = "El artículo 101 señala que, como cuerpos armados, son esencialmente obedientes y no deliberantes, además de profesionales, jerarquizadas y disciplinadas."
        )

        insertarPregunta(
            db, quiz1,
            enunciado = "¿Cuál es la ley orgánica constitucional de Carabineros de Chile?",
            opciones = listOf(
                "Ley N° 18.575",
                "Ley N° 18.961",
                "Ley N° 19.640",
                "Ley N° 20.502"
            ),
            indiceCorrecto = 1,
            justificacion = "La Ley N° 18.961 es la Ley Orgánica Constitucional de Carabineros de Chile. La Ley N° 19.640 corresponde al Ministerio Público y la Ley N° 18.575 a las bases de la Administración del Estado."
        )

        // ---------- UNIDAD 2 ----------
        val unidad2 = insertarUnidad(db, cursoId, "Detención y flagrancia")
        val quiz2 = insertarQuiz(db, unidad2, "Quiz: Detención en flagrancia")

        insertarPregunta(
            db, quiz2,
            enunciado = "¿Quién puede detener a una persona sorprendida en delito flagrante?",
            opciones = listOf(
                "Solo Carabineros",
                "Solo quien tenga una orden judicial",
                "Cualquier persona, debiendo entregar de inmediato al aprehendido a la policía, al Ministerio Público o a la autoridad judicial más próxima",
                "Solo el fiscal"
            ),
            indiceCorrecto = 2,
            justificacion = "El artículo 129 del Código Procesal Penal permite que cualquier persona detenga a quien sorprenda en delito flagrante, con la obligación de entregarlo de inmediato a la policía, al Ministerio Público o a la autoridad judicial más próxima."
        )

        insertarPregunta(
            db, quiz2,
            enunciado = "¿Cuál es el plazo máximo para poner a un detenido en flagrancia a disposición del juez competente?",
            opciones = listOf(
                "12 horas",
                "24 horas",
                "48 horas",
                "72 horas"
            ),
            indiceCorrecto = 1,
            justificacion = "El artículo 19 N° 7 letra c) de la Constitución dispone que el detenido en flagrancia debe ser puesto a disposición del juez competente dentro de las 24 horas siguientes. El artículo 131 del Código Procesal Penal lo reitera."
        )

        insertarPregunta(
            db, quiz2,
            enunciado = "Al practicar una detención, el funcionario policial debe:",
            opciones = listOf(
                "Esperar a que el detenido pregunte el motivo",
                "Informar el motivo de la detención y los derechos que le asisten",
                "Informar solo a los familiares del detenido",
                "Interrogar primero y informar después"
            ),
            indiceCorrecto = 1,
            justificacion = "El artículo 135 del Código Procesal Penal obliga al funcionario a cargo de la detención a informar al afectado el motivo de esta y los derechos que le asisten."
        )
    }

    // ---------- Funciones auxiliares: una por tabla ----------
    // Cada una devuelve el id de la fila insertada (Long), que sirve como clave foránea del "hijo".

    private fun insertarCurso(db: SQLiteDatabase, nombre: String, descripcion: String): Long {
        val valores = ContentValues().apply {
            put(Cursos.NOMBRE, nombre)
            put(Cursos.DESCRIPCION, descripcion)
        }
        return db.insert(Cursos.TABLA, null, valores)
    }

    private fun insertarUnidad(db: SQLiteDatabase, cursoId: Long, nombre: String): Long {
        val valores = ContentValues().apply {
            put(Unidades.CURSO_ID, cursoId)
            put(Unidades.NOMBRE, nombre)
        }
        return db.insert(Unidades.TABLA, null, valores)
    }

    private fun insertarQuiz(db: SQLiteDatabase, unidadId: Long, titulo: String): Long {
        val valores = ContentValues().apply {
            put(Quizzes.UNIDAD_ID, unidadId)
            put(Quizzes.TITULO, titulo)
        }
        return db.insert(Quizzes.TABLA, null, valores)
    }

    private fun insertarPregunta(
        db: SQLiteDatabase,
        quizId: Long,
        enunciado: String,
        opciones: List<String>,
        indiceCorrecto: Int,
        justificacion: String
    ) {
        val valores = ContentValues().apply {
            put(Preguntas.QUIZ_ID, quizId)
            put(Preguntas.ENUNCIADO, enunciado)
            put(Preguntas.OPCION_A, opciones[0])
            put(Preguntas.OPCION_B, opciones[1])
            put(Preguntas.OPCION_C, opciones[2])
            put(Preguntas.OPCION_D, opciones[3])
            put(Preguntas.INDICE_CORRECTO, indiceCorrecto)
            put(Preguntas.JUSTIFICACION, justificacion)
        }
        db.insert(Preguntas.TABLA, null, valores)
    }
}
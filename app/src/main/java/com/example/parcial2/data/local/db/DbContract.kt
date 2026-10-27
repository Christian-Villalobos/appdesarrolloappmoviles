package com.example.parcial2.data.local.db

object DbContract {

    const val DB_NAME = "parcial2.db"
    const val DB_VERSION = 2

    object Usuarios {
        const val TABLA = "usuarios"
        const val ID = "id"
        const val NOMBRES = "nombres"
        const val APELLIDOS = "apellidos"
        const val EMAIL = "email"
        const val PASSWORD_HASH = "password_hash"
    }

    object Cursos {
        const val TABLA = "cursos"
        const val ID = "id"
        const val NOMBRE = "nombre"
        const val DESCRIPCION = "descripcion"
    }

    object Unidades {
        const val TABLA = "unidades"
        const val ID = "id"
        const val CURSO_ID = "curso_id"
        const val NOMBRE = "nombre"
    }

    object Quizzes {
        const val TABLA = "quizzes"
        const val ID = "id"
        const val UNIDAD_ID = "unidad_id"
        const val TITULO = "titulo"
    }

    object Preguntas {
        const val TABLA = "preguntas"
        const val ID = "id"
        const val QUIZ_ID = "quiz_id"
        const val ENUNCIADO = "enunciado"
        const val OPCION_A = "opcion_a"
        const val OPCION_B = "opcion_b"
        const val OPCION_C = "opcion_c"
        const val OPCION_D = "opcion_d"
        const val INDICE_CORRECTO = "indice_correcto"
        const val JUSTIFICACION = "justificacion"
    }

    object Resultados {
        const val TABLA = "resultados"
        const val ID = "id"
        const val USUARIO_ID = "usuario_id"
        const val QUIZ_ID = "quiz_id"
        const val ACIERTOS = "aciertos"
        const val TOTAL_PREGUNTAS = "total_preguntas"
        const val FECHA = "fecha"
    }

    object Archivos {
        const val TABLA = "archivos"
        const val ID = "id"
        const val UNIDAD_ID = "unidad_id"
        const val NOMBRE = "nombre"
        const val RUTA_INTERNA = "ruta_interna"
        const val TAMANO_BYTES = "tamano_bytes"
        const val FECHA = "fecha"
    }
}

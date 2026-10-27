package com.example.parcial2.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Lista cerrada de las pantallas de la app.
sealed class NavRoutes(val ruta: String) {
    data object Login : NavRoutes("login")
    data object Registro : NavRoutes("registro")
    data object Home : NavRoutes("home")
    data object Cursos : NavRoutes("cursos")
    data object Perfil : NavRoutes("perfil")

    // Esta ruta lleva un argumento: el id del curso
    data object CursoDetalle : NavRoutes("curso/{cursoId}") {
        const val ARG_CURSO_ID = "cursoId"
        fun crearRuta(cursoId: Int): String = "curso/$cursoId"
    }

    // Esta ruta lleva el id del quiz
    data object Quiz : NavRoutes("quiz/{quizId}") {
        const val ARG_QUIZ_ID = "quizId"
        fun crearRuta(quizId: Int): String = "quiz/$quizId"
    }

    // Ruta para el material de estudio de una unidad
    data object Materiales : NavRoutes("materiales/{unidadId}?titulo={titulo}") {
        const val ARG_UNIDAD_ID = "unidadId"
        const val ARG_TITULO = "titulo"
        fun crearRuta(unidadId: Int, titulo: String): String {
            val tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8.toString())
            return "materiales/$unidadId?titulo=$tituloCodificado"
        }
    }
}

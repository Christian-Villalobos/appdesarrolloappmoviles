package com.example.parcial2.ui.navigation

// Lista cerrada de las pantallas de la app. En la parte 5C se agregará la del quiz.
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
}
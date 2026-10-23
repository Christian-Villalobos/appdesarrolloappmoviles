package com.example.parcial2.domain.model

data class ResultadoQuiz(
    val id: Int,
    val usuarioId: Int,
    val quizId: Int,
    val aciertos: Int,
    val totalPreguntas: Int,
    val fecha: Long          // momento del intento, en milisegundos
) {
    // Propiedad calculada: no se guarda, se obtiene con una cuenta cada vez que se pide
    val porcentaje: Int
        get() = if (totalPreguntas == 0) 0 else aciertos * 100 / totalPreguntas
}
package com.example.parcial2.domain.model

// Una unidad junto con los quizzes que le pertenecen
data class UnidadConQuizzes(
    val unidad: Unidad,
    val quizzes: List<Quiz>
)
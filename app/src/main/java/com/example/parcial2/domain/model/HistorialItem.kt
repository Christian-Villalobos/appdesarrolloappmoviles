package com.example.parcial2.domain.model

// Un resultado junto con el título del quiz al que pertenece
data class HistorialItem(
    val resultado: ResultadoQuiz,
    val tituloQuiz: String
)
package com.example.parcial2.domain.model

data class Pregunta(
    val id: Int,
    val quizId: Int,                 // a qué quiz pertenece
    val enunciado: String,
    val opciones: List<String>,      // 4 textos: posición 0 = A, 1 = B, 2 = C, 3 = D
    val indiceCorrecto: Int,         // posición de la alternativa correcta (0 a 3)
    val justificacionLegal: String   // se muestra como retroalimentación
)
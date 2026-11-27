package com.example.parcial2.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ResultadoQuizTest {

    // Atajo para no repetir todos los parámetros en cada prueba
    private fun resultado(aciertos: Int, total: Int) = ResultadoQuiz(
        id = 1,
        usuarioId = 1,
        quizId = 1,
        aciertos = aciertos,
        totalPreguntas = total,
        fecha = 0L
    )

    @Test
    fun `un quiz sin preguntas da cero y no falla por division por cero`() {
        assertEquals(0, resultado(aciertos = 0, total = 0).porcentaje)
    }

    @Test
    fun `el porcentaje se calcula con division entera`() {
        // 2 de 3 = 66,66...% y Kotlin lo trunca a 66
        assertEquals(66, resultado(aciertos = 2, total = 3).porcentaje)
    }

    @Test
    fun `un quiz perfecto da cien`() {
        assertEquals(100, resultado(aciertos = 3, total = 3).porcentaje)
    }

    @Test
    fun `un quiz sin aciertos da cero`() {
        assertEquals(0, resultado(aciertos = 0, total = 5).porcentaje)
    }
}
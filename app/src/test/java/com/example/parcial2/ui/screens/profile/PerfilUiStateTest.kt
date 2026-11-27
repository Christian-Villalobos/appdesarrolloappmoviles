package com.example.parcial2.ui.screens.profile

import com.example.parcial2.domain.model.HistorialItem
import com.example.parcial2.domain.model.ResultadoQuiz
import org.junit.Assert.assertEquals
import org.junit.Test

class PerfilUiStateTest {

    private fun item(aciertos: Int, total: Int) = HistorialItem(
        resultado = ResultadoQuiz(
            id = 1,
            usuarioId = 1,
            quizId = 1,
            aciertos = aciertos,
            totalPreguntas = total,
            fecha = 0L
        ),
        tituloQuiz = "Quiz de prueba"
    )

    @Test
    fun `calcula el total de intentos y el promedio`() {
        val estado = PerfilUiState(
            historial = listOf(item(3, 3), item(1, 2)),   // 100% y 50%
            cargando = false
        )

        assertEquals(2, estado.totalIntentos)
        assertEquals(75, estado.promedio)
    }

    @Test
    fun `sin historial el promedio es cero`() {
        val estado = PerfilUiState(historial = emptyList(), cargando = false)

        assertEquals(0, estado.totalIntentos)
        assertEquals(0, estado.promedio)
    }
}
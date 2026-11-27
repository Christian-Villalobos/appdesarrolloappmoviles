package com.example.parcial2.ui.screens.quiz

import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.ResultadoQuiz
import com.example.parcial2.domain.model.Usuario
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository: StudyRepository = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        SessionManager.iniciarSesion(
            Usuario(1, "Juan", "Pérez", "juan.perez@carabineros.cl"),
            token = "token-test"
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        SessionManager.cerrarSesion()
    }

    @Test
    fun `flujo completo de quiz suma aciertos y guarda resultado`() = runTest {
        val quiz = Quiz(1, 1, "Quiz Prueba")
        val preguntas = listOf(
            Pregunta(1, 1, "P1", listOf("A", "B", "C", "D"), indiceCorrecto = 1, "Justificación 1")
        )
        val esperado = ResultadoQuiz(10, 1, 1, aciertos = 1, totalPreguntas = 1, fecha = 123L)

        coEvery { repository.obtenerQuizConPreguntas(1) } returns Result.success(quiz to preguntas)
        coEvery { repository.guardarResultado(1, 1, 1, 1) } returns Result.success(esperado)

        val viewModel = QuizViewModel(repository, quizId = 1)
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.preguntas.size)

        viewModel.seleccionar(1)
        viewModel.responder()
        assertTrue(viewModel.uiState.value.respondida)
        assertEquals(1, viewModel.uiState.value.aciertos)

        viewModel.siguiente()
        assertNotNull(viewModel.uiState.value.resultado)
        assertEquals(100, viewModel.uiState.value.resultado?.porcentaje)
    }
}
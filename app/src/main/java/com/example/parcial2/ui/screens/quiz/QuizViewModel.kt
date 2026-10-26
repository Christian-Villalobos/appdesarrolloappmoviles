package com.example.parcial2.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.ResultadoQuiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class QuizUiState(
    val titulo: String = "",
    val preguntas: List<Pregunta> = emptyList(),
    val indiceActual: Int = 0,
    val seleccion: Int? = null,
    val respondida: Boolean = false,
    val aciertos: Int = 0,
    val resultado: ResultadoQuiz? = null,
    val cargando: Boolean = true
) {
    // Propiedades calculadas: se obtienen a partir de los datos de arriba
    val preguntaActual: Pregunta?
        get() = preguntas.getOrNull(indiceActual)

    val esUltima: Boolean
        get() = indiceActual == preguntas.lastIndex
}

class QuizViewModel(
    private val repository: StudyRepository,
    private val quizId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Evita guardar dos veces el mismo intento si se toca el botón rápido
    private var resultadoGuardado = false

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            val (quiz, preguntas) = withContext(Dispatchers.IO) {
                Pair(repository.obtenerQuiz(quizId), repository.obtenerPreguntas(quizId))
            }
            _uiState.update {
                it.copy(titulo = quiz?.titulo.orEmpty(), preguntas = preguntas, cargando = false)
            }
        }
    }

    // El usuario toca una alternativa (solo si todavía no respondió)
    fun seleccionar(indice: Int) {
        _uiState.update { estado ->
            if (estado.respondida) estado else estado.copy(seleccion = indice)
        }
    }

    // El usuario pulsa "Responder": se corrige y se suma el acierto si corresponde
    fun responder() {
        _uiState.update { estado ->
            val pregunta = estado.preguntaActual
            val seleccion = estado.seleccion

            if (pregunta == null || seleccion == null || estado.respondida) {
                estado
            } else {
                val acerto = seleccion == pregunta.indiceCorrecto
                estado.copy(
                    respondida = true,
                    aciertos = if (acerto) estado.aciertos + 1 else estado.aciertos
                )
            }
        }
    }

    // El usuario pulsa "Siguiente" o "Ver resultado"
    fun siguiente() {
        val estado = _uiState.value
        if (!estado.respondida) return

        if (estado.esUltima) {
            finalizar()
        } else {
            _uiState.update {
                it.copy(indiceActual = it.indiceActual + 1, seleccion = null, respondida = false)
            }
        }
    }

    private fun finalizar() {
        val usuarioId = SessionManager.usuarioActual.value?.id ?: return
        if (resultadoGuardado) return
        resultadoGuardado = true

        val estado = _uiState.value
        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                repository.guardarResultado(
                    usuarioId = usuarioId,
                    quizId = quizId,
                    aciertos = estado.aciertos,
                    totalPreguntas = estado.preguntas.size
                )
            }
            _uiState.update { it.copy(resultado = resultado) }
        }
    }

    // "Reintentar": vuelve a la primera pregunta con el contador en cero
    fun reiniciar() {
        resultadoGuardado = false
        _uiState.update {
            it.copy(
                indiceActual = 0,
                seleccion = null,
                respondida = false,
                aciertos = 0,
                resultado = null
            )
        }
    }
}
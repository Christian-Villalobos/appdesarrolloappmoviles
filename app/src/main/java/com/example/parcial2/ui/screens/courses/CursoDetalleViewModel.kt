package com.example.parcial2.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.UnidadConQuizzes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CursoDetalleUiState(
    val curso: Curso? = null,
    val unidades: List<UnidadConQuizzes> = emptyList(),
    val cargando: Boolean = true
)

class CursoDetalleViewModel(
    private val repository: StudyRepository,
    private val cursoId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(CursoDetalleUiState())
    val uiState: StateFlow<CursoDetalleUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    private fun cargar() {
        viewModelScope.launch {
            val (curso, unidades) = withContext(Dispatchers.IO) {
                Pair(
                    repository.obtenerCurso(cursoId),
                    repository.obtenerUnidadesConQuizzes(cursoId)
                )
            }
            _uiState.update {
                it.copy(curso = curso, unidades = unidades, cargando = false)
            }
        }
    }
}
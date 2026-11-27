package com.example.parcial2.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.UnidadConQuizzes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CursoDetalleUiState(
    val curso: Curso? = null,
    val unidades: List<UnidadConQuizzes> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
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

    fun cargar() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            repository.obtenerDetalleCurso(cursoId).fold(
                onSuccess = { (curso, unidades) ->
                    _uiState.update {
                        it.copy(curso = curso, unidades = unidades, cargando = false, error = null)
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(cargando = false, error = e.message) }
                }
            )
        }
    }
}

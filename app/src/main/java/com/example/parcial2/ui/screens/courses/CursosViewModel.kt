package com.example.parcial2.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.domain.model.Curso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CursosUiState(
    val cursos: List<Curso> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class CursosViewModel(private val repository: StudyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CursosUiState())
    val uiState: StateFlow<CursosUiState> = _uiState.asStateFlow()

    // El bloque init se ejecuta apenas se crea el ViewModel: cargamos los cursos solos
    init {
        cargarCursos()
    }

    fun cargarCursos() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            repository.obtenerCursos().fold(
                onSuccess = { cursos ->
                    _uiState.update { it.copy(cursos = cursos, cargando = false, error = null) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(cargando = false, error = e.message) }
                }
            )
        }
    }
}

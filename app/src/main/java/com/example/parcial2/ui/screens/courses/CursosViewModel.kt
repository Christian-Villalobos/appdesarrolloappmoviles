package com.example.parcial2.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.domain.model.Curso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CursosUiState(
    val cursos: List<Curso> = emptyList(),
    val cargando: Boolean = true
)

class CursosViewModel(private val repository: StudyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CursosUiState())
    val uiState: StateFlow<CursosUiState> = _uiState.asStateFlow()

    // El bloque init se ejecuta apenas se crea el ViewModel: cargamos los cursos solos
    init {
        cargarCursos()
    }

    private fun cargarCursos() {
        viewModelScope.launch {
            val cursos = withContext(Dispatchers.IO) { repository.obtenerCursos() }
            _uiState.update { it.copy(cursos = cursos, cargando = false) }
        }
    }
}
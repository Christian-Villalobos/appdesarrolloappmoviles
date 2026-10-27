package com.example.parcial2.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.domain.model.HistorialItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PerfilUiState(
    val historial: List<HistorialItem> = emptyList(),
    val cargando: Boolean = true
) {
    // Estadísticas calculadas a partir del historial
    val totalIntentos: Int
        get() = historial.size

    val promedio: Int
        get() = if (historial.isEmpty()) 0
        else historial.sumOf { it.resultado.porcentaje } / historial.size
}

class PerfilViewModel(private val repository: StudyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    // Es pública porque la pantalla la llama cada vez que se abre el Perfil
    fun cargar() {
        val usuarioId = SessionManager.usuarioActual.value?.id ?: return

        viewModelScope.launch {
            val historial = withContext(Dispatchers.IO) {
                repository.obtenerHistorial(usuarioId)
            }
            _uiState.update { it.copy(historial = historial, cargando = false) }
        }
    }
}
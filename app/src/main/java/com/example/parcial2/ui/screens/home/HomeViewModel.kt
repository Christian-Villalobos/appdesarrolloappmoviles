package com.example.parcial2.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.remote.FeriadosApiService
import com.example.parcial2.data.remote.dto.FeriadoDto
import com.example.parcial2.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate

// Todo lo que el Home necesita para dibujar la sección de feriados
data class HomeUiState(
    val feriados: List<FeriadoDto> = emptyList(),
    val cargandoFeriados: Boolean = true,
    val errorFeriados: String? = null
)

// @JvmOverloads genera también un constructor sin argumentos, así que
// viewModel() en el NavHost sigue funcionando, y en pruebas podremos pasar un "falso".
class HomeViewModel @JvmOverloads constructor(
    private val api: FeriadosApiService = FeriadosApiService.instancia
) : ViewModel() {

    val usuario: StateFlow<Usuario?> = SessionManager.usuarioActual

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        cargarFeriados()
    }

    fun cargarFeriados() {
        _uiState.update { it.copy(cargandoFeriados = true, errorFeriados = null) }
        viewModelScope.launch {
            try {
                val hoy = LocalDate.now()
                val proximos = api.obtenerFeriados(hoy.year, "CL")
                    .filter { it.date >= hoy.toString() }  // las fechas ISO se comparan como texto
                    .sortedBy { it.date }
                    .take(3)

                _uiState.update {
                    it.copy(feriados = proximos, cargandoFeriados = false, errorFeriados = null)
                }
            } catch (e: HttpException) {
                _uiState.update {
                    it.copy(cargandoFeriados = false, errorFeriados = "No se pudo consultar la API (${e.code()}).")
                }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(cargandoFeriados = false, errorFeriados = "Sin conexión para cargar feriados legales.")
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(cargandoFeriados = false, errorFeriados = "Error inesperado al consultar feriados.")
                }
            }
        }
    }
}
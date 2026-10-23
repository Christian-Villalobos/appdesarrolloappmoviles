package com.example.parcial2.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.repository.ResultadoAuth
import com.example.parcial2.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Todo lo que la pantalla de login necesita saber para dibujarse
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val cargando: Boolean = false
)

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(valor: String) {
        _uiState.update { it.copy(email = valor, error = null) }
    }

    fun onPasswordChange(valor: String) {
        _uiState.update { it.copy(password = valor, error = null) }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val estado = _uiState.value
        _uiState.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            // La base de datos se consulta en un hilo secundario (IO)
            val resultado = withContext(Dispatchers.IO) {
                repository.iniciarSesion(estado.email, estado.password)
            }

            when (resultado) {
                is ResultadoAuth.Exito -> {
                    SessionManager.iniciarSesion(resultado.usuario)
                    _uiState.update { it.copy(cargando = false) }
                    onExito()
                }
                is ResultadoAuth.Error -> {
                    _uiState.update { it.copy(cargando = false, error = resultado.mensaje) }
                }
            }
        }
    }
}
package com.example.parcial2.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.repository.ResultadoAuth
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.data.repository.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Todo lo que la pantalla de login necesita saber para dibujarse
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errorEmail: String? = null,      // error debajo del campo correo
    val errorPassword: String? = null,   // error debajo del campo contraseña
    val error: String? = null,           // error general (por ejemplo, del servidor)
    val cargando: Boolean = false
) {
    // Propiedad calculada (como "porcentaje" en ResultadoQuiz): no se guarda, se obtiene al pedirla
    val puedeEnviar: Boolean
        get() = email.isNotBlank() &&
                password.isNotBlank() &&
                errorEmail == null &&
                errorPassword == null
}

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Al escribir en un campo, se borra su error y el error general
    fun onEmailChange(valor: String) {
        _uiState.update { it.copy(email = valor, errorEmail = null, error = null) }
    }

    fun onPasswordChange(valor: String) {
        _uiState.update { it.copy(password = valor, errorPassword = null, error = null) }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val estado = _uiState.value

        // 1. Validamos ANTES de llamar al servidor (así no gastamos una petición inútil)
        val errorEmail = Validaciones.validarEmail(estado.email)
        val errorPassword = if (estado.password.isBlank()) "Ingresa tu contraseña" else null

        if (errorEmail != null || errorPassword != null) {
            _uiState.update { it.copy(errorEmail = errorEmail, errorPassword = errorPassword) }
            return
        }

        // 2. Si todo es válido, consultamos al servidor
        _uiState.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            val resultado = repository.iniciarSesion(estado.email, estado.password)

            when (resultado) {
                is ResultadoAuth.Exito -> {
                    SessionManager.iniciarSesion(resultado.usuario, resultado.token)
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
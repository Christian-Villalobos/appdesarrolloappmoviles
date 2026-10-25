package com.example.parcial2.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.repository.ResultadoAuth
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.data.repository.Validaciones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RegistroUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val email: String = "",
    val password: String = "",
    val confirmacion: String = "",
    val errorNombres: String? = null,
    val errorApellidos: String? = null,
    val errorEmail: String? = null,
    val errorPassword: String? = null,
    val errorConfirmacion: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

class RegistroViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    // Al escribir en un campo, se borra su error (y el error general)
    fun onNombresChange(valor: String) {
        _uiState.update { it.copy(nombres = valor, errorNombres = null, errorGeneral = null) }
    }

    fun onApellidosChange(valor: String) {
        _uiState.update { it.copy(apellidos = valor, errorApellidos = null, errorGeneral = null) }
    }

    fun onEmailChange(valor: String) {
        _uiState.update { it.copy(email = valor, errorEmail = null, errorGeneral = null) }
    }

    fun onPasswordChange(valor: String) {
        _uiState.update { it.copy(password = valor, errorPassword = null, errorGeneral = null) }
    }

    fun onConfirmacionChange(valor: String) {
        _uiState.update { it.copy(confirmacion = valor, errorConfirmacion = null, errorGeneral = null) }
    }

    fun registrar(onExito: () -> Unit) {
        val estado = _uiState.value

        // 1. Validamos cada campo por separado, para mostrar cada error en su lugar
        val errorNombres = Validaciones.validarNombre(estado.nombres, "Los nombres")
        val errorApellidos = Validaciones.validarNombre(estado.apellidos, "Los apellidos")
        val errorEmail = Validaciones.validarEmail(estado.email)
        val errorPassword = Validaciones.validarPassword(estado.password)
        val errorConfirmacion = Validaciones.validarConfirmacion(estado.password, estado.confirmacion)

        val hayErrores = listOf(
            errorNombres, errorApellidos, errorEmail, errorPassword, errorConfirmacion
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorEmail = errorEmail,
                    errorPassword = errorPassword,
                    errorConfirmacion = errorConfirmacion
                )
            }
            return
        }

        // 2. Si todo es válido, registramos en la base (en un hilo secundario)
        _uiState.update { it.copy(cargando = true, errorGeneral = null) }

        viewModelScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                repository.registrar(
                    nombres = estado.nombres,
                    apellidos = estado.apellidos,
                    email = estado.email,
                    password = estado.password,
                    confirmacion = estado.confirmacion
                )
            }

            when (resultado) {
                is ResultadoAuth.Exito -> {
                    SessionManager.iniciarSesion(resultado.usuario)
                    _uiState.update { it.copy(cargando = false) }
                    onExito()
                }
                is ResultadoAuth.Error -> {
                    _uiState.update { it.copy(cargando = false, errorGeneral = resultado.mensaje) }
                }
            }
        }
    }
}
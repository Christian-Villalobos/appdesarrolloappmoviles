package com.example.parcial2.data.local

import com.example.parcial2.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    // Versión privada y modificable: solo SessionManager puede cambiarla
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)

    // Versión pública y de solo lectura: el resto de la app solo puede observarla
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    // Token JWT para autenticar peticiones contra el microservicio Spring Boot
    var token: String? = null
        private set

    fun iniciarSesion(usuario: Usuario, token: String? = null) {
        _usuarioActual.value = usuario
        this.token = token
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        token = null
    }
}
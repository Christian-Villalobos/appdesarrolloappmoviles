package com.example.parcial2.data.repository

import com.example.parcial2.domain.model.Usuario

// Resultado de registrar o iniciar sesión: o salió bien, o salió mal con un mensaje
sealed class ResultadoAuth {
    data class Exito(val usuario: Usuario) : ResultadoAuth()
    data class Error(val mensaje: String) : ResultadoAuth()
}
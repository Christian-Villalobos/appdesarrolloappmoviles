package com.example.parcial2.data.repository

// Cada función devuelve un mensaje de error, o null si el dato es válido
object Validaciones {

    private val REGEX_EMAIL = Regex("^[A-Za-z0-9._%+-]+@carabineros\\.cl$")

    fun validarNombre(valor: String, campo: String): String? =
        if (valor.trim().length < 3) "$campo debe tener al menos 3 caracteres" else null

    fun validarEmail(email: String): String? {
        val limpio = email.trim().lowercase()
        return when {
            limpio.isEmpty() -> "Ingresa tu correo"
            !REGEX_EMAIL.matches(limpio) -> "Ingresa un correo válido que termine en @carabineros.cl"
            else -> null
        }
    }

    fun validarPassword(password: String): String? = when {
        password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
        !password.any { it.isUpperCase() } -> "La contraseña debe incluir al menos una mayúscula"
        !password.any { it.isDigit() } -> "La contraseña debe incluir al menos un número"
        else -> null
    }

    fun validarConfirmacion(password: String, confirmacion: String): String? =
        if (password != confirmacion) "Las contraseñas no coinciden" else null
}
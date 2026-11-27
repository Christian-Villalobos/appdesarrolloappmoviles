package com.example.parcial2.data.repository

// Cada función devuelve un mensaje de error, o null si el dato es válido
object Validaciones {

    private val REGEX_EMAIL = Regex("^[A-Za-z0-9._%+-]+@carabineros\\.cl$")

    // \p{L} significa "cualquier letra" (incluye tildes y ñ). También permitimos espacios.
    private val REGEX_NOMBRE = Regex("^[\\p{L} ]+$")

    fun validarNombre(valor: String, campo: String): String? {
        val limpio = valor.trim()
        return when {
            limpio.length < 3 -> "El campo $campo debe tener al menos 3 caracteres"
            !REGEX_NOMBRE.matches(limpio) -> "El campo $campo solo puede contener letras y espacios"
            else -> null
        }
    }

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

    fun validarConfirmacion(password: String, confirmacion: String): String? = when {
        confirmacion.isEmpty() -> "Confirma tu contraseña"
        password != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }
}
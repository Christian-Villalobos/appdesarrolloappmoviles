package com.example.parcial2.data.repository

import com.example.parcial2.data.local.dao.UserDao
import com.example.parcial2.domain.model.Usuario
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    fun registrar(
        nombres: String,
        apellidos: String,
        email: String,
        password: String,
        confirmacion: String
    ): ResultadoAuth {
        // Se queda con el primer error que encuentre; si no hay ninguno, queda en null
        val error = Validaciones.validarNombre(nombres, "Nombres")
            ?: Validaciones.validarNombre(apellidos, "Apellidos")
            ?: Validaciones.validarEmail(email)
            ?: Validaciones.validarPassword(password)
            ?: Validaciones.validarConfirmacion(password, confirmacion)

        if (error != null) return ResultadoAuth.Error(error)

        val emailLimpio = email.trim().lowercase()

        if (userDao.existeEmail(emailLimpio)) {
            return ResultadoAuth.Error("Ya existe una cuenta con ese correo")
        }

        val id = userDao.insertar(
            nombres = nombres.trim(),
            apellidos = apellidos.trim(),
            email = emailLimpio,
            passwordHash = encriptar(password)
        )
        if (id == -1L) return ResultadoAuth.Error("No se pudo crear la cuenta")

        return ResultadoAuth.Exito(
            Usuario(id.toInt(), nombres.trim(), apellidos.trim(), emailLimpio)
        )
    }

    fun iniciarSesion(email: String, password: String): ResultadoAuth {
        if (email.isBlank() || password.isBlank()) {
            return ResultadoAuth.Error("Completa todos los campos")
        }

        val usuario = userDao.autenticar(email.trim().lowercase(), encriptar(password))

        return if (usuario != null) {
            ResultadoAuth.Exito(usuario)
        } else {
            ResultadoAuth.Error("Correo o contraseña incorrectos")
        }
    }

    // Convierte la contraseña en un "hash" SHA-256: un texto de 64 caracteres
    private fun encriptar(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
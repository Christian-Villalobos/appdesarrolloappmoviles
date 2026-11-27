package com.example.parcial2.data.repository

import com.example.parcial2.data.remote.BackendApiService
import com.example.parcial2.data.remote.dto.LoginRequestDto
import com.example.parcial2.data.remote.dto.RegisterRequestDto
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class UserRepository(
    private val api: BackendApiService = BackendApiService.instancia
) {

    suspend fun registrar(
        nombres: String,
        apellidos: String,
        email: String,
        password: String,
        confirmacion: String
    ): ResultadoAuth = withContext(Dispatchers.IO) {
        val error = Validaciones.validarNombre(nombres, "Nombres")
            ?: Validaciones.validarNombre(apellidos, "Apellidos")
            ?: Validaciones.validarEmail(email)
            ?: Validaciones.validarPassword(password)
            ?: Validaciones.validarConfirmacion(password, confirmacion)

        if (error != null) return@withContext ResultadoAuth.Error(error)

        try {
            val respuesta = api.register(
                RegisterRequestDto(
                    nombres = nombres.trim(),
                    apellidos = apellidos.trim(),
                    email = email.trim().lowercase(),
                    password = password
                )
            )
            ResultadoAuth.Exito(
                usuario = respuesta.usuario.toDomain(),
                token = respuesta.token
            )
        } catch (e: HttpException) {
            ResultadoAuth.Error(mensajeDeHttp(e))
        } catch (_: IOException) {
            ResultadoAuth.Error("Sin conexión con el servidor. Verifica tu red o que Spring Boot esté activo.")
        } catch (e: Exception) {
            ResultadoAuth.Error(e.message ?: "No se pudo crear la cuenta")
        }
    }

    suspend fun iniciarSesion(
        email: String,
        password: String
    ): ResultadoAuth = withContext(Dispatchers.IO) {
        if (email.isBlank() || password.isBlank()) {
            return@withContext ResultadoAuth.Error("Completa todos los campos")
        }

        try {
            val respuesta = api.login(
                LoginRequestDto(
                    email = email.trim().lowercase(),
                    password = password
                )
            )
            ResultadoAuth.Exito(
                usuario = respuesta.usuario.toDomain(),
                token = respuesta.token
            )
        } catch (e: HttpException) {
            ResultadoAuth.Error(mensajeDeHttp(e))
        } catch (_: IOException) {
            ResultadoAuth.Error("Sin conexión con el servidor. Verifica tu red o que Spring Boot esté activo.")
        } catch (e: Exception) {
            ResultadoAuth.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    private fun mensajeDeHttp(e: HttpException): String {
        val body = e.response()?.errorBody()?.string()
        if (!body.isNullOrBlank()) {
            try {
                val json = JsonParser.parseString(body).asJsonObject
                val msg = json.get("error")?.asString ?: json.get("message")?.asString
                if (!msg.isNullOrBlank()) return msg
            } catch (_: Exception) {
                // Si el cuerpo no es JSON, usamos el mapeo por código HTTP
            }
        }

        return when (e.code()) {
            400 -> "Datos inválidos"
            401 -> "Correo o contraseña incorrectos"
            409 -> "Ya existe una cuenta con ese correo"
            in 500..599 -> "Error interno en el servidor (${e.code()})"
            else -> "Error del servidor (${e.code()})"
        }
    }
}

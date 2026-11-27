package com.example.parcial2.data.repository

import com.example.parcial2.data.remote.BackendApiService
import com.example.parcial2.data.remote.dto.AuthResponseDto
import com.example.parcial2.data.remote.dto.UsuarioDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UserRepositoryTest {

    private val api: BackendApiService = mockk()
    private val repository = UserRepository(api)

    @Test
    fun `iniciarSesion exitoso devuelve usuario y token`() = runTest {
        val dto = AuthResponseDto(
            token = "jwt-123",
            usuario = UsuarioDto(1, "Juan", "Pérez", "juan.perez@carabineros.cl")
        )
        coEvery { api.login(any()) } returns dto

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "Carabinero1")

        assertTrue(resultado is ResultadoAuth.Exito)
        val exito = resultado as ResultadoAuth.Exito
        assertEquals("jwt-123", exito.token)
        assertEquals("Juan", exito.usuario.nombres)
    }

    @Test
    fun `iniciarSesion con 401 devuelve mensaje de credenciales incorrectas`() = runTest {
        val errorBody = """{"error":"Credenciales inválidas"}"""
            .toResponseBody("application/json".toMediaType())
        coEvery { api.login(any()) } throws HttpException(Response.error<Any>(401, errorBody))

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "MalaClave1")

        assertTrue(resultado is ResultadoAuth.Error)
        assertEquals("Credenciales inválidas", (resultado as ResultadoAuth.Error).mensaje)
    }

    @Test
    fun `iniciarSesion sin red devuelve error de conexion`() = runTest {
        coEvery { api.login(any()) } throws IOException("Timeout")

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "Carabinero1")

        assertTrue(resultado is ResultadoAuth.Error)
        assertTrue((resultado as ResultadoAuth.Error).mensaje.contains("Sin conexión"))
    }
}
package com.example.parcial2.data.repository

import com.example.parcial2.data.remote.dto.AuthResponseDto
import com.example.parcial2.data.remote.dto.LoginRequestDto
import com.example.parcial2.data.remote.dto.UsuarioDto
import com.example.parcial2.fakes.FakeBackendApi
import com.example.parcial2.fakes.crearErrorHttp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class UserRepositoryTest {

    private val api = FakeBackendApi()
    private val repository = UserRepository(api)   // le pasamos el servidor falso

    private fun respuestaAuth() = AuthResponseDto(
        token = "jwt-123",
        usuario = UsuarioDto(1, "Juan", "Pérez", "juan.perez@carabineros.cl")
    )

    // "runTest" permite llamar funciones suspend dentro de una prueba
    @Test
    fun `login exitoso devuelve usuario y token`() = runTest {
        api.onLogin = { respuestaAuth() }

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "Carabinero1")

        assertTrue(resultado is ResultadoAuth.Exito)
        val exito = resultado as ResultadoAuth.Exito
        assertEquals("jwt-123", exito.token)
        assertEquals("Juan", exito.usuario.nombres)
    }

    @Test
    fun `el correo se limpia antes de enviarlo al servidor`() = runTest {
        var enviado: LoginRequestDto? = null
        api.onLogin = { peticion ->
            enviado = peticion
            respuestaAuth()
        }

        repository.iniciarSesion("  Juan.Perez@Carabineros.CL ", "Carabinero1")

        assertEquals("juan.perez@carabineros.cl", enviado?.email)
    }

    @Test
    fun `campos vacios devuelven error sin llamar al servidor`() = runTest {
        val resultado = repository.iniciarSesion("", "Carabinero1")

        assertTrue(resultado is ResultadoAuth.Error)
        assertEquals("Completa todos los campos", (resultado as ResultadoAuth.Error).mensaje)
        assertEquals(0, api.llamadasLogin)
    }

    @Test
    fun `error 401 muestra el mensaje que envia el servidor`() = runTest {
        api.onLogin = { throw crearErrorHttp(401, """{"error":"Credenciales inválidas"}""") }

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "MalaClave1")

        assertTrue(resultado is ResultadoAuth.Error)
        assertEquals("Credenciales inválidas", (resultado as ResultadoAuth.Error).mensaje)
    }

    @Test
    fun `error 401 sin cuerpo usa el mensaje por defecto`() = runTest {
        api.onLogin = { throw crearErrorHttp(401) }

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "MalaClave1")

        assertEquals("Correo o contraseña incorrectos", (resultado as ResultadoAuth.Error).mensaje)
    }

    @Test
    fun `error 500 informa error interno del servidor`() = runTest {
        api.onLogin = { throw crearErrorHttp(500) }

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "Carabinero1")

        assertEquals("Error interno en el servidor (500)", (resultado as ResultadoAuth.Error).mensaje)
    }

    @Test
    fun `sin internet informa falta de conexion`() = runTest {
        api.onLogin = { throw IOException("Timeout") }

        val resultado = repository.iniciarSesion("juan.perez@carabineros.cl", "Carabinero1")

        assertTrue(resultado is ResultadoAuth.Error)
        assertTrue((resultado as ResultadoAuth.Error).mensaje.contains("Sin conexión"))
    }

    @Test
    fun `registrar con datos invalidos devuelve el error de validacion`() = runTest {
        val resultado = repository.registrar(
            nombres = "Jo",
            apellidos = "Pérez",
            email = "juan@carabineros.cl",
            password = "Carabinero1",
            confirmacion = "Carabinero1"
        )

        assertTrue(resultado is ResultadoAuth.Error)
        assertEquals(
            "El campo Nombres debe tener al menos 3 caracteres",
            (resultado as ResultadoAuth.Error).mensaje
        )
    }
}
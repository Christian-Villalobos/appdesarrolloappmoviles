package com.example.parcial2.ui.screens.auth

import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.remote.dto.AuthResponseDto
import com.example.parcial2.data.remote.dto.UsuarioDto
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.fakes.FakeBackendApi
import com.example.parcial2.fakes.crearErrorHttp
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val api = FakeBackendApi()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LoginViewModel(UserRepository(api))
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        SessionManager.cerrarSesion()   // SessionManager es global: lo dejamos limpio para la siguiente prueba
    }

    @Test
    fun `al inicio el boton esta bloqueado`() {
        assertFalse(viewModel.uiState.value.puedeEnviar)
    }

    @Test
    fun `con correo y contrasena escritos el boton se habilita`() {
        viewModel.onEmailChange("juan.perez@carabineros.cl")
        viewModel.onPasswordChange("Carabinero1")

        assertTrue(viewModel.uiState.value.puedeEnviar)
    }

    @Test
    fun `correo invalido muestra error, bloquea el boton y no llama al servidor`() {
        viewModel.onEmailChange("juan@gmail.com")
        viewModel.onPasswordChange("Carabinero1")
        var huboExito = false

        viewModel.iniciarSesion { huboExito = true }

        val estado = viewModel.uiState.value
        assertEquals("Ingresa un correo válido que termine en @carabineros.cl", estado.errorEmail)
        assertFalse(estado.puedeEnviar)
        assertFalse(estado.cargando)
        assertFalse(huboExito)
        assertEquals(0, api.llamadasLogin)
    }

    @Test
    fun `contrasena vacia muestra error en ese campo`() {
        viewModel.onEmailChange("juan.perez@carabineros.cl")
        viewModel.onPasswordChange("")

        viewModel.iniciarSesion { }

        assertEquals("Ingresa tu contraseña", viewModel.uiState.value.errorPassword)
        assertEquals(0, api.llamadasLogin)
    }

    @Test
    fun `escribir en un campo borra su error`() {
        viewModel.onEmailChange("juan@gmail.com")
        viewModel.onPasswordChange("Carabinero1")
        viewModel.iniciarSesion { }
        assertTrue(viewModel.uiState.value.errorEmail != null)

        viewModel.onEmailChange("juan.perez@carabineros.cl")

        assertNull(viewModel.uiState.value.errorEmail)
        assertTrue(viewModel.uiState.value.puedeEnviar)
    }

    // Esta prueba espera la respuesta del repositorio, que corre en otro hilo (Dispatchers.IO)
    @Test
    fun `login correcto guarda la sesion y avisa el exito`() = runTest {
        api.onLogin = {
            AuthResponseDto("jwt-123", UsuarioDto(1, "Juan", "Pérez", "juan.perez@carabineros.cl"))
        }
        viewModel.onEmailChange("juan.perez@carabineros.cl")
        viewModel.onPasswordChange("Carabinero1")

        // CompletableDeferred es un "semáforo": await() espera hasta que onExito lo active
        val exito = CompletableDeferred<Unit>()
        viewModel.iniciarSesion { exito.complete(Unit) }
        exito.await()

        assertEquals("Juan", SessionManager.usuarioActual.value?.nombres)
        assertEquals("jwt-123", SessionManager.token)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun `login con credenciales incorrectas muestra el error y no guarda sesion`() = runTest {
        api.onLogin = { throw crearErrorHttp(401, """{"error":"Credenciales inválidas"}""") }
        viewModel.onEmailChange("juan.perez@carabineros.cl")
        viewModel.onPasswordChange("MalaClave1")

        viewModel.iniciarSesion { }
        // Esperamos hasta que aparezca el error general en el estado
        val estado = viewModel.uiState.first { it.error != null }

        assertEquals("Credenciales inválidas", estado.error)
        assertFalse(estado.cargando)
        assertNull(SessionManager.usuarioActual.value)
    }
}
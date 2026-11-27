package com.example.parcial2.ui.screens.home

import com.example.parcial2.data.remote.dto.FeriadoDto
import com.example.parcial2.fakes.FakeFeriadosApi
import com.example.parcial2.fakes.crearErrorHttp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val api = FakeFeriadosApi()

    // Reemplazamos el "hilo principal" de Android por uno de prueba que ejecuta todo al instante
    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // Crea un feriado "dias" días a partir de hoy (negativo = ya pasó)
    private fun feriado(dias: Long, nombre: String) = FeriadoDto(
        date = LocalDate.now().plusDays(dias).toString(),
        localName = nombre
    )

    @Test
    fun `muestra solo los proximos feriados, ordenados y maximo tres`() {
        api.respuesta = {
            listOf(
                feriado(-10, "Pasado"),
                feriado(30, "D"),
                feriado(5, "B"),
                feriado(1, "A"),
                feriado(60, "E"),
                feriado(10, "C")
            )
        }

        val viewModel = HomeViewModel(api)
        val estado = viewModel.uiState.value

        assertFalse(estado.cargandoFeriados)
        assertNull(estado.errorFeriados)
        assertEquals(listOf("A", "B", "C"), estado.feriados.map { it.localName })
    }

    @Test
    fun `un feriado que es hoy se considera proximo`() {
        api.respuesta = { listOf(feriado(0, "Hoy")) }

        val viewModel = HomeViewModel(api)

        assertEquals(listOf("Hoy"), viewModel.uiState.value.feriados.map { it.localName })
    }

    @Test
    fun `si no quedan feriados la lista queda vacia sin error`() {
        api.respuesta = { listOf(feriado(-5, "Pasado")) }

        val viewModel = HomeViewModel(api)
        val estado = viewModel.uiState.value

        assertTrue(estado.feriados.isEmpty())
        assertNull(estado.errorFeriados)
        assertFalse(estado.cargandoFeriados)
    }

    @Test
    fun `sin internet muestra error de conexion`() {
        api.respuesta = { throw IOException("sin red") }

        val viewModel = HomeViewModel(api)
        val estado = viewModel.uiState.value

        assertEquals("Sin conexión para cargar feriados legales.", estado.errorFeriados)
        assertFalse(estado.cargandoFeriados)
    }

    @Test
    fun `error del servidor incluye el codigo HTTP`() {
        api.respuesta = { throw crearErrorHttp(404) }

        val viewModel = HomeViewModel(api)

        assertEquals("No se pudo consultar la API (404).", viewModel.uiState.value.errorFeriados)
    }

    @Test
    fun `reintentar despues de un error carga los feriados`() {
        api.respuesta = { throw IOException("sin red") }
        val viewModel = HomeViewModel(api)
        assertEquals("Sin conexión para cargar feriados legales.", viewModel.uiState.value.errorFeriados)

        // Vuelve internet: ahora el servidor falso responde bien
        api.respuesta = { listOf(feriado(3, "Navidad")) }
        viewModel.cargarFeriados()

        val estado = viewModel.uiState.value
        assertNull(estado.errorFeriados)
        assertEquals(listOf("Navidad"), estado.feriados.map { it.localName })
    }
}
package com.example.parcial2.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    // ---------- Nombres ----------

    @Test
    fun `nombre valido no devuelve error`() {
        assertNull(Validaciones.validarNombre("Juan Carlos", "Nombres"))
        assertNull(Validaciones.validarNombre("María José", "Nombres"))   // las tildes se permiten
    }

    @Test
    fun `nombre muy corto devuelve error`() {
        assertEquals(
            "El campo Nombres debe tener al menos 3 caracteres",
            Validaciones.validarNombre("Jo", "Nombres")
        )
    }

    @Test
    fun `nombre con numeros o simbolos devuelve error`() {
        assertNotNull(Validaciones.validarNombre("Juan123", "Nombres"))
        assertNotNull(Validaciones.validarNombre("Pérez!", "Apellidos"))
    }

    @Test
    fun `los espacios sobrantes se limpian antes de validar`() {
        assertNull(Validaciones.validarNombre("  Juan  ", "Nombres"))
        assertNotNull(Validaciones.validarNombre("  Jo  ", "Nombres"))   // sin espacios queda "Jo"
    }

    // ---------- Correo ----------

    @Test
    fun `correo institucional valido no devuelve error`() {
        assertNull(Validaciones.validarEmail("juan.perez@carabineros.cl"))
        assertNull(Validaciones.validarEmail("  Juan.Perez@Carabineros.CL  "))   // se limpia y pasa a minúsculas
    }

    @Test
    fun `correo vacio pide ingresar el correo`() {
        assertEquals("Ingresa tu correo", Validaciones.validarEmail(""))
    }

    @Test
    fun `correo con otro dominio devuelve error`() {
        assertNotNull(Validaciones.validarEmail("juan@gmail.com"))
        assertNotNull(Validaciones.validarEmail("juan@carabineros.com"))
        assertNotNull(Validaciones.validarEmail("@carabineros.cl"))
    }

    // ---------- Contraseña ----------

    @Test
    fun `contrasena valida no devuelve error`() {
        assertNull(Validaciones.validarPassword("Carabinero1"))
        assertNull(Validaciones.validarPassword("Ab1def"))   // justo 6 caracteres
    }

    @Test
    fun `contrasena corta devuelve error de largo`() {
        assertEquals(
            "La contraseña debe tener al menos 6 caracteres",
            Validaciones.validarPassword("Ab1")
        )
    }

    @Test
    fun `contrasena sin mayuscula devuelve error`() {
        assertEquals(
            "La contraseña debe incluir al menos una mayúscula",
            Validaciones.validarPassword("carabinero1")
        )
    }

    @Test
    fun `contrasena sin numero devuelve error`() {
        assertEquals(
            "La contraseña debe incluir al menos un número",
            Validaciones.validarPassword("Carabinero")
        )
    }

    // ---------- Confirmación ----------

    @Test
    fun `confirmacion igual a la contrasena no devuelve error`() {
        assertNull(Validaciones.validarConfirmacion("Carabinero1", "Carabinero1"))
    }

    @Test
    fun `confirmacion distinta devuelve error`() {
        assertEquals(
            "Las contraseñas no coinciden",
            Validaciones.validarConfirmacion("Carabinero1", "OtraClave1")
        )
    }

    @Test
    fun `confirmacion vacia pide confirmar`() {
        assertEquals("Confirma tu contraseña", Validaciones.validarConfirmacion("Carabinero1", ""))
    }
}
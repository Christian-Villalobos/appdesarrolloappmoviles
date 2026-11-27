package com.example.parcial2.domain.model

/**
 * Representa un archivo de material de estudio adjunto a una unidad.
 */
data class Archivo(
    val id: Int = 0,
    val unidadId: Int,
    val nombre: String,
    val rutaInterna: String,
    val tamanoBytes: Long,
    val fecha: Long
)

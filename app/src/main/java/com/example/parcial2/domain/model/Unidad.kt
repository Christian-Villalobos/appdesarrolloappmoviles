package com.example.parcial2.domain.model

data class Unidad(
    val id: Int,
    val cursoId: Int,   // Este campo hace referencia a que curso pertenece
    val nombre: String
)
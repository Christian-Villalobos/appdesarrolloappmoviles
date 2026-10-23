package com.example.parcial2.domain.model

data class Quiz(
    val id: Int,
    val unidadId: Int,  // a qué unidad pertenece
    val titulo: String
)
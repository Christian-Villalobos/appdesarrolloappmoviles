package com.example.parcial2.data.remote.dto

import com.google.gson.annotations.SerializedName

// Un feriado tal como lo entrega la API. Solo declaramos los campos que usamos:
// Gson ignora el resto del JSON sin problema.
data class FeriadoDto(
    @SerializedName("date") val date: String,           // "2026-12-08"
    @SerializedName("localName") val localName: String  // "Inmaculada Concepción"
)
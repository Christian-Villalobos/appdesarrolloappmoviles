package com.example.parcial2.fakes

import com.example.parcial2.data.remote.FeriadosApiService
import com.example.parcial2.data.remote.dto.FeriadoDto

// API de feriados "de mentira": cada prueba decide qué devuelve o qué error lanza
class FakeFeriadosApi : FeriadosApiService {

    var respuesta: suspend () -> List<FeriadoDto> = { emptyList() }

    override suspend fun obtenerFeriados(anio: Int, pais: String): List<FeriadoDto> = respuesta()
}
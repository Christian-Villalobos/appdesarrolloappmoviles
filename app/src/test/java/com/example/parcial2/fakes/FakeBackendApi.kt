package com.example.parcial2.fakes

import com.example.parcial2.data.remote.BackendApiService
import com.example.parcial2.data.remote.dto.AuthResponseDto
import com.example.parcial2.data.remote.dto.CursoDetalleResponseDto
import com.example.parcial2.data.remote.dto.CursoDto
import com.example.parcial2.data.remote.dto.GuardarResultadoRequestDto
import com.example.parcial2.data.remote.dto.HistorialItemDto
import com.example.parcial2.data.remote.dto.LoginRequestDto
import com.example.parcial2.data.remote.dto.QuizPreguntasResponseDto
import com.example.parcial2.data.remote.dto.RegisterRequestDto
import com.example.parcial2.data.remote.dto.ResultadoQuizDto

// Servidor "de mentira": solo login está implementado; lo demás falla si alguien lo llama
class FakeBackendApi : BackendApiService {

    // Cada prueba define qué responde el login (o qué error lanza)
    var onLogin: suspend (LoginRequestDto) -> AuthResponseDto = { error("onLogin no configurado") }

    // Cuántas veces se llamó al login: sirve para comprobar que NO se llamó
    var llamadasLogin = 0
        private set

    override suspend fun login(request: LoginRequestDto): AuthResponseDto {
        llamadasLogin++
        return onLogin(request)
    }

    override suspend fun register(request: RegisterRequestDto): AuthResponseDto = throw NotImplementedError()
    override suspend fun obtenerCursos(): List<CursoDto> = throw NotImplementedError()
    override suspend fun obtenerCursoDetalle(cursoId: Int): CursoDetalleResponseDto = throw NotImplementedError()
    override suspend fun obtenerQuizPreguntas(quizId: Int): QuizPreguntasResponseDto = throw NotImplementedError()
    override suspend fun guardarResultado(request: GuardarResultadoRequestDto): ResultadoQuizDto = throw NotImplementedError()
    override suspend fun obtenerHistorial(): List<HistorialItemDto> = throw NotImplementedError()
}
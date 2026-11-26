package com.example.parcial2.data.remote.dto

import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.HistorialItem
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.ResultadoQuiz
import com.example.parcial2.domain.model.Unidad
import com.example.parcial2.domain.model.UnidadConQuizzes
import com.example.parcial2.domain.model.Usuario
import com.google.gson.annotations.SerializedName

// --- Autenticación ---

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class UsuarioDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("email") val email: String
) {
    fun toDomain(): Usuario = Usuario(
        id = id,
        nombres = nombres,
        apellidos = apellidos,
        email = email
    )
}

data class AuthResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("usuario") val usuario: UsuarioDto
)

// --- Cursos, Unidades, Quizzes y Preguntas ---

data class CursoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String
) {
    fun toDomain(): Curso = Curso(
        id = id,
        nombre = nombre,
        descripcion = descripcion
    )
}

data class UnidadDto(
    @SerializedName("id") val id: Int,
    @SerializedName("cursoId") val cursoId: Int,
    @SerializedName("nombre") val nombre: String
) {
    fun toDomain(): Unidad = Unidad(
        id = id,
        cursoId = cursoId,
        nombre = nombre
    )
}

data class QuizDto(
    @SerializedName("id") val id: Int,
    @SerializedName("unidadId") val unidadId: Int,
    @SerializedName("titulo") val titulo: String
) {
    fun toDomain(): Quiz = Quiz(
        id = id,
        unidadId = unidadId,
        titulo = titulo
    )
}

data class UnidadConQuizzesDto(
    @SerializedName("unidad") val unidad: UnidadDto,
    @SerializedName("quizzes") val quizzes: List<QuizDto>
) {
    fun toDomain(): UnidadConQuizzes = UnidadConQuizzes(
        unidad = unidad.toDomain(),
        quizzes = quizzes.map { it.toDomain() }
    )
}

data class CursoDetalleResponseDto(
    @SerializedName("curso") val curso: CursoDto,
    @SerializedName("unidades") val unidades: List<UnidadConQuizzesDto>
)

data class PreguntaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("quizId") val quizId: Int,
    @SerializedName("enunciado") val enunciado: String,
    @SerializedName("opciones") val opciones: List<String>,
    @SerializedName("indiceCorrecto") val indiceCorrecto: Int,
    @SerializedName("justificacionLegal") val justificacionLegal: String
) {
    fun toDomain(): Pregunta = Pregunta(
        id = id,
        quizId = quizId,
        enunciado = enunciado,
        opciones = opciones,
        indiceCorrecto = indiceCorrecto,
        justificacionLegal = justificacionLegal
    )
}

data class QuizPreguntasResponseDto(
    @SerializedName("quiz") val quiz: QuizDto,
    @SerializedName("preguntas") val preguntas: List<PreguntaDto>
)

// --- Resultados e Historial ---

data class GuardarResultadoRequestDto(
    @SerializedName("quizId") val quizId: Int,
    @SerializedName("aciertos") val aciertos: Int,
    @SerializedName("totalPreguntas") val totalPreguntas: Int
)

data class ResultadoQuizDto(
    @SerializedName("id") val id: Int,
    @SerializedName("usuarioId") val usuarioId: Int,
    @SerializedName("quizId") val quizId: Int,
    @SerializedName("aciertos") val aciertos: Int,
    @SerializedName("totalPreguntas") val totalPreguntas: Int,
    @SerializedName("fecha") val fecha: Long
) {
    fun toDomain(): ResultadoQuiz = ResultadoQuiz(
        id = id,
        usuarioId = usuarioId,
        quizId = quizId,
        aciertos = aciertos,
        totalPreguntas = totalPreguntas,
        fecha = fecha
    )
}

data class HistorialItemDto(
    @SerializedName("resultado") val resultado: ResultadoQuizDto,
    @SerializedName("tituloQuiz") val tituloQuiz: String
) {
    fun toDomain(): HistorialItem = HistorialItem(
        resultado = resultado.toDomain(),
        tituloQuiz = tituloQuiz
    )
}

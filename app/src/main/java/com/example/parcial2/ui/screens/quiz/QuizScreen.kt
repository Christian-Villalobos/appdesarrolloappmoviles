package com.example.parcial2.ui.screens.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial2.domain.model.Pregunta
import com.example.parcial2.domain.model.ResultadoQuiz
import com.example.parcial2.ui.components.BotonPrincipal
import com.example.parcial2.ui.theme.Parcial2Theme
import com.example.parcial2.ui.theme.VerdeExito

// Cómo se dibuja cada alternativa
private enum class EstadoOpcion { NORMAL, SELECCIONADA, CORRECTA, INCORRECTA }

// Versión "conectada" al ViewModel
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QuizContent(
        uiState = uiState,
        onSeleccionar = viewModel::seleccionar,
        onResponder = viewModel::responder,
        onSiguiente = viewModel::siguiente,
        onReintentar = viewModel::reiniciar,
        onVolver = onVolver,
        modifier = modifier
    )
}

// Versión "pura": solo dibuja
@Composable
fun QuizContent(
    uiState: QuizUiState,
    onSeleccionar: (Int) -> Unit,
    onResponder: () -> Unit,
    onSiguiente: () -> Unit,
    onReintentar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pregunta = uiState.preguntaActual

    when {
        uiState.cargando -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.resultado != null -> {
            PantallaResultado(
                resultado = uiState.resultado,
                onReintentar = onReintentar,
                onVolver = onVolver,
                modifier = modifier
            )
        }

        pregunta == null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Este quiz aún no tiene preguntas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            VistaPregunta(
                uiState = uiState,
                pregunta = pregunta,
                onSeleccionar = onSeleccionar,
                onResponder = onResponder,
                onSiguiente = onSiguiente,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun VistaPregunta(
    uiState: QuizUiState,
    pregunta: Pregunta,
    onSeleccionar: (Int) -> Unit,
    onResponder: () -> Unit,
    onSiguiente: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = uiState.preguntas.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = uiState.titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pregunta ${uiState.indiceActual + 1} de $total",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { (uiState.indiceActual + 1) / total.toFloat() },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = pregunta.enunciado,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Una tarjeta por alternativa: A, B, C y D
        pregunta.opciones.forEachIndexed { indice, texto ->
            val estado = when {
                uiState.respondida && indice == pregunta.indiceCorrecto -> EstadoOpcion.CORRECTA
                uiState.respondida && indice == uiState.seleccion -> EstadoOpcion.INCORRECTA
                indice == uiState.seleccion -> EstadoOpcion.SELECCIONADA
                else -> EstadoOpcion.NORMAL
            }

            OpcionRespuesta(
                letra = 'A' + indice,
                texto = texto,
                estado = estado,
                onClick = { onSeleccionar(indice) }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Retroalimentación con la justificación legal (solo después de responder)
        if (uiState.respondida) {
            Spacer(modifier = Modifier.height(8.dp))
            TarjetaRetroalimentacion(
                esCorrecta = uiState.seleccion == pregunta.indiceCorrecto,
                pregunta = pregunta
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        BotonPrincipal(
            texto = when {
                !uiState.respondida -> "Responder"
                uiState.esUltima -> "Ver resultado"
                else -> "Siguiente"
            },
            onClick = if (uiState.respondida) onSiguiente else onResponder,
            habilitado = uiState.respondida || uiState.seleccion != null
        )
    }
}

@Composable
private fun OpcionRespuesta(
    letra: Char,
    texto: String,
    estado: EstadoOpcion,
    onClick: () -> Unit
) {
    val colorBorde = when (estado) {
        EstadoOpcion.NORMAL -> MaterialTheme.colorScheme.outlineVariant
        EstadoOpcion.SELECCIONADA -> MaterialTheme.colorScheme.primary
        EstadoOpcion.CORRECTA -> VerdeExito
        EstadoOpcion.INCORRECTA -> MaterialTheme.colorScheme.error
    }
    val colorFondo = when (estado) {
        EstadoOpcion.CORRECTA -> VerdeExito.copy(alpha = 0.1f)
        EstadoOpcion.INCORRECTA -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(if (estado == EstadoOpcion.NORMAL) 1.dp else 2.dp, colorBorde)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo con la letra de la alternativa
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(colorBorde, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letra.toString(),
                    fontWeight = FontWeight.Bold,
                    color = if (estado == EstadoOpcion.NORMAL) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        Color.White
                    }
                )
            }

            Spacer(modifier = Modifier.padding(horizontal = 6.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TarjetaRetroalimentacion(
    esCorrecta: Boolean,
    pregunta: Pregunta
) {
    val color = if (esCorrecta) VerdeExito else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (esCorrecta) "¡Correcto!" else "Incorrecto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            if (!esCorrecta) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Respuesta correcta: ${'A' + pregunta.indiceCorrecto}) ${pregunta.opciones[pregunta.indiceCorrecto]}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Justificación legal",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = pregunta.justificacionLegal,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun PantallaResultado(
    resultado: ResultadoQuiz,
    onReintentar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mensaje = when {
        resultado.porcentaje == 100 -> "¡Excelente! Dominas este contenido."
        resultado.porcentaje >= 60 -> "Buen trabajo. Repasa las justificaciones de las preguntas que fallaste."
        else -> "Sigue practicando. Revisa el material e inténtalo nuevamente."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resultado del quiz",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${resultado.porcentaje}%",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (resultado.porcentaje >= 60) VerdeExito else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Acertaste ${resultado.aciertos} de ${resultado.totalPreguntas} preguntas",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        BotonPrincipal(texto = "Volver al curso", onClick = onVolver)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onReintentar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Reintentar", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizPreguntaPreview() {
    val pregunta = Pregunta(
        id = 1,
        quizId = 1,
        enunciado = "Según el artículo 101 de la Constitución, las Fuerzas de Orden y Seguridad Pública son esencialmente:",
        opciones = listOf(
            "Deliberantes y autónomas",
            "Obedientes y no deliberantes",
            "Autónomas y representativas",
            "Políticas y deliberantes"
        ),
        indiceCorrecto = 1,
        justificacionLegal = "El artículo 101 señala que son esencialmente obedientes y no deliberantes."
    )
    Parcial2Theme {
        QuizContent(
            uiState = QuizUiState(
                titulo = "Quiz: Constitución y Carabineros",
                preguntas = listOf(pregunta),
                seleccion = 0,
                respondida = true,
                cargando = false
            ),
            onSeleccionar = {},
            onResponder = {},
            onSiguiente = {},
            onReintentar = {},
            onVolver = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuizResultadoPreview() {
    Parcial2Theme {
        QuizContent(
            uiState = QuizUiState(
                resultado = ResultadoQuiz(1, 1, 1, aciertos = 2, totalPreguntas = 3, fecha = 0L),
                cargando = false
            ),
            onSeleccionar = {},
            onResponder = {},
            onSiguiente = {},
            onReintentar = {},
            onVolver = {}
        )
    }
}
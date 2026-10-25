package com.example.parcial2.ui.screens.courses

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial2.domain.model.Curso
import com.example.parcial2.domain.model.Quiz
import com.example.parcial2.domain.model.Unidad
import com.example.parcial2.domain.model.UnidadConQuizzes
import com.example.parcial2.ui.theme.Parcial2Theme

// Versión "conectada" al ViewModel
@Composable
fun CursoDetalleScreen(
    viewModel: CursoDetalleViewModel,
    onQuizClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CursoDetalleContent(
        uiState = uiState,
        onQuizClick = onQuizClick,
        modifier = modifier
    )
}

// Versión "pura": solo dibuja
@Composable
fun CursoDetalleContent(
    uiState: CursoDetalleUiState,
    onQuizClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.cargando) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado: nombre y descripción del curso
            item {
                Column {
                    Text(
                        text = uiState.curso?.nombre.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.curso?.descripcion.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.unidades.isEmpty()) {
                item {
                    Text(
                        text = "Este curso aún no tiene unidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Por cada unidad: su título y, debajo, sus quizzes
            uiState.unidades.forEach { grupo ->
                item {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Text(
                            text = grupo.unidad.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider()
                    }
                }

                items(grupo.quizzes) { quiz ->
                    FilaQuiz(quiz = quiz, onClick = { onQuizClick(quiz.id) })
                }
            }
        }
    }
}

@Composable
private fun FilaQuiz(quiz: Quiz, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = quiz.titulo,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CursoDetalleContentPreview() {
    Parcial2Theme {
        CursoDetalleContent(
            uiState = CursoDetalleUiState(
                curso = Curso(1, "Fundamentos Jurídicos", "Marco constitucional y detención."),
                unidades = listOf(
                    UnidadConQuizzes(
                        unidad = Unidad(1, 1, "Marco constitucional e institucional"),
                        quizzes = listOf(Quiz(1, 1, "Quiz: Constitución y Carabineros"))
                    )
                ),
                cargando = false
            ),
            onQuizClick = {}
        )
    }
}
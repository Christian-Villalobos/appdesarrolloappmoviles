package com.example.parcial2.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.domain.model.HistorialItem
import com.example.parcial2.domain.model.ResultadoQuiz
import com.example.parcial2.domain.model.Usuario
import com.example.parcial2.ui.theme.Parcial2Theme
import com.example.parcial2.ui.theme.VerdeExito
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Versión "conectada": viene del ViewModel y del SessionManager
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val usuario by SessionManager.usuarioActual.collectAsStateWithLifecycle()

    // Cada vez que se abre el Perfil se recarga el historial (por si se hizo un quiz nuevo)
    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    PerfilContent(
        usuario = usuario,
        uiState = uiState,
        onCerrarSesion = onCerrarSesion,
        modifier = modifier
    )
}

// Versión "pura": solo dibuja
@Composable
fun PerfilContent(
    usuario: Usuario?,
    uiState: PerfilUiState,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Datos del usuario
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(88.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${usuario?.nombres.orEmpty()} ${usuario?.apellidos.orEmpty()}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = usuario?.email.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (uiState.cargando) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            // Estadísticas
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaEstadistica(
                        valor = uiState.totalIntentos.toString(),
                        etiqueta = "Quizzes realizados",
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaEstadistica(
                        valor = "${uiState.promedio}%",
                        etiqueta = "Promedio",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Historial
            item {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Historial de resultados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider()
                }
            }

            if (uiState.historial.isEmpty()) {
                item {
                    Text(
                        text = "Aún no has realizado ningún quiz. Entra a Cursos para comenzar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(uiState.historial, key = { it.resultado.id }) { item ->
                FilaHistorial(item = item)
            }
        }

        // Cerrar sesión
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(text = "Cerrar sesión", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun TarjetaEstadistica(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun FilaHistorial(item: HistorialItem) {
    val resultado = item.resultado

    Card(
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.tituloQuiz,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatearFecha(resultado.fecha),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${resultado.porcentaje}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (resultado.porcentaje >= 60) VerdeExito else MaterialTheme.colorScheme.error
                )
                Text(
                    text = "${resultado.aciertos}/${resultado.totalPreguntas}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Convierte los milisegundos guardados en la base a un texto como "26/10/2026 15:30"
private fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(Date(milisegundos))

@Preview(showBackground = true)
@Composable
fun PerfilContentPreview() {
    Parcial2Theme {
        PerfilContent(
            usuario = Usuario(1, "Juan", "Pérez", "juan.perez@carabineros.cl"),
            uiState = PerfilUiState(
                historial = listOf(
                    HistorialItem(
                        ResultadoQuiz(1, 1, 1, aciertos = 2, totalPreguntas = 3, fecha = 1_790_000_000_000L),
                        "Quiz: Constitución y Carabineros"
                    ),
                    HistorialItem(
                        ResultadoQuiz(2, 1, 2, aciertos = 1, totalPreguntas = 3, fecha = 1_790_100_000_000L),
                        "Quiz: Detención en flagrancia"
                    )
                ),
                cargando = false
            ),
            onCerrarSesion = {}
        )
    }
}
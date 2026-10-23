package com.example.parcial2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Asignamos cada color institucional a un "rol" de Material 3
private val EsquemaInstitucional = lightColorScheme(
    primary = VerdeInstitucionalOscuro,
    onPrimary = BlancoPuro,
    primaryContainer = VerdeSecundario,
    onPrimaryContainer = BlancoPuro,
    secondary = VerdeSecundario,
    onSecondary = BlancoPuro,
    secondaryContainer = GrisNeutroBordes,
    onSecondaryContainer = GrisOscuroTexto,
    tertiary = DoradoSobrio,
    onTertiary = GrisOscuroTexto,
    background = BlancoFondo,
    onBackground = GrisOscuroTexto,
    surface = BlancoPuro,
    onSurface = GrisOscuroTexto,
    surfaceVariant = GrisNeutroBordes,
    onSurfaceVariant = GrisSecundarioTexto,
    outline = GrisSecundarioTexto,
    outlineVariant = GrisNeutroBordes,
    error = RojoAlerta,
    onError = BlancoPuro
)

@Composable
fun Parcial2Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaInstitucional,
        typography = Typography,
        content = content
    )
}
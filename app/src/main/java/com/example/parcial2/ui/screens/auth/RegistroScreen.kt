package com.example.parcial2.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial2.ui.components.BotonPrincipal
import com.example.parcial2.ui.components.CampoPassword
import com.example.parcial2.ui.components.CampoTexto
import com.example.parcial2.ui.theme.Parcial2Theme

// Versión "conectada" al ViewModel
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel,
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegistroContent(
        uiState = uiState,
        onNombresChange = viewModel::onNombresChange,
        onApellidosChange = viewModel::onApellidosChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmacionChange = viewModel::onConfirmacionChange,
        onRegistrar = { viewModel.registrar(onRegistroExitoso) },
        onVolver = onVolver
    )
}

// Versión "pura": solo dibuja
@Composable
fun RegistroContent(
    uiState: RegistroUiState,
    onNombresChange: (String) -> Unit,
    onApellidosChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmacionChange: (String) -> Unit,
    onRegistrar: () -> Unit,
    onVolver: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Usa tu correo institucional @carabineros.cl. La contraseña debe tener al menos 6 caracteres, una mayúscula y un número.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoTexto(
                value = uiState.nombres,
                onValueChange = onNombresChange,
                label = "Nombres",
                icono = Icons.Default.Person,
                error = uiState.errorNombres
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoTexto(
                value = uiState.apellidos,
                onValueChange = onApellidosChange,
                label = "Apellidos",
                icono = Icons.Default.Person,
                error = uiState.errorApellidos
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoTexto(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Correo Electrónico",
                icono = Icons.Default.Email,
                tipoTeclado = KeyboardType.Email,
                error = uiState.errorEmail
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoPassword(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                error = uiState.errorPassword
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoPassword(
                value = uiState.confirmacion,
                onValueChange = onConfirmacionChange,
                label = "Confirmar Contraseña",
                error = uiState.errorConfirmacion
            )

            if (uiState.errorGeneral != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorGeneral,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonPrincipal(
                texto = "Crear Cuenta",
                onClick = onRegistrar,
                cargando = uiState.cargando
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onVolver) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroContentPreview() {
    Parcial2Theme {
        RegistroContent(
            uiState = RegistroUiState(
                nombres = "Jo",
                errorNombres = "Los nombres debe tener al menos 3 caracteres",
                errorEmail = "Ingresa un correo válido que termine en @carabineros.cl"
            ),
            onNombresChange = {},
            onApellidosChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmacionChange = {},
            onRegistrar = {},
            onVolver = {}
        )
    }
}
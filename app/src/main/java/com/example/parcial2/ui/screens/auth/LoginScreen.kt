package com.example.parcial2.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

// Versión "conectada": obtiene el estado del ViewModel y se lo pasa a LoginContent
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onIngresar = { viewModel.iniciarSesion(onLoginSuccess) }
    )
}

// Versión "pura": solo dibuja lo que recibe, no conoce al ViewModel
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onIngresar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bienvenido a la aplicación",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            CampoTexto(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = "Correo Electrónico",
                icono = Icons.Default.Email,
                tipoTeclado = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoPassword(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = "Contraseña"
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonPrincipal(
                texto = "Ingresar",
                onClick = onIngresar,
                cargando = uiState.cargando
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginContentPreview() {
    Parcial2Theme {
        LoginContent(
            uiState = LoginUiState(error = "Correo o contraseña incorrectos"),
            onEmailChange = {},
            onPasswordChange = {},
            onIngresar = {}
        )
    }
}
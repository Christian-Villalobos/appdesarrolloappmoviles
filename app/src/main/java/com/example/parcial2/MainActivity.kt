package com.example.parcial2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.parcial2.ui.LoginScreen
import com.example.parcial2.ui.theme.Parcial2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Parcial2Theme {
                LoginScreen(
                    onLoginSuccess = { email ->
                        // Acción al iniciar sesión correctamente
                    }
                )
            }
        }
    }
}

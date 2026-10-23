package com.example.parcial2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.local.dao.UserDao
import com.example.parcial2.data.local.db.DbHelper
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.ui.screens.auth.LoginScreen
import com.example.parcial2.ui.screens.auth.LoginViewModel
import com.example.parcial2.ui.theme.Parcial2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Armamos las capas de datos: Helper -> DAO -> Repositorio
        val dbHelper = DbHelper(applicationContext)
        val userRepository = UserRepository(UserDao(dbHelper))

        setContent {
            Parcial2Theme {
                // Le enseñamos a Android cómo construir el LoginViewModel (necesita el repositorio)
                val loginViewModel: LoginViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { LoginViewModel(userRepository) }
                    }
                )

                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        // Temporal: en la Fase 4 aquí navegaremos al Home
                        val nombre = SessionManager.usuarioActual.value?.nombres
                        Toast.makeText(this@MainActivity, "Bienvenido, $nombre", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
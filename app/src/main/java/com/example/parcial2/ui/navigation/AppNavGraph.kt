package com.example.parcial2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.ui.screens.auth.LoginScreen
import com.example.parcial2.ui.screens.auth.LoginViewModel
import com.example.parcial2.ui.screens.auth.RegistroScreen
import com.example.parcial2.ui.screens.auth.RegistroViewModel

@Composable
fun AppNavGraph(
    userRepository: UserRepository,
    onAutenticado: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.ruta
    ) {
        composable(NavRoutes.Login.ruta) {
            val viewModel: LoginViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { LoginViewModel(userRepository) }
                }
            )
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = onAutenticado,
                onIrARegistro = { navController.navigate(NavRoutes.Registro.ruta) }
            )
        }

        composable(NavRoutes.Registro.ruta) {
            val viewModel: RegistroViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { RegistroViewModel(userRepository) }
                }
            )
            RegistroScreen(
                viewModel = viewModel,
                onRegistroExitoso = onAutenticado,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
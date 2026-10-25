package com.example.parcial2.ui.navigation

// Lista cerrada de las pantallas de la app. En la Fase 4 agregaremos las demás.
sealed class NavRoutes(val ruta: String) {
    data object Login : NavRoutes("login")
    data object Registro : NavRoutes("registro")
    data object Home : NavRoutes("home")
    data object Cursos : NavRoutes("cursos")
    data object Perfil : NavRoutes("perfil")
}
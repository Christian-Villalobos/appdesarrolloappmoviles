package com.example.parcial2.ui.navigation

import android.app.Activity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.ui.components.AppBottomBar
import com.example.parcial2.ui.components.AppTopBar
import com.example.parcial2.ui.components.DialogConfirmacion
import com.example.parcial2.ui.components.ItemBarra
import com.example.parcial2.ui.screens.auth.LoginScreen
import com.example.parcial2.ui.screens.auth.LoginViewModel
import com.example.parcial2.ui.screens.auth.RegistroScreen
import com.example.parcial2.ui.screens.auth.RegistroViewModel
import com.example.parcial2.ui.screens.courses.CursoDetalleScreen
import com.example.parcial2.ui.screens.courses.CursoDetalleViewModel
import com.example.parcial2.ui.screens.courses.CursosScreen
import com.example.parcial2.ui.screens.courses.CursosViewModel
import com.example.parcial2.ui.screens.home.HomeScreen
import com.example.parcial2.ui.screens.profile.PerfilScreen
import com.example.parcial2.ui.screens.quiz.QuizScreen
import com.example.parcial2.ui.screens.quiz.QuizViewModel
import com.example.parcial2.ui.screens.profile.PerfilViewModel

// Botones de la barra inferior
private val itemsBarra = listOf(
    ItemBarra(NavRoutes.Home.ruta, "Inicio", Icons.Default.Home),
    ItemBarra(NavRoutes.Cursos.ruta, "Cursos", Icons.Default.School),
    ItemBarra(NavRoutes.Perfil.ruta, "Perfil", Icons.Default.Person)
)

// Pantallas con barra inferior (las tres secciones principales)
private val rutasPrincipales = itemsBarra.map { it.ruta }

// Pantallas que requieren sesión iniciada (llevan barra superior)
private val rutasProtegidas = rutasPrincipales + NavRoutes.CursoDetalle.ruta + NavRoutes.Quiz.ruta

private fun tituloDe(ruta: String?): String = when (ruta) {
    NavRoutes.Home.ruta -> "Inicio"
    NavRoutes.Cursos.ruta -> "Cursos"
    NavRoutes.Perfil.ruta -> "Perfil"
    NavRoutes.CursoDetalle.ruta -> "Curso"
    NavRoutes.Quiz.ruta -> "Quiz"
    else -> ""
}

@Composable
fun AppNavGraph(
    userRepository: UserRepository,
    studyRepository: StudyRepository
) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val mostrarBarraInferior = rutaActual in rutasPrincipales
    val mostrarBarraSuperior = rutaActual in rutasProtegidas

    val usuario by SessionManager.usuarioActual.collectAsStateWithLifecycle()
    var mostrarDialogoCierre by rememberSaveable { mutableStateOf(false) }

    // Si no hay sesión y estamos en una pantalla que la requiere, volvemos al Login
    LaunchedEffect(usuario, rutaActual) {
        if (usuario == null && rutaActual in rutasProtegidas) {
            navController.navigate(NavRoutes.Login.ruta) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Íconos de la barra de estado y de navegación: claros sobre verde, oscuros sobre fondo claro
    val vista = LocalView.current
    SideEffect {
        val ventana = (vista.context as Activity).window
        val controlador = WindowCompat.getInsetsController(ventana, vista)
        controlador.isAppearanceLightStatusBars = !mostrarBarraSuperior
        controlador.isAppearanceLightNavigationBars = !mostrarBarraInferior
    }

    // Después de autenticarse (login o registro) vamos al Home y borramos Login/Registro de la pila
    val irAlHome: () -> Unit = {
        navController.navigate(NavRoutes.Home.ruta) {
            popUpTo(NavRoutes.Login.ruta) { inclusive = true }
        }
    }

    val volverAtras: () -> Unit = { navController.popBackStack() }

    Scaffold(
        topBar = {
            if (mostrarBarraSuperior) {
                AppTopBar(
                    titulo = tituloDe(rutaActual),
                    onCerrarSesion = { mostrarDialogoCierre = true },
                    // La flecha de volver solo aparece fuera de las secciones principales
                    onVolver = if (mostrarBarraInferior) null else volverAtras
                )
            }
        },
        bottomBar = {
            if (mostrarBarraInferior) {
                AppBottomBar(
                    items = itemsBarra,
                    rutaActual = rutaActual,
                    onNavegar = { ruta ->
                        navController.navigate(ruta) {
                            popUpTo(NavRoutes.Home.ruta) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.Login.ruta,
            modifier = Modifier
                .padding(paddingInterno)
                .consumeWindowInsets(paddingInterno)
        ) {
            composable(NavRoutes.Login.ruta) {
                val viewModel: LoginViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { LoginViewModel(userRepository) }
                    }
                )
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = irAlHome,
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
                    onRegistroExitoso = irAlHome,
                    onVolver = volverAtras
                )
            }

            composable(NavRoutes.Home.ruta) {
                HomeScreen()
            }

            composable(NavRoutes.Cursos.ruta) {
                val viewModel: CursosViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { CursosViewModel(studyRepository) }
                    }
                )
                CursosScreen(
                    viewModel = viewModel,
                    onCursoClick = { cursoId ->
                        navController.navigate(NavRoutes.CursoDetalle.crearRuta(cursoId))
                    }
                )
            }

            composable(
                route = NavRoutes.CursoDetalle.ruta,
                arguments = listOf(
                    navArgument(NavRoutes.CursoDetalle.ARG_CURSO_ID) { type = NavType.IntType }
                )
            ) { entrada ->
                // Leemos el id del curso que viene dentro de la ruta
                val cursoId = entrada.arguments?.getInt(NavRoutes.CursoDetalle.ARG_CURSO_ID) ?: 0
                val viewModel: CursoDetalleViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { CursoDetalleViewModel(studyRepository, cursoId) }
                    }
                )
                CursoDetalleScreen(
                    viewModel = viewModel,
                    onQuizClick = { quizId ->
                        navController.navigate(NavRoutes.Quiz.crearRuta(quizId))
                    }
                )
            }
            composable(
                route = NavRoutes.Quiz.ruta,
                arguments = listOf(
                    navArgument(NavRoutes.Quiz.ARG_QUIZ_ID) { type = NavType.IntType }
                )
            ) { entrada ->
                val quizId = entrada.arguments?.getInt(NavRoutes.Quiz.ARG_QUIZ_ID) ?: 0
                val viewModel: QuizViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { QuizViewModel(studyRepository, quizId) }
                    }
                )
                QuizScreen(
                    viewModel = viewModel,
                    onVolver = volverAtras
                )
            }
            composable(NavRoutes.Perfil.ruta) {
                val viewModel: PerfilViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer { PerfilViewModel(studyRepository) }
                    }
                )
                PerfilScreen(
                    viewModel = viewModel,
                    onCerrarSesion = { mostrarDialogoCierre = true }
                )
            }
        }
    }

    // Diálogo de confirmación: se muestra SIEMPRE antes de destruir la sesión
    if (mostrarDialogoCierre) {
        DialogConfirmacion(
            titulo = "Cerrar sesión",
            mensaje = "¿Seguro que deseas cerrar sesión?",
            textoConfirmar = "Cerrar sesión",
            onConfirmar = {
                mostrarDialogoCierre = false
                SessionManager.cerrarSesion()   // el LaunchedEffect nos lleva al Login
            },
            onCancelar = { mostrarDialogoCierre = false }
        )
    }
}
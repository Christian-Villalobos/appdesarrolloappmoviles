package com.example.parcial2.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.parcial2.data.local.SessionManager
import com.example.parcial2.domain.model.Usuario
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    // El Home solo necesita saber quién inició sesión
    val usuario: StateFlow<Usuario?> = SessionManager.usuarioActual
}
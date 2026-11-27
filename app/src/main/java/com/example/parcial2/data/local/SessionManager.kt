package com.example.parcial2.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.parcial2.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    // Nombres del archivo y de las "claves" donde guardamos cada dato
    private const val PREFS_NAME = "quiz_carabineros_sesion"
    private const val KEY_ID = "id"
    private const val KEY_NOMBRES = "nombres"
    private const val KEY_APELLIDOS = "apellidos"
    private const val KEY_EMAIL = "email"
    private const val KEY_TOKEN = "token"

    // Empieza en null: se asigna cuando MainActivity llama a inicializar()
    private var prefs: SharedPreferences? = null

    // Versión privada y modificable: solo SessionManager puede cambiarla
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)

    // Versión pública y de solo lectura: el resto de la app solo puede observarla
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    // Token JWT para autenticar peticiones contra el microservicio Spring Boot
    var token: String? = null
        private set

    // Se llama UNA vez al abrir la app. Necesita un Context porque SessionManager
    // es un "object" y no tiene uno propio.
    fun inicializar(context: Context) {
        if (prefs != null) return   // si ya estaba inicializado, no hacemos nada

        val sp = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp

        // Si hay un usuario guardado, lo restauramos junto con su token
        val id = sp.getInt(KEY_ID, -1)
        val email = sp.getString(KEY_EMAIL, null)
        if (id != -1 && email != null) {
            _usuarioActual.value = Usuario(
                id = id,
                nombres = sp.getString(KEY_NOMBRES, "").orEmpty(),
                apellidos = sp.getString(KEY_APELLIDOS, "").orEmpty(),
                email = email
            )
            token = sp.getString(KEY_TOKEN, null)
        }
    }

    fun iniciarSesion(usuario: Usuario, token: String? = null) {
        _usuarioActual.value = usuario
        this.token = token

        // Guardamos todo en el archivo; apply() escribe en segundo plano
        prefs?.edit()
            ?.putInt(KEY_ID, usuario.id)
            ?.putString(KEY_NOMBRES, usuario.nombres)
            ?.putString(KEY_APELLIDOS, usuario.apellidos)
            ?.putString(KEY_EMAIL, usuario.email)
            ?.putString(KEY_TOKEN, token)
            ?.apply()
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        token = null
        prefs?.edit()?.clear()?.apply()   // borramos todo lo guardado
    }
}
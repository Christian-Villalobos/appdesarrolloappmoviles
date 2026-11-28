package com.example.parcial2.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.parcial2.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    // Nombres del archivo y de las "claves" donde guardamos cada dato
    private const val PREFS_NAME = "quiz_carabineros_sesion_segura"
    private const val KEY_ID = "id"
    private const val KEY_NOMBRES = "nombres"
    private const val KEY_APELLIDOS = "apellidos"
    private const val KEY_EMAIL = "email"
    private const val KEY_TOKEN = "token"
    private const val KEY_EXPIRA_EN = "expira_en"

    // 24 horas en milisegundos, según especificación del backend
    private const val DURACION_TOKEN_MS = 86_400_000L

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

        val appContext = context.applicationContext
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        prefs = EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        restaurarSesion()
    }

    fun iniciarSesion(usuario: Usuario, token: String? = null) {
        _usuarioActual.value = usuario
        this.token = token

        val expiraEn = System.currentTimeMillis() + DURACION_TOKEN_MS

        // Guardamos todo cifrado en el archivo; apply() escribe en segundo plano
        prefs?.edit()
            ?.putInt(KEY_ID, usuario.id)
            ?.putString(KEY_NOMBRES, usuario.nombres)
            ?.putString(KEY_APELLIDOS, usuario.apellidos)
            ?.putString(KEY_EMAIL, usuario.email)
            ?.putString(KEY_TOKEN, token)
            ?.putLong(KEY_EXPIRA_EN, expiraEn)
            ?.apply()
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        token = null
        prefs?.edit()?.clear()?.apply()   // borramos todo lo guardado
    }

    private fun restaurarSesion() {
        val sp = prefs ?: return
        val tokenGuardado = sp.getString(KEY_TOKEN, null)
        val expiraEn = sp.getLong(KEY_EXPIRA_EN, 0L)

        // Si no hay token o ya pasaron las 24 h, limpiamos la sesión
        if (tokenGuardado.isNullOrBlank() || System.currentTimeMillis() >= expiraEn) {
            cerrarSesion()
            return
        }

        val id = sp.getInt(KEY_ID, -1)
        val email = sp.getString(KEY_EMAIL, null)
        if (id != -1 && email != null) {
            _usuarioActual.value = Usuario(
                id = id,
                nombres = sp.getString(KEY_NOMBRES, "").orEmpty(),
                apellidos = sp.getString(KEY_APELLIDOS, "").orEmpty(),
                email = email
            )
            token = tokenGuardado
        } else {
            cerrarSesion()
        }
    }
}
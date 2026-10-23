package com.example.parcial2.data.local

import android.content.ContentValues
import com.example.parcial2.data.local.dao.DbContract
import com.example.parcial2.domain.model.Usuario

class UsuarioDao(private val helper: DbContract) {

    fun registrar(nombres: String, apellidos: String, email: String, password: String): Long {
        val values = ContentValues().apply {
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("email", email.trim().lowercase())
            put("password", password) // ver nota de seguridad abajo
        }
        return helper.writableDatabase.insert("usuarios", null, values) // -1 si el email ya existe
    }

    fun login(email: String, password: String): Usuario? {
        val cursor = helper.readableDatabase.query(
            "usuarios",
            arrayOf("id", "nombres", "apellidos", "email"),
            "email = ? AND password = ?",
            arrayOf(email.trim().lowercase(), password),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) Usuario(
                id = it.getInt(0),
                nombres = it.getString(1),
                apellidos = it.getString(2),
                email = it.getString(3)
            ) else null
        }
    }
}
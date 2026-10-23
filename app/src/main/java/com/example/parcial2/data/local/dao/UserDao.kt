package com.example.parcial2.data.local.dao

import android.content.ContentValues
import com.example.parcial2.data.local.db.DbContract.Usuarios
import com.example.parcial2.data.local.db.DbHelper
import com.example.parcial2.domain.model.Usuario

class UserDao(private val dbHelper: DbHelper) {

    // Inserta un usuario. Devuelve su id, o -1 si falló (por ejemplo, correo repetido)
    fun insertar(
        nombres: String,
        apellidos: String,
        email: String,
        passwordHash: String
    ): Long {
        val valores = ContentValues().apply {
            put(Usuarios.NOMBRES, nombres)
            put(Usuarios.APELLIDOS, apellidos)
            put(Usuarios.EMAIL, email)
            put(Usuarios.PASSWORD_HASH, passwordHash)
        }
        return dbHelper.writableDatabase.insert(Usuarios.TABLA, null, valores)
    }

    // ¿Ya existe una cuenta con este correo?
    fun existeEmail(email: String): Boolean {
        val cursor = dbHelper.readableDatabase.query(
            Usuarios.TABLA,
            arrayOf(Usuarios.ID),
            "${Usuarios.EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )
        return cursor.use { it.count > 0 }
    }

    // Busca un usuario con ese correo y esa contraseña (ya encriptada).
    // Devuelve el Usuario, o null si no hay coincidencia.
    fun autenticar(email: String, passwordHash: String): Usuario? {
        val cursor = dbHelper.readableDatabase.query(
            Usuarios.TABLA,
            null,
            "${Usuarios.EMAIL} = ? AND ${Usuarios.PASSWORD_HASH} = ?",
            arrayOf(email, passwordHash),
            null, null, null
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Usuario(
                    id = it.getInt(it.getColumnIndexOrThrow(Usuarios.ID)),
                    nombres = it.getString(it.getColumnIndexOrThrow(Usuarios.NOMBRES)),
                    apellidos = it.getString(it.getColumnIndexOrThrow(Usuarios.APELLIDOS)),
                    email = it.getString(it.getColumnIndexOrThrow(Usuarios.EMAIL))
                )
            } else {
                null
            }
        }
    }
}
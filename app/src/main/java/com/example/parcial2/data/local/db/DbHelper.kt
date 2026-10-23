package com.example.parcial2.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.parcial2.data.local.db.DbContract.Cursos
import com.example.parcial2.data.local.db.DbContract.Preguntas
import com.example.parcial2.data.local.db.DbContract.Quizzes
import com.example.parcial2.data.local.db.DbContract.Resultados
import com.example.parcial2.data.local.db.DbContract.Unidades
import com.example.parcial2.data.local.db.DbContract.Usuarios

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DbContract.DB_NAME, null, DbContract.DB_VERSION) {

    // Se ejecuta antes de crear o abrir la base: activamos las claves foráneas
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // Se ejecuta UNA sola vez: la primera vez que se pide la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${Usuarios.TABLA} (
                ${Usuarios.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Usuarios.NOMBRES} TEXT NOT NULL,
                ${Usuarios.APELLIDOS} TEXT NOT NULL,
                ${Usuarios.EMAIL} TEXT NOT NULL UNIQUE,
                ${Usuarios.PASSWORD_HASH} TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ${Cursos.TABLA} (
                ${Cursos.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Cursos.NOMBRE} TEXT NOT NULL,
                ${Cursos.DESCRIPCION} TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ${Unidades.TABLA} (
                ${Unidades.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Unidades.CURSO_ID} INTEGER NOT NULL,
                ${Unidades.NOMBRE} TEXT NOT NULL,
                FOREIGN KEY (${Unidades.CURSO_ID})
                    REFERENCES ${Cursos.TABLA}(${Cursos.ID}) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ${Quizzes.TABLA} (
                ${Quizzes.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Quizzes.UNIDAD_ID} INTEGER NOT NULL,
                ${Quizzes.TITULO} TEXT NOT NULL,
                FOREIGN KEY (${Quizzes.UNIDAD_ID})
                    REFERENCES ${Unidades.TABLA}(${Unidades.ID}) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ${Preguntas.TABLA} (
                ${Preguntas.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Preguntas.QUIZ_ID} INTEGER NOT NULL,
                ${Preguntas.ENUNCIADO} TEXT NOT NULL,
                ${Preguntas.OPCION_A} TEXT NOT NULL,
                ${Preguntas.OPCION_B} TEXT NOT NULL,
                ${Preguntas.OPCION_C} TEXT NOT NULL,
                ${Preguntas.OPCION_D} TEXT NOT NULL,
                ${Preguntas.INDICE_CORRECTO} INTEGER NOT NULL,
                ${Preguntas.JUSTIFICACION} TEXT NOT NULL,
                FOREIGN KEY (${Preguntas.QUIZ_ID})
                    REFERENCES ${Quizzes.TABLA}(${Quizzes.ID}) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE ${Resultados.TABLA} (
                ${Resultados.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Resultados.USUARIO_ID} INTEGER NOT NULL,
                ${Resultados.QUIZ_ID} INTEGER NOT NULL,
                ${Resultados.ACIERTOS} INTEGER NOT NULL,
                ${Resultados.TOTAL_PREGUNTAS} INTEGER NOT NULL,
                ${Resultados.FECHA} INTEGER NOT NULL,
                FOREIGN KEY (${Resultados.USUARIO_ID})
                    REFERENCES ${Usuarios.TABLA}(${Usuarios.ID}) ON DELETE CASCADE,
                FOREIGN KEY (${Resultados.QUIZ_ID})
                    REFERENCES ${Quizzes.TABLA}(${Quizzes.ID}) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        SeedData.insertar(db)
    }

    // Se ejecuta si se sube DB_VERSION. Como es una app académica, se borra y recrea todo
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${Resultados.TABLA}")
        db.execSQL("DROP TABLE IF EXISTS ${Preguntas.TABLA}")
        db.execSQL("DROP TABLE IF EXISTS ${Quizzes.TABLA}")
        db.execSQL("DROP TABLE IF EXISTS ${Unidades.TABLA}")
        db.execSQL("DROP TABLE IF EXISTS ${Cursos.TABLA}")
        db.execSQL("DROP TABLE IF EXISTS ${Usuarios.TABLA}")
        onCreate(db)
    }
}